package com.mindbox.pe.server.deploy;

import static com.mindbox.pe.common.LogUtil.logDebug;
import static com.mindbox.pe.common.LogUtil.logError;
import static com.mindbox.pe.common.LogUtil.logInfo;
import static com.mindbox.pe.common.LogUtil.logWarn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.server.ExternalProcessUtil;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.generator.OutputController;
import com.mindbox.pe.server.generator.RuleGenerationException;

public class OverallDeployWork implements Callable<GenerateStats> {

	private static final ExecutorService DEPLOY_EXECUTOR = Executors.newFixedThreadPool(6);

	private final Logger logger = Logger.getLogger(getClass());

	private final OutputController outputController;
	private final ExternalProcessUtil externalProcessUtil;
	private final int deployID;
	private final String userID;
	private final CountDownLatch countDownLatch;
	private final GenerateRuleWork generateRuleWork;
	private final GenerateParameterWork generateParameterWork;
	private final GenerateProcessWork generateProcessWork;
	private final GenerateCbrWork generateCbrWork;
	private final ExportWork exportWork;
	private final GenerateStats generateStats;

	public OverallDeployWork(GuidelineReportFilter filter, boolean exportPolicies, OutputController outputController, int deployID, String userID, final GenerateStats generateStats)
			throws RuleGenerationException {
		this.outputController = outputController;
		this.deployID = deployID;
		this.userID = userID;
		this.generateStats = generateStats;
		this.externalProcessUtil = new ExternalProcessUtil(logger);

		int countDownLatchSize = 0;
		final boolean generateCBR = filter.isIncludeCBR();
		if (generateCBR) {
			++countDownLatchSize;
		}
		final boolean generateRules = filter.isIncludeGuidelines();
		if (generateRules) {
			++countDownLatchSize;
		}
		final boolean generateParameters = filter.isIncludeParameters();
		if (generateParameters) {
			++countDownLatchSize;
		}
		final boolean generateProcessData = filter.isIncludeProcessData();
		if (generateProcessData) {
			++countDownLatchSize;
		}
		final boolean exportData = ((filter.isIncludeEntities() || exportPolicies) && deployID > 0 && outputController.getDeployDir() != null);
		if (exportData) {
			++countDownLatchSize;
		}

		final int percentageAllocation = (100 / (countDownLatchSize + (generateRules ? 3 : 0)));
		final int remainder = 100 - percentageAllocation * (countDownLatchSize + (generateRules ? 3 : 0));

		generateStats.addPercentComplete(remainder);

		logDebug(logger, "countDownLatch size=[%d], percentageAllocation=%d", countDownLatchSize, percentageAllocation);

		countDownLatch = new CountDownLatch(countDownLatchSize);

		if (generateRules) {
			final Integer guidelineMaxThreads = ConfigurationManager.getInstance().getPowerEditorConfiguration().getRuleGeneration().getGuidelineMaxThread();

			final boolean mergeFilesPerUsageType = ConfigurationManager.getInstance().getPowerEditorConfiguration().getRuleGeneration().isMergeRuleFilesByUsageType() == null
					? false
					: ConfigurationManager.getInstance().getPowerEditorConfiguration().getRuleGeneration().isMergeRuleFilesByUsageType().booleanValue();

			// rule generation takes considerably longer than other items; hence * 4
			generateRuleWork = new GenerateRuleWork(percentageAllocation * 4, countDownLatch, outputController, filter, generateStats, guidelineMaxThreads == null
					? null
					: guidelineMaxThreads.intValue(), mergeFilesPerUsageType);
		}
		else {
			generateRuleWork = null;
		}

		if (generateParameters) {
			generateParameterWork = new GenerateParameterWork(percentageAllocation, countDownLatch, outputController, filter, generateStats);
		}
		else {
			generateParameterWork = null;
		}

		if (generateProcessData) {
			generateProcessWork = new GenerateProcessWork(percentageAllocation, countDownLatch, outputController, filter, generateStats);
		}
		else {
			generateProcessWork = null;
		}

		if (generateCBR) {
			generateCbrWork = new GenerateCbrWork(percentageAllocation, countDownLatch, outputController, filter, generateStats);
		}
		else {
			generateCbrWork = null;
		}

		if (exportData) {
			exportWork = new ExportWork(percentageAllocation, countDownLatch, outputController, filter, generateStats, exportPolicies, userID);
		}
		else {
			exportWork = null;
		}
	}

