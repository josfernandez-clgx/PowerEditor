package com.mindbox.pe.server.deploy;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.server.generator.CBRGenerator;
import com.mindbox.pe.server.generator.OutputController;
import com.mindbox.pe.server.generator.RuleGenerationException;

class GenerateCbrWork extends AbstractDeployWork {

	private final CBRGenerator cbrGenerator;

	GenerateCbrWork(CountDownLatch countDownLatch, OutputController outputController, GuidelineReportFilter filter,
			AtomicInteger uncaughtErrorCount) throws RuleGenerationException {
		super("Deploy CBR data", countDownLatch, outputController, filter, uncaughtErrorCount);
		this.cbrGenerator = CBRGenerator.getInstance();
	}

	@Override
	void performWork() throws Exception {
		logger.info("Generator.run: deploy cbr data...");
		cbrGenerator.generateCBRData();
		cbrGenerator.writeAll();
	}

	GenerateStats getStats() {
		return cbrGenerator.getStats();
	}
}
