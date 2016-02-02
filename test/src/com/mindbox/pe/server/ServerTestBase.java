/*
 * Created on Jul 24, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.server;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.server.db.DBConnectionManager;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public abstract class ServerTestBase extends AbstractTestWithTestConfig {

	public static final class ClearDB extends ServerTestBase {
		protected ClearDB(String name) {
			super(name);
		}

		public static void main(String[] args) throws Exception {
			ClearDB instance = new ClearDB("");
			instance.clearEntityData();
			instance.clearFilters();
			instance.clearSecurityData();
			instance.clearGrids();
			instance.clearMessages();
			instance.clearAdHocRulesets();
		}
	}

	protected ServerTestBase(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	protected int executeStatement(String sql) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBConnectionManager.getInstance().getConnection();
			conn.setAutoCommit(true);
			ps = conn.prepareStatement(sql);
			return ps.executeUpdate();
		}
		finally {
			if (ps != null)
				ps.close();
			if (conn != null)
				DBConnectionManager.getInstance().freeConnection(conn);
		}

	}

	protected void clearEntityData() throws Exception {
		config.initServer();

		executeStatement("delete from MB_UsersToChannels");
		executeStatement("delete from MB_UsersToInvestors");
		executeStatement("delete from MB_PRODUCT_TERM");
		executeStatement("delete from MB_PRODUCT_CATEGORY");
		executeStatement("delete from MB_OrganizationsToProducts");
		executeStatement("delete from MB_OrganizationsToInvestors");
		executeStatement("delete from MB_InvestorsToProducts");
		executeStatement("delete from MB_ARM_INFO");
		executeStatement("delete from MB_INVESTOR");
		executeStatement("delete from MB_CHANNEL");
		executeStatement("delete from MB_PRODUCT");
		executeStatement("delete from MB_CATEGORY");

		config.refreshServerCache();
	}

	protected void clearFilters() throws Exception {
		executeStatement("delete from MB_NAMED_FILTER");
	}

	protected void clearSecurityData() throws Exception {
		executeStatement("delete from MB_UsersToChannels");
		executeStatement("delete from MB_UsersToInvestors");
		executeStatement("delete from MB_USER_ROLE");
		executeStatement("delete from MB_USER_ACCOUNT");
		executeStatement("delete from MB_ROLE_PRIVILEGE");
		executeStatement("delete from MB_ROLE");
	}

	protected void clearGrids() throws Exception {
		executeStatement("delete from MB_ENTITY_PARAMETER_CONTEXT");
		executeStatement("delete from MB_PARAMETER");
		executeStatement("delete from MB_ENTITY_GRID_CONTEXT");
		executeStatement("delete from MB_GRID");
	}

	protected void clearMessages() throws Exception {
		executeStatement("delete from MB_MESSAGE_TEMPLATE");
		executeStatement("delete from MB_MESSAGE");
	}

	protected void clearAdHocRulesets() throws Exception {
		executeStatement("delete from MB_ADHOC_RULESET_USAGE");
		executeStatement("delete from MB_ADHOC_RULESET_RULES");
		executeStatement("delete from MB_ADHOC_RULESET_CONTEXT");
		executeStatement("delete from MB_ADHOC_RULESET");
	}
}
