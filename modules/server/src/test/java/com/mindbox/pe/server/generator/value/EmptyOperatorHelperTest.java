package com.mindbox.pe.server.generator.value;

import org.junit.Test;

import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.server.config.RuleGenerationConfigHelper;

public class EmptyOperatorHelperTest extends AbstractOperatorHelperTestBase {

	@Test
	public void testFormatForPatternWithValueAsStringConfigHappyCase() throws Exception {
		ruleGenerationConfiguration.getLHSValueConfig(RuleGenerationConfigHelper.VAUE_TYPE_UNSPECIFIED).setValueAsString(Boolean.TRUE);
		testFormatForPattern(
				DEFAULT_VAR + " & \"" + ruleGenerationConfiguration.getLHSValueConfig(RuleGenerationConfigHelper.VAUE_TYPE_UNSPECIFIED).getDeployValue() + "\"",
				Condition.OP_IS_EMPTY,
				false);
	}

	@Test
	public void testFormatForPatternWithIsEmptyAndNotAsStringHappyCase() throws Exception {
		testFormatForPattern(DEFAULT_VAR + " & " + ruleGenerationConfiguration.getLHSValueConfig(RuleGenerationConfigHelper.VAUE_TYPE_UNSPECIFIED).getDeployValue(), Condition.OP_IS_EMPTY, false);
	}

	@Test
	public void testFormatForPatternWithIsEmptyAndAsStringHappyCase() throws Exception {
		testFormatForPattern(DEFAULT_VAR + " & " + ruleGenerationConfiguration.getLHSValueConfig(RuleGenerationConfigHelper.VAUE_TYPE_UNSPECIFIED).getDeployValue(), Condition.OP_IS_EMPTY, true);
	}

	@Test
	public void testFormatForPatternWithIsNotEmptyAndNotAsStringHappyCase() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR + " &:(/= " + DEFAULT_VAR + " " + ruleGenerationConfiguration.getLHSValueConfig(RuleGenerationConfigHelper.VAUE_TYPE_UNSPECIFIED).getDeployValue() + ")",
				Condition.OP_IS_NOT_EMPTY,
				false);
	}

	@Test
	public void testFormatForPatternWithIsNotEmptyAndAsStringHappyCase() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR + " &:(/= " + DEFAULT_VAR + " " + ruleGenerationConfiguration.getLHSValueConfig(RuleGenerationConfigHelper.VAUE_TYPE_UNSPECIFIED).getDeployValue() + ")",
				Condition.OP_IS_NOT_EMPTY,
				true);
	}

	private void testFormatForPattern(String expected, int op, boolean asString) throws Exception {
		testFormatForPattern(expected, new Object(), op, asString, null);
	}

	public void setUp() throws Exception {
		super.setUp();
		operatorHelper = createOperatorHelper("com.mindbox.pe.server.generator.value.EmptyOperatorHelper", TemplateUsageType.getAllInstances()[0]);
	}

	public void tearDown() throws Exception {
		// Tear downs for EmptyOperatorTest
		super.tearDown();
	}
}
