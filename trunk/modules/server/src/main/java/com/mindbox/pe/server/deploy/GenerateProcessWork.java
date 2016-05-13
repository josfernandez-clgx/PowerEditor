package com.mindbox.pe.server.deploy;

import java.util.concurrent.CountDownLatch;

import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.server.generator.OutputController;
import com.mindbox.pe.server.generator.ProcessGenerator;
import com.mindbox.pe.server.generator.RuleGenerationException;

class GenerateProcessWork extends AbstractDeployWork {

	private final ProcessGenerator processGenerator;

	GenerateProcessWork(final int percentageAllocation, final CountDownLatch countDownLatch, final OutputController outputController, final GuidelineReportFilter filter,
			final GenerateStats generateStats) throws RuleGenerationException {
		super(percentageAllocation, "Deploy process data", countDownLatch, outputController, filter, generateStats);
		this.processGenerator = new ProcessGenerator(generateStats, outputController);
	}

	@Override
	void performWork() throws Exception {
		logger.info("Generator.run: deploying process data...");
		processGenerator.generateProcessData(percentageAllocation);
	}
}
