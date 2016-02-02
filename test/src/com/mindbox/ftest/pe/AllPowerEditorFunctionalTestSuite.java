package com.mindbox.ftest.pe;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.mindbox.ftest.pe.rulegen.AllRuleGenerationFunctionalTestSuite;

/**
 * All PE Functional Tests.
 * Do not run this as a part of automated unit tests.
 */
public class AllPowerEditorFunctionalTestSuite {

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("PowerEditor Functional Tests");
		suite.addTest(AllRuleGenerationFunctionalTestSuite.suite());
		return suite;
	}

}
