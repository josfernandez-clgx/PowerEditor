package com.mindbox.pe.server.generator.rule;

import static com.mindbox.pe.server.ServerTestObjectMother.attachAction;
import static com.mindbox.pe.server.ServerTestObjectMother.createRuleDefinition;
import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.LineagePatternConfigHelper;

public class FunctionCallPatternFactoryForRHSActionTest extends AbstractFunctionCallPatternFactoryTest {

	protected boolean isForTestCondition() {
		return false;
	}

	private FunctionCallPattern testCreateFunctionCallPattern(String argumentString, int expectedArgSize) throws Exception {
		String functionName = "action" + createInt();
		actionTypeDefinition.setDeploymentRule(functionName + "(" + argumentString + ")");
		GuidelineFunctionManager.getInstance().insertActionTypeDefinition(actionTypeDefinition);
		RuleDefinition ruleDefinition = attachAction(createRuleDefinition(), actionTypeDefinition);
		ruleDefinition.setUsageType(usageType);

		expect(helperMock.makeAEName(functionName)).andReturn(functionName);
		replay(helperMock);

		FunctionCallPattern functionCallPattern = functionCallPatternFactory.createFunctionCallPattern(template, ruleDefinition, RuleObjectMother.createLHSPatternList());
		verify(helperMock);

		assertEquals(functionName, functionCallPattern.getFunctionName());
		assertEquals(expectedArgSize, functionCallPattern.argSize());
		return functionCallPattern;
	}

	private FunctionArgument testCreateFunctionCallPatternForSingleArgument(String argumentString, boolean qualify, Class<?> functionArgClass) throws Exception {
		FunctionCallPattern functionCallPattern = testCreateFunctionCallPattern((qualify ? "(\"%" + argumentString + "%\")" : argumentString), 1);
		assertTrue(functionArgClass.isInstance(functionCallPattern.getArgAt(0)));
		return functionCallPattern.getArgAt(0);
	}

	private FunctionArgument testCreateFunctionCallPatternForSingleArgument(String argumentString, Class<?> functionArgClass) throws Exception {
		return testCreateFunctionCallPatternForSingleArgument(argumentString, true, functionArgClass);
	}

	private void testCreateFunctionCallPatternForSingleArgumentForDatePropertyValueSlot(String argumentString, String expectedDateType) throws Exception {
		DatePropertyValueSlot functionArgument = (DatePropertyValueSlot) testCreateFunctionCallPatternForSingleArgument(argumentString, DatePropertyValueSlot.class);
		assertEquals(expectedDateType, functionArgument.getSlotValue());
	}

	private void testCreateFunctionCallPatternForSingleArgumentForStaticFunctionArg(String argumentString, boolean qualify, String expectedStaticText) throws Exception {
		StaticFunctionArgument functionArgument = (StaticFunctionArgument) testCreateFunctionCallPatternForSingleArgument(argumentString, qualify, StaticFunctionArgument.class);
		assertEquals(expectedStaticText, functionArgument.getValue());
	}

	private void testCreateFunctionCallPatternForSingleArgumentForStaticFunctionArg(String argumentString, String expectedStaticText) throws Exception {
		testCreateFunctionCallPatternForSingleArgumentForStaticFunctionArg(argumentString, true, expectedStaticText);
	}

	@Test
	public void testCreateFunctionCallPatternWithActivationDateArgumentCreatesDatePropertyValueSlot() throws Exception {
		testCreateFunctionCallPatternForSingleArgumentForDatePropertyValueSlot("activationDate", DatePropertyValueSlot.DATE_TYPE_ACTIVATION_DATE);
	}

	@Test
	public void testCreateFunctionCallPatternWithCategoryIDArgumentCreatesCategoryIDValueSlot() throws Exception {
		testCreateFunctionCallPatternForSingleArgument("categoryID", CategoryIDValueSlot.class);
	}

	@Test
	public void testCreateFunctionCallPatternWithCategoryNameArgumentCreatesCategoryNameValueSlot() throws Exception {
		testCreateFunctionCallPatternForSingleArgument("categoryName", CategoryNameValueSlot.class);
	}

	@Test
	public void testCreateFunctionCallPatternWithCellValueArgumentCreatesCellValueValueSlot() throws Exception {
		testCreateFunctionCallPatternForSingleArgument("cellValue", CellValueValueSlot.class);
	}

	@Test
	public void testCreateFunctionCallPatternWithColumnIDArgumentCreatesColumnReferenceValueSlot() throws Exception {
		int colNo = createInt();
		ColumnReferencePatternValueSlot functionArgument = (ColumnReferencePatternValueSlot) testCreateFunctionCallPatternForSingleArgument("column " + colNo, ColumnReferencePatternValueSlot.class);
		assertEquals(colNo, functionArgument.getColumnNo());
	}

	@Test
	public void testCreateFunctionCallPatternWithContextArgumentCreatesContextValueSlot() throws Exception {
		testCreateFunctionCallPatternForSingleArgument("context", ContextValueSlot.class);
	}