	@Override
	public GenerateStats call() throws Exception {
		logger.debug(">>> Generator.run");

		final List<Future<?>> futures = new ArrayList<Future<?>>();
		try {
			// [1] Generate guideline rules
			if (generateRuleWork != null) {
				futures.add(DEPLOY_EXECUTOR.submit(generateRuleWork));
			}

			// [2] Generate parameters
			if (generateParameterWork != null) {
				futures.add(DEPLOY_EXECUTOR.submit(generateParameterWork));
			}

			// [3] Generate process/phase objects
			if (generateProcessWork != null) {
				futures.add(DEPLOY_EXECUTOR.submit(generateProcessWork));
			}

			// [4] Generate CBR
			if (generateCbrWork != null) {
				futures.add(DEPLOY_EXECUTOR.submit(generateCbrWork));
			}

			// [5] Export
			if (exportWork != null) {
				futures.add(DEPLOY_EXECUTOR.submit(exportWork));
			}

			// Run post-deploy script, if configured to do so
			try {
				logInfo(logger, "waiting for deploy threads to complete...");

				if (countDownLatch.await(12, TimeUnit.HOURS)) {
					logger.info("deploy threads completed");

					final String scriptFile = (ConfigurationManager.getInstance().getServerConfigHelper().getDeploymentConfig().getPostDeployScript() == null
							? null
							: ConfigurationManager.getInstance().getServerConfigHelper().getDeploymentConfig().getPostDeployScript().getFile());
					logInfo(logger, "Generator.run: post deploy script file = [%s]", scriptFile);

					if (!UtilBase.isEmpty(scriptFile)) {
						try {
							final Map<String, String> environmentVarMap = new HashMap<String, String>();
							environmentVarMap.put("KB_STATUS", outputController.getStatus());
							environmentVarMap.put("DEPLOY_DIR", outputController.getDeployDir());
							environmentVarMap.put("USER", userID);

							externalProcessUtil.executeProcess(environmentVarMap, scriptFile, outputController.getStatus(), outputController.getDeployDir());
							logInfo(logger, "Generator.run: started post deploy script: [%s]", scriptFile);
						}
						catch (Exception ex) {
							logError(logger, ex, "Error in execution of post deploy script");
							generateStats.incrementNumErrors();

							try {
								outputController.writeErrorMessage("Post Deployment Script", ex.getMessage());
							}
							catch (RuleGenerationException rge) {
								logWarn(logger, rge, "Failed to report error: %s", ex.getMessage());
							}
						}
					}
				}
				else {
					logWarn(logger, "Deployment process for [%d] timed out! Canceling deployment process...", deployID);
					throw new RuntimeException("Deployment process timed out. See server log for more details.");
				}
			}
			catch (Exception ex) {
				logError(logger, ex, "Error in deployment for [%d]", deployID);
				generateStats.incrementNumErrors();

				try {
					outputController.writeErrorMessage("", ex.getMessage());
				}
				catch (RuleGenerationException rge) {
					logWarn(logger, rge, "Failed to report error: %s", ex.getMessage());
				}
			}

			return generateStats;
		}
		finally {
			logger.debug("Generator.run: end of run");

			generateStats.markAsNotRunning();

			for (final Future<?> future : futures) {
				if (!future.isDone()) {
					future.cancel(true);
					logInfo(logger, "Cancelled running future: %s", future);
				}
			}
		}
	}
}
