package com.mindbox.pe.server.deploy;

import java.util.concurrent.CountDownLatch;

import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.server.generator.OutputController;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGenerator;

class GenerateRuleWork extends AbstractDeployWork {

	private final RuleGenerator ruleGenerator;

	GenerateRuleWork(final int percentageAllocation, final CountDownLatch countDownLatch, final OutputController outputController, final GuidelineReportFilter filter,
			final GenerateStats generateStats, final Integer guidelineMaxThreads, final boolean mergeFilesPerUsageType) throws RuleGenerationException {
		super(percentageAllocation, "Rule generation", countDownLatch, outputController, filter, generateStats);
		this.ruleGenerator = new RuleGenerator(generateStats, outputController, guidelineMaxThreads, mergeFilesPerUsageType);
	}

	@Override
	void performWork() throws Exception {
		logger.info("Generator.run: deploying guidelines...");
		ruleGenerator.generate(percentageAllocation, filter);
	}
}
