package com.mindbox.ftest.pe.rulegen;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Do not run this as a part of automated unit tests.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class AllRuleGenerationFunctionalTestSuite {

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Server Rule Generation Tests");
		suite.addTest(FullRegressionWithRebuildTests.suite());
//		suite.addTest(DeployedRulesBatchTest.suite());
		//suite.addTest(DeployProcessDataTest.suite());
		//suite.addTest(DeployGuidelineDataTest.suite());
		//suite.addTest(DeployCBRDataTest.suite());
		return suite;
	}

}
