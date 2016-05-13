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
 * Guideline Test Updater
 * Responsible for updating guideline tests in DB.
 * @author kim
 * @since PowerEditor  4.0.0
 */
public class GuidelineTestConditionUpdater extends AbstractUpdater {

	private static final String Q_INSERT_TEST_CONDITION = "insert into MB_GUIDELINE_TEST_CONDITION (test_id,test_name,test_desc,deployment_rule) values (?,?,?,?)";

	private static final String Q_DELETE_TEST_CONDITION = "delete from MB_GUIDELINE_TEST_CONDITION where test_id=?";

	private static final String Q_UPDATE_TEST_CONDITION = "update MB_GUIDELINE_TEST_CONDITION set test_name=?,test_desc=?,deployment_rule=? where test_id=?";

	private static final String Q_INSERT_TEST_CONDITION_PARAM = "insert into MB_GUIDELINE_ACTION_PARAM (action_id,param_id,param_name,deploy_type) values (?,?,?,?)";

	private static final String Q_DELETE_TEST_CONDITION_PARAM = "delete from MB_GUIDELINE_ACTION_PARAM where action_id=?";

	public GuidelineTestConditionUpdater() {
	}


	public void insertTest(int testID, String name, String desc, String deployRule, FunctionParameterDefinition[] params) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_INSERT_TEST_CONDITION);
			ps.setInt(1, testID);
			ps.setString(2, name);
			ps.setString(3, desc);
			ps.setString(4, deployRule);

			int count = ps.executeUpdate();
			if (count < 1) { throw new SQLException("no row was inserted"); }

			// insert test params
			insertTestParams(conn, testID, params);
			logger.info("    insertTest: test params inserted");

			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to insert test: " + testID + ",name=" + name, ex);
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

	public void deleteTest(int testID) throws SQLException {
		logger.debug(">>> deleteTest: " + testID);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			// delete prereqs, if any
			ps = conn.prepareStatement(Q_DELETE_TEST_CONDITION_PARAM);
			ps.setInt(1, testID);
			int count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.info("    deleteTest: deleted " + count + " test params");

			// delete from test table
			ps = conn.prepareStatement(Q_DELETE_TEST_CONDITION);
			ps.setInt(1, testID);

			count = ps.executeUpdate();
			logger.debug("   deleteTest: deleted " + count + " test rows");
			if (count < 1) { throw new SQLException("no row was deleted from the test table"); }
			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to delete test: " + testID, ex);
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

	public void updateTest(Connection conn, int testID, String name, String desc, String deployRule, FunctionParameterDefinition[] params) throws SQLException {
		PreparedStatement ps = null;
		try {

			// delete test params, if any
			ps = conn.prepareStatement(Q_DELETE_TEST_CONDITION_PARAM);
			ps.setInt(1, testID);
			int count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.info("    updateTest: deleted " + count + " test params");

			// insert test params
			insertTestParams(conn, testID, params);
			logger.info("    updateTest: test params inserted");

			// update test
			ps = conn.prepareStatement(Q_UPDATE_TEST_CONDITION);
			ps.setString(1, name);
			ps.setString(2, desc);
			ps.setString(3, deployRule);
			ps.setInt(4, testID);

			count = ps.executeUpdate();
			if (count < 1) { throw new SQLException("no row was updated"); }
		}
		catch (Exception ex) {
			logger.error("Failed to update test: " + testID + ",name=" + name, ex);
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
	 * @param testID
	 * @param prereqPhaseIDs
	 * @throws SQLException
	 */
	private void insertTestParams(Connection conn, int testID, FunctionParameterDefinition[] params) throws SQLException {
		if (params == null) {
			throw new NullPointerException("params cannot be null");
		}
		else if (params.length == 0) return;

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_INSERT_TEST_CONDITION_PARAM);

			int count = 0;
			for (int i = 0; i < params.length; i++) {
				ps.setInt(1, testID);
				ps.setInt(2, params[i].getID());
				ps.setString(3, params[i].getName());
				ps.setString(4, params[i].getDeployType().toString());
				
				count = ps.executeUpdate();

				if (count < 1) { throw new SQLException("no row was inserted"); }
			}
		}
		catch (Exception ex) {
			logger.error("Failed to insert test params for " + testID + ", " + UtilBase.toString(params), ex);
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