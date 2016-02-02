package com.mindbox.pe.server.deploy;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.server.generator.OutputController;
import com.mindbox.pe.server.generator.RuleGenerationException;

abstract class AbstractDeployWork implements Runnable {

	private final CountDownLatch countDownLatch;
	private final String workName;
	final GuidelineReportFilter filter;
	final OutputController outputController;
	final Logger logger = Logger.getLogger(getClass());
	private final AtomicInteger uncaughtErrorCount;

	AbstractDeployWork(final String workName, final CountDownLatch countDownLatch, final OutputController outputController,
			final GuidelineReportFilter filter, final AtomicInteger uncaughtErrorCount) {
		this.workName = workName;
		this.countDownLatch = countDownLatch;
		this.filter = filter;
		this.outputController = outputController;
		this.uncaughtErrorCount = uncaughtErrorCount;
	}

	abstract void performWork() throws Exception;

	@Override
	public void run() {
		final long startTime = System.currentTimeMillis();
		try {
			performWork();

			logger.info(String.format("%s completed: took %d (ms)", workName, (System.currentTimeMillis() - startTime)));
		}
		catch (Exception ex) {
			uncaughtErrorCount.incrementAndGet();
			logger.error(String.format("Error in %s", workName), ex);
			try {
				outputController.writeErrorMessage(workName, ex.getMessage());
			}
			catch (RuleGenerationException rge) {
				logger.warn(String.format("Failed to report error for %s: %s", workName, ex.getMessage()), rge);
			}
		}
		finally {
			countDownLatch.countDown();
		}
	}
}
