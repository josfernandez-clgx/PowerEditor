package com.mindbox.pe.server.deploy;

import java.util.concurrent.CountDownLatch;

import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.server.generator.CBRGenerator;
import com.mindbox.pe.server.generator.OutputController;
import com.mindbox.pe.server.generator.RuleGenerationException;

class GenerateCbrWork extends AbstractDeployWork {

	private final CBRGenerator cbrGenerator;

	GenerateCbrWork(final int percentageAllocation, final CountDownLatch countDownLatch, final OutputController outputController, final GuidelineReportFilter filter, final GenerateStats generateStats)
			throws RuleGenerationException {
		super(percentageAllocation, "Deploy CBR data", countDownLatch, outputController, filter, generateStats);
		this.cbrGenerator = new CBRGenerator(generateStats, outputController);
	}

	@Override
	void performWork() throws Exception {
		logger.info("Generator.run: deploy cbr data...");
		cbrGenerator.generateCBRData(percentageAllocation);
	}
}
