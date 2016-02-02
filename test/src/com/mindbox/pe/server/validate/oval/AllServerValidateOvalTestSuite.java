package com.mindbox.pe.server.validate.oval;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public final class AllServerValidateOvalTestSuite {

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Server Validate Oval Tests");
		suite.addTest(HasValidValuesForEntityPropertiesCheckTest.suite());
		suite.addTest(HasValidValuesPropertiesConstraintCheckTest.suite());
		suite.addTest(OValDataValidatorTest.suite());
		suite.addTest(UniqueNameConstraintCheckTest.suite());
		return suite;
	}
}
