package com.mindbox.pe.server.generator.value;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.server.cache.DeploymentManager;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

public class AbstractOperatorHelperTest extends OperatorHelperTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractOperatorHelperTest Tests");
		suite.addTestSuite(AbstractOperatorHelperTest.class);
		return suite;
	}

	private DomainClass domainClass;

	public AbstractOperatorHelperTest(String name) {
		super(name);
	}

	public void testAppendFormattedNumericValueWithInvalidReferenceThrowsRuleGeneratorException() throws Exception {
		assertThrowsException(
				operatorHelper,
				"appendFormattedNumericValue",
				new Class[] { StringBuffer.class, Reference.class, Number.class },
				new Object[] { new StringBuffer(), ObjectMother.createReference("bogus", "test"), new Integer(0) },
				RuleGenerationException.class);
	}

	public void testAppendFormattedNumericValueForCurrentyType() throws Exception {
		testAppendFormattedNumericValue(RuleGeneratorHelper.percentFormatter.format(1357024680.3456789), DeployType.CURRENCY, new Double(
				1357024680.3456789));
	}

	public void testAppendFormattedNumericValueForFloatType() throws Exception {
		testAppendFormattedNumericValue(RuleGeneratorHelper.floatFormatter.format(12.3456789), DeployType.FLOAT, new Double(12.3456789));
	}

	public void testAppendFormattedNumericValueForPercentType() throws Exception {
		testAppendFormattedNumericValue(RuleGeneratorHelper.floatFormatter.format(0.3456789), DeployType.PERCENT, new Double(0.3456789));
	}

	public void testAppendFormattedNumericValueForIntegerType() throws Exception {
		testAppendFormattedNumericValue("12345678", DeployType.INTEGER, new Integer(12345678));
	}

	public void testAppendFormattedNumericValueForStringType() throws Exception {
		testAppendFormattedNumericValue("\"20000.75\"", DeployType.STRING, new Double(20000.75));
	}

	public void testAppendFormattedNumericValueForSymbolType() throws Exception {
		testAppendFormattedNumericValue("45601.7505", DeployType.SYMBOL, new Double(45601.7505));
	}

	private void testAppendFormattedNumericValue(String expected, DeployType deployType, Number value) throws Exception {
		((DomainAttribute) domainClass.getDomainAttributes().get(0)).setDeployType(deployType);
		StringBuffer buff = new StringBuffer();
		invokeAppendFormattedNumericValue(buff, ObjectMother.createReference(domainClass), value);
		assertEquals(expected, buff.toString());
	}

	public void testInvokeAppendEnumValuesForEnumValuesExcludeMultiple() throws Exception {
		EnumValues<EnumValue> enumValues = ObjectMother.attachEnumValue(ObjectMother.createEnumValues(), 2);
		DeploymentManager.getInstance().addEnumValueMap(
				domainClass.getName(),
				((DomainAttribute) domainClass.getDomainAttributes().get(0)).getName(),
				enumValues);
		testAppendEnumValues(
				" ~ " + ((EnumValue) enumValues.get(0)).getDeployValue() + " &  ~ " + ((EnumValue) enumValues.get(1)).getDeployValue(),
				enumValues,
				Condition.OP_NOT_IN,
				false);
	}

	public void testInvokeAppendEnumValuesForEnumValuesExcludeSingle() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add("value");
		testAppendEnumValues(" ~ value", enumValues, Condition.OP_NOT_IN, false);
		
		enumValues.setSelectionExclusion(true);
		testAppendEnumValues(" ~ value", enumValues, Condition.OP_IN, false);
	}

	public void testInvokeAppendEnumValuesForEnumValuesIncludeMultiple() throws Exception {
		EnumValues<EnumValue> enumValues = ObjectMother.attachEnumValue(ObjectMother.createEnumValues(), 2);
		DeploymentManager.getInstance().addEnumValueMap(
				domainClass.getName(),
				((DomainAttribute) domainClass.getDomainAttributes().get(0)).getName(),
				enumValues);
		testAppendEnumValues(
				((EnumValue) enumValues.get(0)).getDeployValue() + " | " + ((EnumValue) enumValues.get(1)).getDeployValue(),
				enumValues,
				Condition.OP_IN,
				false);
	}

	public void testInvokeAppendEnumValuesForEnumValuesIncludeSingle() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add("value");
		testAppendEnumValues("value", enumValues, Condition.OP_IN, false);

		enumValues.setSelectionExclusion(true);
		testAppendEnumValues("value", enumValues, Condition.OP_NOT_IN, false);
	}

	public void testInvokeAppendEnumValuesForCategoryOrEntityValuesHappyCase() throws Exception {
		testAppendEnumValues("", new CategoryOrEntityValues(), Condition.OP_IN, false);
	}

	private void testAppendEnumValues(String expected, EnumValues<?> enumValues, int op, boolean asString) throws Exception {
		StringBuffer buff = new StringBuffer();
		invokeAppendEnumValues(buff, enumValues, op, ObjectMother.createReference(domainClass), asString);
		assertEquals(expected, buff.toString());
	}

	private void invokeAppendEnumValues(StringBuffer buff, EnumValues<?> enumValues, int op, Reference reference, boolean asString) throws Exception {
		ReflectionUtil.executePrivate(operatorHelper, "appendEnumValues", new Class[] { StringBuffer.class, EnumValues.class, int.class,
				Reference.class, boolean.class }, new Object[] { buff, enumValues, new Integer(op), reference, new Boolean(asString) });
	}

	private void invokeAppendFormattedNumericValue(StringBuffer buff, Reference reference, Number number) throws Exception {
		ReflectionUtil.executePrivate(
				operatorHelper,
				"appendFormattedNumericValue",
				new Class[] { StringBuffer.class, Reference.class, Number.class },
				new Object[] { buff, reference, number });
	}

	protected void setUp() throws Exception {
		super.setUp();
		operatorHelper = createOperatorHelper(
				"com.mindbox.pe.server.generator.value.ComparisonOperatorHelper",
				TemplateUsageType.getAllInstances()[0]);
		domainClass = ObjectMother.attachDomainAttributes(ObjectMother.createDomainClass(), 1);
		DomainManager.getInstance().addDomainClass(domainClass);
	}

	protected void tearDown() throws Exception {
		DomainManager.getInstance().removeFromCache(domainClass.getName());
		DeploymentManager.getInstance().startLoading();
		super.tearDown();
	}
}
