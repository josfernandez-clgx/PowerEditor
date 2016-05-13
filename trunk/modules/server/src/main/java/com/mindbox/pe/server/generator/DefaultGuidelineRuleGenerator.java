package com.mindbox.pe.server.generator;

import static com.mindbox.pe.common.LogUtil.logDebug;
import static com.mindbox.pe.common.LogUtil.logError;
import static com.mindbox.pe.common.LogUtil.logInfo;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.common.validate.TemplateValidator;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.generator.rule.GuidelineRule;
import com.mindbox.pe.server.generator.rule.GuidelineRuleFactory;
import com.mindbox.pe.server.model.TimeSlice;
import com.mindbox.pe.server.model.TimeSliceContainer;
import com.mindbox.pe.xsd.config.RuleGenerationLHS.Pattern;

/**
 * Guideline rule generator. This replaces {@link com.mindbox.pe.server.generator.AeRuleFinisher},
 * {@link com.mindbox.pe.server.generator.AeRuleBuilder},and
 * {@link com.mindbox.pe.server.generator.RuleGeneratorHelper}.
 * 
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 * @see com.mindbox.pe.server.generator.RuleGenerator
 */
public class DefaultGuidelineRuleGenerator implements GuidelineRuleGenerator, ErrorContextProvider {

	private final Logger log = Logger.getLogger(getClass());
	private final GridTemplate template;
	private final AeMapper aeMapper;
	private final GuidelinePostProcessItemHelper postProcessItemHelper;
	private final GuidelineLHSHelper lhsHelper;
	private final GuidelineRHSHelper rhsHelper;
	private final GuidelineRuleFactory guidelineRuleFactory;
	private int currRow = 0;
	private int currColumn = -1;
	private GuidelineGenerateParams currentGenerateParams;
	private final BufferedGenerator bufferedGenerator;

	public DefaultGuidelineRuleGenerator(final GridTemplate template, final GenerateStats generateStats, final OutputController outputController) throws RuleGenerationException {
		this.bufferedGenerator = new DefaultBufferedGenerator(generateStats, outputController, outputController.getRuleFile(template.getUsageType(), template.getName()), this);
		this.aeMapper = AeMapper.getGuidelineInstance();
		this.template = template;
		postProcessItemHelper = new GuidelinePostProcessItemHelper(bufferedGenerator);
		lhsHelper = new GuidelineLHSHelper(bufferedGenerator);
		rhsHelper = new GuidelineRHSHelper(bufferedGenerator);
		this.guidelineRuleFactory = new GuidelineRuleFactory(this);
		currentGenerateParams = null;
		currRow = 0;
		currColumn = -1;
		aeMapper.reInitRuleVariables();
		postProcessItemHelper.clear();
		lhsHelper.clear();
		rhsHelper.clear();
	}

	private void generate(TimeSliceContainer timeSliceContainer, GuidelineRule guidelineRule, GuidelineGenerateParams ruleParam) throws RuleGenerationException {
		logDebug(log, ">>> generate(guidelineRule,ruleParam): %s, %s", guidelineRule, ruleParam);
		synchronized (this) {
			this.currentGenerateParams = ruleParam;
		}
		this.currRow = ruleParam.getRowNum();
		this.currColumn = ruleParam.getColumnNum();

		logDebug(log, "   generate(ruleDefinition,ruleParam): currParam  = %s", currentGenerateParams);
		logDebug(log, "   generate(ruleDefinition,ruleParam): currRow    = %s", currRow);
		logDebug(log, "   generate(ruleDefinition,ruleParam): currColumn = %s", currColumn);

		// reset invariants for a single rule
		resetInvariantsForEachRule();

		try {
			long startTime = System.currentTimeMillis();
			int count = writeRulesOptimized(timeSliceContainer, guidelineRule, ruleParam);
			if (count > 0) {
				bufferedGenerator.getGenerateStats().addNumRulesGenerated(count);
			}
			logDebug(log, "Generated %d rules for %s for Rule[%s]; time=%d (ms)", count, guidelineRule, ruleParam, (System.currentTimeMillis() - startTime));
		}
		catch (RuleGenerationException ex) {
			reportError("Error generating rule for " + guidelineRule + ": " + ex.getMessage());
			log.error("write(AeRule,AbstractGenerateParms): Rule generation error", ex);
		}
		catch (Exception ex) {
			reportError("Failed to generate rule: " + ex.getMessage() + " (" + ex.getClass().getName() + ")");
			log.error("write(AeRule,AbstractGenerateParms): Exception", ex);
		}

		logDebug(log, "<<< generate(ruleDefinition,ruleParam): %s", guidelineRule);
	}

