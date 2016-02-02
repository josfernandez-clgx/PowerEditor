package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.LineagePatternConfigSet;

public class FunctionCallPatternFactoryForRHSActionTest extends AbstractFunctionCallPatternFactoryTest {

	public static Test suite() {
		TestSuite suite = new TestSuite("FunctionCallPatternFactoryTest Tests");
		suite.addTestSuite(FunctionCallPatternFactoryForRHSActionTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public FunctionCallPatternFactoryForRHSActionTest(String name) {
		super(name);
	}

	public void testCreateFunctionCallPatternWithCreate$HappyCase() throws Exception {
		mockControl.expectAndReturn(helperMock.makeAEName("$create"), "$create");
		mockControl.expectAndReturn(helperMock.makeAEName("rule_name"), "rule-name");
		mockControl.expectAndReturn(helperMock.makeAEName("create$"), "create$");

		FunctionCallPattern functionCallPattern = (FunctionCallPattern) testCreateFunctionCallPatternForSingleArgument(
				"$create,rule_name,\"%ruleName%\",create$",
				false,
				FunctionCallPattern.class);
		assertEquals(2, functionCallPattern.argSize());
		assertTrue(functionCallPattern.getArgAt(0) instanceof StaticFunctionArgument);
		assertEquals("rule-name", ((StaticFunctionArgument) functionCallPattern.getArgAt(0)).getValue());
		assertTrue(functionCallPattern.getArgAt(1) instanceof RuleNameValueSlot);
	}

	public void testCreateFunctionCallPatternWithColumnIDArgumentCreatesColumnReferenceValueSlot() throws Exception {
		int colNo = ObjectMother.createInt();
		ColumnReferencePatternValueSlot functionArgument = (ColumnReferencePatternValueSlot) testCreateFunctionCallPatternForSingleArgument(
				"column " + colNo,
				ColumnReferencePatternValueSlot.class);
		assertEquals(colNo, functionArgument.getColumnNo());
	}

	public void testCreateFunctionCallPatternWithParameterArgumentCallsFunctionArgumentFactory() throws Exception {
		int paramNo = ObjectMother.createInt();
		try {
			testCreateFunctionCallPatternForSingleArgument("parameter " + paramNo, StaticFunctionArgument.class);
			fail("Expected IndexOutOfBoundsException");
		}
		catch (IndexOutOfBoundsException ex) {
			// expected
		}
	}

	public void testCreateFunctionCallPatternWithCellValueArgumentCreatesCellValueValueSlot() throws Exception {
		testCreateFunctionCallPatternForSingleArgument("cellValue", CellValueValueSlot.class);
	}

	public void testCreateFunctionCallPatternWithRuleNameArgumentCreatesRuleNameValueSlot() throws Exception {
		testCreateFunctionCallPatternForSingleArgument("ruleName", RuleNameValueSlot.class);
	}

	public void testCreateFunctionCallPatternWithTemplateIDArgumentHappyCase() throws Exception {
		testCreateFunctionCallPatternForSingleArgumentForStaticFunctionArg("templateID", String.valueOf(template.getID()));
	}

	public void testCreateFunctionCallPatternWithTemplateNameArgumentHappyCase() throws Exception {
		testCreateFunctionCallPatternForSingleArgumentForStaticFunctionArg("templateName", "\"" + template.getName() + "\"");
	}

	public void testCreateFunctionCallPatternWithRowNumberArgumentCreatesRowNumberValueSlot() throws Exception {
		testCreateFunctionCallPatternForSingleArgument("rowNumber", RowNumberValueSlot.class);
	}

	public void testCreateFunctionCallPatternWithLineageIDArgumentHappyCase() throws Exception {
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				1);

		LineagePatternConfigSet lineagePatternSet = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getLineagePatternConfigSet();
		testCreateFunctionCallPatternForSingleArgumentForStaticFunctionArg("lineageID", "?"
				+ lineagePatternSet.getLineagePatternConfigs(lineagePatternSet.getPrefix()[0])[0].getVariable());
	}

	public void testCreateFunctionCallPatternWithLineageIDArgumentAndNoLineageAddsNoArgument() throws Exception {
		// TODO Kim: find a way to test this
	}

	public void testCreateFunctionCallPatternWithActivationDateArgumentCreatesDatePropertyValueSlot() throws Exception {
		testCreateFunctionCallPatternForSingleArgumentForDatePropertyValueSlot(
				"activationDate",
				DatePropertyValueSlot.DATE_TYPE_ACTIVATION_DATE);
	}

	public void testCreateFunctionCallPatternWithExpirationDateArgumentCreatesDatePropertyValueSlot() throws Exception {
		testCreateFunctionCallPatternForSingleArgumentForDatePropertyValueSlot(
				"expirationDate",
				DatePropertyValueSlot.DATE_TYPE_EXPIRATION_DATE);
	}

	public void testCreateFunctionCallPatternWithCategoryIDArgumentCreatesCategoryIDValueSlot() throws Exception {
		testCreateFunctionCallPatternForSingleArgument("categoryID", CategoryIDValueSlot.class);
	}

	public void testCreateFunctionCallPatternWithCategoryNameArgumentCreatesCategoryNameValueSlot() throws Exception {
		testCreateFunctionCallPatternForSingleArgument("categoryName", CategoryNameValueSlot.class);
	}

	public void testCreateFunctionCallPatternWithEntityIDArgumentCreatesEntityIDValueSlot() throws Exception {
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				1);
		mockControl.expectAndReturn(helperMock.asVariableName(domainAttribute.getName()), "?" + domainAttribute.getName());
		mockControl.expectAndReturn(helperMock.findDomainAttributeForContextElement(
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getControlPatternConfig(),
				entityType.getName()), domainAttribute);

		testCreateFunctionCallPatternForSingleArgument(entityType.getName() + "ID", EntityIDValueSlot.class);
	}

