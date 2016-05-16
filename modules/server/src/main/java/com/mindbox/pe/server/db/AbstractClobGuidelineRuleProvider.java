/*
 * Created on 2005. 7. 1.
 *
 */
package com.mindbox.pe.server.db;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.server.spi.GuidelineRuleInfo;


/**
 * Abstract Clob-based guideline rule provider.
 * The name of clob column is <b>'big_rule_def'</b>.
 * <p>
 * This class shoud not, and does not, use JDBC vendor specific methods. This only uses JDBC classes.
 * @author Geneho Kim
 * @since PowerEditor 4.3.3
 */
public abstract class AbstractClobGuidelineRuleProvider extends DefaultGuidelineRuleProvider {

	private static final String Q_LOAD_TEMPLATE_DEPLOY_RULES = "select template_id,column_no,big_rule_def,rule_def from MB_TEMPLATE_DEPLOY_RULE order by template_id";

	private static final String Q_INSERT_TEMPLATE_DEPLOY_RULE = "insert into MB_TEMPLATE_DEPLOY_RULE (template_id,column_no,big_rule_def) values (?,?,?)";

	private static final String Q_UPDATE_TEMPLATE_DEPLOY_RULE = "update MB_TEMPLATE_DEPLOY_RULE set big_rule_def=? where template_id=? and column_no=?";

	private static final String Q_SELECT_TEMPLATE_DEPLOY_RULE_DEF = "select big_rule_def from MB_TEMPLATE_DEPLOY_RULE where template_id=? and column_no=? for update";

	/**
	 * Creates a new clob (temporary, if supported by JDBC driver) for the specifieid connection and the initial value.
	 * The returned clob object must be a valid Clob object for the JDBC driver in use. 
	 * Technically, <code>PreparedStaetement.setClob()</code> must work with the clob object returned by this.
	 * @param conn connection
	 * @param value initial value
	 * @return a new clob pointer; must be one acccepted by the JDBC driver in use
	 * @throws SQLException on DB error
	 */
	protected abstract Clob createTemporaryClob(Connection conn, String value) throws SQLException;

