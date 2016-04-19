package com.mindbox.pe.server.deploy;

import static com.mindbox.pe.common.LogUtil.logInfo;
import static com.mindbox.pe.common.LogUtil.logWarn;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.server.generator.OutputController;
import com.mindbox.pe.server.generator.RuleGenerationException;

abstract class AbstractDeployWork implements Runnable {

	private final CountDownLatch countDownLatch;
	private final String workName;
	final int percentageAllocation;
	final GuidelineReportFilter filter;
	final OutputController outputController;
	final Logger logger = Logger.getLogger(getClass());
	final GenerateStats generateStats;

	AbstractDeployWork(final int percentageAllocation, final String workName, final CountDownLatch countDownLatch, final OutputController outputController, final GuidelineReportFilter filter,
			final GenerateStats generateStats) {
		this.percentageAllocation = percentageAllocation;
		this.workName = workName;
		this.countDownLatch = countDownLatch;
		this.filter = filter;
		this.outputController = outputController;
		this.generateStats = generateStats;
	}

	abstract void performWork() throws Exception;

	@Override
	public void run() {
		logInfo(logger, "---> run: %d", percentageAllocation);
		final long startTime = System.currentTimeMillis();
		try {
			performWork();

			logInfo(logger, "%s completed: took %d (ms)", workName, (System.currentTimeMillis() - startTime));
		}
		catch (Exception ex) {
			generateStats.incrementNumErrors();
			logger.error(String.format("Error in %s", workName), ex);
			try {
				outputController.writeErrorMessage(workName, ex.getMessage());
			}
			catch (RuleGenerationException rge) {
				logWarn(logger, rge, "Failed to report error for %s: %s", workName, ex.getMessage());
			}
		}
		finally {
			countDownLatch.countDown();
			logInfo(logger, "<--- run (latch.size=%d)", countDownLatch.getCount());
		}
	}
}
