package com.mindbox.pe.server.servlet.handlers;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class AllServletHandlersTestSuite {

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(AllServletHandlersTestSuite.class.getName());
		suite.addTest(DateSynonymInUseRequestHandlerTest.suite());
		suite.addTest(FetchCustomReportsRequestHandlerTest.suite());
		suite.addTest(FetchUserProfileRequestHandlerTest.suite());
		suite.addTest(LoginAttemptTest.suite());
		suite.addTest(LoginRequestHandlerTest.suite());
		suite.addTest(PasswordChangeRequestHandlerTest.suite());
		suite.addTest(SaveRequestHandlerTest.suite());
		return suite;
	}
}
