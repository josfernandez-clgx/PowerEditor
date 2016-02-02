package com.mindbox.pe.server.sysext.usertracking;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.admin.UserPassword;
import com.mindbox.pe.server.db.DefaultUserManagementProvider;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.spi.UserManagementProvider;
import com.mindbox.pe.server.spi.db.UserSecurityDataHolder;

public class UserTrackingUserManagementProvider extends DefaultUserManagementProvider implements UserManagementProvider {

	private static final Logger LOG = Logger.getLogger(UserTrackingUserManagementProvider.class);

	private final UserTrackingDao userTrackingDao = new DefaultUserTrackingDao();

	@Override
	public void insertUser(String userID, String name, String status, boolean passwordChangeRequired, int failedLoginCounter,
			int[] roleIDs, List<UserPassword> passwordHistory, String actingUserID) throws SQLException {
		super.insertUser(userID, name, status, passwordChangeRequired, failedLoginCounter, roleIDs, passwordHistory, actingUserID);

		userTrackingDao.insertUserTrackingData(userID, actingUserID);
	}

	@Override
	public void updateUser(String userID, String name, String status, boolean passwordChangeRequired, int failedLoginCounter,
			int[] roleIDs, List<UserPassword> passwordHistory, String actingUserID) throws SQLException {
		super.updateUser(userID, name, status, passwordChangeRequired, failedLoginCounter, roleIDs, passwordHistory, actingUserID);

		// update last modified details if someone else modified the user
		if (!userID.equalsIgnoreCase(actingUserID)) {
			userTrackingDao.updateLastModifiedDetails(userID, new Date(), actingUserID);
		}
	}

	@Override
	public void enableUser(String userID, String actingUserID) throws SQLException {
		userTrackingDao.enableUserRecord(userID, actingUserID);
	}

	@Override
	public void loadAllUsers(UserSecurityDataHolder dataHolder) throws SQLException, ParseException {
		super.loadAllUsers(dataHolder);

		Iterator<User> iter = dataHolder.getUsers();
		while (iter.hasNext()) {
			User user = iter.next();
			if (LOG.isDebugEnabled()) {
				LOG.debug("Checking if user is disabled for " + user.getUserID());
			}
			try {
				if (userTrackingDao.isUserDisabled(user.getUserID())) {
					user.setDisabled(true);
					if (LOG.isDebugEnabled()) {
						LOG.debug("User is disabled: " + user.getUserID());
					}
				}
			}
			catch (Exception e) {
				LOG.warn("Unable to determine if user is disabled for " + user.getUserID(), e);
			}
		}
	}
}
