package com.mindbox.pe.server.bizlogic;

import static com.mindbox.pe.common.LogUtil.logDebug;
import static com.mindbox.pe.common.LogUtil.logError;
import static com.mindbox.pe.common.LogUtil.logInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.mindbox.pe.server.config.ConfigurationManager;

class ClearFailedLoginCounterWorkManager {

	private class ResetFailedCounterWork implements Callable<Boolean> {
		private final String userID;

		private ResetFailedCounterWork(String userID) {
			super();
			this.userID = userID;
		}

		@Override
		public Boolean call() throws Exception {
			logDebug(LOG, "running for %s...", userID);
			try {
				BizActionCoordinator.getInstance().updateFailedLoginCounter(userID, 0);
				logInfo(LOG, "Cleared failed login counter for %s", userID);
				return true;
			}
			catch (Exception e) {
				logError(LOG, e, "Failed to clear failed login counter for %s", userID);
				return false;
			}
			finally {
				synchronized (map) {
					map.remove(userID);
				}
			}
		}
	}

	private static final Logger LOG = Logger.getLogger(ClearFailedLoginCounterWorkManager.class);

	private final Map<String, ScheduledFuture<Boolean>> map = new HashMap<String, ScheduledFuture<Boolean>>();
	private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(12);

	public void cancelResetFailedCounterWork(final String userID) {
		if (userID == null) {
			throw new IllegalArgumentException("userID cannot be null");
		}
		synchronized (map) {
			cancelWork(userID);
		}
	}

	private void cancelWork(final String userID) {
		if (map.containsKey(userID)) {
			logDebug(LOG, "canceling work for %s...", userID);
			map.get(userID).cancel(true);
			logDebug(LOG, "previous work for %s canceled.", userID);
		}
	}

	public void restartResetFailedCounterWork(final String userID) {
		if (userID == null) {
			throw new IllegalArgumentException("userID cannot be null");
		}
		synchronized (map) {
			cancelWork(userID);

			map.put(userID, scheduleWork(userID));
			logInfo(LOG, "work restarted for %s", userID);
		}
	}

	private ScheduledFuture<Boolean> scheduleWork(final String userID) {
		final long attemptsTimeToLiveMins = ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper().getLockoutCounterResetIntervalMins();
		return scheduledExecutorService.schedule(new ResetFailedCounterWork(userID), attemptsTimeToLiveMins, TimeUnit.MINUTES);
	}
}
