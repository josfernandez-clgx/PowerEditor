package com.mindbox.pe.server.db;

import java.io.IOException;
import java.io.Writer;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.sql.CLOB;

import com.mindbox.pe.common.UtilBase;

/**
 * Oracle rule provider that uses CLOB.
 */
public class Oracle9GuidelineRuleProvider extends AbstractClobGuidelineRuleProvider {

	private static final String Q_INSERT_TEMPLATE_DEPLOY_RULE = "insert into MB_TEMPLATE_DEPLOY_RULE (template_id,column_no,big_rule_def) values (?,?,empty_clob())";

	public Oracle9GuidelineRuleProvider() {
		logger.debug("created!!!");
	}

	protected String getInsertDeployRuleQuery() {
		return Q_INSERT_TEMPLATE_DEPLOY_RULE;
	}

	// Note: this should not be called for Oracle
	protected Clob createTemporaryClob(Connection conn, String value) throws SQLException {
		CLOB tempClob = CLOB.createTemporary(conn, true, CLOB.DURATION_CALL);
		setCLOBValue(tempClob, value);
		return tempClob;
	}

	protected String fetchStringFromClob(ResultSet rs, int column) throws SQLException {
		String strFromSuper = super.fetchStringFromClob(rs, column);
		if (strFromSuper != null) {
			int index = strFromSuper.indexOf("</Rule>");
			if (index > 0 && index < strFromSuper.length() - 7) {
				strFromSuper = strFromSuper.substring(0, index + 7);
				logger.info("revised string = " + strFromSuper);
			}
		}
		return strFromSuper;
	}

	protected void setClobValue(Clob clob, String value) throws SQLException {
		if (!(clob instanceof CLOB)) throw new SQLException("Invalid clob object: not an instance of oracle.sql.CLOB: " + clob);
		setCLOBValue((CLOB) clob, value);
	}

	@SuppressWarnings("deprecation")
	private void setCLOBValue(CLOB clob, String value) throws SQLException {
		logger.debug("--> setCLOBValue: " + clob + "," + value);
		if (UtilBase.isEmpty(value)) {
			clob.trim(0L);
		}
		else {
			clob.open(CLOB.MODE_READWRITE);
			Writer writer = null;
			try {
				writer = clob.getCharacterOutputStream();
				writer.write(value);
				writer.flush();
				writer.close();
				writer = null;
				clob.close();
				clob.trim(value.length());
				logger.debug("<-- setCLOBValue: new size = " + clob.length());
			}
			catch (IOException ex) {
				throw new SQLException("Failed to set content of CLOB: " + ex.getMessage());
			}
			finally {
				if (writer != null) {
					try {
						writer.close();
					}
					catch (IOException e) {
						logger.warn("Failed to close " + writer, e);
					}
				}
				if (clob != null && clob.isOpen()) {
					clob.close();
				}
			}
		}
	}

	protected void releaseResource(Clob clob) throws SQLException {
		if (clob != null) CLOB.freeTemporary((CLOB) clob);
	}

}
