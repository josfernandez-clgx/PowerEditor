/*
 * Created on 2004. 10. 19.
 */
package com.mindbox.pe.server.db.updaters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.cbr.CBRAttributeValue;
import com.mindbox.pe.model.cbr.CBRCase;
import com.mindbox.pe.model.cbr.CBRCaseAction;
import com.mindbox.pe.server.db.DateSynonymReferenceUpdater;

/**
 * CBR Case Updater.
 * Responsible for updating cases in DB.
 * @author Inna Nill
 * @since PowerEditor 4.1.0
 */
public class CBRCaseUpdater extends DateSynonymReferenceUpdater {

	private static final String Q_INSERT_CBR_CASE = "insert into MB_CBR_CASE (case_id, case_base_id, display_name, notes) " + "values (?,?,?,?)";

	private static final String Q_DELETE_CBR_CASE = "delete from MB_CBR_CASE where case_id=?";

	private static final String Q_UPDATE_CBR_CASE = "update MB_CBR_CASE set " + "case_base_id=?, display_name=?, notes=? where case_id=?";

	private static final String Q_INSERT_CBR_ATTRIBUTE_VALUE = "insert into MB_CBR_ATTRIBUTE_VALUE (attribute_value_id, case_id, attribute_id, match_contribution, "
			+ "mismatch_penalty, display_name, notes) values (?,?,?,?,?,?,?)";

	private static final String Q_DELETE_CBR_ATTRIBUTE_VALUES = "delete from MB_CBR_ATTRIBUTE_VALUE where case_id=?";

	private static final String Q_INSERT_CBR_CASE_ACTIONS_MAPPING = "insert into MB_CBR_CASE_ACTIONS_MAPPING (case_id, action_id, action_order) values (?,?,?)";

	private static final String Q_DELETE_CBR_CASE_ACTIONS_MAPPING = "delete from MB_CBR_CASE_ACTIONS_MAPPING where case_id = ?";

	private static final String Q_INSERT_CBR_CASE_DATE_SYNONYM = "insert into MB_CBR_CASE_DATE_SYNONYM (case_id, effective_synonym_id,expiration_synonym_id) values (?,?,?)";

	private static final String Q_DELETE_CBR_CASE_DATE_SYNONYM = "delete from MB_CBR_CASE_DATE_SYNONYM where case_id = ?";

	private static final String Q_REPLACE_ALL_CBR_CASE_EFFECTIVE_DATE_SYNONYM = "update MB_CBR_CASE_DATE_SYNONYM set effective_synonym_id=? where effective_synonym_id in ("
			+ IN_CLAUSE_LIST_HOLDER + ")";

	private static final String Q_REPLACE_ALL_CBR_CASE_EXPIRATION_DATE_SYNONYM = "update MB_CBR_CASE_DATE_SYNONYM set expiration_synonym_id=? where expiration_synonym_id in ("
			+ IN_CLAUSE_LIST_HOLDER + ")";

	private static final String Q_SELECT_CBR_CASES_FOR_CASE_BASE = "select case_id from MB_CBR_CASE where case_base_id=?";

	private static final String Q_DELETE_ALL_CASES_FOR_CASE_BASE = "delete from MB_CBR_CASE where case_base_id=?";

	private final Logger logger = Logger.getLogger(CBRCaseUpdater.class);


	public CBRCaseUpdater() {
	}

	public CBRCaseUpdater(Connection conn) {
		setConnection(conn);
	}

	public void cloneCBRCase(Connection conn, CBRCase cbrCase) throws SQLException {
		doInsert(conn, cbrCase);
	}

