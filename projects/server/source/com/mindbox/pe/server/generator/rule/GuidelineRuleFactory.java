package com.mindbox.pe.server.generator.rule;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.server.generator.RuleGenerationException;

/**
 * Factory for {@link LHSPatternList}.
 * Usage:
 * <ol><li>Create a new instance; e.g., <code>new GuidelineRuleFactory(helper);</code></li>
 * <li>Call {@link #createGuidelineRule(RuleDefinition)}</li>
 * </ol>
 * This is not thread-safe. At most one invocation of {@link #createGuidelineRule(RuleDefinition)} on the same object is allowed. 
 *
 */
public final class GuidelineRuleFactory {

	private final PatternFactoryHelper helper;
	private final Logger logger;

	public GuidelineRuleFactory() {
		this.helper = new DefaultPatternFactoryHelper();
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
