package com.mindbox.pe.server.spi.db;

import java.sql.SQLException;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.model.admin.Role;

public class AbstractNotCachedUserDataProviderTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractNotCachedUserDataProviderTest Tests");
		suite.addTestSuite(AbstractNotCachedUserDataProviderTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private static class TestImpl extends AbstractNotCachedUserDataProvider {

		private boolean getRolesCalled = false;
		public List<Role> getRoles(String userID) throws SQLException {
			getRolesCalled = true;
			return null;
		}

		public void loadAllPrivileges(UserSecurityDataHolder dataHolder) throws SQLException {
		}

		public void loadAllPrivilegesToRoles(UserSecurityDataHolder dataHolder) throws SQLException {
		}

		public void loadAllRoles(UserSecurityDataHolder dataHolder) throws SQLException {
		}
	}

	private TestImpl testImpl;

	public AbstractNotCachedUserDataProviderTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.testImpl = new TestImpl();
	}

	public void testCacheUserObjectsReturnsFalse() throws Exception {
		assertFalse(testImpl.cacheUserObjects());
	}

	public void testGetRolesWorks() throws Exception {
		testImpl.getRoles("str");
		assertTrue(testImpl.getRolesCalled);
	}

	public void testLoadAllUsersThrowsUnsupportedOperationException() throws Exception {
		assertThrowsException(
				testImpl,
				"loadAllUsers",
				new Class[] { UserSecurityDataHolder.class },
				new Object[] { null },
				UnsupportedOperationException.class);
	}

	public void testLoadAllUsersToRolesThrowsUnsupportedOperationException() throws Exception {
		assertThrowsException(
				testImpl,
				"loadAllUsersToRoles",
				new Class[] { UserSecurityDataHolder.class },
				new Object[] { null },
				UnsupportedOperationException.class);
	}
}
