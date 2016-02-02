package com.mindbox.pe.server.deploy;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.server.generator.OutputController;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGenerator;

class GenerateRuleWork extends AbstractDeployWork {

	private final RuleGenerator ruleGenerator;

	GenerateRuleWork(CountDownLatch countDownLatch, OutputController outputController, GuidelineReportFilter filter,
			AtomicInteger uncaughtErrorCount) throws RuleGenerationException {
		super("Rule generation", countDownLatch, outputController, filter, uncaughtErrorCount);
		this.ruleGenerator = new RuleGenerator(outputController);
	}

	@Override
	void performWork() throws Exception {
		logger.info("Generator.run: deploying guidelines...");

		ruleGenerator.generate(filter);
	}

	List<GenerateStats> getStats() {
		return ruleGenerator.collectGenerateStats();
	}

}
