package com.mindbox.pe.server.generator.value;

import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

public class EqualityOperatorHelperTest extends OperatorHelperTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("EqualityOperatorHelperTest Tests");
		suite.addTestSuite(EqualityOperatorHelperTest.class);
		return suite;
	}

	private DomainClass domainClass;

	public EqualityOperatorHelperTest(String name) {
		super(name);
	}

	public void testFormatForPatternWithBooleanAttributePositiveCase() throws Exception {
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.BOOLEAN);
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " " + RuleGeneratorHelper.AE_TRUE + ")",
				Boolean.TRUE,
				Condition.OP_EQUAL,
				false,
				ObjectMother.createReference(domainClass));
		
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " " + RuleGeneratorHelper.AE_TRUE + ")",
				"Yes",
				Condition.OP_EQUAL,
				false,
				ObjectMother.createReference(domainClass));
		
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " " + RuleGeneratorHelper.AE_TRUE + ")",
				"YES",
				Condition.OP_EQUAL,
				false,
				ObjectMother.createReference(domainClass));
	}
	
	public void testFormatForPatternWithBooleanAttributeNegativeCase() throws Exception {
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.BOOLEAN);
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " " + RuleGeneratorHelper.AE_NIL + ")",
				Boolean.FALSE,
				Condition.OP_EQUAL,
				false,
				ObjectMother.createReference(domainClass));
		
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " " + RuleGeneratorHelper.AE_NIL + ")",
				"No",
				Condition.OP_EQUAL,
				false,
				ObjectMother.createReference(domainClass));
		
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " " + RuleGeneratorHelper.AE_NIL + ")",
				"NO",
				Condition.OP_EQUAL,
				false,
				ObjectMother.createReference(domainClass));
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " " + RuleGeneratorHelper.AE_NIL + ")",
				"bogus value",
				Condition.OP_EQUAL,
				false,
				ObjectMother.createReference(domainClass));
	}
	
	public void testFormatForPatternWithIntegerHappyCase() throws Exception {
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.INTEGER);
		Integer value = ObjectMother.createInteger();
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION_NUMERIC + " " + DEFAULT_VAR + " " + value.toString() + ")",
				value,
				Condition.OP_EQUAL,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithFloatHappyCase() throws Exception {
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.FLOAT);
		Double value = ObjectMother.createInteger() * 1.2d;
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION_NUMERIC + " " + DEFAULT_VAR + " " + RuleGeneratorHelper.floatFormatter.format(value) + ")",
				value,
				Condition.OP_EQUAL,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithPercentHappyCase() throws Exception {
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.PERCENT);
		Double value = ObjectMother.createInteger() * 1.2d;
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION_NUMERIC + " " + DEFAULT_VAR + " " + RuleGeneratorHelper.floatFormatter.format(value) + ")",
				value,
				Condition.OP_EQUAL,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithCurrencyHappyCase() throws Exception {
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.CURRENCY);
		Double value = ObjectMother.createInteger() * 1.2d;
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION_NUMERIC + " " + DEFAULT_VAR + " " + RuleGeneratorHelper.percentFormatter.format(value) + ")",
				value,
				Condition.OP_EQUAL,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithStringHappyCase() throws Exception {
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.STRING);
		String testStr = "string-\"" + ObjectMother.createInt() + "\"";
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " \"" + testStr.replaceAll("\"", "\\\\\"") + "\")",
				testStr,
				Condition.OP_EQUAL,
				true,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithEnumValuePositiveCaseString() throws Exception {
		EnumValue enumValue = ObjectMother.createEnumValue();
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.STRING);
		testFormatForPattern(String.format("%s &:(eq %s \"%s\")", DEFAULT_VAR, DEFAULT_VAR, enumValue.getDeployValue()),
				enumValue,
				Condition.OP_EQUAL,
				true,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithEnumValueNegativeCaseString() throws Exception {
		EnumValue enumValue = ObjectMother.createEnumValue();
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.STRING);
		testFormatForPattern(String.format("%s &:(/= %s \"%s\")", DEFAULT_VAR, DEFAULT_VAR, enumValue.getDeployValue()),
				enumValue,
				Condition.OP_NOT_EQUAL,
				true,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithEnumValuePositiveCaseNonString() throws Exception {
		EnumValue enumValue = ObjectMother.createEnumValue();
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.STRING);
		testFormatForPattern(String.format("%s &:(eq %s %s)", DEFAULT_VAR, DEFAULT_VAR, enumValue.getDeployValue()),
				enumValue,
				Condition.OP_EQUAL,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithEnumValueNegativeCaseNonString() throws Exception {
		EnumValue enumValue = ObjectMother.createEnumValue();
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.STRING);
		testFormatForPattern(String.format("%s &:(/= %s %s)", DEFAULT_VAR, DEFAULT_VAR, enumValue.getDeployValue()),
				enumValue,
				Condition.OP_NOT_EQUAL,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithNullValuePositiveCase() throws Exception {
		testFormatForPattern(DEFAULT_VAR,
				null,
				Condition.OP_EQUAL,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithNullValueNegativeCase() throws Exception {
		testFormatForPattern(DEFAULT_VAR,
				null,
				Condition.OP_NOT_EQUAL,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithEmptyValuePositiveCaseSymbol() throws Exception {
		testFormatForPattern(DEFAULT_VAR,
				"",
				Condition.OP_EQUAL,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithEmptyValueNegativeCaseSymbol() throws Exception {
		testFormatForPattern(DEFAULT_VAR,
				"",
				Condition.OP_NOT_EQUAL,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithEmptyValuePositiveCaseString() throws Exception {
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.STRING);
		testFormatForPattern(DEFAULT_VAR,
				"",
				Condition.OP_EQUAL,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithEmptyValueNegativeCaseString() throws Exception {
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.STRING);
		testFormatForPattern(DEFAULT_VAR,
				"",
				Condition.OP_NOT_EQUAL,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithVariableStringWriteAsIsAndIgnoresAsStringFlag() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " ?some-variable)",
				"?some-variable",
				Condition.OP_EQUAL,
				false,
				ObjectMother.createReference(domainClass));

		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " ?some-variable)",
				"?some-variable",
				Condition.OP_EQUAL,
				true,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithOpenParanStringWriteAsIsAndIgnoresAsStringFlag() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " (some-string))",
				"(some-string)",
				Condition.OP_EQUAL,
				false,
				ObjectMother.createReference(domainClass));

		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " (some-string))",
				"(some-string)",
				Condition.OP_EQUAL,
				true,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithBooleanIgnoresAsStringFlag() throws Exception {
		testFormatForPattern(DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " "
				+ RuleGeneratorHelper.AE_TRUE + ")", Boolean.TRUE, Condition.OP_EQUAL, false, ObjectMother.createReference(domainClass));

		testFormatForPattern(DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " "
				+ RuleGeneratorHelper.AE_TRUE + ")", Boolean.TRUE, Condition.OP_EQUAL, true, ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithDateHappyCase() throws Exception {
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.DATE);
		Date date = getDate(2000, 1, 1, 12, 30, 30);
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION_NUMERIC + " " + DEFAULT_VAR + " "
						+ RuleGeneratorHelper.formatDateValueForLHS(date) + ")",
				date,
				Condition.OP_EQUAL,
				false,
				ObjectMother.createReference(domainClass));

		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION_NUMERIC + " " + DEFAULT_VAR + " \""
						+ RuleGeneratorHelper.formatDateValueForLHS(date) + "\")",
				date,
				Condition.OP_EQUAL,
				true,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithSymbolHappyCase() throws Exception {
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.SYMBOL);
		String symbolStr = "symbol_" + ObjectMother.createInt();
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " " + symbolStr + ")",
				symbolStr,
				Condition.OP_EQUAL,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithEnumValuesPositiveCase() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add("one");
		enumValues.add("two");
		testFormatForPattern(DEFAULT_VAR + " & one | two", enumValues, Condition.OP_EQUAL, false, ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithEnumValuesNegativeCase() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add("one");
		testFormatForPattern(
				DEFAULT_VAR + " &  ~ one",
				enumValues,
				Condition.OP_NOT_EQUAL,
				false,
				ObjectMother.createReference(domainClass));
	}

	protected void setUp() throws Exception {
		super.setUp();
		operatorHelper = createOperatorHelper(
				"com.mindbox.pe.server.generator.value.EqualityOperatorHelper",
				TemplateUsageType.getAllInstances()[0]);
		domainClass = ObjectMother.attachDomainAttributes(ObjectMother.createDomainClass(), 1);
		DomainManager.getInstance().addDomainClass(domainClass);
	}

	protected void tearDown() throws Exception {
		DomainManager.getInstance().removeFromCache(domainClass.getName());
		super.tearDown();
	}
}
