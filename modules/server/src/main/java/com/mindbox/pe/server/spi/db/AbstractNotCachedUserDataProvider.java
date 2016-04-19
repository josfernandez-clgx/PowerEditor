package com.mindbox.pe.server.spi.db;

import java.sql.SQLException;

public abstract class AbstractNotCachedUserDataProvider implements UserDataProvider {

	/**
	 * @return <code>true</code>
	 */
	public final boolean cacheUserObjects() {
		return false;
	}

	public final void loadAllUsers(UserSecurityDataHolder dataHolder) throws SQLException {
		throw new UnsupportedOperationException("Not supported since user objects are not cached");
	}

	public final void loadAllUsersToRoles(UserSecurityDataHolder dataHolder) throws SQLException {
		throw new UnsupportedOperationException("Not supported since user objects are not cached");
	}

}
