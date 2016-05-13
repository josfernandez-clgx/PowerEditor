package com.mindbox.pe.server.deploy;

import java.util.concurrent.CountDownLatch;

import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.server.generator.OutputController;
import com.mindbox.pe.server.generator.ParameterGenerator;
import com.mindbox.pe.server.generator.RuleGenerationException;

class GenerateParameterWork extends AbstractDeployWork {

	private final ParameterGenerator parameterGenerator;

	GenerateParameterWork(final int percentageAllocation, final CountDownLatch countDownLatch, final OutputController outputController, final GuidelineReportFilter filter,
			final GenerateStats generateStats) throws RuleGenerationException {
		super(percentageAllocation, "Parameter generation", countDownLatch, outputController, filter, generateStats);
		this.parameterGenerator = new ParameterGenerator(generateStats, outputController);
	}

	@Override
	void performWork() throws Exception {
		logger.info("Generator.run: deploying parameter objects...");
		parameterGenerator.generate(percentageAllocation, filter);
	}
}
