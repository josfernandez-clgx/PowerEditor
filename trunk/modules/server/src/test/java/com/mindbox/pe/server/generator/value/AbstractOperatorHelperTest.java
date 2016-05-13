package com.mindbox.pe.server.generator.value;

import static com.mindbox.pe.server.ServerTestObjectMother.attachDomainAttributes;
import static com.mindbox.pe.server.ServerTestObjectMother.attachEnumValue;
import static com.mindbox.pe.server.ServerTestObjectMother.createDomainClass;
import static com.mindbox.pe.server.ServerTestObjectMother.createEnumValues;
import static com.mindbox.pe.server.ServerTestObjectMother.createReference;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.server.cache.DeploymentManager;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

public class AbstractOperatorHelperTest extends AbstractOperatorHelperTestBase {

	private DomainClass domainClass;

	private void invokeAppendEnumValues(StringBuilder buff, EnumValues<?> enumValues, int op, Reference reference) throws Exception {
		ReflectionUtil.executePrivate(operatorHelper, "appendEnumValues", new Class[] { StringBuilder.class, EnumValues.class, int.class, Reference.class }, new Object[] {
				buff,
				enumValues,
				new Integer(op),
				reference });
	}

	private void invokeAppendFormattedNumericValue(StringBuilder buff, Reference reference, Number number) throws Exception {
		ReflectionUtil.executePrivate(operatorHelper, "appendFormattedNumericValue", new Class[] { StringBuilder.class, Reference.class, Number.class, Integer.class }, new Object[] {
				buff,
				reference,
				number,
				null });
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		operatorHelper = createOperatorHelper("com.mindbox.pe.server.generator.value.ComparisonOperatorHelper", TemplateUsageType.getAllInstances()[0]);
		domainClass = attachDomainAttributes(createDomainClass(), 1);
		DomainManager.getInstance().addDomainClass(domainClass);
	}

	@After
	@Override
	public void tearDown() throws Exception {
		DomainManager.getInstance().removeFromCache(domainClass.getName());
		super.tearDown();
	}

	private void testAppendEnumValues(String expected, EnumValues<?> enumValues, int op) throws Exception {
		StringBuilder buff = new StringBuilder();
		invokeAppendEnumValues(buff, enumValues, op, createReference(domainClass));
		assertEquals(expected, buff.toString());
	}

	private void testAppendFormattedNumericValue(String expected, DeployType deployType, Number value) throws Exception {
		((DomainAttribute) domainClass.getDomainAttributes().get(0)).setDeployType(deployType);
		StringBuilder buff = new StringBuilder();
		invokeAppendFormattedNumericValue(buff, createReference(domainClass), value);
		assertEquals(expected, buff.toString());
	}

	@Test
	public void testAppendFormattedNumericValueForCurrentyType() throws Exception {
		testAppendFormattedNumericValue(RuleGeneratorHelper.getFormatterFactoryForCurrentThread().getCurrencyFormatter(null).format(1357024680.3456789), DeployType.CURRENCY, new Double(
				1357024680.3456789));
	}

	@Test
	public void testAppendFormattedNumericValueForFloatType() throws Exception {
		testAppendFormattedNumericValue(RuleGeneratorHelper.getFormatterFactoryForCurrentThread().getFloatFormatter(null).format(12.003456789), DeployType.FLOAT, new Double(12.003456789));
	}

	@Test
	public void testAppendFormattedNumericValueForIntegerType() throws Exception {
		testAppendFormattedNumericValue("12345678", DeployType.INTEGER, new Integer(12345678));
	}

	@Test
	public void testAppendFormattedNumericValueForCurrencyType() throws Exception {
		testAppendFormattedNumericValue(RuleGeneratorHelper.getFormatterFactoryForCurrentThread().getCurrencyFormatter(null).format(0.345678905), DeployType.CURRENCY, new Double(0.345678905));
	}

	@Test
	public void testAppendFormattedNumericValueForPercentType() throws Exception {
		testAppendFormattedNumericValue(RuleGeneratorHelper.getFormatterFactoryForCurrentThread().getFloatFormatter(null).format(0.3456789012), DeployType.PERCENT, new Double(0.3456789012));
	}

	@Test
	public void testAppendFormattedNumericValueForStringType() throws Exception {
		testAppendFormattedNumericValue("\"20000.75\"", DeployType.STRING, new Double(20000.75));
	}

	@Test
	public void testAppendFormattedNumericValueForSymbolType() throws Exception {
		testAppendFormattedNumericValue("45601.7505", DeployType.SYMBOL, new Double(45601.7505));
	}

	@Test(expected = RuleGenerationException.class)
	public void testAppendFormattedNumericValueWithInvalidReferenceThrowsRuleGeneratorException() throws Exception {
		ComparisonOperatorHelper.class.cast(operatorHelper).appendFormattedNumericValue(new StringBuilder(), createReference("bogus", "test"), 0, null);
	}

	@Test
	public void testInvokeAppendEnumValuesForCategoryOrEntityValuesHappyCase() throws Exception {
		testAppendEnumValues("", new CategoryOrEntityValues(), Condition.OP_IN);
	}

	@Test
	public void testInvokeAppendEnumValuesForEnumValuesExcludeMultiple() throws Exception {
		EnumValues<EnumValue> enumValues = attachEnumValue(createEnumValues(), 2);
		DeploymentManager.getInstance().addEnumValueMap(domainClass.getName(), ((DomainAttribute) domainClass.getDomainAttributes().get(0)).getName(), enumValues);
		testAppendEnumValues(" ~ " + ((EnumValue) enumValues.get(0)).getDeployValue() + " &  ~ " + ((EnumValue) enumValues.get(1)).getDeployValue(), enumValues, Condition.OP_NOT_IN);
	}

	@Test
	public void testInvokeAppendEnumValuesForEnumValuesExcludeSingle() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add("value");
		testAppendEnumValues(" ~ value", enumValues, Condition.OP_NOT_IN);

		enumValues.setSelectionExclusion(true);
		testAppendEnumValues(" ~ value", enumValues, Condition.OP_IN);
	}

	@Test
	public void testInvokeAppendEnumValuesForEnumValuesIncludeMultiple() throws Exception {
		EnumValues<EnumValue> enumValues = attachEnumValue(createEnumValues(), 2);
		DeploymentManager.getInstance().addEnumValueMap(domainClass.getName(), ((DomainAttribute) domainClass.getDomainAttributes().get(0)).getName(), enumValues);
		testAppendEnumValues(((EnumValue) enumValues.get(0)).getDeployValue() + " | " + ((EnumValue) enumValues.get(1)).getDeployValue(), enumValues, Condition.OP_IN);
	}

	@Test
	public void testInvokeAppendEnumValuesForEnumValuesIncludeSingle() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add("value");
		testAppendEnumValues("value", enumValues, Condition.OP_IN);

		enumValues.setSelectionExclusion(true);
		testAppendEnumValues("value", enumValues, Condition.OP_NOT_IN);
	}
}
