package com.mindbox.pe.server.cache;

import static com.mindbox.pe.common.LogUtil.logError;
import static com.mindbox.pe.common.LogUtil.logInfo;
import static com.mindbox.pe.common.LogUtil.logWarn;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.server.audit.AuditLogger;

public class DeployProcessChecker extends Thread {

	private static final Logger LOG = Logger.getLogger(DeployProcessChecker.class);
	private static final int TIMEOUT_HOURS = 14;

	private final Date startDate;
	private final String userID;
	private final Future<GenerateStats> future;

	public DeployProcessChecker(Date startDate, String userID, Future<GenerateStats> future) {
		super();
		setDaemon(true);
		this.startDate = startDate;
		this.userID = userID;
		this.future = future;
	}

	@Override
	public void run() {
		logInfo(LOG, "Running...");

		try {
			final GenerateStats generateStats = future.get(TIMEOUT_HOURS, TimeUnit.HOURS);

			logInfo(LOG, "Deployment process completed: elapsed time = %d (ms)", (System.currentTimeMillis() - startDate.getTime()));

			AuditLogger.getInstance().logDeployCompleted(
					String.format(
							"Deployment process completed with %d errors, %d rules and %d objects",
							generateStats.getNumErrorsGenerated(),
							generateStats.getNumRulesGenerated(),
							generateStats.getNumObjectsGenerated()),
					userID);
		}
		catch (ExecutionException e) {
			logError(LOG, e.getCause(), "Failed to complete deployment process");
			AuditLogger.getInstance().logDeployCompleted(String.format("Failed to complete deployment process started by %s: %s", userID, e.getCause().getMessage()), userID);
		}
		catch (TimeoutException e) {
			logWarn(LOG, "Deployment process timeed out!");
			AuditLogger.getInstance().logDeployCompleted(String.format("Deployment started by %s timed out!", userID), userID);
		}
		catch (InterruptedException e) {
			logWarn(LOG, e, "Interrupted while waiting deployment process to complete");
		}

		logInfo(LOG, "Done!");
		DeploymentManager.getInstance().resetCurrentDeployId();
	}
}
