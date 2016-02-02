package com.mindbox.pe.server.bizlogic;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Collection of all server bizlogic test cases.
 * 
 * @author Gene Kim
 * @author MindBox
 * @since PowerEditor 4.5.0
 */
public final class AllServerBizLogicTestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Server BizLogic Tests");
		suite.addTest(BizActionCoordinatorTest.suite());
		suite.addTest(GridActionCoordinatorTest.suite());
		suite.addTest(SearchCoordinatorTest.suite());
		return suite;
	}
}
