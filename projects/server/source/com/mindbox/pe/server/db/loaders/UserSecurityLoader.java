package com.mindbox.pe.server.db.loaders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.admin.UserPassword;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.db.DBConnectionManager;
import com.mindbox.pe.server.db.DBUtil;
import com.mindbox.pe.server.spi.db.AbstractCachedUserDataProvider;
import com.mindbox.pe.server.spi.db.UserDataProvider;
import com.mindbox.pe.server.spi.db.UserSecurityDataHolder;

public class UserSecurityLoader extends AbstractCachedUserDataProvider implements UserDataProvider {

	private static final String Q_LOAD_PRIVILEGES = "select privilege_name, privilege_id, display_string, privilege_type from MB_PRIVILEGE";

	private static final String Q_LOAD_ROLES = "select role_id, role_name from MB_ROLE";

	private static final String Q_LOAD_ROLES_PRIVILEGES = "select role_id, privilege_id  from MB_ROLE_PRIVILEGE";

	private static final String Q_LOAD_USERS_ROLES = "select account_id, role_id from MB_USER_ROLE";

	private static final String USER_LOAD_QUERY = "select account_id, name, status, reset_password, failed_login_counter from MB_USER_ACCOUNT ";

    private static final String USER_PASSWORD_QUERY = "select password, password_change_date " + 
        "from MB_USER_PASSWORD where account_id = ? ORDER BY password_change_date DESC";    

	protected final Logger logger;

	public UserSecurityLoader() {
		logger = Logger.getLogger(getClass());
	}

	public void loadAllPrivileges(UserSecurityDataHolder dataHolder) throws SQLException {
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection connection = dbconnectionmanager.getConnection();
		assert (dataHolder == null) : "dataHolder instance is null";

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(Q_LOAD_PRIVILEGES);
			rs = ps.executeQuery();
			logger.info("===== Privileges ======");
			String s;
			int i;
			String s1;
			int privType;
			for (; rs.next(); dataHolder.addPrivilege(i, s, s1,privType)) {
				s = UtilBase.trim(rs.getString(1));
				i = rs.getInt(2);
				s1 = UtilBase.trim(rs.getString(3));
				privType = rs.getInt(4);
				logger.info(s);
			}
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
			dbconnectionmanager.freeConnection(connection);
		}
	}

	public void loadAllUsersToRoles(UserSecurityDataHolder dataHolder) throws SQLException {
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection connection = dbconnectionmanager.getConnection();
		assert (dataHolder == null) : "dataHolder instance is null";

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(Q_LOAD_USERS_ROLES);
			rs = ps.executeQuery();
			logger.info("===== User Roles ======");
			String s1;
			int roleID;
			while (rs.next()) {
				s1 = UtilBase.trim(rs.getString(1));
				roleID = rs.getInt(2);

				if (!SecurityCacheManager.getInstance().hasUser(s1)) {
					logger.warn("Skipping User-to-Role - invalid user ID (" + "user=" + s1 + ",role=" + roleID + ")");
				}
				else {
					dataHolder.addUserToRole(s1, roleID);
					logger.info(s1 + "; " + roleID);
				}
			}
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
			dbconnectionmanager.freeConnection(connection);
		}
	}

	public void loadAllRoles(UserSecurityDataHolder dataHolder) throws SQLException {
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection connection = dbconnectionmanager.getConnection();
		assert (dataHolder == null) : "dataHolder instance is null";

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(Q_LOAD_ROLES);
			rs = ps.executeQuery();
			logger.info("===== Roles ======");
			int i;
			String s;
			for (; rs.next(); dataHolder.addRole(i, s)) {
				i = rs.getInt(1);
				s = UtilBase.trim(rs.getString(2));
				logger.info(s);
			}
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
			dbconnectionmanager.freeConnection(connection);
		}
	}

	public void loadAllPrivilegesToRoles(UserSecurityDataHolder dataHolder) throws SQLException {
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection connection = dbconnectionmanager.getConnection();
		assert (dataHolder == null) : "dataHolder instance is null";

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(Q_LOAD_ROLES_PRIVILEGES);
			rs = ps.executeQuery();
			logger.info("===== Role Privileges ======");
			int i;
			int j;
			for (; rs.next(); dataHolder.addPrivilegeToRole(j, i)) {
				i = rs.getInt(1);
				j = rs.getInt(2);
				logger.info(i + "; " + j);
			}
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
			dbconnectionmanager.freeConnection(connection);
		}
	}

	public void loadAllUsers(UserSecurityDataHolder dataHolder) throws SQLException, ParseException {
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection connection = dbconnectionmanager.getConnection();
		assert (dataHolder == null) : "dataHolder instance is null";

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(USER_LOAD_QUERY);
			rs = ps.executeQuery();
			logger.info("===== Users ======");
			while (rs.next()) {
				String userID = UtilBase.trim(rs.getString(1));
				String userName = UtilBase.trim(rs.getString(2));
				String status = UtilBase.trim(rs.getString(3));
				boolean passwordChangeRequired = rs.getBoolean(4);
				int failedLoginAttempts = rs.getInt(5);
                List<UserPassword> passwordHistory = loadUserPasswordHistory(userID);
                dataHolder.addUser(userID, userName, status, passwordChangeRequired, 
                        failedLoginAttempts, passwordHistory);
				logger.info(userID + "; " + userName);
			}
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
			dbconnectionmanager.freeConnection(connection);
		}
	}
	
	private List<UserPassword> loadUserPasswordHistory(String userID) throws SQLException, ParseException {
        DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
        Connection connection = dbconnectionmanager.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<UserPassword> passwordHistory = new ArrayList<UserPassword>();
        try {
            ps = connection.prepareStatement(USER_PASSWORD_QUERY);
            ps.setString(1, userID);
            rs = ps.executeQuery();
            logger.info("===== Users ======");
            while (rs.next()) {
                String pwd = UtilBase.trim(rs.getString(1));
                Date pwdChangeDate = DBUtil.getDateValue(rs,2);
                UserPassword userPassword = new UserPassword(pwd, pwdChangeDate);
                passwordHistory.add(userPassword);
            }
        }
        finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            dbconnectionmanager.freeConnection(connection);
        }
        return passwordHistory;
    }

    public void load() throws Exception {
        // NOT USED
    }
}