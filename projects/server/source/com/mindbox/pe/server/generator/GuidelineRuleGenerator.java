package com.mindbox.pe.server.generator;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.common.validate.TemplateValidator;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.LineagePatternConfigSet;
import com.mindbox.pe.server.config.RuleGenerationConfiguration;
import com.mindbox.pe.server.generator.rule.GuidelineRule;
import com.mindbox.pe.server.generator.rule.GuidelineRuleFactory;
import com.mindbox.pe.server.model.TimeSlice;
import com.mindbox.pe.server.model.TimeSliceContainer;

/**
 * Guideline rule generator. This replaces {@link com.mindbox.pe.server.generator.AeRuleFinisher},
 * {@link com.mindbox.pe.server.generator.AeRuleBuilder},and
 * {@link com.mindbox.pe.server.generator.RuleGeneratorHelper}.
 * 
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 * @see com.mindbox.pe.server.generator.RuleGenerator
 */
public class GuidelineRuleGenerator extends AbstractBufferedGenerator {

	private static GuidelineRuleGenerator instance = null;

	/**
	 * Gets the one and only instance of this class.
	 * 
	 * @return the only instance
	 */
	public static GuidelineRuleGenerator getInstance() {
		if (instance == null) {
			instance = new GuidelineRuleGenerator();
		}
		return instance;
	}

	private GridTemplate currTemplate = null;
	private GuidelineGenerateParams currGrid = null;
	private int currRow = 0;
	private int currColumn = -1;
	private final AeMapper aeMapper;
	private final GuidelinePostProcessItemHelper postProcessItemHelper;
	private final GuidelineLHSHelper lhsHelper;
	private final GuidelineRHSHelper rhsHelper;

	private GuidelineRuleGenerator() {
		this.aeMapper = AeMapper.getGuidelineInstance();
		postProcessItemHelper = new GuidelinePostProcessItemHelper(this);
		lhsHelper = new GuidelineLHSHelper(this);
		rhsHelper = new GuidelineRHSHelper(this);
	}

	public synchronized void init(OutputController outputController) {
		super.init(outputController);
		currTemplate = null;
		currGrid = null;
		currRow = 0;
		currColumn = -1;
		aeMapper.reInitRuleVariables();
		postProcessItemHelper.clear();
		lhsHelper.clear();
		rhsHelper.clear();
	}

	public synchronized void writeAll() throws RuleGenerationException {
		super.writeAll();
		getOutputController().closeRuleWriters();
	}

	protected PrintWriter getPrintWriter(String status, OutputController outputController) throws RuleGenerationException {
		if (currGrid == null) {
			return outputController.getAEFileWriter(status, "UNKNOWN");
		}
		else {
			return outputController.getRuleWriter(status, currGrid);
		}
	}

	protected String getErrorContext() {
		if (currTemplate != null) {
			StringBuffer errorBuff = new StringBuffer("Template:");
			errorBuff.append(currTemplate.getAuditDescription());
			if (currGrid != null) {
				errorBuff.append("; Guideline: ");
				if (currGrid.getSunrise() != null) {
					errorBuff.append(currGrid.getSunrise());
				}
				errorBuff.append("-");
				if (currGrid.getSunset() != null) {
					errorBuff.append(currGrid.getSunset());
				}
				errorBuff.append(",row=");
				errorBuff.append(currRow);
				if (currColumn > 0) {
					errorBuff.append(",col=");
					errorBuff.append(currColumn);
				}
			}
			else {
				errorBuff.append("*UNKNOWN*");
			}
			return errorBuff.toString();
		}
		else {
			return null;
		}
	}

