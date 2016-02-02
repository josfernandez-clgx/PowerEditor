package com.mindbox.pe.server.spi.db;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class AbstractUnmodifiableUserDataUpdaterTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractUnmodifiableUserDataUpdaterTest Tests");
		suite.addTestSuite(AbstractUnmodifiableUserDataUpdaterTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private static class TestImpl extends AbstractUnmodifiableUserDataUpdater {

		public void deleteRole(int roleID) throws SQLException {
		}

		public void insertRole(int roleID, String name, int[] privilegeIDs) throws SQLException {
		}

		public void updateRole(int roleID, String name, int[] privilegeIDs) throws SQLException {
		}

		public void insertRoleWithUnknownPrivileges(int roleID, String name, int[] privilegeIDs, List<String> unknownPrivsForRole)
				throws SQLException {
		}

		public void updateRoleWithUnknownPrivileges(int roleID, String name, int[] privilegeIDs, List<String> unknownPrivsForRole)
				throws SQLException {
		}

		@Override
		public void enableUser(String userID, String actingUserID) throws SQLException {
		}

	}

	private TestImpl testImpl;

	public AbstractUnmodifiableUserDataUpdaterTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.testImpl = new TestImpl();
	}

	public void testDeleteUserThrowsUnsupportedOperationException() throws Exception {
		assertThrowsException(
				testImpl,
				"deleteUser",
				new Class[] { String.class },
				new Object[] { "" },
				UnsupportedOperationException.class);
	}

	public void testInsertUserThrowsUnsupportedOperationException() throws Exception {
		assertThrowsException(
				testImpl,
				"insertUser",
				new Class[] { String.class, String.class, String.class, boolean.class, int.class, int[].class, List.class },
				new Object[] { "", "", "", Boolean.TRUE, new Integer(0), new int[0], Collections.EMPTY_LIST },
				UnsupportedOperationException.class);
	}

	public void testUpdateUserThrowsUnsupportedOperationException() throws Exception {
		assertThrowsException(
				testImpl,
				"updateUser",
				new Class[] { String.class, String.class, String.class, boolean.class, int.class, int[].class, List.class },
				new Object[] { "", "", "", Boolean.TRUE, new Integer(0), new int[0], Collections.EMPTY_LIST },
				UnsupportedOperationException.class);
	}

	public void testUpdateFailedLoginCounterThrowsUnsupportedOperationException() throws Exception {
		assertThrowsException(testImpl, "updateFailedLoginCounter", new Class[] { String.class, int.class }, new Object[] {
				"",
				new Integer(0) }, UnsupportedOperationException.class);
	}
}
