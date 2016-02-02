package com.mindbox.pe.server.sysext.usertracking;

import static com.mindbox.pe.server.db.DBUtil.closeLocallyManagedResources;
import static com.mindbox.pe.server.db.DBUtil.closeLocallyManagedStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;

import com.mindbox.pe.server.db.DBConnectionManager;

public class DefaultUserTrackingDao implements UserTrackingDao {

	private static final Logger LOG = Logger.getLogger(DefaultUserTrackingDao.class);

	private static final String Q_SELECT_CHECK = "select USERNAME from POWEREDITOR_USER_TRACKING where USERNAME=?";
	private static final String Q_SELECT_DISABLED = "select USERDISABLED from POWEREDITOR_USER_TRACKING where USERNAME=?";
	private static final String Q_INSERT_TRACKING = "insert into POWEREDITOR_USER_TRACKING (USERNAME,USERLASTMODIFIEDDATE,MODIFIEDBY,USERCREATEDATE,USERDISABLED,ENABLED_DATE) values (?,?,?,?,'0',?)";
	private static final String Q_UPDATE_LAST_LOGIN = "update POWEREDITOR_USER_TRACKING set USERLASTLOGINDATE=? where USERNAME=?";
	private static final String Q_UPDATE_LAST_MODIFIED = "update POWEREDITOR_USER_TRACKING set USERLASTMODIFIEDDATE=?, MODIFIEDBY=? where USERNAME=?";
	private static final String Q_UPDATE_ENABLED = "update POWEREDITOR_USER_TRACKING set USERLASTMODIFIEDDATE=?, MODIFIEDBY=?, USERDISABLED='0', ENABLED_DATE=? where USERNAME=?";

