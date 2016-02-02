package com.mindbox.pe.server.generator.value;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.table.DynamicStringValue;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.generator.rule.ColumnReferencePatternValueSlot;
import com.mindbox.pe.server.generator.rule.RuleObjectMother;
import com.mindbox.pe.server.generator.rule.ValueSlot;

public class ColumnReferenceRHSValueSlotHelperTest extends AbstractTestWithTestConfig {

	public static Test suite() {
		TestSuite suite = new TestSuite("ColumnReferenceRHSValueSlotHelperTest Tests");
		suite.addTestSuite(ColumnReferenceRHSValueSlotHelperTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private ColumnReferenceRHSValueSlotHelper valueSlotHelper;

	public ColumnReferenceRHSValueSlotHelperTest(String name) {
		super(name);
	}

	public void testGenerateValueWithNullRuleParamsThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				valueSlotHelper,
				"generateValue",
				new Class[] { GuidelineGenerateParams.class, ValueSlot.class },
				new Object[] { null, RuleObjectMother.createColumnReferencePatternValueSlot(1) });
	}

	public void testGenerateValueWithNullValueSlotThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				valueSlotHelper,
				"generateValue",
				new Class[] { GuidelineGenerateParams.class, ValueSlot.class },
				new Object[] { ObjectMother.createGuidelineGenerateParams(), null });
	}

	@SuppressWarnings("unchecked")
	public void testGenerateValueWithStringValueForStringDeployTypeHappyCase() throws Exception {
		String value = ObjectMother.createString();

		GuidelineGenerateParams guidelineGenerateParams = ObjectMother.createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(
				ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		((List<String>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(value);
		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		valueSlot.setParameterDeployType(DeployType.STRING);

		testGenerateValue(RuleGeneratorHelper.QUOTE + value + RuleGeneratorHelper.QUOTE, guidelineGenerateParams, valueSlot);
	}

	@SuppressWarnings("unchecked")
	public void testGenerateValueWithDynamicStringValueForStringDeployTypeHappyCase() throws Exception {
		String value = ObjectMother.createString();
		DynamicStringValue dsValue = new DynamicStringValue();
		dsValue.setDeployValues(new String[] { value });

		GuidelineGenerateParams guidelineGenerateParams = ObjectMother.createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(
				ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		((List<String>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(value);
		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		valueSlot.setParameterDeployType(DeployType.STRING);

		testGenerateValue(RuleGeneratorHelper.QUOTE + value + RuleGeneratorHelper.QUOTE, guidelineGenerateParams, valueSlot);
	}

	@SuppressWarnings("unchecked")
	public void testGenerateValueWithIntegerValueForStringDeployTypeHappyCase() throws Exception {
		Integer value = ObjectMother.createInteger();

		GuidelineGenerateParams guidelineGenerateParams = ObjectMother.createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(
				ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		((List<Integer>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(value);
		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		valueSlot.setParameterDeployType(DeployType.STRING);

		testGenerateValue(RuleGeneratorHelper.QUOTE + String.valueOf(value) + RuleGeneratorHelper.QUOTE, guidelineGenerateParams, valueSlot);
	}

	@SuppressWarnings("unchecked")
	public void testGenerateValueWithStringValueForNonStringDeployTypeAndStringColumnHappyCase() throws Exception {
		String value = ObjectMother.createString();

		GuidelineGenerateParams guidelineGenerateParams = ObjectMother.createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(
				ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		((List<String>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(value);
		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		valueSlot.setParameterDeployType(DeployType.SYMBOL);

		testGenerateValue(value, guidelineGenerateParams, valueSlot);
	}

	@SuppressWarnings("unchecked")
	public void testGenerateValueWithStringValueForNonStringDeployTypeAndEnumColumnHappyCase() throws Exception {
		String value = ObjectMother.createString();

		GuidelineGenerateParams guidelineGenerateParams = ObjectMother.createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(
				ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		((List<String>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(value);
		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		valueSlot.setParameterDeployType(DeployType.SYMBOL);

		testGenerateValue(value, guidelineGenerateParams, valueSlot);

		// try with value in the column enum value list
		List<String> enumList = new ArrayList<String>();
		enumList.add(value);
		testGenerateValue(value, guidelineGenerateParams, valueSlot);
	}

	private void testGenerateValue(String expectedValue, GuidelineGenerateParams ruleParams, ValueSlot valueSlot) throws Exception {
		assertEquals(expectedValue, valueSlotHelper.generateValue(ruleParams, valueSlot));
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		valueSlotHelper = new ColumnReferenceRHSValueSlotHelper();
	}
}
