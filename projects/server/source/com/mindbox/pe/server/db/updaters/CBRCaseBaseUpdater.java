/*
 * Created on 2004. 10. 07.
 */
package com.mindbox.pe.server.db.updaters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.mindbox.pe.model.CBRCaseBase;
import com.mindbox.pe.model.CBRCaseClass;
import com.mindbox.pe.model.CBRScoringFunction;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.server.db.DateSynonymReferenceUpdater;

/**
 * CBR Case Base Updater.
 * Responsible for updating case bases in DB.
 * @author Inna Nill
 * @since PowerEditor 4.1.0
 */
public class CBRCaseBaseUpdater extends DateSynonymReferenceUpdater {

	private static final String Q_INSERT_CBR_CASE_BASE = "insert into MB_CBR_CASE_BASE (case_base_id,display_name,case_class_id,index_file, scoring_function_id,naming_attribute,match_threshold,maximum_matches,notes) values (?,?,?,?,?,?,?,?,?)";

	private static final String Q_DELETE_CBR_CASE_BASE = "delete from MB_CBR_CASE_BASE where case_base_id=?";

	private static final String Q_UPDATE_CBR_CASE_BASE = "update MB_CBR_CASE_BASE set display_name=?,case_class_id=?,index_file=?,scoring_function_id=?,naming_attribute=?,match_threshold=?,maximum_matches=?,notes=? where case_base_id=?";

	private static final String Q_INSERT_CBR_CASE_BASE_DATE_SYNONYM = "insert into MB_CBR_CASE_BASE_DATE_SYN (case_base_id,effective_synonym_id,expiration_synonym_id) values (?,?,?)";

	private static final String Q_UPDATE_CBR_CASE_BASE_DATE_SYNONYM = "update MB_CBR_CASE_BASE_DATE_SYN set effective_synonym_id=?,expiration_synonym_id=? where case_base_id=?";

	private static final String Q_REPLACE_ALL_CBR_CASE_BASE_EFFECTIVE_DATE_SYNONYM = "update MB_CBR_CASE_BASE_DATE_SYN set effective_synonym_id=? where effective_synonym_id in (" + IN_CLAUSE_LIST_HOLDER + ")";

	private static final String Q_REPLACE_ALL_CBR_CASE_BASE_EXPIRATION_DATE_SYNONYM = "update MB_CBR_CASE_BASE_DATE_SYN set expiration_synonym_id=? where expiration_synonym_id in (" + IN_CLAUSE_LIST_HOLDER + ")";

	private static final String Q_DELETE_CBR_CASE_BASE_DATE_SYNONYM = "delete from MB_CBR_CASE_BASE_DATE_SYN where case_base_id=?";

	public CBRCaseBaseUpdater() {
	}

	public CBRCaseBaseUpdater(Connection conn) {
		setConnection(conn);
	}

	public void insertCBRCaseBase(CBRCaseBase caseBase) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			doInsert(conn, caseBase);

			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to insert case base: " + caseBase.getID() + ",name=" + caseBase.getName(), ex);
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

