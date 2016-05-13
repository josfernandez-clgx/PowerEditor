package com.mindbox.pe.server.db;

import static com.mindbox.pe.common.LogUtil.logDebug;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.PasswordOneWayHashUtil;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletActionException;
import com.mindbox.pe.server.spi.UserAuthenticationProviderPlugin;

public class PeDbUserAuthenticationProvider implements UserAuthenticationProviderPlugin {

	private Logger logger = Logger.getLogger(getClass());

	@Override
	public boolean authenticate(String username, String pwd) throws ServletActionException {
		String hashedPassword = PasswordOneWayHashUtil.convertToOneWayHash(pwd, PasswordOneWayHashUtil.HASH_ALGORITHM_MD5);
		return isValidPassword(username, hashedPassword);
	}

	@Override
	public boolean arePasswordsStoredExternally() {
		return false;
	}

	private boolean isValidPassword(String userId, String password) throws ServletActionException {
		logDebug(logger, "isValidPassword: %s", userId);
		if (userId == null || password == null) {
			return false;
		}
		User user = SecurityCacheManager.getInstance().getUser(userId);
		if (user == null) {
			logDebug(logger, "user %s not found", userId);
			return false;
		}
		else if (user.getStatus().equalsIgnoreCase(Constants.ACTIVE_STATUS)) {
			if (!password.equals(user.getCurrentPassword())) {
				logDebug(logger, "... isValidPassword: password check failed");
				BizActionCoordinator.getInstance().updateFailedLoginCounter(userId, user.getFailedLoginCounter() + 1);
				return false;
			}
			else {
				BizActionCoordinator.getInstance().updateFailedLoginCounter(userId, 0);
				return true;
			}
		}
		else {
			logDebug(logger, "... isValidPassword: status check failed; actual = %s", user.getStatus());
			return false;
		}
	}

	/**
	 * This implementation does nothing.
	 */
	@Override
	public void notifySsoAuthentication(String userId) throws Exception {
		// no-op
	}

}
