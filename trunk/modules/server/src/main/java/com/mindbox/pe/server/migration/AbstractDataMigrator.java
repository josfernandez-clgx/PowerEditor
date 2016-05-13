package com.mindbox.pe.server.migration;

import static com.mindbox.pe.common.LogUtil.logDebug;
import static com.mindbox.pe.common.LogUtil.logError;
import static com.mindbox.pe.common.LogUtil.logInfo;
import static com.mindbox.pe.common.UtilBase.isEmptyAfterTrim;
import static com.mindbox.pe.common.VersionUtil.isNewer;
import static com.mindbox.pe.server.db.DBUtil.closeLocallyManagedResources;
import static com.mindbox.pe.server.db.DBUtil.closeLocallyManagedStatement;
import static com.mindbox.pe.server.db.DBUtil.rollBackLocallyManagedConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.mindbox.pe.server.db.DBConnectionManager;

abstract class AbstractDataMigrator implements DataMigrator {

	private static final String Q_GET_PE_VERSION = "select pe_version from MB_PE_VERSION";
	private static final String Q_SET_PE_VERSION = "insert into MB_PE_VERSION (pe_version) values (?)";
	private static final String Q_DELETE_PE_VERSION = "delete from MB_PE_VERSION";

	protected final Logger log = Logger.getLogger(getClass());

	private String getCurrentVersion(final Connection connection) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(Q_GET_PE_VERSION);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			}
			else {
				return null;
			}
		}
		finally {
			closeLocallyManagedResources(rs, ps);
		}
	}

	@Override
	public final void migrateData(final String targetVersion) throws SQLException {
		logDebug(log, "---> migrateData: %s", targetVersion);
		Connection connection = DBConnectionManager.getInstance().getConnection();
		PreparedStatement ps = null;
		try {
			String currentVersion = getCurrentVersion(connection);
			if (isEmptyAfterTrim(currentVersion)) {
				currentVersion = "0.0.0";
			}
			if (isNewer(targetVersion, currentVersion)) {
				logInfo(log, "target version (%s) is newer than the current version (%s); migrating...", targetVersion, currentVersion);
				connection.setAutoCommit(false);

				migrateData(currentVersion, targetVersion, connection);

				// Last step is to set the version to the target version
				ps = connection.prepareStatement(Q_DELETE_PE_VERSION);
				ps.executeUpdate();
				ps.close();
				ps = null;

				ps = connection.prepareStatement(Q_SET_PE_VERSION);
				ps.setString(1, targetVersion);
				int count = ps.executeUpdate();
				if (count < 1) {
					throw new SQLException("No PE version row updated");
				}

				connection.commit();
				logDebug(log, "<--- migrateData");
			}
			else {
				logInfo(log, "target version (%s) is not newer than the current version (%s). Done.", targetVersion, currentVersion);
			}
		}
		catch (Exception e) {
			logError(log, e, "Failed to migrated data for version %s", targetVersion);
			rollBackLocallyManagedConnection(connection);
		}
		finally {
			closeLocallyManagedStatement(ps);
			DBConnectionManager.getInstance().freeConnection(connection);
		}
	}

	protected abstract void migrateData(String currentVersion, String targetVersion, Connection connection) throws SQLException;
}