	// TODO This generates rules sequentially. Optimize this!!!
	private void generate(TimeSliceContainer timeSliceContainer, RuleDefinition ruleDefinition, int columnNumber, List<GuidelineGenerateParams> ruleParamList) throws RuleGenerationException {
		logDebug(log, ">>> generate(ruleDefinition,ruleParamList): %s,%s params", ruleDefinition, ruleParamList.size());

		try {
			final GuidelineRule guidelineRule = guidelineRuleFactory.createGuidelineRule(template, ruleDefinition);

			logDebug(log, "... generate: guidelineRule = %s", guidelineRule);

			// Write rules for each generation parameters
			for (GuidelineGenerateParams ruleGenerateParams : ruleParamList) {
				ruleGenerateParams.setColumNumber(columnNumber);
				if (columnNumber < 0 || RuleGeneratorHelper.isNotEmpty(ruleGenerateParams.getColumnValue(columnNumber))) {
					ruleGenerateParams.setDescription(aeMapper.generateRuleDescription(ruleGenerateParams)
							+ (ruleGenerateParams.getTemplate() == null ? "" : " for template " + ruleGenerateParams.getTemplate().getID()));
					logDebug(log, "... generate: generating rules for %s", ruleGenerateParams);
					try {
						generate(timeSliceContainer, guidelineRule, ruleGenerateParams);
					}
					catch (RuleGenerationException ex) {
						reportError("Failed to generate rules for " + ruleGenerateParams + ": " + ex.getErrorMessage());
					}
				}
				else {
					logInfo(log, "not generated for empty column: %s", ruleGenerateParams);
				}
			}
			logDebug(log, "<<< generate(ruleDefinition,template,ruleParamList): %s", guidelineRule);
		}
		catch (RuleGenerationException ex) {
			logError(log, ex, "Exception in generate for rule=%s, column=%s, ruleParams=%s", ruleDefinition, columnNumber, ruleParamList);
			reportError(ex.getErrorMessage());
		}
	}

	@Override
	public synchronized final void generateOptimized(final int percentageAllocation, final TimeSliceContainer timeSliceContainer, final GuidelineReportFilter filter) throws RuleGenerationException {
		if (timeSliceContainer == null) {
			throw new NullPointerException("timeSliceContainer cannot be null");
		}
		if (timeSliceContainer.size() == 0) {
			throw new IllegalArgumentException("timeSliceContainer cannot be empty");
		}

		logDebug(log, ">>> generateOptimized(template): %s, %s", template, filter);

		int templateID = template.getID();

		logDebug(log, "generateOptimized: consolidating grids of status %s, templateID = %s", bufferedGenerator.getOutputController().getStatus(), templateID);
		try {
			List<ProductGrid> prodGridList = GridManager.getInstance().getAllGridsForTemplate(templateID);
			// sanity check
			if (prodGridList != null) {
				logInfo(log, "generateOptimized: number of grids = %d", prodGridList.size());

				long startTime = System.currentTimeMillis();

				final List<GuidelineGenerateParams> ruleParamsList = new GuidelineParamsProducer(this).generateProductGenParms(
						bufferedGenerator.getOutputController().getStatus(),
						template,
						prodGridList,
						filter);
				logInfo(log, "***");
				logInfo(log, "*** Number of ruleparams = %s for template [%s]; took %d (ms)", ruleParamsList.size(), template.getName(), (System.currentTimeMillis() - startTime));
				logInfo(log, "***");

				if (!ruleParamsList.isEmpty()) {
					bufferedGenerator.startGeneration();

					generateRules(timeSliceContainer, ruleParamsList);
				}
			}
			logInfo(log, "<<< generateOptimized(template): %s", template);
		}
		catch (RuleGenerationException ex) {
			reportError("Failed to generate rules for template: " + template.getAuditDescription() + ": " + ex.getMessage());
		}
		catch (Exception ex) {
			log.error("Failed to generate rules for " + template, ex);
			reportError("Failed to generate rules for template: " + template.getAuditDescription() + ": " + ex.getMessage());
		}
		finally {
			bufferedGenerator.endGeneration();
		}
	}

