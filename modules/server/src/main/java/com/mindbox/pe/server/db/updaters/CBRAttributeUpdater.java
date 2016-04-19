/*
 * Created on 2004. 10. 07.
 */
package com.mindbox.pe.server.db.updaters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.communication.ValidationException;
import com.mindbox.pe.model.cbr.CBRAttribute;
import com.mindbox.pe.model.cbr.CBRAttributeType;
import com.mindbox.pe.model.cbr.CBREnumeratedValue;
import com.mindbox.pe.model.cbr.CBRValueRange;

/**
 * CBR Case Base Updater.
 * Responsible for updating case bases in DB.
 * @author Inna Nill
 * @since PowerEditor 4.1.0
 */
public class CBRAttributeUpdater extends AbstractUpdater {

	private static final String Q_INSERT_CBR_ATTRIBUTE = "insert into MB_CBR_ATTRIBUTE (attribute_id, case_base_id, display_name,attribute_type_id,"
			+ "match_contribution,mismatch_penalty,absence_penalty,lowest_value,highest_value,match_interval,value_range_id,notes) "
			+ "values (?,?,?,?,?,?,?,?,?,?,?,?)";

	private static final String Q_DELETE_CBR_ATTRIBUTE = "delete from MB_CBR_ATTRIBUTE where attribute_id=?";

	private static final String Q_UPDATE_CBR_ATTRIBUTE = "update MB_CBR_ATTRIBUTE set " + "case_base_id=?, display_name=?,attribute_type_id=?,"
			+ "match_contribution=?,mismatch_penalty=?,absence_penalty=?,lowest_value=?,highest_value=?,"
			+ "match_interval=?,value_range_id=?,notes=? where attribute_id=?";

	private static final String Q_INSERT_CBR_ENUMERATED_VALUE = "insert into MB_CBR_ENUMERATED_VALUE (attribute_id, value_string) values (?,?)";

	private static final String Q_DELETE_CBR_ENUMERATED_VALUES = "delete from MB_CBR_ENUMERATED_VALUE where attribute_id=?";

	private static final String Q_SELECT_CBR_ATTRIBUTES_FOR_CASE_BASE = "select attribute_id from MB_CBR_ATTRIBUTE where case_base_id=?";

	private static final String Q_SELECT_CBR_ATTRIBUTE_VALUES_FOR_ATTRIBUTE = "select attribute_value_id from MB_CBR_ATTRIBUTE_VALUE where attribute_id=?";

	private static final String Q_DELETE_ALL_ATTRIBUTES_FOR_CASE_BASE = "delete from MB_CBR_ATTRIBUTE where case_base_id=?";

	public CBRAttributeUpdater() {
	}

