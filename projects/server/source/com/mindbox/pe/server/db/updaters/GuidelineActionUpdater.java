/*
 * Created on 2004. 6. 25.
 */
package com.mindbox.pe.server.db.updaters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.rule.FunctionParameterDefinition;

/**
 * Guideline Action Updater.
 * Responsible for updating guideline actions in DB.
 * @author kim
 * @since PowerEditor  4.0.0
 */
public class GuidelineActionUpdater extends AbstractUpdater {

	private static final String Q_INSERT_ACTION = "insert into MB_GUIDELINE_ACTION (action_id,action_name,action_desc,deployment_rule) values (?,?,?,?)";

	private static final String Q_DELETE_ACTION = "delete from MB_GUIDELINE_ACTION where action_id=?";

	private static final String Q_UPDATE_ACTION = "update MB_GUIDELINE_ACTION set action_name=?,action_desc=?,deployment_rule=? where action_id=?";

	private static final String Q_INSERT_ACTION_USAGE = "insert into MB_GUIDELINE_ACTION_USAGE (action_id,usage_type) values (?,?)";

	private static final String Q_DELETE_ACTION_USAGE = "delete from MB_GUIDELINE_ACTION_USAGE where action_id=?";

	private static final String Q_INSERT_ACTION_PARAM = "insert into MB_GUIDELINE_ACTION_PARAM (action_id,param_id,param_name,deploy_type) values (?,?,?,?)";

	private static final String Q_DELETE_ACTION_PARAM = "delete from MB_GUIDELINE_ACTION_PARAM where action_id=?";

	public GuidelineActionUpdater() {
	}


	public void insertAction(int actionID, String name, String desc, String deployRule, FunctionParameterDefinition[] params, String[] usageTypes) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_INSERT_ACTION);
			ps.setInt(1, actionID);
			ps.setString(2, name);
			ps.setString(3, desc);
			ps.setString(4, deployRule);
			int count = ps.executeUpdate();
			if (count < 1) { throw new SQLException("no row was inserted"); }
			ps.close();
			ps = null;

			// insert action params
			insertActionParams(conn, actionID, params);
			logger.info("    insertAction: action params inserted");

			// insert usage types
			insertActionUsages(conn, actionID, usageTypes);
			logger.info("    insertAction: action usages inserted");

			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to insert action: " + actionID + ",name=" + name, ex);
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

	public void deleteAction(int actionID) throws SQLException {
		logger.debug(">>> deleteAction: " + actionID);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			// delete prereqs, if any
			ps = conn.prepareStatement(Q_DELETE_ACTION_PARAM);
			ps.setInt(1, actionID);
			int count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.info("    deleteAction: deleted " + count + " action params");

			// delete action links
			ps = conn.prepareStatement(Q_DELETE_ACTION_USAGE);
			ps.setInt(1, actionID);
			count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.info("    deleteAction: deleted " + count + " action usages");

			// delete from action table
			ps = conn.prepareStatement(Q_DELETE_ACTION);
			ps.setInt(1, actionID);

			count = ps.executeUpdate();
			logger.debug("   deleteAction: deleted " + count + " action rows");
			if (count < 1) { throw new SQLException("no row was deleted from the action table"); }
			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to delete action: " + actionID, ex);
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

	public void updateAction(Connection conn, int actionID, String name, String desc, String deployRule, FunctionParameterDefinition[] params, String[] usageTypes) throws SQLException {
		PreparedStatement ps = null;
		try {

			// delete action params, if any
			ps = conn.prepareStatement(Q_DELETE_ACTION_PARAM);
			ps.setInt(1, actionID);
			int count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.info("    updateAction: deleted " + count + " action params");

			// insert action params
			insertActionParams(conn, actionID, params);
			logger.info("    updateAction: action params inserted");

			// delete usage types			
			ps = conn.prepareStatement(Q_DELETE_ACTION_USAGE);
			ps.setInt(1, actionID);
			count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.info("    updateAction: deleted " + count + " action usage types");

			// insert usage types
			insertActionUsages(conn, actionID, usageTypes);
			logger.info("    updateAction: action usages inserted");

			// update action
			ps = conn.prepareStatement(Q_UPDATE_ACTION);
			ps.setString(1, name);
			ps.setString(2, desc);
			ps.setString(3, deployRule);
			ps.setInt(4, actionID);

			count = ps.executeUpdate();
			if (count < 1) { throw new SQLException("no row was updated"); }
		}
		catch (Exception ex) {
			logger.error("Failed to update action: " + actionID + ",name=" + name, ex);
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
	 * 
	 * @param conn its autoCommit must be set to false
	 * @param actionID
	 * @param childPhaseIDs
	 * @throws SQLException
	 */
	private void insertActionUsages(Connection conn, int actionID, String[] usageTypes) throws SQLException {
		if (usageTypes == null) {
			throw new NullPointerException("usageTypes cannot be null");
		}
		else if (usageTypes.length == 0) return;

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_INSERT_ACTION_USAGE);

			int count = 0;
			for (int i = 0; i < usageTypes.length; i++) {
				ps.setInt(1, actionID);
				ps.setString(2, usageTypes[i]);
				count = ps.executeUpdate();

				if (count < 1) { throw new SQLException("no row was inserted"); }
			}
		}
		catch (Exception ex) {
			logger.error("Failed to insert action usage for " + actionID + ", " + UtilBase.toString(usageTypes), ex);
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
	 * 
	 * @param conn its autoCommit must be set to false
	 * @param actionID
	 * @param prereqPhaseIDs
	 * @throws SQLException
	 */
	private void insertActionParams(Connection conn, int actionID, FunctionParameterDefinition[] params) throws SQLException {
		if (params == null) {
			throw new NullPointerException("params cannot be null");
		}
		else if (params.length == 0) return;

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_INSERT_ACTION_PARAM);

			int count = 0;
			for (int i = 0; i < params.length; i++) {
				ps.setInt(1, actionID);
				ps.setInt(2, params[i].getID());
				ps.setString(3, params[i].getName());
				ps.setString(4, params[i].getDeployType().toString());
				
				count = ps.executeUpdate();

				if (count < 1) { throw new SQLException("no row was inserted"); }
			}
		}
		catch (Exception ex) {
			logger.error("Failed to insert action params for " + actionID + ", " + UtilBase.toString(params), ex);
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
}