	@Override
	public void enableUserRecord(String userId, String modifiedBy) throws SQLException {
		long currentTime = System.currentTimeMillis();
		Connection connection = DBConnectionManager.getInstance().getConnection();
		PreparedStatement ps = null;
		try {
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(Q_UPDATE_ENABLED);
			ps.setTimestamp(1, new Timestamp(currentTime));
			ps.setString(2, modifiedBy);
			ps.setTimestamp(3, new Timestamp(currentTime));
			ps.setString(4, userId);

			int count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("No row updated");
			}

			connection.commit();
		}
		catch (Exception e) {
			connection.rollback();
			if (SQLException.class.isInstance(e)) {
				throw SQLException.class.cast(e);
			}
			else {
				throw new SQLException(e);
			}
		}
		finally {
			DBConnectionManager.getInstance().freeConnection(connection);
		}
	}

	@Override
	public boolean isUserDisabled(String userId) throws SQLException {
		Connection connection = DBConnectionManager.getInstance().getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(Q_SELECT_DISABLED);
			ps.setString(1, userId);

			rs = ps.executeQuery();

			// If no record is found, the user is considered disabled
			boolean result = true;
			if (rs.next()) {
				String value = rs.getString(1);

				result = (value != null && value.equalsIgnoreCase("1"));
			}

			if (LOG.isDebugEnabled()) LOG.debug(String.format("Determined disabled status for %s: %b", userId, result));

			return result;
		}
		finally {
			closeLocallyManagedResources(rs, ps);
			DBConnectionManager.getInstance().freeConnection(connection);
		}
	}

	@Override
	public void insertUserTrackingData(String userId, String modifiedBy) throws SQLException {
		Connection connection = DBConnectionManager.getInstance().getConnection();
		try {
			connection.setAutoCommit(false);

			insertUserTrackingData(connection, userId, modifiedBy);

			connection.commit();
		}
		catch (Exception e) {
			connection.rollback();
			if (SQLException.class.isInstance(e)) {
				throw SQLException.class.cast(e);
			}
			else {
				throw new SQLException(e);
			}
		}
		finally {
			DBConnectionManager.getInstance().freeConnection(connection);
		}
	}

	private void insertUserTrackingData(Connection connection, String userId, String modifiedBy) throws SQLException {
		PreparedStatement ps = null;
		long currentTime = System.currentTimeMillis();
		try {
			ps = connection.prepareStatement(Q_INSERT_TRACKING);
			ps.setString(1, userId);
			ps.setTimestamp(2, new Timestamp(currentTime));
			ps.setString(3, modifiedBy);
			ps.setTimestamp(4, new Timestamp(currentTime));
			ps.setTimestamp(5, new Timestamp(currentTime));

			int count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException(String.format("Failed to insert tracking data for %s: no row updated", userId));
			}

			if (LOG.isDebugEnabled()) LOG.debug(String.format("Inserted new tracking data for %s", userId));
		}
		finally {
			closeLocallyManagedStatement(ps);
		}
	}

	@Override
	public void updateLastLoginDate(String userId, Date lastLoginDate) throws SQLException {
		Connection connection = DBConnectionManager.getInstance().getConnection();
		PreparedStatement ps = null;
		try {
			connection.setAutoCommit(false);

			if (isTrackingRecordThere(connection, userId)) {
				ps = connection.prepareStatement(Q_UPDATE_LAST_LOGIN);
				ps.setTimestamp(1, new Timestamp(lastLoginDate.getTime()));
				ps.setString(2, userId);

				int count = ps.executeUpdate();
				if (count < 1) {
					throw new SQLException(String.format("Failed to update last login for %s: no row updated", userId));
				}

				connection.commit();

				if (LOG.isDebugEnabled()) LOG.debug(String.format("Updated last login for %s", userId));
			}
		}
		catch (Exception e) {
			connection.rollback();
			if (SQLException.class.isInstance(e)) {
				throw SQLException.class.cast(e);
			}
			else {
				throw new SQLException(e);
			}
		}
		finally {
			closeLocallyManagedStatement(ps);
			DBConnectionManager.getInstance().freeConnection(connection);
		}
	}

	@Override
	public void updateLastModifiedDetails(String userId, Date lastModifiedDate, String modifiedBy) throws SQLException {
		Connection connection = DBConnectionManager.getInstance().getConnection();
		PreparedStatement ps = null;
		try {
			connection.setAutoCommit(false);

			insertTrackingRecordIfNotThere(connection, userId, modifiedBy);

			ps = connection.prepareStatement(Q_UPDATE_LAST_MODIFIED);
			ps.setTimestamp(1, new Timestamp(lastModifiedDate.getTime()));
			ps.setString(2, modifiedBy);
			ps.setString(3, userId);

			int count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException(String.format("Failed to update last modified details for %s: no row updated", userId));
			}

			connection.commit();

			if (LOG.isDebugEnabled()) LOG.debug(String.format("Updated last modified details for %s", userId));
		}
		catch (Exception e) {
			connection.rollback();
			if (SQLException.class.isInstance(e)) {
				throw SQLException.class.cast(e);
			}
			else {
				throw new SQLException(e);
			}
		}
		finally {
			closeLocallyManagedStatement(ps);
			DBConnectionManager.getInstance().freeConnection(connection);
		}
	}

	private void insertTrackingRecordIfNotThere(Connection connection, String userId, String modifiedBy) throws SQLException {
		if (!isTrackingRecordThere(connection, userId)) {
			insertUserTrackingData(connection, userId, modifiedBy);
		}
	}

	@Override
	public boolean hasUserTrackingRecord(String userId) throws SQLException {
		Connection connection = DBConnectionManager.getInstance().getConnection();
		try {
			return isTrackingRecordThere(connection, userId);
		}
		finally {
			DBConnectionManager.getInstance().freeConnection(connection);
		}
	}

	private boolean isTrackingRecordThere(Connection connection, String userId) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(Q_SELECT_CHECK);
			ps.setString(1, userId);
			rs = ps.executeQuery();

			boolean result = rs.next();

			return result;
		}
		finally {
			closeLocallyManagedResources(rs, ps);
		}
	}

}
