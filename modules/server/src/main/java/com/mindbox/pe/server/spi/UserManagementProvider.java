package com.mindbox.pe.server.spi;

import com.mindbox.pe.server.spi.db.UserDataProvider;
import com.mindbox.pe.server.spi.db.UserDataUpdater;

/**
 * Provides management of user data.
 * @see com.mindbox.pe.server.spi.db.UserDataProvider
 * @see com.mindbox.pe.server.spi.db.UserDataUpdater
 * @since PowerEditor 4.5
 */
public interface UserManagementProvider extends UserDataProvider, UserDataUpdater {
	/**
	 * Does the user management provider allow password values to be persisted by PE.
	 * 
	 * There is a complex set of interactions between elements of the PE configuration
	 * to determine whether PE can persist passwords.  This method will return false when 
	 * authentication is performed by an external plugin (in which case passwords are managed 
	 * entirely by that external system), or, when user data is stored outside of the PEDB 
	 * (e.g. LDAP) and that data store's password data is read-only.
	 * 
	 * Otherwise, this method returns true.
	 * 
	 * @return true if persistable; false, otherwise
	 * @see UserAuthenticationProviderPlugin#arePasswordsStoredExternally()
	 */
	boolean arePasswordsPersistable();

}
