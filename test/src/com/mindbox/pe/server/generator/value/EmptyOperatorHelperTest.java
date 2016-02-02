package com.mindbox.pe.server.generator.value;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.server.config.RuleGenerationConfiguration;

public class EmptyOperatorHelperTest extends OperatorHelperTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("EmptyOperatorTest Tests");
		suite.addTestSuite(EmptyOperatorHelperTest.class);
		return suite;
	}

	public EmptyOperatorHelperTest(String name) {
		super(name);
	}

	public void testFormatForPatternWithValueAsStringConfigHappyCase() throws Exception {
		ruleGenerationConfiguration.getLHSValueConfig(RuleGenerationConfiguration.VAUE_TYPE_UNSPECIFIED).setValueAsString(true);
		testFormatForPattern(
				DEFAULT_VAR + " & \""
						+ ruleGenerationConfiguration.getLHSValueConfig(RuleGenerationConfiguration.VAUE_TYPE_UNSPECIFIED).getDeployValue() + "\"",
				Condition.OP_IS_EMPTY,
				false);
	}

	public void testFormatForPatternWithIsEmptyAndNotAsStringHappyCase() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR + " & "
						+ ruleGenerationConfiguration.getLHSValueConfig(RuleGenerationConfiguration.VAUE_TYPE_UNSPECIFIED).getDeployValue(),
				Condition.OP_IS_EMPTY,
				false);
	}

	public void testFormatForPatternWithIsEmptyAndAsStringHappyCase() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR + " & "
						+ ruleGenerationConfiguration.getLHSValueConfig(RuleGenerationConfiguration.VAUE_TYPE_UNSPECIFIED).getDeployValue(),
				Condition.OP_IS_EMPTY,
				true);
	}

	public void testFormatForPatternWithIsNotEmptyAndNotAsStringHappyCase() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR + " &:(/= " + DEFAULT_VAR + " "
						+ ruleGenerationConfiguration.getLHSValueConfig(RuleGenerationConfiguration.VAUE_TYPE_UNSPECIFIED).getDeployValue() + ")",
				Condition.OP_IS_NOT_EMPTY,
				false);
	}

	public void testFormatForPatternWithIsNotEmptyAndAsStringHappyCase() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR + " &:(/= " + DEFAULT_VAR + " " +
						ruleGenerationConfiguration.getLHSValueConfig(RuleGenerationConfiguration.VAUE_TYPE_UNSPECIFIED).getDeployValue() + ")",
				Condition.OP_IS_NOT_EMPTY,
				true);
	}

	private void testFormatForPattern(String expected, int op, boolean asString) throws Exception {
		testFormatForPattern(expected, new Object(), op, asString, null);
	}

	protected void setUp() throws Exception {
		super.setUp();
		operatorHelper = createOperatorHelper("com.mindbox.pe.server.generator.value.EmptyOperatorHelper", TemplateUsageType.getAllInstances()[0]);
	}

	protected void tearDown() throws Exception {
		// Tear downs for EmptyOperatorTest
		super.tearDown();
	}
}
