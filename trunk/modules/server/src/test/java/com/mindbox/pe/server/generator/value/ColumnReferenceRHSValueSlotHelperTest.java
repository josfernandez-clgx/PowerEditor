package com.mindbox.pe.server.generator.value;

import static com.mindbox.pe.server.ServerTestObjectMother.attachColumnDataSpecDigest;
import static com.mindbox.pe.server.ServerTestObjectMother.createGridTemplateColumn;
import static com.mindbox.pe.server.ServerTestObjectMother.createGuidelineGenerateParams;
import static com.mindbox.pe.unittest.TestObjectMother.createInteger;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.table.DynamicStringValue;
import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.generator.rule.ColumnReferencePatternValueSlot;
import com.mindbox.pe.server.generator.rule.RuleObjectMother;
import com.mindbox.pe.server.generator.rule.ValueSlot;

public class ColumnReferenceRHSValueSlotHelperTest extends AbstractTestWithTestConfig {

	private ColumnReferenceRHSValueSlotHelper valueSlotHelper;

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		valueSlotHelper = new ColumnReferenceRHSValueSlotHelper();
	}

	private void testGenerateValue(String expectedValue, GuidelineGenerateParams ruleParams, ValueSlot valueSlot) throws Exception {
		assertEquals(expectedValue, valueSlotHelper.generateValue(ruleParams, valueSlot));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateValueWithDynamicStringValueForStringDeployTypeHappyCase() throws Exception {
		String value = createString();
		DynamicStringValue dsValue = new DynamicStringValue(value);
		dsValue.setDeployValues(new String[] { value });

		GuidelineGenerateParams guidelineGenerateParams = createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		((List<String>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(value);
		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		valueSlot.setParameterDeployType(DeployType.STRING);

		testGenerateValue(RuleGeneratorHelper.QUOTE + value + RuleGeneratorHelper.QUOTE, guidelineGenerateParams, valueSlot);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateValueWithIntegerValueForStringDeployTypeHappyCase() throws Exception {
		Integer value = createInteger();

		GuidelineGenerateParams guidelineGenerateParams = createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		((List<Integer>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(value);
		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		valueSlot.setParameterDeployType(DeployType.STRING);

		testGenerateValue(RuleGeneratorHelper.QUOTE + String.valueOf(value) + RuleGeneratorHelper.QUOTE, guidelineGenerateParams, valueSlot);
	}

	@Test
	public void testGenerateValueWithNullRuleParamsThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				valueSlotHelper,
				"generateValue",
				new Class[] { GuidelineGenerateParams.class, ValueSlot.class },
				new Object[] { null, RuleObjectMother.createColumnReferencePatternValueSlot(1) });
	}

	@Test
	public void testGenerateValueWithNullValueSlotThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(valueSlotHelper, "generateValue", new Class[] { GuidelineGenerateParams.class, ValueSlot.class }, new Object[] { createGuidelineGenerateParams(), null });
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateValueWithStringValueForNonStringDeployTypeAndEnumColumnHappyCase() throws Exception {
		String value = createString();

		GuidelineGenerateParams guidelineGenerateParams = createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		((List<String>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(value);
		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		valueSlot.setParameterDeployType(DeployType.SYMBOL);

		testGenerateValue(value, guidelineGenerateParams, valueSlot);

		// try with value in the column enum value list
		List<String> enumList = new ArrayList<String>();
		enumList.add(value);
		testGenerateValue(value, guidelineGenerateParams, valueSlot);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateValueWithStringValueForNonStringDeployTypeAndStringColumnHappyCase() throws Exception {
		String value = createString();

		GuidelineGenerateParams guidelineGenerateParams = createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		((List<String>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(value);
		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		valueSlot.setParameterDeployType(DeployType.SYMBOL);

		testGenerateValue(value, guidelineGenerateParams, valueSlot);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateValueWithStringValueForStringDeployTypeHappyCase() throws Exception {
		String value = createString();

		GuidelineGenerateParams guidelineGenerateParams = createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		((List<String>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(value);
		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		valueSlot.setParameterDeployType(DeployType.STRING);

		testGenerateValue(RuleGeneratorHelper.QUOTE + value + RuleGeneratorHelper.QUOTE, guidelineGenerateParams, valueSlot);
	}
}
