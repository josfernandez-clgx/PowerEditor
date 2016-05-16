package com.mindbox.pe.server.spi.db;

import java.sql.SQLException;
import java.util.List;

import com.mindbox.pe.model.admin.UserPassword;

/**
 * User data updater, a part of PEDBC SPI.
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PE 2.0.0
 */
public interface UserDataUpdater {
	/**   Used as pwd value in PEDB or LDAP when Authentication is external. */
	public static final String BOGUS_USER_PASSWORD = "BOGUS";

	// PEDBC update methods -------------------------------------------

	/**
	 * Removes the specified role from the storage. This must delete all references to the specified
	 * role, thereby ensuring data integrity.
	 * 
	 * @param roleID roleID
	 *            the role to delete
	 * @throws SQLException on error
	 *             on error
	 */
	void deleteRole(int roleID) throws SQLException;

	void deleteUser(String userID, String actingUserID) throws SQLException;

	/**
	 * Enables the specified user
	 * @param userID user to enable
	 * @param actingUserID action user id
	 * @throws SQLException on error
	 * @since 5.8.2 build 9
	 */
	void enableUser(String userID, String actingUserID) throws SQLException;

	/**
	 * @param roleID roleID
	 * @param name name
	 * @param privilegeIDs privilegeIDs
	 * @throws SQLException on error
	 */
	void insertRole(int roleID, String name, int[] privilegeIDs) throws SQLException;

	/**
	 * For upgrade from 4.5.x to 5.0.0. This is called during an import, when privileges are identified in a new role
	 * that dont currently exist in MB_Privilege table in the database. If those unknown privileges contain, EditEntityData,
	 * ViewEntityData or ManageTemplates, then they are processesd accordingly. All other unknown privileges are ignored.
	 * @param roleID roleID
	 * @param name name
	 * @param privilegeIDs privilegeIDs
	 * @param unknownPrivsForRole unknownPrivsForRole
	 * @throws SQLException on error
	 * @since 5.0.0
	 */
	void insertRoleWithUnknownPrivileges(int roleID, String name, int[] privilegeIDs, List<String> unknownPrivsForRole) throws SQLException;

	void insertUser(String userID, String name, String status, boolean passwordChangeRequired, int failedLoginCounter, int[] roleIDs, List<UserPassword> passwordHistory,
			String actingUserID) throws SQLException;

	/**
	 * Updates the failed login counter of the specified user to the specified value.
	 * @param userID user id
	 * @param newValue the new failed login counter value
	 * @throws SQLException on error
	 * @since 5.1.0
	 */
	void updateFailedLoginCounter(String userID, int newValue) throws SQLException;

	/**
	 * Updates a role by <br>
	 * 1. Updating the roles table <br>
	 * 2. Deletes all existing role-privilege relationships <br>
	 * 3. Inserts new role-privilege relationships.
	 * @param roleID roleID
	 * @param name name
	 * @param privilegeIDs privilegeIDs
	 * @throws SQLException on error
	 */
	void updateRole(int roleID, String name, int[] privilegeIDs) throws SQLException;

	/**
	 * Mainly used for upgrade from 4.5.x to 5.0.0. This is called during an import, when privileges are identified in an existing role
	 * that dont currently exist in MB_Privilege table in the database. If those unknown privileges contain, EditEntityData,
	 * ViewEntityData or ManageTemplates, then they are processesd accordingly. All other unknown privileges are ignored.<br>
	 * Calls {@link #updateRole(int roleID, String name, int[] privilegeIDs)} first to update all privileges which
	 * exist in the database.
	 * @param roleID roleID
	 * @param name name
	 * @param privilegeIDs privilegeIDs
	 * @param unknownPrivsForRole unknownPrivsForRole
	 * @throws SQLException on error
	 * @since 5.0.0
	 */
	void updateRoleWithUnknownPrivileges(int roleID, String name, int[] privilegeIDs, List<String> unknownPrivsForRole) throws SQLException;

	void updateUser(String userID, String name, String status, boolean passwordChangeRequired, int failedLoginCounter, int[] roleIDs, List<UserPassword> passwordHistory,
			String actingUserID) throws SQLException;
}
