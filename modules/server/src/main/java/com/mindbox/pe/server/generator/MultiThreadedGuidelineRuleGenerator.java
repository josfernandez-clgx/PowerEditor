package com.mindbox.pe.server.generator;

import java.util.List;

import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.server.model.TimeSliceContainer;

/**
 * Guideline rule generator that generates all rules in multiple threads.

 * @author Geneho Kim
 * @since PowerEditor 5.9.0
 * @see com.mindbox.pe.server.generator.RuleGenerator
 */
public class MultiThreadedGuidelineRuleGenerator extends DefaultGuidelineRuleGenerator implements GuidelineRuleGenerator, ErrorContextProvider {

	public MultiThreadedGuidelineRuleGenerator(GridTemplate template, GenerateStats generateStats, OutputController outputController) throws RuleGenerationException {
		super(template, generateStats, outputController);
	}

	@Override
	void generateRules(TimeSliceContainer timeSliceContainer, List<GuidelineGenerateParams> ruleParamsList) throws RuleGenerationException {
		// TODO Auto-generated method stub
		super.generateRules(timeSliceContainer, ruleParamsList);
	}
}