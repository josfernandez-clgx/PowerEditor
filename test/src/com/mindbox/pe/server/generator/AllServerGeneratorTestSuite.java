package com.mindbox.pe.server.generator;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.server.generator.processor.AllServerGeneratorProcessorTestSuite;
import com.mindbox.pe.server.generator.rule.AllServerGeneratorRuleTests;
import com.mindbox.pe.server.generator.value.AllServerGeneratorValueTests;

/**
 * Collection of all server test cases. All tests in this collection calls server code directory,
 * bypassing communication layer.
 * 
 */
public final class AllServerGeneratorTestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Server Generator Tests");
		suite.addTest(AbstractGenerateParmsTest.suite());
		suite.addTest(GuidelineGenerateParamsTest.suite());
		suite.addTest(GuidelineRuleGeneratorTest.suite());
		suite.addTest(GuidelinePostProcessItemHelperTest.suite());
		suite.addTest(OutputControllerTest.suite());
		suite.addTest(ParameterGeneratorTest.suite());
		suite.addTest(RuleGeneratorHelperTest.suite());
		suite.addTest(RuleGeneratorHelperWithTestConfigTest.suite());
		suite.addTest(TimeSliceGeneratorTest.suite());
		suite.addTest(AllServerGeneratorProcessorTestSuite.suite());
		suite.addTest(AllServerGeneratorRuleTests.suite());
		suite.addTest(AllServerGeneratorValueTests.suite());
		return suite;
	}
}