	/**
	 * Generates one guideline generate params for each rule;
	 * This does NOT optimize number of rules generatoed
	 * No longer used as of PE 5.5.4
	 */
	private List<GuidelineGenerateParams> buildProductGenParms(String status, GridTemplate gridtemplate, List<ProductGrid> gridList,
			GuidelineReportFilter filter) throws RuleGenerationException {
		logger.debug(">>> buildProductGenParms: " + gridtemplate);
		LinkedList<GuidelineGenerateParams> linkedlist = new LinkedList<GuidelineGenerateParams>();
		try {
			for (ProductGrid prodGrid : gridList) {
				if (logger.isDebugEnabled()) logger.debug("buildProductGenParms: processing " + prodGrid);

				// If effective date is the same as the expiration date, generate a deploy error message
				// and skip the guideline activation
				if (RuleGeneratorHelper.hasSameEffectiveAndExpirationDates(prodGrid)) {
					reportError("WARNING: No rules generated for activation " + prodGrid.getAuditDescription()
							+ " because it's effective date is the same as the expiration date");
				}
				// TT 1946, part (3)
				// If prodGrid contains context not allowed in the control pattern, throw RuleGenerationException
				else if (RuleGeneratorHelper.getFirstDisallowedEntityInContext(prodGrid) != null) {
					throw new RuleGenerationException(RuleGeneratorHelper.getFirstDisallowedEntityInContext(prodGrid).getDisplayName()
							+ " is not allowed in the context for " + gridtemplate.getUsageType().getDisplayName() + " guideline type");
				}
				// add only if the status is allowed and it's not too long ago.
				else if (filter.isAcceptable(prodGrid)) {
					int rowCount = prodGrid.getNumRows();

					// iterate over rows to generate a rule per grid row
					for (int rowi = 0; rowi < rowCount; rowi++) {
						// on InvalidDataException, throw it and stop rule gen.
						try {
							GuidelineGenerateParams ruleParams = new GuidelineGenerateParams(
									prodGrid.getEffectiveDate(),
									prodGrid.getExpirationDate(),
									prodGrid,
									-1,
									rowi + 1,
									false);
							linkedlist.add(ruleParams);
						}
						catch (InvalidDataException _ex) {
							logger.error("Invalid data for " + prodGrid + " at row " + (rowi + 1) + ": " + _ex.getMessage());
							throw new RuleGenerationException("Invalid data at row " + (rowi + 1) + ": " + _ex.getMessage());
						}
					} // for
				}
				else {
					if (logger.isDebugEnabled()) logger.debug("Ignoring " + prodGrid + "; didn't pass the filter");
				}
			}
			logger.debug("<<< buildProductGenParms");
			return linkedlist;
		}
		catch (RuleGenerationException ex) {
			logger.error("Failed to genereate rue-generataion-params for " + gridtemplate, ex);
			throw ex;
		}
		catch (Exception exception) {
			logger.error("Failed to genereate rue-generataion-params for " + gridtemplate, exception);
			throw new RuleGenerationException(exception.getMessage());
		}
	}

	public synchronized void generateOptimized(TimeSliceContainer timeSliceContainer, GridTemplate template, GuidelineReportFilter filter)
			throws RuleGenerationException {
		if (timeSliceContainer == null) throw new NullPointerException("timeSliceContainer cannot be null");
		if (timeSliceContainer.size() == 0) throw new IllegalArgumentException("timeSliceContainer cannot be empty");
		logger.debug(">>> generateOptimized(template): " + template + "," + filter);

		int templateID = template.getID();
		this.currTemplate = template;

		logger.debug("generateOptimized: consolidating grids of status " + getInitStatus() + ", templateID = " + templateID);
		try {
			List<ProductGrid> prodGridList = GridManager.getInstance().getAllGridsForTemplate(templateID);
			// sanity check
			if (prodGridList != null) {
				logger.debug("generateOptimized: grid data list size = " + prodGridList.size());
				List<GuidelineGenerateParams> ruleParamsList = new GuidelineParamsProducer(this).generateProductGenParms(
						getInitStatus(),
						template,
						prodGridList,
						filter);
				logger.info("***");
				logger.info("*** Number of ruleparams = " + ruleParamsList.size());
				logger.info("***");

				logger.debug("generateOptimized: ruleParamsList size = " + ruleParamsList.size());
				if (!ruleParamsList.isEmpty()) {
					RuleDefinition ruleDefinition = template.getRuleDefinition();
					if (ruleDefinition != null && !ruleDefinition.isEmpty()) {
						logger.debug("generateOptimized: handling template deploy rule...");
						String validationError = TemplateValidator.isValid(ruleDefinition, template, DomainManager.getInstance());
						if (validationError != null) {
							reportError("Error validating rule for " + ruleDefinition + ": " + validationError);
						}
						else {
							String msg = TemplateValidator.checkForIncompleteElements(ruleDefinition, template, -1);
							if (msg != null) {
								logger.warn(msg);
							}
							generate(timeSliceContainer, template.getRuleDefinition(), -1, template, ruleParamsList);
						}
					}

					logger.debug("generateOptimized: handling column deploy rules, if any...");
					for (Iterator<GridTemplateColumn> iter = template.getColumns().iterator(); iter.hasNext();) {
						GridTemplateColumn element = iter.next();
						ruleDefinition = element.getRuleDefinition();
						if (ruleDefinition != null && !ruleDefinition.isEmpty()) {
							logger.debug("generateOptimized: generating rules for column " + element.getColumnNumber());
							String validationError = TemplateValidator.isValid(ruleDefinition, template, DomainManager.getInstance());
							if (validationError != null)
								reportError("Error validating rule for " + ruleDefinition + ": " + validationError);
							else {
								generate(timeSliceContainer, ruleDefinition, element.getColumnNumber(), template, ruleParamsList);
							}
						}
					}
				}
			}
			logger.info("<<< generateOptimized(template): " + template);
		}
		catch (RuleGenerationException ex) {
			reportError("Failed to generate rules for template: " + template.getAuditDescription() + ": " + ex.getMessage());
		}
		catch (Exception ex) {
			logger.error("Failed to generate rules for " + template, ex);
			reportError("Failed to generate rules for template: " + template.getAuditDescription() + ": " + ex.getMessage());
		}
	}

