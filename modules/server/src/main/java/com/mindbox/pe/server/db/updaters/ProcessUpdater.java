/*
 * Created on 2004. 6. 25.
 */
package com.mindbox.pe.server.db.updaters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.mindbox.pe.common.UtilBase;

/**
 * Process Updater.
 * Responsible for updating requests and phases.
 * @author kim
 * @since PowerEditor  3.3.0
 */
public class ProcessUpdater extends AbstractUpdater {

	private static final String Q_INSERT_PHASE = "insert into MB_PHASE (phase_id,phase_type,phase_name,display_name,task_name,prereq_type) values (?,?,?,?,?,?)";

	private static final String Q_DELETE_PHASE = "delete from MB_PHASE where phase_id=?";

	private static final String Q_UPDATE_PHASE = "update MB_PHASE set phase_type=?,phase_name=?,display_name=?,task_name=?,prereq_type=? where phase_id=?";

	private static final String Q_INSERT_PHASE_LINK = "insert into MB_PHASE_LINK (parent_phase_id,child_phase_id) values (?,?)";

	private static final String Q_DELETE_PHASE_LINK = "delete from MB_PHASE_LINK where parent_phase_id=?";

	private static final String Q_INSERT_PHASE_PREREQ = "insert into MB_PHASE_PREREQ (phases_id,prereq_phase_id) values (?,?)";

	private static final String Q_DELETE_PHASE_PREREQ = "delete from MB_PHASE_PREREQ where phases_id=?";

	private static final String Q_INSERT_REQUEST = "insert into MB_REQUEST (request_id,request_name,request_type,display_name,description,init_function,purpose,phase_id) values (?,?,?,?,?,?,?,?)";

	private static final String Q_DELETE_REQUEST = "delete from MB_REQUEST where request_id=?";

	private static final String Q_UPATE_REQUEST = "update MB_REQUEST set request_name=?,request_type=?,display_name=?,description=?,init_function=?,purpose=?,phase_id=? where request_id=?";

	public ProcessUpdater() {
	}

