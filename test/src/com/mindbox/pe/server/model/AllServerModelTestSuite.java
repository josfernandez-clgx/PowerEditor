package com.mindbox.pe.server.model;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllServerModelTestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AllServerModelTestSuite Tests");
		suite.addTest(AbstractTypeIDIdentityTest.suite());
		suite.addTest(TimeSliceTest.suite());
		suite.addTest(TimeSliceContainerTest.suite());
		return suite;
	}

}