	public synchronized void generate(TimeSliceContainer timeSliceContainer, GridTemplate template, GuidelineReportFilter filter)
			throws RuleGenerationException {
		if (timeSliceContainer == null) throw new NullPointerException("timeSliceContainer cannot be null");
		if (timeSliceContainer.size() == 0) throw new IllegalArgumentException("timeSliceContainer cannot be empty");
		logger.debug(">>> generate(template): " + template + "," + filter);

		int templateID = template.getID();
		this.currTemplate = template;

		logger.debug("generate: consolidating grids of status " + getInitStatus() + ", templateID = " + templateID);
		try {
			List<ProductGrid> prodGridList = GridManager.getInstance().getAllGridsForTemplate(templateID);

			// sanity check
			if (prodGridList != null) {
				logger.debug("generate: grid data list size = " + prodGridList.size());
				List<GuidelineGenerateParams> ruleParamsList = buildProductGenParms(getInitStatus(), template, prodGridList, filter);
				logger.debug("generate: ruleParamsList size = " + ruleParamsList.size());
				if (!ruleParamsList.isEmpty()) {
					RuleDefinition ruleDefinition = template.getRuleDefinition();

					if (ruleDefinition != null && !ruleDefinition.isEmpty()) {
						logger.debug("RuleGenerator.generate: handling template deploy rule...");
						String validationError = TemplateValidator.isValid(ruleDefinition, template, DomainManager.getInstance());
						if (validationError != null) {
							reportError("Error validating rule for " + ruleDefinition + ": " + validationError);
						}
						else {
							String msg = TemplateValidator.checkForIncompleteElements(ruleDefinition, template, -1);
							if (msg != null) {
								logger.warn(msg);
							}
							generate(timeSliceContainer, template.getRuleDefinition(), -1, template, ruleParamsList);
						}
					}

					logger.debug("RuleGenerator.generate: handling column deploy rules, if any...");
					for (Iterator<GridTemplateColumn> iter = template.getColumns().iterator(); iter.hasNext();) {
						GridTemplateColumn element = iter.next();
						ruleDefinition = element.getRuleDefinition();
						if (ruleDefinition != null && !ruleDefinition.isEmpty()) {
							logger.debug("RuleGenerator.generate: generating rules for column " + element.getColumnNumber());
							String validationError = TemplateValidator.isValid(ruleDefinition, template, DomainManager.getInstance());
							if (validationError != null)
								reportError("Error validating rule for " + ruleDefinition + ": " + validationError);
							else {
								generate(timeSliceContainer, ruleDefinition, element.getColumnNumber(), template, ruleParamsList);
							}
						}
					}
				}
			}
			logger.info("<<< generate(template): " + template);
		}
		catch (RuleGenerationException ex) {
			reportError("Failed to generate rules for template: " + template.getAuditDescription() + ": " + ex.getMessage());
		}
		catch (Exception ex) {
			logger.error("Failed to generate rules for " + template, ex);
			reportError("Failed to generate rules for template: " + template.getAuditDescription() + ": " + ex.getMessage());
		}
	}