	public void insertRequest(int requestID, String name, String type, String dispName, String description, String initFunction, String purpose,
			int phaseID) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_INSERT_REQUEST);
			ps.setInt(1, requestID);
			ps.setString(2, name);
			ps.setString(3, type);
			ps.setString(4, dispName);
			ps.setString(5, description);
			ps.setString(6, initFunction);
			ps.setString(7, purpose);
			ps.setInt(8, phaseID);

			int count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("no row was inserted");
			}

			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to insert request: " + requestID + ",name=" + name + ",type=" + type, ex);
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

	public void updateRequest(int requestID, String name, String type, String dispName, String description, String initFunction, String purpose,
			int phaseID) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_UPATE_REQUEST);
			ps.setString(1, name);
			ps.setString(2, type);
			ps.setString(3, dispName);
			ps.setString(4, description);
			ps.setString(5, initFunction);
			ps.setString(6, purpose);
			ps.setInt(7, phaseID);
			ps.setInt(8, requestID);

			int count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("no row was updated");
			}

			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to update request: " + requestID + ",name=" + name + ",type=" + type, ex);
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

	public void deleteRequest(int requestID) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_DELETE_REQUEST);
			ps.setInt(1, requestID);

			int count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("no row was delete");
			}

			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to delete request: " + requestID);
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

	public void insertPhase(int phaseID, int type, String name, String dispName, String taskName, int[] childPhaseIDs, int[] prereqPhaseIDs,
			boolean isDisjunctivePrereqs) throws SQLException {
		insertPhase(phaseID, type, name, dispName, taskName, childPhaseIDs, prereqPhaseIDs, (isDisjunctivePrereqs ? 1 : 0));
	}

	public void insertPhase(int phaseID, int type, String name, String dispName, String taskName, int[] childPhaseIDs, int[] prereqPhaseIDs,
			int prereqType) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_INSERT_PHASE);
			ps.setInt(1, phaseID);
			ps.setInt(2, type);
			ps.setString(3, name);
			ps.setString(4, dispName);
			ps.setString(5, (taskName == null ? "" : taskName));
			ps.setInt(6, prereqType);

			int count = ps.executeUpdate();
			ps.close();
			ps = null;
			if (count < 1) {
				throw new SQLException("no row was inserted");
			}

			// insert child phases
			insertPhaseLinks(conn, phaseID, childPhaseIDs);
			logger.info("    updatePhase: phase links inserted");

			// insert prereq phases
			insertPrereqPhases(conn, phaseID, prereqPhaseIDs);
			logger.info("    updatePhase: prereq phases inserted");

			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to insert phase: " + phaseID + ",type=" + type, ex);
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

	public void deletePhase(int phaseID) throws SQLException {
		logger.debug(">>> deletePhase: " + phaseID);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			// delete prereqs, if any
			ps = conn.prepareStatement(Q_DELETE_PHASE_PREREQ);
			ps.setInt(1, phaseID);
			int count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.info("    deletePhase: deleted " + count + " prereq phases");

			// delete phase links
			ps = conn.prepareStatement(Q_DELETE_PHASE_LINK);
			ps.setInt(1, phaseID);
			count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.info("    deletePhase: deleted " + count + " phase links");

			// delete from phase table
			ps = conn.prepareStatement(Q_DELETE_PHASE);
			ps.setInt(1, phaseID);

			count = ps.executeUpdate();
			logger.debug("   deletePhase: deleted " + count + " phase rows");
			if (count < 1) {
				throw new SQLException("no row was deleted from the Phase table");
			}
			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to delete phase: " + phaseID, ex);
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

	public void updatePhase(int phaseID, int type, String name, String dispName, String taskName, int[] childPhaseIDs, int[] prereqPhaseIDs,
			boolean isDisjunctivePrereqs) throws SQLException {
		updatePhase(phaseID, type, name, dispName, taskName, childPhaseIDs, prereqPhaseIDs, (isDisjunctivePrereqs ? 1 : 0));
	}

	public void updatePhase(int phaseID, int type, String name, String dispName, String taskName, int[] childPhaseIDs, int[] prereqPhaseIDs,
			int prereqType) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			// delete prereqs, if any
			ps = conn.prepareStatement(Q_DELETE_PHASE_PREREQ);
			ps.setInt(1, phaseID);
			int count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.info("    updatePhase: deleted " + count + " prereq phases");

			// insert prereq phases
			insertPrereqPhases(conn, phaseID, prereqPhaseIDs);
			logger.info("    updatePhase: prereq phases inserted");

			// delete phase links			
			ps = conn.prepareStatement(Q_DELETE_PHASE_LINK);
			ps.setInt(1, phaseID);
			count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.info("    updatePhase: deleted " + count + " phase links");

			// insert child phases
			insertPhaseLinks(conn, phaseID, childPhaseIDs);
			logger.info("    updatePhase: phase links inserted");

			// update phase
			ps = conn.prepareStatement(Q_UPDATE_PHASE);
			ps.setInt(1, type);
			ps.setString(2, name);
			ps.setString(3, dispName);
			ps.setString(4, (taskName == null ? "" : taskName));
			ps.setInt(5, prereqType);
			ps.setInt(6, phaseID);

			count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("no row was updated");
			}
			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to update phase: " + phaseID + ",type=" + type, ex);
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
	 * 
	 * @param conn its autoCommit must be set to false
	 * @param phaseID
	 * @param childPhaseIDs
	 * @throws SQLException
	 */
	private void insertPhaseLinks(Connection conn, int phaseID, int[] childPhaseIDs) throws SQLException {
		if (childPhaseIDs == null) {
			throw new NullPointerException("childPhaseIDs cannot be null");
		}
		else if (childPhaseIDs.length == 0)
			return;

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_INSERT_PHASE_LINK);

			int count = 0;
			for (int i = 0; i < childPhaseIDs.length; i++) {
				ps.setInt(1, phaseID);
				ps.setInt(2, childPhaseIDs[i]);
				count = ps.executeUpdate();

				if (count < 1) {
					throw new SQLException("no row was inserted");
				}
			}
		}
		catch (Exception ex) {
			logger.error("Failed to insert phase links for " + phaseID + ", " + UtilBase.toString(childPhaseIDs), ex);
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

	/**
	 * 
	 * @param conn its autoCommit must be set to false
	 * @param phaseID
	 * @param prereqPhaseIDs
	 * @throws SQLException
	 */
	private void insertPrereqPhases(Connection conn, int phaseID, int[] prereqPhaseIDs) throws SQLException {
		if (prereqPhaseIDs == null) {
			throw new NullPointerException("prereqPhaseIDs cannot be null");
		}
		else if (prereqPhaseIDs.length == 0)
			return;

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_INSERT_PHASE_PREREQ);

			int count = 0;
			for (int i = 0; i < prereqPhaseIDs.length; i++) {
				ps.setInt(1, phaseID);
				ps.setInt(2, prereqPhaseIDs[i]);
				count = ps.executeUpdate();

				if (count < 1) {
					throw new SQLException("no row was inserted");
				}
			}
		}
		catch (Exception ex) {
			logger.error("Failed to insert prereq phases for " + phaseID + ", " + UtilBase.toString(prereqPhaseIDs), ex);
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
}