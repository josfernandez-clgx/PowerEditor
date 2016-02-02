package com.mindbox.pe.server.servlet;

import com.mindbox.pe.server.servlet.handlers.AllServletHandlersTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class AllServletTestSuite {

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(AllServletTestSuite.class.getName());
		suite.addTest(AllServletHandlersTestSuite.suite());
		suite.addTest(HandlerFactoryTest.suite());
		suite.addTest(LoaderTest.suite());
		return suite;
	}
}
