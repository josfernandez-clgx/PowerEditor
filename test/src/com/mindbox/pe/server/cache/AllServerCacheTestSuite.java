package com.mindbox.pe.server.cache;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Collection of all server test cases.
 * All tests in this collection calls server code directory, bypassing communication layer.
 * @author Gene Kim
 * @author MindBox
 * @since PowerEditor 3.2.0
 */
public final class AllServerCacheTestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Server Cache Tests");
		suite.addTest(DateSynonymManagerTest.suite());
		suite.addTest(DeploymentManagerTest.suite());
		suite.addTest(EntityManagerTest.suite());
		suite.addTest(GridManagerTest.suite());
		suite.addTest(GuidelineTemplateManagerTest.suite());
		suite.addTest(LockManagerTest.suite());
		suite.addTest(ParameterManagerTest.suite());
		suite.addTest(SecurityCacheManagerTest.suite());
		return suite;
	}
}
