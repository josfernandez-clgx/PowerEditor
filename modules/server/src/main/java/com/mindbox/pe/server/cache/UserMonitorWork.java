package com.mindbox.pe.server.cache;

import static com.mindbox.pe.common.LogUtil.logDebug;
import static com.mindbox.pe.common.LogUtil.logInfo;
import static com.mindbox.pe.common.LogUtil.logWarn;

import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.Constants;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.model.User;

/**
 * This monitors user records for possible lock outs and reset failed logi counters.
 * This is primarily used for cases where user records are updated by an external process.
 *
 */
class UserMonitorWork implements Runnable {

	private static final Logger LOG = Logger.getLogger(UserMonitorWork.class);

	private final SecurityCacheManager securityCacheManager;

	UserMonitorWork(SecurityCacheManager securityCacheManager) {
		this.securityCacheManager = securityCacheManager;
	}

	@Override
	public void run() {
		logDebug(LOG, "---> run");

		final int maxAttempts = ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper().getMaxAttempts();

		final List<User> users = securityCacheManager.getAllUsers();
		for (User user : users) {
			logDebug(LOG, "checking %s (id=%d)...", user.getName(), user.getID());
			try {
				if (maxAttempts > 0) {
					if (maxAttempts <= user.getFailedLoginCounter() && !user.getStatus().equals(Constants.LOCKOUT_STATUS)) {
						logInfo(LOG, "mark user %s as locked out (exceeded lockout counter)", user.getUserID());
						user.setStatus(Constants.LOCKOUT_STATUS);
						BizActionCoordinator.getInstance().save(user, user);
					}
				}
			}
			catch (Exception e) {
				logWarn(LOG, e, "Failed to update user %s", user.getUserID());
			}
		}

		logDebug(LOG, "<--- run");
	}
}