	/**
	 * Delete CBR Case Base from the db.
	 * @param caseBaseID
	 * @throws SQLException
	 */
	public void deleteCBRCaseBase(int caseBaseID) throws SQLException {
		logger.debug(">>> deleteCBRCaseBase: " + caseBaseID);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			// delete from case base table
			ps = conn.prepareStatement(Q_DELETE_CBR_CASE_BASE);
			ps.setInt(1, caseBaseID);

			int count = ps.executeUpdate();
			logger.debug("   deleteCBRCaseBase: deleted " + count + " case base rows");
			if (count < 1) { throw new SQLException("no row was deleted from the case base table"); }
			ps.close();
			ps = null;
			
			ps = conn.prepareStatement(Q_DELETE_CBR_CASE_BASE_DATE_SYNONYM);
			ps.setInt(1, caseBaseID);
			count = ps.executeUpdate();
			logger.debug("    deleteCBRCaseBase: removed " + count + " date-synonym mappings");
			ps.close();
			ps = null;

			// Now delete case base's attributes.
			CBRAttributeUpdater attrUpdater = new CBRAttributeUpdater();
			attrUpdater.deleteAllCBRAttributes(caseBaseID);

			// Now delete the case base's cases.
			CBRCaseUpdater caseUpdater = new CBRCaseUpdater();
			caseUpdater.deleteAllCBRCases(caseBaseID);

			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to delete case base: " + caseBaseID, ex);
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

	public void updateCBRCaseBase(CBRCaseBase caseBase) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			// update case base
			ps = conn.prepareStatement(Q_UPDATE_CBR_CASE_BASE);
			ps.setString(1, caseBase.getName());
			CBRCaseClass cc = caseBase.getCaseClass();
			if (cc == null)
				ps.setNull(2, Types.INTEGER);
			else
				ps.setInt(2, cc.getId());
			ps.setString(3, caseBase.getIndexFile());
			CBRScoringFunction sf = caseBase.getScoringFunction();
			if (sf == null)
				ps.setNull(4, Types.INTEGER);
			else
				ps.setInt(4, sf.getId());
			ps.setString(5, caseBase.getNamingAttribute());
			ps.setInt(6, caseBase.getMatchThreshold());
			ps.setInt(7, caseBase.getMaximumMatches());
			ps.setString(8, caseBase.getDescription());
			ps.setInt(9, caseBase.getId());
			int count = ps.executeUpdate();
			if (count < 1) { throw new SQLException("no row was updated"); }
			ps.close();
			ps = null;

			logger.debug(" updateCBRCaseBase: updating date synonym " + caseBase.getEffectiveDate() + ","
					+ caseBase.getExpirationDate());
			ps = conn.prepareStatement(Q_UPDATE_CBR_CASE_BASE_DATE_SYNONYM);
			ps.setInt(1, (caseBase.getEffectiveDate() == null ? -1 : caseBase.getEffectiveDate().getID()));
			ps.setInt(2, (caseBase.getExpirationDate() == null ? -1 : caseBase.getExpirationDate().getID()));
			ps.setInt(3, caseBase.getID());
			count = ps.executeUpdate();
			if (count < 1) {
				// Maybe couldn't update it because it's not there in the first place...
				// Try to insert one.
				logger.debug("    insertCBRCaseBase: inserting activation date synonym");
				ps = conn.prepareStatement(Q_INSERT_CBR_CASE_BASE_DATE_SYNONYM);
				ps.setInt(1, caseBase.getID());
				ps.setInt(2, (caseBase.getEffectiveDate() == null ? -1 : caseBase.getEffectiveDate().getID()));
				ps.setInt(3, (caseBase.getExpirationDate() == null ? -1 : caseBase.getExpirationDate().getID()));
				count = ps.executeUpdate();
				if (count < 1) { throw new SQLException("Failed to update or insert the casebase-date synonym row"); }
			}

			ps.close();
			ps = null;

			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to update case base: " + caseBase.getId() + ",name=" + caseBase.getName(), ex);
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

	public void cloneCBRCaseBase(Connection conn, CBRCaseBase caseBase) throws SQLException {
		doInsert(conn, caseBase);
	}

	private void doInsert(Connection conn, CBRCaseBase caseBase) throws SQLException {
		logger.info(">>> doInsert");
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_INSERT_CBR_CASE_BASE);
			ps.setInt(1, caseBase.getId());
			ps.setString(2, caseBase.getName());
			CBRCaseClass cc = caseBase.getCaseClass();
			if (cc == null)
				ps.setNull(3, Types.INTEGER);
			else
				ps.setInt(3, cc.getId());
			ps.setString(4, caseBase.getIndexFile());
			CBRScoringFunction sf = caseBase.getScoringFunction();
			if (sf == null)
				ps.setNull(5, Types.INTEGER);
			else
				ps.setInt(5, sf.getId());
			ps.setString(6, caseBase.getNamingAttribute());
			ps.setInt(7, caseBase.getMatchThreshold());
			ps.setInt(8, caseBase.getMaximumMatches());
			ps.setString(9, caseBase.getDescription());

			int count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("no row was inserted");
			}
			ps.close();
			ps = null;

			logger.debug("    insertCBRCaseBase: inserting date synonym");
			ps = conn.prepareStatement(Q_INSERT_CBR_CASE_BASE_DATE_SYNONYM);
			ps.setInt(1, caseBase.getID());
			ps.setInt(2, (caseBase.getEffectiveDate() == null ? -1 : caseBase.getEffectiveDate().getID()));
			ps.setInt(3, (caseBase.getExpirationDate() == null ? -1 : caseBase.getExpirationDate().getID()));
			count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("Failed to insert the casebase-date synonym row");
			}
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to insert case base: " + caseBase.getID() + ",name=" + caseBase.getName(), ex);
			if (ex instanceof SQLException) {
				throw (SQLException) ex;
			}
			else {
				throw new SQLException(ex.getMessage());
			}
		}
		finally {
			if (ps != null)
				ps.close();
		}
	}

	public void replaceDateSynonymReferences(DateSynonym[] toBeReplaced, DateSynonym replacement) throws SQLException {
		replaceDateSynonymReferencesInIntersectionTable(toBeReplaced, replacement, Q_REPLACE_ALL_CBR_CASE_BASE_EFFECTIVE_DATE_SYNONYM, 
				Q_REPLACE_ALL_CBR_CASE_BASE_EXPIRATION_DATE_SYNONYM);
	}
}