package com.mindbox.pe.server.db;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.admin.UserPassword;
import com.mindbox.pe.server.db.loaders.UserSecurityLoader;
import com.mindbox.pe.server.db.updaters.UserSecurityUpdater;
import com.mindbox.pe.server.spi.ServiceProviderFactory;
import com.mindbox.pe.server.spi.UserManagementProvider;
import com.mindbox.pe.server.spi.db.UserDataProvider;
import com.mindbox.pe.server.spi.db.UserDataUpdater;
import com.mindbox.pe.server.spi.db.UserSecurityDataHolder;


/**
 * Default implementation of {@link UserManagementProvider} that uses PowerEditor DB.
 * @author Geneho Kim
 * @since PowerEditor 
 */
public class DefaultUserManagementProvider implements UserManagementProvider {

	private final UserDataProvider userLoader;
	private final UserDataUpdater userUpdater;

	public DefaultUserManagementProvider() {
		this.userLoader = new UserSecurityLoader();
		this.userUpdater = new UserSecurityUpdater();
	}

	protected DefaultUserManagementProvider(UserDataProvider userLoader, UserDataUpdater userUpdater) {
		this.userLoader = userLoader;
		this.userUpdater = userUpdater;
	}

	public void loadAllPrivileges(UserSecurityDataHolder dataHolder) throws SQLException {
		userLoader.loadAllPrivileges(dataHolder);
	}

	public void loadAllRoles(UserSecurityDataHolder dataHolder) throws SQLException {
		userLoader.loadAllRoles(dataHolder);
	}

	public void loadAllPrivilegesToRoles(UserSecurityDataHolder dataHolder) throws SQLException {
		userLoader.loadAllPrivilegesToRoles(dataHolder);
	}

	public void loadAllUsers(UserSecurityDataHolder dataHolder) throws SQLException, ParseException {
		userLoader.loadAllUsers(dataHolder);
	}

	public void loadAllUsersToRoles(UserSecurityDataHolder dataHolder) throws SQLException {
		userLoader.loadAllUsersToRoles(dataHolder);
	}

	public void deleteRole(int roleID) throws SQLException {
		userUpdater.deleteRole(roleID);
	}

	public void deleteUser(String userID, String actingUserID) throws SQLException {
		userUpdater.deleteUser(userID, actingUserID);
	}

	public void insertRole(int roleID, String name, int[] privilegeIDs) throws SQLException {
		userUpdater.insertRole(roleID, name, privilegeIDs);
	}

	public void insertRoleWithUnknownPrivileges(int roleID, String name, int[] privilegeIDs, List<String> unknownPrivsForRole)
			throws SQLException {
		userUpdater.insertRoleWithUnknownPrivileges(roleID, name, privilegeIDs, unknownPrivsForRole);
	}

	public void insertUser(String userID, String name, String status, boolean passwordChangeRequired, int failedLoginCounter,
			int[] roleIDs, List<UserPassword> passwordHistory, String actingUserID) throws SQLException {
		userUpdater.insertUser(userID, name, status, passwordChangeRequired, failedLoginCounter, roleIDs, passwordHistory, actingUserID);
	}

	public void updateRole(int roleID, String name, int[] privilegeIDs) throws SQLException {
		userUpdater.updateRole(roleID, name, privilegeIDs);
	}

	public void updateRoleWithUnknownPrivileges(int roleID, String name, int[] privilegeIDs, List<String> unknownPrivsForRole)
			throws SQLException {
		userUpdater.updateRoleWithUnknownPrivileges(roleID, name, privilegeIDs, unknownPrivsForRole);
	}

	public void updateUser(String userID, String name, String status, boolean passwordChangeRequired, int failedLoginCounter,
			int[] roleIDs, List<UserPassword> passwordHistory, String actingUserID) throws SQLException {
		userUpdater.updateUser(userID, name, status, passwordChangeRequired, failedLoginCounter, roleIDs, passwordHistory, actingUserID);
	}

	public void updateFailedLoginCounter(String userID, int newValue) throws SQLException {
		userUpdater.updateFailedLoginCounter(userID, newValue);
	}

	public boolean cacheUserObjects() {
		return userLoader.cacheUserObjects();
	}

	public List<Role> getRoles(String userID) throws SQLException {
		return userLoader.getRoles(userID);
	}

	/**
	 * User passwords stored in PEDB are always changeable.  User passwords stored in an external system
	 * cannot be changed by PE.
	 */
	public boolean arePasswordsPersistable() {
		return !ServiceProviderFactory.getUserAuthenticationProvider().arePasswordsStoredExternally();
	}

	@Override
	public void enableUser(String userID, String actingUserID) throws SQLException {
		userUpdater.enableUser(userID, actingUserID);
	}

}
