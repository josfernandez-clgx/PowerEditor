package com.mindbox.pe.server.db.updaters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.admin.UserPassword;
import com.mindbox.pe.server.db.DBConnectionManager;
import com.mindbox.pe.server.db.DBUtil;
import com.mindbox.pe.server.spi.db.UserDataUpdater;

public class UserSecurityUpdater implements UserDataUpdater {

	public static final String USER_INSERT_QUERY = "insert into MB_USER_ACCOUNT(account_id,name,status,reset_password,failed_login_counter) values (?,?,?,?,?)";

	public static final String USER_INSERT_PASSWORD_QUERY = "insert into MB_USER_PASSWORD(account_id,password,password_change_date) values (?,?,?)";

	public static final String USER_UPDATE_QUERY = "update MB_USER_ACCOUNT set name=?,status=?,reset_password=?,failed_login_counter=? where account_id=?";

	public static final String USER_UPDATE_LOGIN_FAIL_COUNTER = "update MB_USER_ACCOUNT set failed_login_counter=? where account_id=?";

	public static final String USER_UPDATE_PASSWORD_QUERY = "update MB_USER_ACCOUNT set name=?,password=?,status=?,reset_password=?,password_change_date=?,failed_login_counter=? where account_id=?";

	public static final String USER_STATUS_UPDATE_QUERY = "update MB_USER_ACCOUNT set status=? where account_id=?";

	public static final String Q_DELETE_USER = "delete from MB_USER_ACCOUNT where account_id=?";

	public static final String Q_DELETE_USER_PASSWORD = "delete from MB_USER_PASSWORD where account_id=?";

	public static final String Q_DELETE_ROLE_FROM_USER = "delete from MB_USER_ROLE where role_id=?";

	public static final String Q_DELETE_USER_ROLE = "delete from MB_USER_ROLE where account_id=?";

	public static final String Q_DELETE_USER_CHANNEL = "delete from MB_UsersToChannels where user_account_id=?";

	public static final String Q_DELETE_USER_INVESTOR = "delete from MB_UsersToInvestors where  user_account_id=?";

	public static final String Q_DELETE_ALL_USER_CHANNEL = "delete from MB_UsersToChannels where channel_id=?";

	public static final String Q_DELETE_ALL_USER_INVESTOR = "delete from MB_UsersToInvestors where investor_id=?";

	public static final String Q_INSERT_USER_CHANNEL = "insert into MB_UsersToChannels (user_account_id,channel_id) values (?,?)";

	public static final String Q_INSERT_USER_INVESTOR = "insert into MB_UsersToInvestors (user_account_id,investor_id) values (?,?)";

	public static final String Q_INSERT_USER_ROLE = "insert into MB_USER_ROLE(account_id,role_id) values (?,?)";

	// MB_PRIVILEGE - Privilege related queries
	public static final String Q_LOAD_PRIVILEGES = "select privilege_name, privilege_id, display_string, privilege_type from MB_PRIVILEGE";

	public static final String Q_LOAD_PRIVILEGE_IDS = "select privilege_id from MB_PRIVILEGE where privilege_type=?";

	public static final String Q_LOAD_PRIVILEGE_IDS_ON_NAME = "select privilege_id from MB_PRIVILEGE where privilege_type=? and privilege_name like ?";

	public static final String Q_INSERT_PRIVILEGE = "insert into MB_PRIVILEGE (privilege_id,privilege_name,display_string,privilege_type) values (?,?,?,?)";

	public static final String Q_UPDATE_PRIVILEGE = "update MB_PRIVILEGE set display_string=?,privilege_name=? where privilege_id=?";

	public static final String Q_DELETE_PRIVILEGE = "delete from MB_PRIVILEGE where privilege_id=?";

	// MB_ROLE - Role related queries
	public static final String Q_LOAD_ROLES = "select role_id,role_name from MB_ROLE";

	private static final String Q_LOAD_ROLES_PRIVILEGES = "select privilege_id  from MB_ROLE_PRIVILEGE where role_id=?";

	public static final String ROLE_INSERT_QUERY = "insert into MB_ROLE(role_id,role_name) values (?,?)";