	private void generate(TimeSliceContainer timeSliceContainer, RuleDefinition ruleDefinition, int columnNumber, GridTemplate template,
			List<GuidelineGenerateParams> ruleParamList) throws RuleGenerationException {
		logger.debug(">>> generate2(ruleDefinition,ruleParamList): " + ruleDefinition + "," + ruleParamList.size() + " params");

		GuidelineRule guidelineRule = null;
		try {
			guidelineRule = new GuidelineRuleFactory().createGuidelineRule(template, ruleDefinition);
		}
		catch (RuleGenerationException ex) {
			reportError(ex.getErrorMessage());
			return;
		}

		logger.debug("... generate2: guidelineRule = " + guidelineRule);

		// Write rules for each generation parameters
		for (Iterator<GuidelineGenerateParams> iterator = ruleParamList.iterator(); iterator.hasNext();) {
			GuidelineGenerateParams ruleGenerateParams = iterator.next();
			ruleGenerateParams.setColumNumber(columnNumber);
			if (columnNumber < 0 || RuleGeneratorHelper.isNotEmpty(ruleGenerateParams.getColumnValue(columnNumber))) {
				ruleGenerateParams.setDescription(aeMapper.generateRuleDescription(ruleGenerateParams)
						+ (ruleGenerateParams.getTemplate() == null ? "" : " for template " + ruleGenerateParams.getTemplate().getID()));
				logger.debug("... generate2: generating rules for " + ruleGenerateParams);
				try {
					generate(timeSliceContainer, guidelineRule, ruleGenerateParams);
				}
				catch (RuleGenerationException ex) {
					reportError("Failed to generate rules for " + ruleGenerateParams + ": " + ex.getErrorMessage());
				}
			}
			else {
				logger.info("not generated for empty column: " + ruleGenerateParams);
			}
		}
		logger.debug("<<< generate2(ruleDefinition,ruleParamList): " + guidelineRule);
	}

	private void generate(TimeSliceContainer timeSliceContainer, GuidelineRule guidelineRule, GuidelineGenerateParams ruleParam)
			throws RuleGenerationException {
		logger.debug(">>> generate2(guidelineRule,ruleParam): " + guidelineRule + "," + ruleParam);
		this.currGrid = ruleParam;
		this.currRow = ruleParam.getRowNum();
		this.currColumn = ruleParam.getColumnNum();

		logger.debug("   generate2(ruleDefinition,ruleParam): currParam  = " + currGrid);
		logger.debug("   generate2(ruleDefinition,ruleParam): currRow    = " + currRow);
		logger.debug("   generate2(ruleDefinition,ruleParam): currColumn = " + currColumn);

		// reset invariants for a single rule
		resetInvariantsForEachRule();

		try {
			int count = writeRulesOptimized(timeSliceContainer, guidelineRule, ruleParam); //writeRules(timeSliceContainer, guidelineRule, ruleParam);

			if (count > 0) incrementRuleCount(count);
		}
		catch (RuleGenerationException ex) {
			reportError("Error generating rule for " + guidelineRule + ": " + ex.getMessage());
			logger.error("write(AeRule,AbstractGenerateParms): Rule generation error", ex);
		}
		catch (Exception ex) {
			reportError("Failed to generate rule: " + ex.getMessage() + " (" + ex.getClass().getName() + ")");
			logger.error("write(AeRule,AbstractGenerateParms): Exception", ex);
		}

		logger.debug("<<< generate2(ruleDefinition,ruleParam): " + guidelineRule);
	}

	private void resetInvariantsForEachRule() {
		this.aeMapper.reInitRuleVariables();
		postProcessItemHelper.clear();
		lhsHelper.clear();
		rhsHelper.clear();
	}

