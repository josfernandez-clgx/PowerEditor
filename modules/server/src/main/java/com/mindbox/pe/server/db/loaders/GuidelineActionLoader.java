/*
 * Created on 2004. 7. 20.
 *
 */
package com.mindbox.pe.server.db.loaders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.FunctionParameterDefinition;
import com.mindbox.pe.model.rule.FunctionTypeDefinition;
import com.mindbox.pe.model.rule.TestTypeDefinition;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;
import com.mindbox.pe.server.db.DBConnectionManager;
import com.mindbox.pe.server.db.DBUtil;
import com.mindbox.pe.xsd.config.KnowledgeBaseFilter;
import com.mindbox.server.parser.jtb.rule.ParseException;


/**
 * Loads guideline actions from DB.
 * @author Geneho
 * @since PowerEditor 4.0.0
 */
public class GuidelineActionLoader extends AbstractLoader {

	private static final String Q_LOAD_ACTION = "select action_id,action_name,action_desc,deployment_rule from MB_GUIDELINE_ACTION order by action_id";

	// must be loaded ordered by param_id
	private static final String Q_LOAD_FUNCTION_PARAMETERS = "select action_id,param_id,param_name,deploy_type from MB_GUIDELINE_ACTION_PARAM order by action_id,param_id";

	private static final String Q_LOAD_ACTION_USAGES = "select action_id,usage_type from MB_GUIDELINE_ACTION_USAGE order by action_id";

	private static final String Q_LOAD_TEST = "select test_id,test_name,test_desc,deployment_rule from MB_GUIDELINE_TEST_CONDITION order by test_id";

	private static GuidelineActionLoader instance = null;

	public static GuidelineActionLoader getInstance() {
		if (instance == null) {
			instance = new GuidelineActionLoader();
		}
		return instance;
	}

	private GuidelineActionLoader() {
	}


	public void load(KnowledgeBaseFilter knowledgeBaseFilterConfig) throws SQLException, ParseException {
		// NOTE: filter not used as date filter is not applicable to guideline actions

		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection conn = dbconnectionmanager.getConnection();
		try {
			load(conn);
		}
		finally {
			dbconnectionmanager.freeConnection(conn);
		}
	}

	public void load(Connection connection) throws SQLException, ParseException {
		GuidelineFunctionManager.getInstance().startLoading();
		try {
			loadAction(connection);
			loadTest(connection);
			loadFunctionParameters(connection);
			loadActionUsages(connection);

		}
		finally {
			GuidelineFunctionManager.getInstance().finishLoading();
		}
	}

	private void loadAction(Connection conn) throws SQLException, ParseException {
		logger.info("=== GUIDELINE-ACTION: ACTIONS ===");
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_LOAD_ACTION);
			rs = ps.executeQuery();

			while (rs.next()) {
				int id = rs.getInt(1);
				String name = UtilBase.trim(rs.getString(2));
				String desc = UtilBase.trim(rs.getString(3));
				String ruleText = UtilBase.trim(rs.getString(4));

				ActionTypeDefinition actionTypeDef = new ActionTypeDefinition(id, name, desc);
				actionTypeDef.setDeploymentRule(ruleText);

				GuidelineFunctionManager.getInstance().insertActionTypeDefinition(actionTypeDef);
				logger.info("Guideline Action: " + id + ",name=" + name + ",desc=" + desc + ",ruleText=" + ruleText);
			}
		}
		finally {
			DBUtil.closeLocallyManagedResources(rs, ps);
		}
	}

	private void loadTest(Connection conn) throws SQLException, ParseException {
		logger.info("=== GUIDELINE-FUNCTION: TEST===");
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_LOAD_TEST);
			rs = ps.executeQuery();

			while (rs.next()) {
				int id = rs.getInt(1);
				String name = UtilBase.trim(rs.getString(2));
				String desc = UtilBase.trim(rs.getString(3));
				String ruleText = UtilBase.trim(rs.getString(4));

				TestTypeDefinition testTypeDef = new TestTypeDefinition(id, name, desc);
				testTypeDef.setDeploymentRule(ruleText);

				GuidelineFunctionManager.getInstance().insertTestTypeDefinition(testTypeDef);
				logger.info("Guideline Test: " + id + ",name=" + name + ",desc=" + desc + ",ruleText=" + ruleText);
			}
		}
		finally {
			DBUtil.closeLocallyManagedResources(rs, ps);
		}
	}

	private void loadActionUsages(Connection conn) throws SQLException {
		logger.info("=== GUIDELINE-ACTION: USAGES ===");

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_LOAD_ACTION_USAGES);
			rs = ps.executeQuery();

			ActionTypeDefinition actionTypeDef = null;
			while (rs.next()) {
				int actionID = rs.getInt(1);
				String usageStr = UtilBase.trim(rs.getString(2));

				if (actionTypeDef == null || actionTypeDef.getID() != actionID) {
					actionTypeDef = GuidelineFunctionManager.getInstance().getActionTypeDefinition(actionID);
				}
				if (actionTypeDef == null) {
					logger.warn("Guideline Action-Usage: invalid action id: " + actionID);
				}
				actionTypeDef.addUsageTypeString(usageStr);
				logger.info("Guideline Action-Param: actionID=" + actionID + ",usage-type=" + usageStr);
			}
		}
		finally {
			DBUtil.closeLocallyManagedResources(rs, ps);
		}
	}


	private void loadFunctionParameters(Connection conn) throws SQLException {
		logger.info("=== GUIDELINE-ACTION: PARAMETERS ===");
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_LOAD_FUNCTION_PARAMETERS);
			rs = ps.executeQuery();

			FunctionTypeDefinition def = null;
			while (rs.next()) {
				int actionID = rs.getInt(1);
				int paramID = rs.getInt(2);
				String paramName = UtilBase.trim(rs.getString(3));
				String deployType = UtilBase.trim(rs.getString(4));

				if (def == null || def.getID() != actionID) {
					def = GuidelineFunctionManager.getInstance().getActionTypeDefinition(actionID);
					if (def == null) {
						def = GuidelineFunctionManager.getInstance().getTestTypeDefinition(actionID);
					}
				}
				if (def == null) {
					logger.warn("Action Parameter: ignored - no action/test function " + actionID + " exists");
				}
				else if (paramID < 1) {
					logger.warn("Action Parameter: ignored - invalid row id: paramID=" + paramID + ",actionID=" + actionID);
				}
				FunctionParameterDefinition paramDef = new FunctionParameterDefinition(paramID, paramName);
				paramDef.setDeployTypeString(deployType);
				def.addParameterDefinition(paramDef);
				logger.info("Guideline Action-Param: param-id=" + paramID + ";name=" + paramName + ",type=" + deployType);
			}
		}
		finally {
			DBUtil.closeLocallyManagedResources(rs, ps);
		}
	}
}