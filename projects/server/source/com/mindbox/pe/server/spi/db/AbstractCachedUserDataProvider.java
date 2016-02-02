package com.mindbox.pe.server.spi.db;

import java.sql.SQLException;
import java.util.List;

import com.mindbox.pe.model.admin.Role;

/**
 * Abstract implementation of {@link UserDataProvider} that caches user objects.
 * @author kim
 *
 */
public abstract class AbstractCachedUserDataProvider implements UserDataProvider {

	/**
	 * @return <code>false</code>
	 */
	public final boolean cacheUserObjects() {
		return true;
	}

	public final List<Role> getRoles(String userID) throws SQLException {
		throw new UnsupportedOperationException("Not supported since users are cached");
	}

}