	// TODO This generate all rules for the specified data sequentially one at a time. Optimize this!!!
	/**
	 * Generate rules for the spcified rule params list with the specified time slice container.
	 * <p>
	 * This generates rules for all rule generate params for the template rule sequentially.
	 * Then, it generate rules for column rules, one by one, sequentially.
	 * </p>
	 * @param timeSliceContainer
	 * @param ruleParamsList
	 * @throws RuleGenerationException
	 */
	void generateRules(final TimeSliceContainer timeSliceContainer, final List<GuidelineGenerateParams> ruleParamsList) throws RuleGenerationException {
		RuleDefinition ruleDefinition = template.getRuleDefinition();
		if (ruleDefinition != null && !ruleDefinition.isEmpty()) {
			logInfo(log, "generateOptimized: generating rules for template deploy rule...");
			String validationError = TemplateValidator.isValid(ruleDefinition, template, DomainManager.getInstance());
			if (validationError != null) {
				reportError("Error validating rule for " + ruleDefinition + ": " + validationError);
			}
			else {
				final String msg = TemplateValidator.checkForIncompleteElements(ruleDefinition, template, -1);
				if (msg != null) {
					log.warn(msg);
				}
				generate(timeSliceContainer, template.getRuleDefinition(), -1, ruleParamsList);
			}
		}

		logDebug(log, "generateOptimized: handling column deploy rules, if any...");
		for (Iterator<GridTemplateColumn> iter = template.getColumns().iterator(); iter.hasNext();) {
			GridTemplateColumn element = iter.next();
			ruleDefinition = element.getRuleDefinition();
			if (ruleDefinition != null && !ruleDefinition.isEmpty()) {
				logInfo(log, "generateOptimized: generating rules for column %s rule...", element.getColumnNumber());
				String validationError = TemplateValidator.isValid(ruleDefinition, template, DomainManager.getInstance());
				if (validationError != null)
					reportError("Error validating rule for " + ruleDefinition + ": " + validationError);
				else {
					generate(timeSliceContainer, ruleDefinition, element.getColumnNumber(), ruleParamsList);
				}
			}
		}

	}

	@Override
	public final String getErrorContext() {
		final StringBuilder errorBuff = new StringBuilder("Template:");
		errorBuff.append(template.getAuditDescription());
		synchronized (this) {
			if (currentGenerateParams != null) {
				errorBuff.append("; Guideline: ");
				if (currentGenerateParams.getSunrise() != null) {
					errorBuff.append(currentGenerateParams.getSunrise());
				}
				errorBuff.append("-");
				if (currentGenerateParams.getSunset() != null) {
					errorBuff.append(currentGenerateParams.getSunset());
				}
				errorBuff.append(",row=");
				errorBuff.append(currRow);
				if (currColumn > 0) {
					errorBuff.append(",col=");
					errorBuff.append(currColumn);
				}
			}
		}
		return errorBuff.toString();
	}

	@Override
	public final void reportError(String str) throws RuleGenerationException {
		bufferedGenerator.reportError(str);
	}

	private void resetInvariantsForEachRule() {
		this.aeMapper.reInitRuleVariables();
		postProcessItemHelper.clear();
		lhsHelper.clear();
		rhsHelper.clear();
	}

