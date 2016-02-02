package com.mindbox.pe.server.validate;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.mindbox.pe.server.validate.oval.AllServerValidateOvalTestSuite;

public final class AllServerValidateTestSuite {

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Server Validate Tests");
		suite.addTest(AllServerValidateOvalTestSuite.suite());
		suite.addTest(DefaultDataValidatorTest.suite());
		return suite;
	}
}
