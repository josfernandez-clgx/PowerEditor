package com.mindbox.pe.server.sysext.usertracking;

import java.util.Date;

import org.apache.log4j.Logger;

import com.mindbox.pe.server.db.PeDbUserAuthenticationProvider;
import com.mindbox.pe.server.servlet.ServletActionException;
import com.mindbox.pe.server.servlet.UserDisabledException;

public class UserTrackingDbUserAuthenticationProvider extends PeDbUserAuthenticationProvider {

	private final UserTrackingDao userTrackingDao = new DefaultUserTrackingDao();

	@Override
	public boolean authenticate(String username, String pwd) throws ServletActionException {
		boolean result = super.authenticate(username, pwd);
		if (result) {
			try {
				// Fail authentication if user tracking record doesn't exist
				if (!userTrackingDao.hasUserTrackingRecord(username)) {
					result = false;
				}
				else {
					if (userTrackingDao.isUserDisabled(username)) {
						throw new UserDisabledException("DisabledUserMsg", username);
					}
				}
			}
			catch (ServletActionException e) {
				throw e;
			}
			catch (Exception e) {
				Logger.getLogger(getClass()).warn(
						"Couldn't authenticated: failed to check user tracking disabled status for user " + username,
						e);
				result = false;
			}
		}

		// Update last login date
		if (result) {
			try {
				userTrackingDao.updateLastLoginDate(username, new Date());
			}
			catch (Exception e) {
				Logger.getLogger(getClass()).warn("Failed to update last login date for user " + username, e);
			}
		}

		return result;
	}
	
	@Override
	public void notifySsoAuthentication(String userId) throws Exception {
		Logger.getLogger(getClass()).info("SSO auth notification: " + userId);
		userTrackingDao.updateLastLoginDate(userId, new Date());
	}
}
