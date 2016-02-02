package com.mindbox.pe.server.ldap;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class UserObjectUserAuthenticationProviderTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("UserObjectUserAuthenticationProviderTest Tests");
		suite.addTestSuite(UserObjectUserAuthenticationProviderTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public UserObjectUserAuthenticationProviderTest(String name) {
		super(name);
	}
	
	public void testArePasswordsStoredExternallyReturnsTrueAlways() throws Exception {
		assertTrue(new UserObjectUserAuthenticationProvider().arePasswordsStoredExternally());
	}
}