	void writeLineagePatternIfMatch(String className, TemplateUsageType usageType) {
		LineagePatternConfigSet lineagePatternSet = ConfigurationManager.getInstance().getRuleGenerationConfiguration(usageType).getLineagePatternConfigSet();
		if (lineagePatternSet.size() == 0) return;

		String[] lineagePatternPrefix = lineagePatternSet.getPrefix();
		for (int i = 0; i < lineagePatternPrefix.length; i++) {
			if (className.toUpperCase().startsWith(lineagePatternPrefix[i])) {
				RuleGenerationConfiguration.LineagePatternConfig[] configs = lineagePatternSet.getLineagePatternConfigs(lineagePatternPrefix[i]);
				for (int j = 0; j < configs.length; j++) {
					print(configs[j].getText());
					nextLine();
				}
			}
		}
	}

	private int writeRulesOptimized(TimeSliceContainer timeSliceContainer, GuidelineRule guidelineRule, GuidelineGenerateParams ruleParams)
			throws RuleGenerationException {
		int count = 0;
		List<TimeSlice> timeSliceList = timeSliceContainer.getApplicableTimeSlices(ruleParams.getSunrise(), ruleParams.getSunset());
		TimeSlice[] timeSlices = timeSliceList.toArray(new TimeSlice[0]);
		if (ruleParams.hasEntitySpecificMessage()) {
			GenericEntityType type = GenericEntityType.forID(ConfigurationManager.getInstance().getEntityConfiguration().getEntityTypeForMessageContext().getTypeID());
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

	private void writeSingleRuleForMessageContextEntityID(TimeSlice[] timeSlices, GuidelineRule guidelineRule,
			GuidelineGenerateParams ruleParams, GenericEntityType typeForMessageContext, int messageContextEntityID)
			throws RuleGenerationException {

		assert (timeSlices.length > 0);
		if (logger.isDebugEnabled()) {
			logger.debug(">>> writeSingleRule: params=" + ruleParams + ",msgEntityID=" + messageContextEntityID);
		}
		resetTab();
		// Generate unique rule name for each A*E rule
		ruleParams.setName(aeMapper.generateRuleName(ruleParams));

		if (logger.isDebugEnabled()) {
			logger.debug("    writeSingleRule: writing LHS of " + ruleParams.getName());
		}
		// begin rule
		openParan();
		print("define-rule ");
		print(ruleParams.getName());
		print(" ");
		quote();
		print(ruleParams.getDescription());
		quote();

		// generate the (declare (ruleset ...)) pattern. This pattern is generated by default,
		// unless explicitly turned off in the PE config file.
		RuleGenerationConfiguration.PatternConfig rulesetConfig = ConfigurationManager.getInstance().getRuleGenerationConfiguration(
				ruleParams.getUsage()).getRulesetPatternConfig();
		if (rulesetConfig == null || rulesetConfig.isPatternOn()) {
			nextLineIndent();
			openParan();
			print("declare ");
			openParan();
			print("ruleset ");
			print(AeMapper.getRuleset(ruleParams));
			closeParan();
			closeParan();
		}
		nextLine();
		nextLineIndent();

		// write LHS patterns
		lhsHelper.generateLHS(guidelineRule, ruleParams, timeSlices, typeForMessageContext, messageContextEntityID);

		// process for dynamic strings on RHS, if any
		postProcessItemHelper.processForAttributeAndClassReferencesInMessages(ruleParams, messageContextEntityID);
		postProcessItemHelper.processForDynamicStringInRHS(guidelineRule.getRHSFunctionCall(), ruleParams);
		postProcessItemHelper.processForDynamicStringInMessage(ruleParams, messageContextEntityID);
		postProcessItemHelper.processForAttributeAndClassReferencesInRHS(guidelineRule.getRHSFunctionCall(), ruleParams);

		postProcessItemHelper.writePatterns(guidelineRule, ruleParams.getUsage(), false);

		nextLineOutdent();
		print("=>");
		nextLineIndent();

		if (logger.isDebugEnabled()) logger.debug("... writeSingleRule: writing RHS...");

		// write RHS function call
		rhsHelper.generateRHS(guidelineRule, ruleParams, messageContextEntityID);

		if (logger.isDebugEnabled()) logger.debug("... writeSingleRule: finishing rule file for pe-action");

		outdent();
		outdent();
		closeParan();
		nextLine(2);

		super.writeAll();

		if (logger.isDebugEnabled()) logger.debug("<<< writeSingleRule");
	}

}