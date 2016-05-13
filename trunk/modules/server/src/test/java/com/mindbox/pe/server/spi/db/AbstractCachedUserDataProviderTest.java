package com.mindbox.pe.server.spi.db;

import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class AbstractCachedUserDataProviderTest extends AbstractTestBase {

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

	@Before
	public void setUp() throws Exception {
		this.testImpl = new TestImpl();
	}

	@Test
	public void testCacheUserObjectsReturnsTrue() throws Exception {
		assertTrue(testImpl.cacheUserObjects());
	}

	@Test
	public void testGetRolesThrowsUnsupportedOperationException() throws Exception {
		assertThrowsException(testImpl, "getRoles", new Class[] { String.class }, new Object[] { "user" }, UnsupportedOperationException.class);
	}

	@Test
	public void testLoadAllUsersThrowsUnsupportedOperationException() throws Exception {
		testImpl.loadAllUsers(null);
		assertTrue(testImpl.loadAllUsersCalled);
	}

	@Test
	public void testLoadAllUsersToRolesThrowsUnsupportedOperationException() throws Exception {
		testImpl.loadAllUsersToRoles(null);
		assertTrue(testImpl.loadAllUsersToRolesCalled);
	}
}