	@Test
	public void testCreateFunctionCallPatternWithCreate$HappyCase() throws Exception {
		expect(helperMock.makeAEName("$create")).andReturn("$create");
		expect(helperMock.makeAEName("rule_name")).andReturn("rule-name");
		expect(helperMock.makeAEName("create$")).andReturn("create$");

		FunctionCallPattern functionCallPattern = (FunctionCallPattern) testCreateFunctionCallPatternForSingleArgument("$create,rule_name,\"%ruleName%\",create$", false, FunctionCallPattern.class);
		assertEquals(2, functionCallPattern.argSize());
		assertTrue(functionCallPattern.getArgAt(0) instanceof StaticFunctionArgument);
		assertEquals("rule-name", ((StaticFunctionArgument) functionCallPattern.getArgAt(0)).getValue());
		assertTrue(functionCallPattern.getArgAt(1) instanceof RuleNameValueSlot);
	}

	@Test
	public void testCreateFunctionCallPatternWithEntityIDArgumentCreatesEntityIDValueSlot() throws Exception {
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(1);
		expect(helperMock.asVariableName(domainAttribute.getName())).andReturn("?" + domainAttribute.getName());
		expect(helperMock.findDomainAttributeForContextElement(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper().getControlPatternConfig(), entityType.getName())).andReturn(
				domainAttribute);

		testCreateFunctionCallPatternForSingleArgument(entityType.getName() + "ID", EntityIDValueSlot.class);
	}

	@Test
	public void testCreateFunctionCallPatternWithEntityIDArgumentWithInvalidEntityTypeReportsError() throws Exception {
		String invalidToken = "\"%dummyID%\"";
		helperMock.reportError("WARNING: " + invalidToken + " is not valid");

		testCreateFunctionCallPattern(invalidToken, 0);
	}

	@Test
	public void testCreateFunctionCallPatternWithExpirationDateArgumentCreatesDatePropertyValueSlot() throws Exception {
		testCreateFunctionCallPatternForSingleArgumentForDatePropertyValueSlot("expirationDate", DatePropertyValueSlot.DATE_TYPE_EXPIRATION_DATE);
	}

	@Test
	public void testCreateFunctionCallPatternWithLineageIDArgumentHappyCase() throws Exception {
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(1);

		LineagePatternConfigHelper lineagePatternSet = ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper().getLineagePatternConfigSet();
		testCreateFunctionCallPatternForSingleArgumentForStaticFunctionArg("lineageID", "?" + lineagePatternSet.getLineagePatternConfigs(lineagePatternSet.getPrefix().get(0)).get(0).getVariable());
	}

	@Test
	public void testCreateFunctionCallPatternWithMultipleArgumentHappyCase() throws Exception {
		expect(helperMock.makeAEName("arg1")).andReturn("arg1");
		expect(helperMock.makeAEName("arg2")).andReturn("arg2");
		expect(helperMock.makeAEName("arg3")).andReturn("arg3");

		FunctionCallPattern functionCallPattern = testCreateFunctionCallPattern("arg1,arg2,arg3", 3);
		assertTrue(functionCallPattern.getArgAt(0) instanceof StaticFunctionArgument);
		assertEquals("arg1", ((StaticFunctionArgument) functionCallPattern.getArgAt(0)).getValue());
		assertTrue(functionCallPattern.getArgAt(1) instanceof StaticFunctionArgument);
		assertEquals("arg2", ((StaticFunctionArgument) functionCallPattern.getArgAt(1)).getValue());
		assertTrue(functionCallPattern.getArgAt(2) instanceof StaticFunctionArgument);
		assertEquals("arg3", ((StaticFunctionArgument) functionCallPattern.getArgAt(2)).getValue());
	}

	@Test
	public void testCreateFunctionCallPatternWithNonLiteralArgumentHappyCase() throws Exception {
		String argStr = "some_arg" + createInt();
		expect(helperMock.makeAEName(argStr)).andReturn(argStr);

		testCreateFunctionCallPatternForSingleArgumentForStaticFunctionArg(argStr, false, argStr);
	}

	@Test
	public void testCreateFunctionCallPatternWithParameterArgumentCallsFunctionArgumentFactory() throws Exception {
		int paramNo = createInt();
		try {
			testCreateFunctionCallPatternForSingleArgument("parameter " + paramNo, StaticFunctionArgument.class);
			fail("Expected IndexOutOfBoundsException");
		}
		catch (IndexOutOfBoundsException ex) {
			// expected
		}
	}

	@Test
	public void testCreateFunctionCallPatternWithRowNumberArgumentCreatesRowNumberValueSlot() throws Exception {
		testCreateFunctionCallPatternForSingleArgument("rowNumber", RowNumberValueSlot.class);
	}

	@Test
	public void testCreateFunctionCallPatternWithRuleNameArgumentCreatesRuleNameValueSlot() throws Exception {
		testCreateFunctionCallPatternForSingleArgument("ruleName", RuleNameValueSlot.class);
	}

	@Test
	public void testCreateFunctionCallPatternWithTemplateIDArgumentHappyCase() throws Exception {
		testCreateFunctionCallPatternForSingleArgumentForStaticFunctionArg("templateID", String.valueOf(template.getID()));
	}

	@Test
	public void testCreateFunctionCallPatternWithTemplateNameArgumentHappyCase() throws Exception {
		testCreateFunctionCallPatternForSingleArgumentForStaticFunctionArg("templateName", "\"" + template.getName() + "\"");
	}
}
