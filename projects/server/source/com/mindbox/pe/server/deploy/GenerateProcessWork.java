package com.mindbox.pe.server.deploy;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.server.generator.OutputController;
import com.mindbox.pe.server.generator.ProcessGenerator;
import com.mindbox.pe.server.generator.RuleGenerationException;

class GenerateProcessWork extends AbstractDeployWork {

	private final ProcessGenerator processGenerator;

	GenerateProcessWork(CountDownLatch countDownLatch, OutputController outputController, GuidelineReportFilter filter,
			AtomicInteger uncaughtErrorCount) throws RuleGenerationException {
		super("Deploy process data", countDownLatch, outputController, filter, uncaughtErrorCount);
		this.processGenerator = ProcessGenerator.getInstance();
	}

	@Override
	void performWork() throws Exception {
		logger.info("Generator.run: deploying process data...");
		processGenerator.generateProcessData();
		processGenerator.writeAll();
	}

	GenerateStats getStats() {
		return processGenerator.getStats();
	}
}