	private int writeRulesOptimized(TimeSliceContainer timeSliceContainer, GuidelineRule guidelineRule, GuidelineGenerateParams ruleParams) throws RuleGenerationException, IOException {
		int count = 0;
		List<TimeSlice> timeSliceList = timeSliceContainer.getApplicableTimeSlices(ruleParams.getSunrise(), ruleParams.getSunset());
		TimeSlice[] timeSlices = timeSliceList.toArray(new TimeSlice[0]);
		// TODO GKIM: Tests if timeSlices is empty or not; if empty, do something here!!!
		if (ruleParams.hasEntitySpecificMessage()) {
			GenericEntityType type = GenericEntityType.forID(ConfigurationManager.getInstance().getEntityTypeForMessageContext().getTypeID().intValue());
			int[] messageContextEntityIDs = ruleParams.extractEntityIDsForControlPattern(timeSlices[0], type);
			if (messageContextEntityIDs != null && messageContextEntityIDs.length > 0) {
				for (int i = 0; i < messageContextEntityIDs.length; i++) {
					writeSingleRuleForMessageContextEntityID(timeSlices, guidelineRule, ruleParams, type, messageContextEntityIDs[i]);
					++count;
				}
			}
			else {
				writeSingleRuleForMessageContextEntityID(timeSlices, guidelineRule, ruleParams, null, -1);
				++count;
			}
		}
		else {
			writeSingleRuleForMessageContextEntityID(timeSlices, guidelineRule, ruleParams, null, -1);
			++count;
		}
		return count;
	}

	private final void writeSingleRuleForMessageContextEntityID(TimeSlice[] timeSlices, GuidelineRule guidelineRule, GuidelineGenerateParams ruleParams, GenericEntityType typeForMessageContext,
			int messageContextEntityID) throws IOException, RuleGenerationException {
		assert (timeSlices.length > 0);
		logDebug(log, ">>> writeSingleRule: params=%s,msgEntityID=%s", ruleParams, messageContextEntityID);
		bufferedGenerator.resetTab();

		// Generate unique rule name for each A*E rule
		ruleParams.setName(aeMapper.generateRuleName(ruleParams));

		logDebug(log, "    writeSingleRule: writing LHS of %s", ruleParams.getName());

		// begin rule
		bufferedGenerator.openParan();
		bufferedGenerator.print("define-rule ");
		bufferedGenerator.print(ruleParams.getName());
		bufferedGenerator.print(" ");
		bufferedGenerator.quote();
		bufferedGenerator.print(ruleParams.getDescription());
		bufferedGenerator.quote();

		// generate the (declare (ruleset ...)) pattern. This pattern is generated by default,
		// unless explicitly turned off in the PE config file.
		final Pattern rulesetConfig = ConfigurationManager.getInstance().getRuleGenerationConfigHelper(ruleParams.getUsage()).getRulesetPatternConfig();
		if (rulesetConfig == null || ConfigUtil.isPatternOn(rulesetConfig)) {
			bufferedGenerator.nextLineIndent();
			bufferedGenerator.openParan();
			bufferedGenerator.print("declare ");
			bufferedGenerator.openParan();
			bufferedGenerator.print("ruleset ");
			bufferedGenerator.print(AeMapper.getRuleset(ruleParams));
			bufferedGenerator.closeParan();
			bufferedGenerator.closeParan();
		}
		bufferedGenerator.nextLine();
		bufferedGenerator.nextLineIndent();

		// write LHS patterns
		lhsHelper.generateLHS(guidelineRule, ruleParams, timeSlices, typeForMessageContext, messageContextEntityID);

		// process for dynamic strings on RHS, if any
		postProcessItemHelper.processForAttributeAndClassReferencesInMessages(ruleParams, messageContextEntityID);
		postProcessItemHelper.processForDynamicStringInRHS(guidelineRule.getRHSFunctionCall(), ruleParams);
		postProcessItemHelper.processForDynamicStringInMessage(ruleParams, messageContextEntityID);
		postProcessItemHelper.processForAttributeAndClassReferencesInRHS(guidelineRule.getRHSFunctionCall(), ruleParams);

		postProcessItemHelper.writePatterns(guidelineRule, ruleParams.getUsage(), false);

		bufferedGenerator.nextLineOutdent();
		bufferedGenerator.print("=>");
		bufferedGenerator.nextLineIndent();

		logDebug(log, "... writeSingleRule: writing RHS...");

		// write RHS function call
		rhsHelper.generateRHS(guidelineRule, ruleParams, messageContextEntityID);

		logDebug(log, "... writeSingleRule: finishing rule file for pe-action");

		bufferedGenerator.outdent();
		bufferedGenerator.outdent();
		bufferedGenerator.closeParan();
		bufferedGenerator.nextLine();
		bufferedGenerator.nextLine();

		bufferedGenerator.writeOut();

		logDebug(log, "<<< writeSingleRule");
	}

}