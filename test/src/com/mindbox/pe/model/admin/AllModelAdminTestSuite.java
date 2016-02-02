package com.mindbox.pe.model.admin;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllModelAdminTestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AllModelAdminTestSuite Tests");
		suite.addTest(UserDataTest.suite());
        suite.addTest(RoleTest.suite());
		return suite;
	}

}
