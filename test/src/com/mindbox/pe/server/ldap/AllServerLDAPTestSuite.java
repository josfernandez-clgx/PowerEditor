package com.mindbox.pe.server.ldap;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class AllServerLDAPTestSuite {

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(AllServerLDAPTestSuite.class.getName());
		suite.addTest(DefaultUserAuthenticationProviderTest.suite());
		suite.addTest(UserObjectUserAuthenticationProviderTest.suite());
		return suite;
	}
}
