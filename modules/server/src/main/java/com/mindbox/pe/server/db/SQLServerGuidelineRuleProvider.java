/*
 * Created on 2005. 7. 1.
 *
 */
package com.mindbox.pe.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.server.spi.GuidelineRuleInfo;


/**
 * {@link com.mindbox.pe.server.spi.GuidelineRuleProvider} that uses SQL Server IMAGE data type.
 * To use this, 
 * Add &lt;GuidelineRuleProviderClass&gt;com.mindbox.pe.server.db.SQLServerGuidelineRuleProvider&lt;/GuidelineRuleProviderClass&gt;
 * in the &lt;Database&gt; section of PowereditorConfiguration.xml.
 * @author Geneho Kim
 * @since PowerEditor 4.3.6
 */
public class SQLServerGuidelineRuleProvider extends DefaultGuidelineRuleProvider {

	private static final String Q_LOAD_TEMPLATE_DEPLOY_RULES = "select template_id,column_no,big_rule_def,rule_def from MB_TEMPLATE_DEPLOY_RULE order by template_id";

	private static final String Q_INSERT_TEMPLATE_DEPLOY_RULE = "insert into MB_TEMPLATE_DEPLOY_RULE (template_id,column_no,big_rule_def) values (?,?,?)";

	private static final String Q_UPDATE_TEMPLATE_DEPLOY_RULE = "update MB_TEMPLATE_DEPLOY_RULE set big_rule_def=? where template_id=? and column_no=?";

	public List<GuidelineRuleInfo> fetchAllGuidelineRules() throws SQLException {
		Connection conn = DBConnectionManager.getInstance().getConnection();

		List<GuidelineRuleInfo> infoList = new ArrayList<GuidelineRuleInfo>();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_LOAD_TEMPLATE_DEPLOY_RULES);
			rs = ps.executeQuery();

			int templateID = -1;
			int columnNo = -1;
			while (rs.next()) {
				templateID = rs.getInt(1);
				columnNo = rs.getInt(2);
				byte[] bytes = rs.getBytes(3);
				String ruleDefStr = null;
				if (bytes != null && bytes.length > 1) {
					ruleDefStr = new String(bytes);
				}
				else {
					ruleDefStr = UtilBase.trim(rs.getString(4));
				}

				infoList.add(new GuidelineRuleInfo(templateID, columnNo, ruleDefStr));
			}

			return infoList;
		}
		finally {
			DBUtil.closeLocallyManagedResources(rs, ps);
			DBConnectionManager.getInstance().freeConnection(conn);
		}
	}

	public void insertGuidelineRule(Connection conn, int templateID, int columnNo, String deploymentRule) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_INSERT_TEMPLATE_DEPLOY_RULE);
			ps.setInt(1, templateID);
			ps.setInt(2, columnNo);
			ps.setBytes(3, deploymentRule.getBytes());
			int count = ps.executeUpdate();
			if (count < 1) { throw new SQLException("Failed to insert the template-rule row for " + templateID + "," + columnNo); }
			ps.close();
			ps = null;
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	public void updateGuidelineRule(Connection conn, int templateID, int columnNo, String deploymentRule) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_UPDATE_TEMPLATE_DEPLOY_RULE);
			ps.setBytes(1, deploymentRule.getBytes());
			ps.setInt(2, templateID);
			ps.setInt(3, columnNo);
			int count = ps.executeUpdate();
			if (count < 1) { throw new SQLException("Failed to update the template-rule row: " + templateID + "," + columnNo); }
			ps.close();
			ps = null;
		}
		finally {
			if (ps != null) ps.close();
		}
	}

}