	/**
	 * Delete all cases for a given case base id.
	 * @param caseBaseID caseBaseID
	 * @throws SQLException on error
	 */
	public void deleteAllCBRCases(int caseBaseID) throws SQLException {
		logger.debug(">>> deleteAllCBRAttributes for: " + caseBaseID);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		//PreparedStatement ps2 = null;
		ResultSet rs = null;
		int count = 0;
		try {
			conn.setAutoCommit(false);

			// first select all attributes for the given case base
			ps = conn.prepareStatement(Q_SELECT_CBR_CASES_FOR_CASE_BASE);
			ps.setInt(1, caseBaseID);
			rs = ps.executeQuery();
			while (rs.next()) {
				deleteAssociated(conn, rs.getInt(1));
			}
			ps.close();

			ps = conn.prepareStatement(Q_DELETE_ALL_CASES_FOR_CASE_BASE);
			ps.setInt(1, caseBaseID);
			count = ps.executeUpdate();
			logger.debug("   deleteAllCBRAttributes: deleted " + count + " attributes");
			ps.close();

			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to delete attribute for case base: " + caseBaseID, ex);
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

	public void deleteAssociated(Connection conn, int caseID) throws SQLException {
		logger.debug(">>> deleteCBRCase: " + caseID);
		// Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			// conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_DELETE_CBR_ATTRIBUTE_VALUES);
			ps.setInt(1, caseID);
			int count = ps.executeUpdate();
			logger.debug("   deleteCBRCase: deleted " + count + " attribute value rows");
			ps.close();

			ps = conn.prepareStatement(Q_DELETE_CBR_CASE_ACTIONS_MAPPING);
			ps.setInt(1, caseID);
			count = ps.executeUpdate();
			logger.debug("   deleteCBRCase: deleted " + count + " case action mapping rows");
			ps.close();

			// Don't bother with this until we put in a way to create ActivationLabels in the UI
			// screen, along with the cases and case bases.
			/*
			 int date synonymId = -1;
			 ps = conn.prepareStatement(Q_SELECT_CBR_CASE_LABEL_MAPPING);
			 ps.setInt(1, caseID);
			 rs = ps.executeQuery();
			 if ( rs.next() ) {
			 date synonymId = rs.getInt(1);
			 }
			 ps.close();
			 */

			ps = conn.prepareStatement(Q_DELETE_CBR_CASE_DATE_SYNONYM);
			ps.setInt(1, caseID);
			count = ps.executeUpdate();
			logger.debug("   deleteCBRCase: deleted " + count + " case date synonym mapping rows");
			ps.close();

			// Don't delete, until we put in a way to create these in the UI screen along
			// with the cases and case bases.
			/*
			 ActivationLabelUpdater updater = new ActivationLabelUpdater();
			 if ( date synonymId > 0) {
			 updater.deleteActivationLabel(date synonymId);
			 }
			 */
			ps = null;
		}
		catch (Exception ex) {
			logger.error("Failed to delete items associated with case: " + caseID, ex);
			if (ex instanceof SQLException) {
				throw (SQLException) ex;
			}
			else {
				throw new SQLException(ex.getMessage());
			}
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	/**
	 * Delete CBR Case from the db.
	 * @param caseID caseID
	 * @throws SQLException on error
	 */
	public void deleteCBRCase(int caseID) throws SQLException {
		logger.debug(">>> deleteCBRCase: " + caseID);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			// delete from case base table
			ps = conn.prepareStatement(Q_DELETE_CBR_CASE);
			ps.setInt(1, caseID);
			int count = ps.executeUpdate();
			logger.debug("   deleteCBRCase: deleted " + count + " case rows");
			if (count < 1) {
				throw new SQLException("no row was deleted from the case table");
			}
			ps.close();

			ps = conn.prepareStatement(Q_DELETE_CBR_ATTRIBUTE_VALUES);
			ps.setInt(1, caseID);
			count = ps.executeUpdate();
			logger.debug("   deleteCBRCase: deleted " + count + " attribute value rows");
			ps.close();

			ps = conn.prepareStatement(Q_DELETE_CBR_CASE_ACTIONS_MAPPING);
			ps.setInt(1, caseID);
			count = ps.executeUpdate();
			logger.debug("   deleteCBRCase: deleted " + count + " case action mapping rows");
			ps.close();

			// Don't bother with this until we put in a way to create ActivationLabels in the UI
			// screen, along with the cases and case bases.
			/*
			 int date synonymId = -1;
			 ps = conn.prepareStatement(Q_SELECT_CBR_CASE_LABEL_MAPPING);
			 ps.setInt(1, caseID);
			 rs = ps.executeQuery();
			 if ( rs.next() ) {
			 date synonymId = rs.getInt(1);
			 }
			 ps.close();
			 */

			ps = conn.prepareStatement(Q_DELETE_CBR_CASE_DATE_SYNONYM);
			ps.setInt(1, caseID);
			count = ps.executeUpdate();
			logger.debug("   deleteCBRCase: deleted " + count + " case date synonym mapping rows");
			ps.close();

			// Don't delete, until we put in a way to create these in the UI screen along
			// with the cases and case bases.
			/*
			 ActivationLabelUpdater updater = new ActivationLabelUpdater();
			 if ( date synonymId > 0) {
			 updater.deleteActivationLabel(date synonymId);
			 }
			 */
			ps = null;

			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to delete case: " + caseID, ex);
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

	private void doInsert(Connection conn, CBRCase cbrCase) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_INSERT_CBR_CASE);
			ps.setInt(1, cbrCase.getId());
			ps.setInt(2, cbrCase.getCaseBase().getID());
			ps.setString(3, cbrCase.getName());
			ps.setString(4, cbrCase.getDescription());

			logger.info("About to insert case in insertCBRCase: " + cbrCase);
			int count = ps.executeUpdate();
			logger.info("Finished executeUpdate. count = " + count);
			if (count < 1) {
				throw new SQLException("no row was inserted");
			}
			ps.close();

			ps = conn.prepareStatement(Q_INSERT_CBR_ATTRIBUTE_VALUE);
			Iterator<CBRAttributeValue> it = cbrCase.getAttributeValues().iterator();
			while (it.hasNext()) {
				CBRAttributeValue attrVal = it.next();
				logger.info("Going to insert attributevalue: " + attrVal);
				ps.setInt(1, attrVal.getId());
				ps.setInt(2, cbrCase.getId());
				ps.setInt(3, attrVal.getAttribute().getId());
				ps.setInt(4, attrVal.getMatchContribution());
				ps.setInt(5, attrVal.getMismatchPenalty());
				ps.setString(6, UtilBase.trim(attrVal.getName()));
				ps.setString(7, UtilBase.trim(attrVal.getDescription()));
				count = ps.executeUpdate();
				if (count < 1) {
					throw new SQLException("no CBR_ATTRIBUTE_VALUE row was inserted");
				}
			}
			ps.close();

			ps = conn.prepareStatement(Q_INSERT_CBR_CASE_ACTIONS_MAPPING);
			Iterator<CBRCaseAction> it2 = cbrCase.getCaseActions().iterator();
			int action_order = 1;
			while (it2.hasNext()) {
				CBRCaseAction action = it2.next();
				ps.setInt(1, cbrCase.getId());
				ps.setInt(2, action.getId());
				ps.setInt(3, action_order++);
				count = ps.executeUpdate();
				if (count < 1) {
					throw new SQLException("no CBR_CASE_ACTIONS_MAPPING row was inserted");
				}
			}
			ps.close();
			logger.debug("   isnertCBRCase: added " + cbrCase.getCaseActions() + " action mapping rows");

			ps = conn.prepareStatement(Q_INSERT_CBR_CASE_DATE_SYNONYM);
			ps.setInt(1, cbrCase.getId());
			ps.setInt(2, (cbrCase.getEffectiveDate() == null ? -1 : cbrCase.getEffectiveDate().getID()));
			ps.setInt(3, (cbrCase.getExpirationDate() == null ? -1 : cbrCase.getExpirationDate().getID()));
			count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("no CBR_CASE_LABEL_MAPPING row was inserted");
			}
			ps.close();

			ps.close();
			ps = null;
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to insert attribute: " + cbrCase.getID() + ",name=" + cbrCase.getName(), ex);
			if (ex instanceof SQLException) {
				throw (SQLException) ex;
			}
			else {
				logger.error("exception: " + ex.getMessage());
				throw new SQLException(ex.getMessage());
			}
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	public void insertCBRCase(CBRCase cbrCase) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			doInsert(conn, cbrCase);

			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to insert case: " + cbrCase.getID() + ",name=" + cbrCase.getName(), ex);
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

	@Override
	public void replaceDateSynonymReferences(DateSynonym[] toBeReplaced, DateSynonym replacement) throws SQLException {
		replaceDateSynonymReferencesInIntersectionTable(toBeReplaced, replacement, Q_REPLACE_ALL_CBR_CASE_EFFECTIVE_DATE_SYNONYM, Q_REPLACE_ALL_CBR_CASE_EXPIRATION_DATE_SYNONYM);
	}

	public void updateCBRCase(CBRCase cbrCase) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			// update case
			ps = conn.prepareStatement(Q_UPDATE_CBR_CASE);
			ps.setInt(1, cbrCase.getCaseBase().getID());
			ps.setString(2, cbrCase.getName());
			ps.setString(3, cbrCase.getDescription());
			ps.setInt(4, cbrCase.getId());

			int count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("no row was updated");
			}

			ps.close();
			ps = conn.prepareStatement(Q_DELETE_CBR_ATTRIBUTE_VALUES);
			ps.setInt(1, cbrCase.getId());
			count = ps.executeUpdate();
			logger.debug("   updateCBRCase: deleted " + count + " attribute value rows");
			ps.close();

			ps = conn.prepareStatement(Q_INSERT_CBR_ATTRIBUTE_VALUE);
			Iterator<CBRAttributeValue> it = cbrCase.getAttributeValues().iterator();
			while (it.hasNext()) {
				CBRAttributeValue attrVal = it.next();
				ps.setInt(1, attrVal.getId());
				ps.setInt(2, cbrCase.getId());
				ps.setInt(3, attrVal.getAttribute().getId());
				ps.setInt(4, attrVal.getMatchContribution());
				ps.setInt(5, attrVal.getMismatchPenalty());
				ps.setString(6, UtilBase.trim(attrVal.getName()));
				ps.setString(7, UtilBase.trim(attrVal.getDescription()));
				count = ps.executeUpdate();
				if (count < 1) {
					throw new SQLException("no CBR_ATTRIBUTE_VALUE row was inserted");
				}
			}
			ps.close();

			ps = conn.prepareStatement(Q_DELETE_CBR_CASE_ACTIONS_MAPPING);
			ps.setInt(1, cbrCase.getId());
			count = ps.executeUpdate();
			ps.close();
			logger.debug("   updateCBRCase: deleted " + count + " action mapping rows");

			ps = conn.prepareStatement(Q_INSERT_CBR_CASE_ACTIONS_MAPPING);
			Iterator<CBRCaseAction> it2 = cbrCase.getCaseActions().iterator();
			int action_order = 1;
			while (it2.hasNext()) {
				CBRCaseAction action = it2.next();
				ps.setInt(1, cbrCase.getId());
				ps.setInt(2, action.getId());
				ps.setInt(3, action_order++);
				count = ps.executeUpdate();
				if (count < 1) {
					throw new SQLException("no CBR_CASE_ACTIONS_MAPPING row was inserted");
				}
			}
			ps.close();
			logger.debug("   updateCBRCase: added " + cbrCase.getCaseActions() + " action mapping rows");

			ps = conn.prepareStatement(Q_DELETE_CBR_CASE_DATE_SYNONYM);
			ps.setInt(1, cbrCase.getId());
			count = ps.executeUpdate();
			logger.debug("   deleteCBRCase: deleted " + count + " case date synonym mapping rows");
			ps.close();

			ps = conn.prepareStatement(Q_INSERT_CBR_CASE_DATE_SYNONYM);
			ps.setInt(1, cbrCase.getId());
			ps.setInt(2, (cbrCase.getEffectiveDate() == null ? -1 : cbrCase.getEffectiveDate().getID()));
			ps.setInt(3, (cbrCase.getExpirationDate() == null ? -1 : cbrCase.getExpirationDate().getID()));
			count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("no CBR_CASE_LABEL_MAPPING row was inserted");
			}
			ps.close();
			ps = null;

			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to update case: " + cbrCase.getId() + ",name=" + cbrCase.getName(), ex);
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