package com.mindbox.pe.server.db.loaders;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Collection of all server test cases.
 * All tests in this collection calls server code directory, bypassing communication layer.
 * @author Gene Kim
 * @author MindBox, Inc
 */
public final class AllDBLoadersTestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite("All DB Loaders Tests");
		suite.addTest(GridLoaderTest.suite());
		suite.addTest(UserSecurityLoaderTest.suite());
		return suite;
	}
}