	public static final String ROLE_UPDATE_QUERY = "update MB_ROLE set role_name=? where role_id=?";

	public static final String Q_DELETE_ROLE = "delete from MB_ROLE where role_id=?";

	// MB_ROLE_PRIVILEGE - Privilege-Role related queries
	public static final String ROLE_PRIVILEGE_INSERT_QUERY = "insert into MB_ROLE_PRIVILEGE(role_id,privilege_id) values (?,?)";

	public static final String Q_SELECT_ROLE_PRIVILEGE = "select role_id, privilege_id from MB_ROLE_PRIVILEGE where role_id=? and privilege_id=?";

	public static final String Q_DELETE_PRIVILEGE_ROLE = "delete from MB_ROLE_PRIVILEGE where privilege_id=?";

	public static final String Q_DELETE_ROLE_PRIVILEGE = "delete from MB_ROLE_PRIVILEGE where role_id=?";

	public static final String Q_DELETE_ROLE_PRIVILEGE_2 = "delete from MB_ROLE_PRIVILEGE where role_id=? and privilege_id=?";

	private static Privilege findPrivilegeById(final int id, final List<Privilege> privileges) {
		for (final Privilege privilege : privileges) {
			if (privilege.getId() == id) {
				return privilege;
			}
		}
		return null;
	}

	private final Logger logger = Logger.getLogger(getClass());

	public UserSecurityUpdater() {
	}

	/**
	 * Deletes a privelege from MB_Privilege table in the Database. Also deletes
	 * all role-privilege relationships for that privilege in the MB_ROLE_PRIVILEGE
	 * table.
	 * @param connection connection
	 * @param priv priv
	 * @throws SQLException on error
	 */
	public void deletePrivilege(Connection connection, Privilege priv) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(Q_DELETE_PRIVILEGE);
			int privilege_id = priv.getID();
			ps.setInt(1, privilege_id);
			int rowCount = ps.executeUpdate();
			if (rowCount == 0) {
				throw new SQLException("Failed to delete privilege =" + priv.toString());
				//logger.info("cannot delete from DB, privilege=" + priv.toStringComplete());
			}
			else {
				logger.info("deleted from DB, privilege=" + priv.toStringComplete());
			}
			ps.close();
			ps = null;

