package com.mindbox.pe.server.db;

import com.mindbox.pe.server.db.loaders.AllDBLoadersTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Collection of all server test cases.
 * All tests in this collection calls server code directory, bypassing communication layer.
 * @author Gene Kim
 */
public final class AllDBTestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite("All Database Tests");
		suite.addTest(DBUtilTest.suite());
		suite.addTest(DefaultUserManagementProviderTest.suite());
		suite.addTest(LDAPUserManagementProviderTest.suite());
		suite.addTest(AllDBLoadersTestSuite.suite());
		return suite;
	}
}