	public void testCreateFunctionCallPatternWithEntityIDArgumentWithInvalidEntityTypeReportsError() throws Exception {
		String invalidToken = "\"%dummyID%\"";
		helperMock.reportError("WARNING: " + invalidToken + " is not valid");

		testCreateFunctionCallPattern(invalidToken, 0);
	}

	public void testCreateFunctionCallPatternWithContextArgumentCreatesContextValueSlot() throws Exception {
		testCreateFunctionCallPatternForSingleArgument("context", ContextValueSlot.class);
	}

	public void testCreateFunctionCallPatternWithNonLiteralArgumentHappyCase() throws Exception {
		String argStr = "some_arg" + ObjectMother.createInt();
		mockControl.expectAndReturn(helperMock.makeAEName(argStr), argStr);

		testCreateFunctionCallPatternForSingleArgumentForStaticFunctionArg(argStr, false, argStr);
	}

	public void testCreateFunctionCallPatternWithMultipleArgumentHappyCase() throws Exception {
		mockControl.expectAndReturn(helperMock.makeAEName("arg1"), "arg1");
		mockControl.expectAndReturn(helperMock.makeAEName("arg2"), "arg2");
		mockControl.expectAndReturn(helperMock.makeAEName("arg3"), "arg3");

		FunctionCallPattern functionCallPattern = testCreateFunctionCallPattern("arg1,arg2,arg3", 3);
		assertTrue(functionCallPattern.getArgAt(0) instanceof StaticFunctionArgument);
		assertEquals("arg1", ((StaticFunctionArgument) functionCallPattern.getArgAt(0)).getValue());
		assertTrue(functionCallPattern.getArgAt(1) instanceof StaticFunctionArgument);
		assertEquals("arg2", ((StaticFunctionArgument) functionCallPattern.getArgAt(1)).getValue());
		assertTrue(functionCallPattern.getArgAt(2) instanceof StaticFunctionArgument);
		assertEquals("arg3", ((StaticFunctionArgument) functionCallPattern.getArgAt(2)).getValue());
	}

	private void testCreateFunctionCallPatternForSingleArgumentForDatePropertyValueSlot(String argumentString, String expectedDateType)
			throws Exception {
		DatePropertyValueSlot functionArgument = (DatePropertyValueSlot) testCreateFunctionCallPatternForSingleArgument(
				argumentString,
				DatePropertyValueSlot.class);
		assertEquals(expectedDateType, functionArgument.getSlotValue());
	}

	private void testCreateFunctionCallPatternForSingleArgumentForStaticFunctionArg(String argumentString, String expectedStaticText)
			throws Exception {
		testCreateFunctionCallPatternForSingleArgumentForStaticFunctionArg(argumentString, true, expectedStaticText);
	}

	private void testCreateFunctionCallPatternForSingleArgumentForStaticFunctionArg(String argumentString, boolean qualify,
			String expectedStaticText) throws Exception {
		StaticFunctionArgument functionArgument = (StaticFunctionArgument) testCreateFunctionCallPatternForSingleArgument(
				argumentString,
				qualify,
				StaticFunctionArgument.class);
		assertEquals(expectedStaticText, functionArgument.getValue());
	}

	private FunctionArgument testCreateFunctionCallPatternForSingleArgument(String argumentString, Class<?> functionArgClass) throws Exception {
		return testCreateFunctionCallPatternForSingleArgument(argumentString, true, functionArgClass);
	}

	private FunctionArgument testCreateFunctionCallPatternForSingleArgument(String argumentString, boolean qualify, Class<?> functionArgClass)
			throws Exception {
		FunctionCallPattern functionCallPattern = testCreateFunctionCallPattern((qualify
				? "(\"%" + argumentString + "%\")"
				: argumentString), 1);
		assertTrue(functionArgClass.isInstance(functionCallPattern.getArgAt(0)));
		return functionCallPattern.getArgAt(0);
	}

	private FunctionCallPattern testCreateFunctionCallPattern(String argumentString, int expectedArgSize) throws Exception {
		String functionName = "action" + ObjectMother.createInt();
		actionTypeDefinition.setDeploymentRule(functionName + "(" + argumentString + ")");
		GuidelineFunctionManager.getInstance().insertActionTypeDefinition(actionTypeDefinition);
		RuleDefinition ruleDefinition = ObjectMother.attachAction(ObjectMother.createRuleDefinition(), actionTypeDefinition);
		ruleDefinition.setUsageType(usageType);

		mockControl.expectAndReturn(helperMock.makeAEName(functionName), functionName);
		mockControl.replay();

		FunctionCallPattern functionCallPattern = functionCallPatternFactory.createFunctionCallPattern(
				template,
				ruleDefinition,
				RuleObjectMother.createLHSPatternList());
		mockControl.verify();

		assertEquals(functionName, functionCallPattern.getFunctionName());
		assertEquals(expectedArgSize, functionCallPattern.argSize());
		return functionCallPattern;
	}

	protected boolean isForTestCondition() {
		return false;
	}
}
