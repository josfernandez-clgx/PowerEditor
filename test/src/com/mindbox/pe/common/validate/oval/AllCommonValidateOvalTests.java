package com.mindbox.pe.common.validate.oval;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 */
public class AllCommonValidateOvalTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(AllCommonValidateOvalTests.class.getName());
		return suite;
	}

}
