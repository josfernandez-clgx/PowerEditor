package com.mindbox.pe.server.generator.value;

import static com.mindbox.pe.server.ServerTestObjectMother.attachDomainAttributes;
import static com.mindbox.pe.server.ServerTestObjectMother.createDomainClass;
import static com.mindbox.pe.server.ServerTestObjectMother.createEnumValue;
import static com.mindbox.pe.server.ServerTestObjectMother.createReference;
import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static com.mindbox.pe.unittest.TestObjectMother.createInteger;
import static com.mindbox.pe.unittest.UnitTestHelper.getDate;

import java.util.Date;

import org.junit.Test;

import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

public class EqualityOperatorHelperTest extends AbstractOperatorHelperTestBase {

	private DomainClass domainClass;

	@Test
	public void testFormatForPatternWithBooleanAttributePositiveCase() throws Exception {
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.BOOLEAN);
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " " + RuleGeneratorHelper.AE_TRUE + ")",
				Boolean.TRUE,
				Condition.OP_EQUAL,
				false,
				createReference(domainClass));

		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " " + RuleGeneratorHelper.AE_TRUE + ")",
				"Yes",
				Condition.OP_EQUAL,
				false,
				createReference(domainClass));

		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " " + RuleGeneratorHelper.AE_TRUE + ")",
				"YES",
				Condition.OP_EQUAL,
				false,
				createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithBooleanAttributeNegativeCase() throws Exception {
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.BOOLEAN);
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " " + RuleGeneratorHelper.AE_NIL + ")",
				Boolean.FALSE,
				Condition.OP_EQUAL,
				false,
				createReference(domainClass));

		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " " + RuleGeneratorHelper.AE_NIL + ")",
				"No",
				Condition.OP_EQUAL,
				false,
				createReference(domainClass));

		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " " + RuleGeneratorHelper.AE_NIL + ")",
				"NO",
				Condition.OP_EQUAL,
				false,
				createReference(domainClass));
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " " + RuleGeneratorHelper.AE_NIL + ")",
				"bogus value",
				Condition.OP_EQUAL,
				false,
				createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithIntegerHappyCase() throws Exception {
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.INTEGER);
		Integer value = createInteger();
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION_NUMERIC + " " + DEFAULT_VAR + " " + value.toString() + ")",
				value,
				Condition.OP_EQUAL,
				false,
				createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithFloatHappyCase() throws Exception {
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.FLOAT);
		Double value = createInteger() * 1.2d;
		testFormatForPattern(DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION_NUMERIC + " " + DEFAULT_VAR + " "
				+ RuleGeneratorHelper.getFormatterFactoryForCurrentThread().getFloatFormatter(null).format(value) + ")", value, Condition.OP_EQUAL, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithPercentHappyCase() throws Exception {
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.PERCENT);
		Double value = createInteger() * 1.2d;
		testFormatForPattern(DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION_NUMERIC + " " + DEFAULT_VAR + " "
				+ RuleGeneratorHelper.getFormatterFactoryForCurrentThread().getFloatFormatter(null).format(value) + ")", value, Condition.OP_EQUAL, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithCurrencyHappyCase() throws Exception {
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.CURRENCY);
		Double value = createInteger() * 1.2d;
		testFormatForPattern(DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION_NUMERIC + " " + DEFAULT_VAR + " "
				+ RuleGeneratorHelper.getFormatterFactoryForCurrentThread().getCurrencyFormatter(null).format(value) + ")", value, Condition.OP_EQUAL, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithStringHappyCase() throws Exception {
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.STRING);
		String testStr = "string-\"" + createInt() + "\"";
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " \"" + testStr.replaceAll("\"", "\\\\\"") + "\")",
				testStr,
				Condition.OP_EQUAL,
				true,
				createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithEnumValuePositiveCaseString() throws Exception {
		EnumValue enumValue = createEnumValue();
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.STRING);
		testFormatForPattern(String.format("%s &:(eq %s \"%s\")", DEFAULT_VAR, DEFAULT_VAR, enumValue.getDeployValue()), enumValue, Condition.OP_EQUAL, true, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithEnumValueNegativeCaseString() throws Exception {
		EnumValue enumValue = createEnumValue();
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.STRING);
		testFormatForPattern(String.format("%s &:(/= %s \"%s\")", DEFAULT_VAR, DEFAULT_VAR, enumValue.getDeployValue()), enumValue, Condition.OP_NOT_EQUAL, true, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithEnumValuePositiveCaseNonString() throws Exception {
		EnumValue enumValue = createEnumValue();
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.STRING);
		testFormatForPattern(String.format("%s &:(eq %s %s)", DEFAULT_VAR, DEFAULT_VAR, enumValue.getDeployValue()), enumValue, Condition.OP_EQUAL, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithEnumValueNegativeCaseNonString() throws Exception {
		EnumValue enumValue = createEnumValue();
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.STRING);
		testFormatForPattern(String.format("%s &:(/= %s %s)", DEFAULT_VAR, DEFAULT_VAR, enumValue.getDeployValue()), enumValue, Condition.OP_NOT_EQUAL, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithNullValuePositiveCase() throws Exception {
		testFormatForPattern(DEFAULT_VAR, null, Condition.OP_EQUAL, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithNullValueNegativeCase() throws Exception {
		testFormatForPattern(DEFAULT_VAR, null, Condition.OP_NOT_EQUAL, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithEmptyValuePositiveCaseSymbol() throws Exception {
		testFormatForPattern(DEFAULT_VAR, "", Condition.OP_EQUAL, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithEmptyValueNegativeCaseSymbol() throws Exception {
		testFormatForPattern(DEFAULT_VAR, "", Condition.OP_NOT_EQUAL, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithEmptyValuePositiveCaseString() throws Exception {
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.STRING);
		testFormatForPattern(DEFAULT_VAR, "", Condition.OP_EQUAL, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithEmptyValueNegativeCaseString() throws Exception {
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.STRING);
		testFormatForPattern(DEFAULT_VAR, "", Condition.OP_NOT_EQUAL, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithVariableStringWriteAsIsAndIgnoresAsStringFlag() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " ?some-variable)",
				"?some-variable",
				Condition.OP_EQUAL,
				false,
				createReference(domainClass));

		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " ?some-variable)",
				"?some-variable",
				Condition.OP_EQUAL,
				true,
				createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithOpenParanStringWriteAsIsAndIgnoresAsStringFlag() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " (some-string))",
				"(some-string)",
				Condition.OP_EQUAL,
				false,
				createReference(domainClass));

		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " (some-string))",
				"(some-string)",
				Condition.OP_EQUAL,
				true,
				createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithBooleanIgnoresAsStringFlag() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " " + RuleGeneratorHelper.AE_TRUE + ")",
				Boolean.TRUE,
				Condition.OP_EQUAL,
				false,
				createReference(domainClass));

		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " " + RuleGeneratorHelper.AE_TRUE + ")",
				Boolean.TRUE,
				Condition.OP_EQUAL,
				true,
				createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithDateHappyCase() throws Exception {
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.DATE);
		Date date = getDate(2000, 1, 1, 12, 30, 30);
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION_NUMERIC + " " + DEFAULT_VAR + " " + RuleGeneratorHelper.formatDateValueForLHS(date) + ")",
				date,
				Condition.OP_EQUAL,
				false,
				createReference(domainClass));

		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION_NUMERIC + " " + DEFAULT_VAR + " \"" + RuleGeneratorHelper.formatDateValueForLHS(date) + "\")",
				date,
				Condition.OP_EQUAL,
				true,
				createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithSymbolHappyCase() throws Exception {
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.SYMBOL);
		String symbolStr = "symbol_" + createInt();
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " " + symbolStr + ")",
				symbolStr,
				Condition.OP_EQUAL,
				false,
				createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithEnumValuesPositiveCase() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add("one");
		enumValues.add("two");
		testFormatForPattern(DEFAULT_VAR + " & one | two", enumValues, Condition.OP_EQUAL, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithEnumValuesNegativeCase() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add("one");
		testFormatForPattern(DEFAULT_VAR + " &  ~ one", enumValues, Condition.OP_NOT_EQUAL, false, createReference(domainClass));
	}

	public void setUp() throws Exception {
		super.setUp();
		operatorHelper = createOperatorHelper("com.mindbox.pe.server.generator.value.EqualityOperatorHelper", TemplateUsageType.getAllInstances()[0]);
		domainClass = attachDomainAttributes(createDomainClass(), 1);
		DomainManager.getInstance().addDomainClass(domainClass);
	}

	public void tearDown() throws Exception {
		DomainManager.getInstance().removeFromCache(domainClass.getName());
		super.tearDown();
	}
}
