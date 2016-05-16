package com.mindbox.pe.server.generator.rule;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.server.generator.GuidelineRuleGenerator;
import com.mindbox.pe.server.generator.RuleGenerationException;

/**
 * Factory for {@link LHSPatternList}.
 * Usage:
 * <ol><li>Create a new instance; e.g., <code>new GuidelineRuleFactory(helper);</code></li>
 * <li>Call {@link #createGuidelineRule(GridTemplate, RuleDefinition)}</li>
 * </ol>
 * This is not thread-safe. At most one invocation of {@link #createGuidelineRule(GridTemplate, RuleDefinition)} on the same object is allowed. 
 *
 */
public final class GuidelineRuleFactory {

	private final PatternFactoryHelper helper;
	private final Logger logger;

	public GuidelineRuleFactory(final GuidelineRuleGenerator guidelineRuleGenerator) {
		this.helper = new DefaultPatternFactoryHelper(guidelineRuleGenerator);
		this.logger = Logger.getLogger(getClass());
	}

	public GuidelineRule createGuidelineRule(GridTemplate template, RuleDefinition ruleDefinition) throws RuleGenerationException {
		logger.debug(">>> createGuidelineRule: " + ruleDefinition);
		try {
			GuidelineRule rule = new GuidelineRule();
			LHSPatternList patternList = new LHSPatternListFactory(helper).produce(ruleDefinition, template.getUsageType());
			rule.setLHSPatternList(patternList);

			logger.debug(">>> createGuidelineRule: LHS set; getting RHS...");

			FunctionCallPattern rhsPattern = new FunctionCallPatternFactory(helper, false).createFunctionCallPattern(template, ruleDefinition, patternList);
			rule.setRHSFunctionCall(rhsPattern);

			logger.debug("<<< createGuidelineRule: " + rule);
			return rule;
		}
		catch (RuleGenerationException ex) {
			throw ex;
		}
		catch (Exception ex) {
			logger.error("Failed to generate guideline rule from " + ruleDefinition, ex);
			throw new RuleGenerationException("Failed to generate guideline rule for " + template + ": " + ex.getMessage());
		}
	}
}
