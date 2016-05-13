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

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.server.spi.GuidelineRuleInfo;
import com.mindbox.pe.server.spi.GuidelineRuleProvider;


/**
 * @author Geneho Kim
 * @since PowerEditor 4.3.3
 */
public class DefaultGuidelineRuleProvider implements GuidelineRuleProvider {

	private static final String Q_LOAD_TEMPLATE_DEPLOY_RULES = "select template_id,column_no,rule_def from MB_TEMPLATE_DEPLOY_RULE order by template_id";

	private static final String Q_INSERT_TEMPLATE_DEPLOY_RULE = "insert into MB_TEMPLATE_DEPLOY_RULE"
			+ " (template_id,column_no,rule_def) values (?,?,?)";

	private static final String Q_UPDATE_TEMPLATE_DEPLOY_RULE = "update MB_TEMPLATE_DEPLOY_RULE"
			+ " set rule_def=? where template_id=? and column_no=?";

	protected final Logger logger = Logger.getLogger(getClass());

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
				String ruleDefStr = UtilBase.trim(rs.getString(3));

				infoList.add(new GuidelineRuleInfo(templateID, columnNo, ruleDefStr));
			}
			
			return infoList;
		}
		finally {
			DBUtil.closeLocallyManagedResources(rs, ps);
			DBConnectionManager.getInstance().freeConnection(conn);
		}

	}

	public void insertGuidelineRule(int templateID, int columnNo, String deploymentRule) throws SQLException {
		Connection conn = DBConnectionManager.getInstance().getConnection();
		try {
			conn.setAutoCommit(false);
			insertGuidelineRule(conn, templateID, columnNo, deploymentRule);
			conn.commit();
		}
		finally {
			DBConnectionManager.getInstance().freeConnection(conn);
		}
	}

	public void insertGuidelineRule(Connection conn, int templateID, int columnNo, String deploymentRule) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_INSERT_TEMPLATE_DEPLOY_RULE);
			ps.setInt(1, templateID);
			ps.setInt(2, columnNo);
			ps.setString(3, deploymentRule);
			int count = ps.executeUpdate();
			if (count < 1) { throw new SQLException("Failed to insert the template-rule row for " + templateID + "," + columnNo); }
			ps.close();
			ps = null;
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	public void updateGuidelineRule(int templateID, int columnNo, String deploymentRule) throws SQLException {
		Connection conn = DBConnectionManager.getInstance().getConnection();
		try {
			conn.setAutoCommit(false);
			updateGuidelineRule(conn, templateID, columnNo, deploymentRule);
			conn.commit();
		}
		finally {
			DBConnectionManager.getInstance().freeConnection(conn);
		}
	}

	public void updateGuidelineRule(Connection conn, int templateID, int columnNo, String deploymentRule) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_UPDATE_TEMPLATE_DEPLOY_RULE);
			ps.setString(1, deploymentRule);
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
