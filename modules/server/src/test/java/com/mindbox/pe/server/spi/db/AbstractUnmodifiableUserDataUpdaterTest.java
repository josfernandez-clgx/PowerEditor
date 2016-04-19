package com.mindbox.pe.server.spi.db;

import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class AbstractUnmodifiableUserDataUpdaterTest extends AbstractTestBase {

	private static class TestImpl extends AbstractUnmodifiableUserDataUpdater {

		public void deleteRole(int roleID) throws SQLException {
		}

		public void insertRole(int roleID, String name, int[] privilegeIDs) throws SQLException {
		}

		public void insertRoleWithUnknownPrivileges(int roleID, String name, int[] privilegeIDs, List<String> unknownPrivsForRole) throws SQLException {
		}

		public void updateRole(int roleID, String name, int[] privilegeIDs) throws SQLException {
		}

		public void updateRoleWithUnknownPrivileges(int roleID, String name, int[] privilegeIDs, List<String> unknownPrivsForRole) throws SQLException {
		}

		@Override
		public void enableUser(String userID, String actingUserID) throws SQLException {
		}
	}

	private TestImpl testImpl;

	@Before
	public void setUp() throws Exception {
		this.testImpl = new TestImpl();
	}

	@Test
	public void testDeleteUserThrowsUnsupportedOperationException() throws Exception {
		assertThrowsException(testImpl, "deleteUser", new Class[] { String.class, String.class }, new Object[] { "", "user" }, UnsupportedOperationException.class);
	}

	@Test
	public void testInsertUserThrowsUnsupportedOperationException() throws Exception {
		assertThrowsException(testImpl, "insertUser", new Class[] { String.class, String.class, String.class, boolean.class, int.class, int[].class, List.class,

		String.class }, new Object[] { "", "", "", Boolean.TRUE, new Integer(0), new int[0], Collections.EMPTY_LIST, "user" }, UnsupportedOperationException.class);
	}

	@Test
	public void testUpdateFailedLoginCounterThrowsUnsupportedOperationException() throws Exception {
		assertThrowsException(testImpl, "updateFailedLoginCounter", new Class[] { String.class, int.class }, new Object[] { "", new Integer(0) }, UnsupportedOperationException.class);
	}

	@Test
	public void testUpdateUserThrowsUnsupportedOperationException() throws Exception {
		assertThrowsException(testImpl, "updateUser", new Class[] { String.class, String.class, String.class, boolean.class, int.class, int[].class, List.class, String.class }, new Object[] {
				"",
				"",
				"",
				Boolean.TRUE,
				new Integer(0),
				new int[0],
				Collections.EMPTY_LIST,
				"user" }, UnsupportedOperationException.class);
	}
}