	public void insertCBRAttribute(CBRAttribute attribute) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			doInsert(conn, attribute);

			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to insert attribute: " + attribute.getID() + ",name=" + attribute.getName(), ex);
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
			releaseConnection();
		}
	}

	/**
	 * Delete CBR Attribute from the db.
	 * @param attributeID the id of the attribute to delete
	 * @throws SQLException on DB error
	 */
	public void deleteCBRAttribute(int attributeID) throws SQLException, ValidationException {
		logger.debug(">>> deleteCBRAttribute: " + attributeID);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			if (validateAllowDeletion(conn, attributeID)) {
				// delete from attribute table
				ps = conn.prepareStatement(Q_DELETE_CBR_ATTRIBUTE);
				ps.setInt(1, attributeID);
				int count = ps.executeUpdate();
				logger.debug("   deleteCBRAttribute: deleted " + count + " attribute rows");
				if (count < 1) {
					throw new SQLException("no row was deleted from the attribute table");
				}
				ps.close();

				ps = conn.prepareStatement(Q_DELETE_CBR_ENUMERATED_VALUES);
				ps.setInt(1, attributeID);
				count = ps.executeUpdate();
				logger.debug("   deleteCBRAttribute: deleted " + count + " enumerated value rows");
				ps.close();
				ps = null;

				conn.commit();

			}
			else {
				throw new ValidationException();
			}
		}
		catch (ValidationException ex) {
			conn.rollback();
			logger.error("Caught validation exception for " + attributeID, ex);
			throw ex;
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to delete attribute: " + attributeID, ex);
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
			releaseConnection();
		}
	}

	/**
	 * Delete all attributes for a given case base id.
	 * @param caseBaseID
	 * @throws SQLException
	 */
	public void deleteAllCBRAttributes(int caseBaseID) throws SQLException {
		logger.debug(">>> deleteAllCBRAttributes for: " + caseBaseID);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;

		ResultSet rs = null;
		int count = 0;
		try {
			conn.setAutoCommit(false);

			// first select all attributes for the given case base
			ps = conn.prepareStatement(Q_SELECT_CBR_ATTRIBUTES_FOR_CASE_BASE);
			ps.setInt(1, caseBaseID);
			rs = ps.executeQuery();
			//logger.debug("   deleteAllCBRAttributes: selected " + count + " attribute rows");
			//if (count < 1) { throw new SQLException("no row was deleted from the attribute table"); }
			while (rs.next()) {
				ps2 = conn.prepareStatement(Q_DELETE_CBR_ENUMERATED_VALUES);
				ps2.setInt(1, rs.getInt(1));
				count = ps2.executeUpdate();
				logger.debug("   deleteAllCBRAttributes: deleted " + count + " enumerated value rows");
			}
			ps.close();

			ps = conn.prepareStatement(Q_DELETE_ALL_ATTRIBUTES_FOR_CASE_BASE);
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
			if (ps != null)
				ps.close();
			releaseConnection();
		}
	}

	public void updateCBRAttribute(CBRAttribute attribute) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			// update case base
			ps = conn.prepareStatement(Q_UPDATE_CBR_ATTRIBUTE);
			ps.setInt(1, attribute.getCaseBase().getID());
			ps.setString(2, attribute.getName());
			CBRAttributeType at = attribute.getAttributeType();
			if (at == null)
				ps.setNull(3, Types.INTEGER);
			else
				ps.setInt(3, at.getId());
			ps.setInt(4, attribute.getMatchContribution());
			ps.setInt(5, attribute.getMismatchPenalty());
			ps.setInt(6, attribute.getAbsencePenalty());
			ps.setDouble(7, attribute.getLowestValue());
			ps.setDouble(8, attribute.getHighestValue());
			ps.setDouble(9, attribute.getMatchInterval());
			CBRValueRange vr = attribute.getValueRange();
			if (vr == null)
				ps.setNull(10, Types.INTEGER);
			else
				ps.setInt(10, vr.getId());
			ps.setString(11, attribute.getDescription());
			ps.setInt(12, attribute.getId());

			int count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("no row was updated");
			}

			ps.close();
			ps = conn.prepareStatement(Q_DELETE_CBR_ENUMERATED_VALUES);
			ps.setInt(1, attribute.getId());
			count = ps.executeUpdate();
			logger.debug("   deleteCBRAttribute: deleted " + count + " enumerated value rows");
			ps.close();

			ps = conn.prepareStatement(Q_INSERT_CBR_ENUMERATED_VALUE);
			Iterator<CBREnumeratedValue> it = attribute.getEnumeratedValues().iterator();
			while (it.hasNext()) {
				CBREnumeratedValue ev = it.next();
				ps.setInt(1, attribute.getId());
				ps.setString(2, UtilBase.trim(ev.getName()));
				count = ps.executeUpdate();
				if (count < 1) {
					throw new SQLException("no CBR_ENUMERATED_VALUE row was inserted");
				}
			}
			ps.close();

			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to update attribute: " + attribute.getId() + ",name=" + attribute.getName(), ex);
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
			releaseConnection();
		}
	}

	public void cloneCBRAttribute(Connection conn, CBRAttribute attr) throws SQLException {
		doInsert(conn, attr);
	}

	private void doInsert(Connection conn, CBRAttribute attribute) throws SQLException {
		//Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			//conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_INSERT_CBR_ATTRIBUTE);
			ps.setInt(1, attribute.getId());
			ps.setInt(2, attribute.getCaseBase().getID());
			ps.setString(3, attribute.getName());
			CBRAttributeType at = attribute.getAttributeType();
			if (at == null)
				ps.setNull(4, Types.INTEGER);
			else
				ps.setInt(4, at.getId());
			ps.setInt(5, attribute.getMatchContribution());
			ps.setInt(6, attribute.getMismatchPenalty());
			ps.setInt(7, attribute.getAbsencePenalty());
			ps.setDouble(8, attribute.getLowestValue());
			ps.setDouble(9, attribute.getHighestValue());
			ps.setDouble(10, attribute.getMatchInterval());
			CBRValueRange vr = attribute.getValueRange();
			if (vr == null)
				ps.setNull(11, Types.INTEGER);
			else
				ps.setInt(11, vr.getId());
			ps.setString(12, attribute.getDescription());

			int count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("no row was inserted");
			}
			ps.close();

			ps = conn.prepareStatement(Q_INSERT_CBR_ENUMERATED_VALUE);
			Iterator<CBREnumeratedValue> it = attribute.getEnumeratedValues().iterator();
			while (it.hasNext()) {
				CBREnumeratedValue ev = it.next();
				ps.setInt(1, attribute.getId());
				ps.setString(2, UtilBase.trim(ev.getName()));
				count = ps.executeUpdate();
				if (count < 1) {
					throw new SQLException("no CBR_ENUMERATED_VALUE row was inserted");
				}
			}
			ps.close();
			ps = null;

			//conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to insert attribute: " + attribute.getID() + ",name=" + attribute.getName(), ex);
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
			//releaseConnection();
		}
	}

	/**
	 * Check if deletion of attributes is allowed.
	 * If there are any attribute values for the given attribute, then it is not allowed. Otherwise, it's ok.
	 * @param conn
	 * @param attributeID
	 * @return True if it's ok to delete (ie, no CBRAttributeValues for this attribute). False if there
	 * are CBRAttributeValues for this attribute.
	 * @throws SQLException
	 */
	private boolean validateAllowDeletion(Connection conn, int attributeID) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_SELECT_CBR_ATTRIBUTE_VALUES_FOR_ATTRIBUTE);
			ps.setInt(1, attributeID);
			rs = ps.executeQuery();
			logger.debug("   validateAllowDeletion: completed query");
			boolean retVal = !(rs.next());
			rs.close();
			rs = null;
			return retVal;
		}
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}

	}
}