			ps = connection.prepareStatement(Q_DELETE_PRIVILEGE_ROLE);
			ps.setInt(1, privilege_id);
			int rowCount1 = ps.executeUpdate();
			if (rowCount1 == 0) {
				//throw new SQLException("Failed to delete role-privilege ="+ priv.toStringComplete());
			}
			else {
				logger.info("deleted from DB, role-privilege relation for privilege=" + priv.toStringComplete());
			}
			ps.close();
			ps = null;

		}
		finally {
			if (ps != null) ps.close();
		}
	}

	/**
	 * Deletes a list of privileges for a specific role
	 * @param connection connection
	 * @param privilegeIds privilege ids
	 * @param roleID roleID
	 * @throws SQLException on error
	 */
	protected void deletePrivilegeRole(Connection connection, List<Integer> privilegeIds, int roleID) throws SQLException {
		PreparedStatement ps = null;
		try {
			for (Iterator<Integer> itr = privilegeIds.iterator(); itr.hasNext();) {
				ps = connection.prepareStatement(Q_DELETE_ROLE_PRIVILEGE_2);
				Integer privilege_id = itr.next();
				ps.setInt(1, roleID);
				ps.setInt(2, privilege_id.intValue());
				int rowCount = ps.executeUpdate();
				logger.info("deleted " + rowCount + " privileges from role!");
				ps.close();
				ps = null;
			}
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	/**
	 * Deletes the specified role.
	 * Calls {@link #deleteUserRoles(Connection, String)} to remove the specified role
	 * from all users.
	 */
	@Override
	public void deleteRole(int roleID) throws SQLException {
		logger.debug(">>> deleteRole: " + roleID);
		Connection connection = getConnection();
		PreparedStatement ps = null;
		try {
			connection.setAutoCommit(false);

			int rowCount;
			deleteRoleFromUsers(connection, roleID);

			ps = connection.prepareStatement(Q_DELETE_ROLE_PRIVILEGE);
			ps.setInt(1, roleID);
			rowCount = ps.executeUpdate();
			logger.info("deleted " + rowCount + " privileges for role");
			ps.close();
			ps = null;

			ps = connection.prepareStatement(Q_DELETE_ROLE);
			ps.setInt(1, roleID);
			rowCount = ps.executeUpdate();
			logger.info("deleted " + rowCount + " roles");

			connection.commit();
			logger.debug("<<< deleteRole");
		}
		catch (SQLException ex) {
			connection.rollback();
			throw ex;
		}
		catch (Exception ex) {
			connection.rollback();
			logger.error("Error while deleting role " + roleID, ex);
			throw new SQLException("Error - " + ex.getMessage());
		}
		finally {
			if (ps != null) ps.close();
			releaseConnection(connection);
		}
	}

	/**
	 * Removes the specified role from all users.
	 * The {@link #deleteRole(int)} method calls this.
	 * @param connection DB connection
	 * @param roleID role ID
	 * @throws SQLException on error
	 */
	protected void deleteRoleFromUsers(Connection connection, int roleID) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(Q_DELETE_ROLE_FROM_USER);
			ps.setInt(1, roleID);
			int rowCount = ps.executeUpdate();
			logger.info("deleted " + rowCount + " user's roles");
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	/**
	 * Deletes the specified user.
	 * Calls {@link #deleteUserRoles(Connection, String)}.
	 */
	@Override
	public void deleteUser(String userID, String actingUserID) throws SQLException {
		logger.debug(">>> deleteUser: " + userID);
		Connection connection = getConnection();
		PreparedStatement ps = null;
		try {
			connection.setAutoCommit(false);
			deleteUserRoles(connection, userID);
			deleteUserPassword(connection, userID);

			ps = connection.prepareStatement(Q_DELETE_USER);
			ps.setString(1, userID);
			int i = ps.executeUpdate();
			logger.info("removed " + i + " user row(s)!");

			connection.commit();
			logger.debug("<<< deleteUser");
		}
		catch (SQLException ex) {
			connection.rollback();
			throw ex;
		}
		catch (Exception ex) {
			connection.rollback();
			logger.error("Error while deleting user " + userID, ex);
			throw new SQLException("Error - " + ex.getMessage());
		}
		finally {
			if (ps != null) ps.close();
			releaseConnection(connection);
		}
	}

	/**
	 * Deletes user password.
	 * @param connection DB connection
	 * @param userID user id
	 * @throws SQLException on error
	 */
	private void deleteUserPassword(Connection connection, String userID) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(Q_DELETE_USER_PASSWORD);
			ps.setString(1, userID);
			int rowCount = ps.executeUpdate();
			logger.info("deleted " + rowCount + " passwords from user");
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	/**
	 * Deletes user roles.
	 * @param connection DB connection
	 * @param userID user id
	 * @throws SQLException on error
	 */
	protected void deleteUserRoles(Connection connection, String userID) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(Q_DELETE_USER_ROLE);
			ps.setString(1, userID);
			int rowCount = ps.executeUpdate();
			logger.info("deleted " + rowCount + " roles from user");
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	@Override
	public void enableUser(String userID, String actingUserID) throws SQLException {
		throw new UnsupportedOperationException();
	}

	private Connection getConnection() throws SQLException {
		return DBConnectionManager.getInstance().getConnection();
	}

	/**
	 * Inserts a privilege to MB_Privilege table in the db
	 * @param connection connection
	 * @param priv priv
	 * @throws SQLException on error
	 */
	public void insertPrivilege(Connection connection, Privilege priv) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(Q_INSERT_PRIVILEGE);
			String privilege_name = priv.getName();
			int privilege_id = priv.getID();
			String display_string = priv.getDisplayString();
			int privilege_type = priv.getPrivilegeType();
			ps.setInt(1, privilege_id);
			ps.setString(2, privilege_name);
			ps.setString(3, display_string);
			ps.setInt(4, privilege_type);

			int rowCount = ps.executeUpdate();
			if (rowCount == 0) {
				throw new SQLException("Failed to insert the new privilege");
			}
			else {
				logger.info("added to DB, privilege=" + priv.toStringComplete());
			}
			ps.close();
			ps = null;

		}
		finally {
			if (ps != null) ps.close();
		}
	}

	/** 
	 * Adds privilege-role relationship. Also checks if that relationship exists or not before adding it.
	 * @param connection connection
	 * @param privilegeIds privilegeIds
	 * @param roleId roleId
	 * @throws SQLException on error
	 * @since 5.0.0  
	 */
	public void insertPrivilegeRole(Connection connection, List<Integer> privilegeIds, int roleId) throws SQLException {
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		try {
			for (Iterator<Integer> itr = privilegeIds.iterator(); itr.hasNext();) {
				ps = connection.prepareStatement(ROLE_PRIVILEGE_INSERT_QUERY);
				ps1 = connection.prepareStatement(Q_SELECT_ROLE_PRIVILEGE);

				Integer privilegeId = itr.next();
				ps.setInt(1, roleId);
				ps.setInt(2, privilegeId.intValue());
				ps1.setInt(1, roleId);
				ps1.setInt(2, privilegeId.intValue());

				rs1 = ps1.executeQuery();
				if (!rs1.next()) {
					int rowCount = ps.executeUpdate();
					if (rowCount == 0) {
						logger.info("rewrote relationship b/w: " + privilegeId + " and " + roleId);
					}
					else {
						logger.info("added relationship b/w: " + privilegeId + " and " + roleId);
					}
					ps.close();
					ps = null;
				}
				ps1.close();
				ps1 = null;
			}
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	/** Adds privilege-role relationships.
	 * All new privileges created from config file are assigned to all existing roles
	 * @param connection connection
	 * @param priv priv
	 * @param roles roles
	 * @throws SQLException on error
	 */
	public void insertPrivilegeRoles(final Connection connection, final Privilege priv, final List<Role> roles) throws SQLException {
		PreparedStatement ps = null;
		try {
			for (final Role role : roles) {
				ps = connection.prepareStatement(ROLE_PRIVILEGE_INSERT_QUERY);
				int privilegeId = priv.getID();
				ps.setInt(1, role.getID());
				ps.setInt(2, privilegeId);

				int rowCount = ps.executeUpdate();
				if (rowCount == 0) {
					throw new SQLException("Failed to insert the new role-privilege relationship");
				}
				else {
					logger.info("added relationship b/w: " + priv.toString() + " and " + role.toString());
				}
				ps.close();
				ps = null;
			}
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	@Override
	public void insertRole(int roleID, String name, int privilegeIDs[]) throws SQLException {
		logger.debug(">>> insertRole: " + roleID + ",name=" + name);
		Connection connection = getConnection();
		connection.setAutoCommit(false);
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(ROLE_INSERT_QUERY);
			ps.setInt(1, roleID);
			ps.setString(2, name);
			int rowCount = ps.executeUpdate();

			logger.info("Inserted " + rowCount + " role(s)!");
			ps.close();
			ps = null;

			ps = connection.prepareStatement(ROLE_PRIVILEGE_INSERT_QUERY);
			if (privilegeIDs != null) {
				for (int i = 0; i < privilegeIDs.length; i++) {
					ps.setInt(1, roleID);
					ps.setInt(2, privilegeIDs[i]);
					rowCount = ps.executeUpdate();
					if (rowCount == 0) {
						throw new SQLException("Failed to insert privilege for role: " + privilegeIDs[i]);
					}
				}
				logger.info("Inserted " + privilegeIDs.length + " privileges for role");
			}

			connection.commit();
			logger.debug("<<< insertRole");
		}
		catch (SQLException ex) {
			connection.rollback();
			throw ex;
		}
		catch (Exception ex) {
			connection.rollback();
			logger.error(ex);
			throw new SQLException("Failed to insert new role " + name + ": " + ex.getMessage());
		}
		finally {
			if (ps != null) ps.close();
			releaseConnection(connection);
		}
	}


	@Override
	public void insertRoleWithUnknownPrivileges(int roleID, String name, int privilegeIDs[], List<String> unknownPrivsForRole) throws SQLException {
		insertRole(roleID, name, privilegeIDs);
		updateUnknownPrivileges(roleID, unknownPrivsForRole);
	}

	/**
	 * Inserts the specified user.
	 * Calls {@link #insertUserRoles(Connection, String, int[])}.
	 */
	@Override
	public void insertUser(String userID, String name, String status, boolean reset_password, int failedLoginCounter, int[] roleIDs, List<UserPassword> passwordHistory,
			String actingUserID) throws SQLException {
		logger.debug(">>> insertUser: " + userID + ",name=" + name + ",status=" + status);
		Connection connection = getConnection();
		connection.setAutoCommit(false);
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(USER_INSERT_QUERY);
			ps.setString(1, userID);
			ps.setString(2, name);
			ps.setString(3, status);
			ps.setBoolean(4, reset_password);
			ps.setInt(5, failedLoginCounter);
			int rowCount = ps.executeUpdate();
			if (rowCount == 0) {
				throw new SQLException("Failed to insert the new user row");
			}
			ps.close();
			ps = null;
			insertUserPasswords(connection, userID, passwordHistory);
			insertUserRoles(connection, userID, roleIDs);
			connection.commit();
			logger.debug("<<< insertUser");
		}
		catch (SQLException ex) {
			connection.rollback();
			throw ex;
		}
		catch (Exception ex) {
			connection.rollback();
			logger.error("Error while deleting user " + userID, ex);
			throw new SQLException("Error - " + ex.getMessage());
		}
		finally {
			if (ps != null) ps.close();
			releaseConnection(connection);
		}
	}

	/**
	 * Inserts user passwords.
	 * @param connection DB connection
	 * @param userID user id
	 * @param roleIDs role ids
	 * @throws SQLException on error
	 */
	private void insertUserPasswords(Connection connection, String userID, List<UserPassword> passwords) throws SQLException {
		PreparedStatement ps = null;
		try {
			if (passwords != null && passwords.size() > 0) {
				ps = connection.prepareStatement(USER_INSERT_PASSWORD_QUERY);
				for (UserPassword userPassword : passwords) {
					ps.setString(1, userID);
					ps.setString(2, userPassword.getPassword());
					DBUtil.setDateValue(ps, 3, userPassword.getPasswordChangeDate());
					int rowCount = ps.executeUpdate();
					if (rowCount == 0) {
						throw new SQLException("Failed to insert password for user: " + userID);
					}
				}
				logger.info("Inserted " + passwords.size() + " passwords for user");
			}
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	/**
	 * Inserts user roles.
	 * @param connection DB connection
	 * @param userID user id
	 * @param roleIDs role ids
	 * @throws SQLException on error
	 */
	protected void insertUserRoles(Connection connection, String userID, int[] roleIDs) throws SQLException {
		PreparedStatement ps = null;
		try {
			if (roleIDs != null && roleIDs.length > 0) {
				ps = connection.prepareStatement(Q_INSERT_USER_ROLE);
				for (int i = 0; i < roleIDs.length; i++) {
					ps.setString(1, userID);
					ps.setInt(2, roleIDs[i]);
					int rowCount = ps.executeUpdate();
					if (rowCount == 0) {
						throw new SQLException("Failed to insert role for user: " + roleIDs[i]);

					}
				}
				logger.info("Inserted " + roleIDs.length + " roles for user");
			}
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	/**
	 * Loads all privilegeIds which are currently in the database based on privilege_type
	 * @param connection connection
	 * @param privilegeType privilege type
	 * @return privilege id list
	 * @throws SQLException on error
	 * @since 5.0.0
	 */
	public List<Integer> loadPrivilegeIds(Connection connection, int privilegeType) throws SQLException {
		LinkedList<Integer> privilegeIds = new LinkedList<Integer>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(Q_LOAD_PRIVILEGE_IDS);
			ps.setInt(1, privilegeType);
			rs = ps.executeQuery();

			while (rs.next()) {
				int privId = rs.getInt(1);
				privilegeIds.add(privId);
			}
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
		}
		return privilegeIds;
	}

	/**
	 * Loads all privilegeIds which are currently in the database based on privilege_type and a 
	 * partial string match
	 * @param connection connection
	 * @param privilegeType privilegeType
	 * @param patternToMatch patternToMatch
	 * @return privilegeIds
	 * @throws SQLException on error
	 * @since 5.0.0
	 */
	public List<Integer> loadPrivilegeIds(Connection connection, int privilegeType, String patternToMatch) throws SQLException {
		LinkedList<Integer> privilegeIds = new LinkedList<Integer>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(Q_LOAD_PRIVILEGE_IDS_ON_NAME);
			ps.setInt(1, privilegeType);
			ps.setString(2, patternToMatch);
			rs = ps.executeQuery();

			while (rs.next()) {
				int privId = rs.getInt(1);
				privilegeIds.add(privId);
			}
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
		}
		return privilegeIds;
	}

	/** 
	 * Loads all privileges from the MB_Privilege table to a hashmap.
	 * @param connection connection
	 * @return HashMap of privileges
	 * @throws SQLException on error
	 * @since 5.0.0
	 */
	public List<Privilege> loadPrivileges(Connection connection) throws SQLException {
		List<Privilege> list = new ArrayList<Privilege>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(Q_LOAD_PRIVILEGES);
			rs = ps.executeQuery();

			String s;
			int i;
			String s1;
			int privType;

			while (rs.next()) {
				s = UtilBase.trim(rs.getString(1));
				i = rs.getInt(2);
				s1 = UtilBase.trim(rs.getString(3));
				privType = rs.getInt(4);
				list.add(new Privilege(i, s, s1, privType));
			}

			return list;
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
		}
	}

	/**
	 * Loads roles to a linked list. These roles have no privileges attached to them.
	 * This list is used in {@link #insertPrivilegeRoles(Connection, Privilege, List)} to
	 * add privilege-role relationship in MB_ROLE_PRIVILEGE table when a new privilege
	 * is added from the config file
	 * @param connection connection
	 * @param privileges privileges
	 * @return roles
	 * @throws SQLException on error
	 * @since 5.0.0
	 */
	public List<Role> loadRoles(Connection connection, final List<Privilege> privileges) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		try {
			ps2 = connection.prepareStatement(Q_LOAD_ROLES_PRIVILEGES);
			ps = connection.prepareStatement(Q_LOAD_ROLES);

			rs = ps.executeQuery();

			final List<Role> roles = new LinkedList<Role>();
			while (rs.next()) {
				int roleId = rs.getInt(1);
				String roleName = UtilBase.trim(rs.getString(2));
				final Role role = new Role(roleId, roleName, null);

				ps2.setInt(1, roleId);
				rs2 = ps2.executeQuery();
				while (rs2.next()) {
					final Privilege privilege = findPrivilegeById(rs2.getInt(1), privileges);
					if (privilege != null) {
						role.addPrivilege(privilege);
					}
				}
				rs2.close();
				rs2 = null;

				roles.add(role);
			}

			return roles;
		}
		finally {
			if (rs != null) rs.close();
			if (rs2 != null) rs2.close();
			if (ps2 != null) ps2.close();
			if (ps != null) ps.close();
		}
	}


	private void releaseConnection(Connection conn) {
		DBConnectionManager.getInstance().freeConnection(conn);
	}


	@Override
	public void updateFailedLoginCounter(String userID, int newValue) throws SQLException {
		logger.debug(">>> updateFailedLoginCounter: " + userID + ",newValue=" + newValue);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		connection.setAutoCommit(false);
		try {
			ps = connection.prepareStatement(USER_UPDATE_LOGIN_FAIL_COUNTER);
			ps.setInt(1, newValue);
			ps.setString(2, userID);
			ps.executeUpdate();
			connection.commit();
			logger.debug("<<< updateFailedLoginCounter");
		}
		catch (SQLException ex) {
			connection.rollback();
			throw ex;
		}
		catch (Exception ex) {
			connection.rollback();
			logger.error("Error while updating failed login counter of user " + userID, ex);
			throw new SQLException("Error - " + ex.getMessage());
		}
		finally {
			if (ps != null) ps.close();
			releaseConnection(connection);
		}
	}

	/**
	 * Updates the display_string and privilege_name of a privilege in MB_Privilege table
	 * @param connection connection
	 * @param priv priv
	 * @throws SQLException on error
	 */
	public void updatePrivilege(Connection connection, Privilege priv) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(Q_UPDATE_PRIVILEGE);
			int priv_id = priv.getID();
			String privilege_name = priv.getName();
			String display_string = priv.getDisplayString();
			ps.setString(1, display_string);
			ps.setString(2, privilege_name);
			ps.setInt(3, priv_id);

			int rowCount = ps.executeUpdate();
			if (rowCount == 0) {
				throw new SQLException("Failed to update privilege =" + priv.toString());
			}
			else {
				logger.info("updated privilege=" + priv.toStringComplete());
			}
			ps.close();
			ps = null;

		}
		finally {
			if (ps != null) ps.close();
		}
	}

	@Override
	public void updateRole(int roleID, String name, int privilegeIDs[]) throws SQLException {
		logger.debug(">>> updateRole: " + roleID + "," + name);
		Connection connection = getConnection();
		connection.setAutoCommit(false);
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(ROLE_UPDATE_QUERY);
			ps.setString(1, name);
			ps.setInt(2, roleID);
			int rowCount = ps.executeUpdate();
			logger.info("updated " + rowCount + " role rows");
			ps.close();
			ps = null;

			ps = connection.prepareStatement(Q_DELETE_ROLE_PRIVILEGE);
			ps.setInt(1, roleID);
			rowCount = ps.executeUpdate();
			logger.info("deleted " + rowCount + " privileges from role!");
			ps.close();
			ps = null;

			ps = connection.prepareStatement(ROLE_PRIVILEGE_INSERT_QUERY);
			if (privilegeIDs != null) {
				for (int i = 0; i < privilegeIDs.length; i++) {
					ps.setInt(1, roleID);
					ps.setInt(2, privilegeIDs[i]);
					rowCount = ps.executeUpdate();
					if (rowCount == 0) {
						throw new SQLException("Failed to insert privilege for role: " + privilegeIDs[i]);
					}
				}
				logger.info("inserted " + privilegeIDs.length + " privileges for role");
			}
			connection.commit();
			logger.debug("<<< updateRole");
		}
		catch (SQLException exp) {
			connection.rollback();
			throw exp;
		}
		catch (Exception ex) {
			connection.rollback();
			logger.error("Error while updating role " + roleID, ex);
			throw new SQLException("Error - " + ex.getMessage());
		}
		finally {
			if (ps != null) ps.close();
			releaseConnection(connection);
		}

	}


	@Override
	public void updateRoleWithUnknownPrivileges(int roleID, String name, int privilegeIDs[], List<String> unknownPrivsForRole) throws SQLException {
		updateRole(roleID, name, privilegeIDs);
		updateUnknownPrivileges(roleID, unknownPrivsForRole);
	}

	/**
	 * @param roleID roleID
	 * @param unknownPrivsForRole unknownPrivsForRole
	 * @throws SQLException on error
	 * @since 5.0.0
	 */
	public void updateUnknownPrivileges(int roleID, List<String> unknownPrivsForRole) throws SQLException {
		Connection connection = getConnection();
		connection.setAutoCommit(false);
		PreparedStatement ps = null;
		try {
			if (unknownPrivsForRole.contains(PrivilegeConstants.PRIV_EDIT_ENTITIES) && unknownPrivsForRole.contains(PrivilegeConstants.PRIV_VIEW_ENTITIES)) {
				List<Integer> privilegeIds = loadPrivilegeIds(connection, PrivilegeConstants.ENTITY_TYPE_PRIV);
				insertPrivilegeRole(connection, privilegeIds, roleID);

			}
			else if (unknownPrivsForRole.contains(PrivilegeConstants.PRIV_EDIT_ENTITIES)) {
				String patternToMatchEdit = PrivilegeConstants.EDIT_PRIV_NAME_PREFIX + "%";
				List<Integer> privilegeIdsEdit = loadPrivilegeIds(connection, PrivilegeConstants.ENTITY_TYPE_PRIV, patternToMatchEdit);
				insertPrivilegeRole(connection, privilegeIdsEdit, roleID);

				String patternToMatchView = PrivilegeConstants.VIEW_PRIV_NAME_PREFIX + "%";
				List<Integer> privilegeIdsView = loadPrivilegeIds(connection, PrivilegeConstants.ENTITY_TYPE_PRIV, patternToMatchView);
				deletePrivilegeRole(connection, privilegeIdsView, roleID);

			}
			else if (unknownPrivsForRole.contains(PrivilegeConstants.PRIV_VIEW_ENTITIES)) {
				String patternToMatchView = PrivilegeConstants.VIEW_PRIV_NAME_PREFIX + "%";
				List<Integer> privilegeIdsView = loadPrivilegeIds(connection, PrivilegeConstants.ENTITY_TYPE_PRIV, patternToMatchView);
				insertPrivilegeRole(connection, privilegeIdsView, roleID);

				String patternToMatchEdit = PrivilegeConstants.EDIT_PRIV_NAME_PREFIX + "%";
				List<Integer> privilegeIdsEdit = loadPrivilegeIds(connection, PrivilegeConstants.ENTITY_TYPE_PRIV, patternToMatchEdit);
				deletePrivilegeRole(connection, privilegeIdsEdit, roleID);
			}

			if (unknownPrivsForRole.contains(PrivilegeConstants.PRIV_MANAGE_TEMPLATES)) {
				List<Integer> privilegeIds = loadPrivilegeIds(connection, PrivilegeConstants.USAGE_TYPE_PRIV);
				insertPrivilegeRole(connection, privilegeIds, roleID);
			}
			else {
				List<Integer> privilegeIds = loadPrivilegeIds(connection, PrivilegeConstants.USAGE_TYPE_PRIV);
				deletePrivilegeRole(connection, privilegeIds, roleID);
			}
			connection.commit();
		}
		catch (SQLException ex) {
			connection.rollback();
			throw ex;
		}
		catch (Exception ex) {
			connection.rollback();
			logger.error(ex);
			throw new SQLException("Failed to update unknown privileges for role " + roleID + ": " + ex.getMessage());
		}
		finally {
			if (ps != null) ps.close();
			releaseConnection(connection);
		}
	}

	/**
	 * Updates the specified user.
	 * Calls {@link #insertUserRoles(Connection, String, int[])},
	 * and {@link #deleteUserRoles(Connection, String)}.
	 */
	@Override
	public void updateUser(String userID, String name, String status, boolean passwordChangeRequired, int failedLoginCounter, int[] roleIDs, List<UserPassword> passwordHistory,
			String actingUserID) throws SQLException {
		logger.debug(">>> updateUser: " + userID + ",name=" + name + ",status=" + status + ",passwordChangeRequired=" + passwordChangeRequired);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		connection.setAutoCommit(false);
		try {
			ps = connection.prepareStatement(USER_UPDATE_QUERY);
			ps.setString(1, name);
			ps.setString(2, status);
			ps.setBoolean(3, passwordChangeRequired);
			ps.setInt(4, failedLoginCounter);
			ps.setString(5, userID);
			ps.executeUpdate();
			ps.close();
			ps = null;

			deleteUserRoles(connection, userID);
			insertUserRoles(connection, userID, roleIDs);
			deleteUserPassword(connection, userID);
			insertUserPasswords(connection, userID, passwordHistory);
			connection.commit();
			logger.debug("<<< updateUser");
		}
		catch (SQLException exp) {
			connection.rollback();
			throw exp;
		}
		catch (Exception ex) {
			connection.rollback();
			logger.error("Error while updating user " + userID, ex);
			throw new SQLException("Error - " + ex.getMessage());
		}
		finally {
			if (ps != null) ps.close();
			releaseConnection(connection);
		}
	}

}