	@Override
	public final List<GuidelineRuleInfo> fetchAllGuidelineRules() throws SQLException {
		logger.debug(">>> fetchAllGuidelineRules");
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
				logger.debug("... fetchAllGuidelineRules: processing template " + templateID + ", column " + columnNo);
				String ruleDefStr = fetchStringFromClob(rs, 3);
				if (ruleDefStr == null || ruleDefStr.length() < 1) {
					ruleDefStr = UtilBase.trim(rs.getString(4));
				}

				infoList.add(new GuidelineRuleInfo(templateID, columnNo, ruleDefStr));
			}
			logger.debug("<<< fetchAllGuidelineRules: #of rules = " + infoList.size());
			return infoList;
		}
		finally {
			DBUtil.closeLocallyManagedResources(rs, ps);
			DBConnectionManager.getInstance().freeConnection(conn);
		}
	}

	/**
	 * Retrieves string value from the specified CLOB column from the specified result set.
	 * This uses JDBC <code>java.sql.Clob</code> class and ResultSet.getClob() method to get the clob object. 
	 * Then, it uses <code>Clob.getCharacterStream()</code> to fetch the string.
	 * If these two methods doesn't work for a particular JDBC driver, override this.
	 * <p>
	 * This is used by {@link #fetchAllGuidelineRules()}.
	 * @param rs rs
	 * @param column column
	 * @return string value of the specified CLOB column
	 * @throws SQLException on error
	 * @see #fetchAllGuidelineRules()
	 */
	protected String fetchStringFromClob(ResultSet rs, int column) throws SQLException {
		logger.debug(">>> fetchStringFromClob: rs = " + rs);
		Clob clob = rs.getClob(column);
		logger.debug("... fetchStringFromClob: clobJdbc = " + clob);

		if (clob == null) return null;
		Reader in = null;
		try {
			in = clob.getCharacterStream();
			char[] chars = new char[(int) clob.length()];
			in.read(chars);
			in.close();
			in = null;

			logger.debug("<<< fetchStringFromClob");
			return String.valueOf(chars);
		}
		catch (IOException ex) {
			logger.error("IO error while getting CLOB value", ex);
			throw new SQLException("Failed to retrieve value at column " + column + " from " + rs + ": " + ex.getMessage());
		}
		finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (IOException e) {
					logger.warn("Failed to close " + in, e);
				}
			}
		}
	}

	protected String getInsertDeployRuleQuery() {
		return Q_INSERT_TEMPLATE_DEPLOY_RULE;
	}

	@Override
	public final void insertGuidelineRule(Connection conn, int templateID, int columnNo, String deploymentRule) throws SQLException {
		logger.debug(">>> insertGuidelineRule: template=" + templateID + ",col=" + columnNo);
		PreparedStatement ps = null;
		Clob tempClob = null;
		try {
			ps = conn.prepareStatement(getInsertDeployRuleQuery());
			ps.setInt(1, templateID);
			ps.setInt(2, columnNo);
			int count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("Failed to insert the template-rule row for " + templateID + "," + columnNo);
			}
			ps.close();
			ps = null;
			logger.debug("... insertGuidelineRule: row inserted. updating CLOB...");

			setStringToClob(conn, templateID, columnNo, deploymentRule);

			logger.debug("<<< insertGuidelineRule");
		}
		finally {
			releaseResource(tempClob);
			if (ps != null) ps.close();
		}
	}

	/**
	 * Releases DB resource associated with the specified clob object.
	 * This implementation is a no-op. 
	 * Override for a particular JDBC driver, if necessary.
	 * <p>
	 * This is used by {@link #insertGuidelineRule(Connection, int, int, String)},
	 * {@link #updateGuidelineRule(Connection, int, int, String)}, and
	 * {@link #setStringToClob(Connection, int, int, String)}.
	 * </p>
	 * <b>Note:</b>
	 * <p>
	 * Implementations of this must graciously handle when <code>clob</code> is <code>null</code>, in which case,
	 * It is recommended that this does nothing.
	 * </p>
	 * @param clob clob
	 * @throws SQLException on error
	 */
	protected abstract void releaseResource(Clob clob) throws SQLException;

	/**
	 * Updates the specified clob to the specified value.
	 * @param clob clob
	 * @param value value
	 * @throws SQLException on error
	 */
	protected abstract void setClobValue(Clob clob, String value) throws SQLException;

	/**
	 * Makes sue the clob column for the specified template and column has the specified value.
	 * This implementation uses <code>java.sql.Clob.setCharacterStream(long)</code> to set the value of the clob.
	 * If this doesn't work for a particular JDBC driver, override this.
	 * @param conn conn
	 * @param templateID templateID
	 * @param columnID columnID
	 * @param value value
	 * @throws SQLException on error
	 */
	protected void setStringToClob(Connection conn, int templateID, int columnID, String value) throws SQLException {
		logger.debug(">>> setStringToClob");
		PreparedStatement ps = null;
		ResultSet rs = null;
		Clob tempClob = null;
		try {
			ps = conn.prepareStatement(Q_SELECT_TEMPLATE_DEPLOY_RULE_DEF);
			ps.setInt(1, templateID);
			ps.setInt(2, columnID);
			rs = ps.executeQuery();
			logger.debug("    setStringToClob: rs = " + rs);
			if (rs.next()) {
				Clob clob = rs.getClob(1);
				if (clob != null) {
					logger.debug("    setStringToClob: clob = " + clob);
					setClobValue(clob, value);
				}
				else {
					rs.close();
					rs = null;
					ps.close();
					ps = null;

					logger.debug("    setStringToClob: creating new CLOB...");

					// create a new CLOB and set the value
					tempClob = createTemporaryClob(conn, value);
					logger.debug("    setStringToClob: new CLOB = " + tempClob);

					ps = conn.prepareStatement(Q_UPDATE_TEMPLATE_DEPLOY_RULE);
					ps.setClob(1, tempClob);
					ps.setInt(2, templateID);
					ps.setInt(3, columnID);
					int count = ps.executeUpdate();

					if (count < 1) throw new SQLException("Failed to create a new CLOB");
				}
				logger.debug("<<< setStringToClob");
			}
			else {
				throw new SQLException("No row found for template=" + templateID + ",col=" + columnID);
			}
		}
		catch (SQLException e) {
			logger.error("Failed to set string to clob", e);
			throw e;
		}
		finally {
			releaseResource(tempClob);
			if (rs != null) rs.close();
			if (ps != null) ps.close();
		}
	}

	@Override
	public final void updateGuidelineRule(Connection conn, int templateID, int columnNo, String deploymentRule) throws SQLException {
		logger.debug(">>> updateGuidelineRule: template=" + templateID + ",col=" + columnNo);
		setStringToClob(conn, templateID, columnNo, deploymentRule);
		logger.debug("<<< updateGuidelineRule");
	}
}
