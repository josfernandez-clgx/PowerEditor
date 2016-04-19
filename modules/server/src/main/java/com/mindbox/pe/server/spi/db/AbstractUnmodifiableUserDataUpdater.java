package com.mindbox.pe.server.spi.db;

import java.sql.SQLException;
import java.util.List;

import com.mindbox.pe.model.admin.UserPassword;

public abstract class AbstractUnmodifiableUserDataUpdater implements UserDataUpdater {

	public final void deleteUser(String userID, String actingUserID) throws SQLException {
		throw new UnsupportedOperationException("User data cannot be modified");
	}

	public final void insertUser(String userID, String name, String status, boolean passwordChangeRequired, int failedLoginCounter,
			int[] roleIDs, List<UserPassword> passwordHistory, String actingUserID) throws SQLException {
		throw new UnsupportedOperationException("User data cannot be modified");
	}

	public final void updateUser(String userID, String name, String status, boolean passwordChangeRequired, int failedLoginCounter,
			int[] roleIDs, List<UserPassword> passwordHistory, String actingUserID) throws SQLException {
		throw new UnsupportedOperationException("User data cannot be modified");
	}

	public final void updateFailedLoginCounter(String userID, int newValue) throws SQLException {
		throw new UnsupportedOperationException("User data cannot be modified");
	}
}
