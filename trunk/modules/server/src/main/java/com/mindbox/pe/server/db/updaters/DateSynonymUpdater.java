/*
 * Created on 2004. 12. 08.
 */
package com.mindbox.pe.server.db.updaters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import com.mindbox.pe.server.db.DBUtil;

/**
 * Date synonym Updater.
 * Responsible for updating date synonyms in DB.
 * @author kim
 * @since PowerEditor  4.2.0
 */
public class DateSynonymUpdater extends AbstractUpdater {

	private static final String Q_INSERT_DATE_SYNONYM = 
		"insert into MB_DATE_SYNONYM (synonym_id,synonym_name,synonym_desc,synonym_date,is_named) values (?,?,?,?,?)";

	private static final String Q_DELETE_DATE_SYNONYM = "delete from MB_DATE_SYNONYM where synonym_id=?";

	private static final String Q_UPDATE_DATE_SYNONYM = "update MB_DATE_SYNONYM set synonym_name=?,synonym_desc=?,synonym_date=?,is_named=? where synonym_id=?";

	public DateSynonymUpdater() {
	}

	public DateSynonymUpdater(Connection conn) {
		setConnection(conn);
	}

	public void insertDateSynonym(int synonymID, String name, String desc, Date date, boolean isNamed) throws SQLException {
		logger.debug(">>> insertDateSynonym: " + synonymID + ", " + name + ", " + date + ", " + isNamed);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_INSERT_DATE_SYNONYM);
			ps.setInt(1, synonymID);
			ps.setString(2, name);
			ps.setString(3, desc);
			DBUtil.setDateValue(ps, 4, date);
			ps.setBoolean(5, isNamed);

			int count = ps.executeUpdate();
			if (count < 1) { throw new SQLException("no row was inserted"); }

			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to insert date synonym: " + synonymID + ",name=" + name, ex);
			if (ex instanceof SQLException) {
				throw (SQLException) ex;
			}
			else {
				throw new SQLException(ex.getMessage());
			}
		}
		finally {
			if (ps != null) ps.close();
			releaseConnection();
		}
	}

	public void deleteDateSynonym(int synonymID) throws SQLException {
		logger.debug(">>> deleteDateSynonym: " + synonymID);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			// delete from date synonym table
			ps = conn.prepareStatement(Q_DELETE_DATE_SYNONYM);
			ps.setInt(1, synonymID);

			int count = ps.executeUpdate();
			logger.debug("   deleteDateSynonym: deleted " + count + " date synonym rows");
			if (count < 1) { throw new SQLException("no row was deleted from the date synonym table"); }
			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to delete date synonym: " + synonymID, ex);
			if (ex instanceof SQLException) {
				throw (SQLException) ex;
			}
			else {
				throw new SQLException(ex.getMessage());
			}
		}
		finally {
			if (ps != null) ps.close();
			releaseConnection();
		}
	}

	public void updateDateSynonym(int synonymID, String name, String desc, Date date, boolean isNamed) throws SQLException {
		logger.debug(">>> updateDateSynonym: " + synonymID + ", " + name + ", " + date + ", " + isNamed);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			// update date synonym
			ps = conn.prepareStatement(Q_UPDATE_DATE_SYNONYM);
			ps.setString(1, name);
			ps.setString(2, desc);
			DBUtil.setDateValue(ps, 3, date);
			ps.setBoolean(4, isNamed);
			ps.setInt(5, synonymID);

			int count = ps.executeUpdate();
			if (count < 1) { throw new SQLException("no row was updated"); }
			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to update date synonym: " + synonymID + ",name=" + name, ex);
			if (ex instanceof SQLException) {
				throw (SQLException) ex;
			}
			else {
				throw new SQLException(ex.getMessage());
			}
		}
		finally {
			if (ps != null) ps.close();
			releaseConnection();
		}
	}

}