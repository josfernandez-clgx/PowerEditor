package com.mindbox.pe.server.db.loaders;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class UserSecurityLoaderTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("UserSecurityLoaderTest Tests");
		suite.addTestSuite(UserSecurityLoaderTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public UserSecurityLoaderTest(String name) {
		super(name);
	}

	public void testCacheUserObjectsReturnsTrue() throws Exception {
		assertTrue(new UserSecurityLoader().cacheUserObjects());
	}

	public void testGetRolesThrowsRuntimeException() throws Exception {
		assertThrowsException(new UserSecurityLoader(), "getRoles", new Class[] { String.class }, new Object[] { "str" }, RuntimeException.class);
	}
}
