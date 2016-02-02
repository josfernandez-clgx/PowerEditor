package com.mindbox.pe.server.spi.db;

import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class AbstractCachedUserDataProviderTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractCachedUserDataProviderTest Tests");
		suite.addTestSuite(AbstractCachedUserDataProviderTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private static class TestImpl extends AbstractCachedUserDataProvider {

		private boolean loadAllUsersCalled = false;
		private boolean loadAllUsersToRolesCalled = false;

		public void loadAllPrivileges(UserSecurityDataHolder dataHolder) throws SQLException {
		}

		public void loadAllPrivilegesToRoles(UserSecurityDataHolder dataHolder) throws SQLException {
		}

		public void loadAllRoles(UserSecurityDataHolder dataHolder) throws SQLException {
		}

		public void loadAllUsers(UserSecurityDataHolder dataHolder) throws SQLException {
			loadAllUsersCalled = true;
		}

		public void loadAllUsersToRoles(UserSecurityDataHolder dataHolder) throws SQLException {
			loadAllUsersToRolesCalled = true;
		}
	}

	private TestImpl testImpl;

	public AbstractCachedUserDataProviderTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.testImpl = new TestImpl();
	}

	public void testCacheUserObjectsReturnsTrue() throws Exception {
		assertTrue(testImpl.cacheUserObjects());
	}

	public void testGetRolesThrowsUnsupportedOperationException() throws Exception {
		assertThrowsException(
				testImpl,
				"getRoles",
				new Class[] { String.class },
				new Object[] { "user" },
				UnsupportedOperationException.class);
	}

	public void testLoadAllUsersThrowsUnsupportedOperationException() throws Exception {
		testImpl.loadAllUsers(null);
		assertTrue(testImpl.loadAllUsersCalled);
	}

	public void testLoadAllUsersToRolesThrowsUnsupportedOperationException() throws Exception {
		testImpl.loadAllUsersToRoles(null);
		assertTrue(testImpl.loadAllUsersToRolesCalled);
	}
}
