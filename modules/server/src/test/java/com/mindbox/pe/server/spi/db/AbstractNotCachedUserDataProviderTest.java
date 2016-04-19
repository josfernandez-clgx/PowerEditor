package com.mindbox.pe.server.spi.db;

import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.unittest.AbstractTestBase;

public class AbstractNotCachedUserDataProviderTest extends AbstractTestBase {

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

	@Before
	public void setUp() throws Exception {
		this.testImpl = new TestImpl();
	}

	@Test
	public void testCacheUserObjectsReturnsFalse() throws Exception {
		assertFalse(testImpl.cacheUserObjects());
	}

	@Test
	public void testGetRolesWorks() throws Exception {
		testImpl.getRoles("str");
		assertTrue(testImpl.getRolesCalled);
	}

	@Test
	public void testLoadAllUsersThrowsUnsupportedOperationException() throws Exception {
		assertThrowsException(testImpl, "loadAllUsers", new Class[] { UserSecurityDataHolder.class }, new Object[] { null }, UnsupportedOperationException.class);
	}

	@Test
	public void testLoadAllUsersToRolesThrowsUnsupportedOperationException() throws Exception {
		assertThrowsException(testImpl, "loadAllUsersToRoles", new Class[] { UserSecurityDataHolder.class }, new Object[] { null }, UnsupportedOperationException.class);
	}
}
