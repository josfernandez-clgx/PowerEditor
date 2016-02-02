package com.mindbox.pe.common.validate;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 */
public class AllCommonValidateTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(AllCommonValidateTests.class.getName());
		suite.addTest(DataTypeCompatibilityValidatorTest.suite());
		suite.addTest(MessageDetailTest.suite());
		return suite;
	}

}
