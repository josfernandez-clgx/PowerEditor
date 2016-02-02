package com.mindbox.pe.server.generator.value;

import java.text.MessageFormat;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.table.BooleanDataHelper;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.server.cache.DeploymentManager;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.generator.rule.ColumnReferencePatternValueSlot;
import com.mindbox.pe.server.generator.rule.PatternValueSlot;
import com.mindbox.pe.server.generator.rule.RuleObjectMother;
import com.mindbox.pe.server.generator.rule.StringValuePatternValueSlot;

public class OperatorBasedLHSValueSlotHelperTest extends AbstractTestWithTestConfig {

	public static Test suite() {
		TestSuite suite = new TestSuite("OperatorBasedLHSValueSlotHelperTest Tests");
		suite.addTestSuite(OperatorBasedLHSValueSlotHelperTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private OperatorBasedLHSValueSlotHelper operatorBasedLHSValueSlotHelper;

	public OperatorBasedLHSValueSlotHelperTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		operatorBasedLHSValueSlotHelper = new OperatorBasedLHSValueSlotHelper(
				ConfigurationManager.getInstance().getRuleGenerationConfiguration(null));
	}

	@Override
	protected void tearDown() throws Exception {
		DomainManager.getInstance().startLoading();
		DeploymentManager.getInstance().startLoading();
		super.tearDown();
	}

	public void testGetValueObjectForBooleanColumnWithNullValueReturnsNull() throws Exception {
		GuidelineGenerateParams guidelineGenerateParams = ObjectMother.createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(
				ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_BOOLEAN);

		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		testGetValueObject(valueSlot, guidelineGenerateParams, null);
	}

	@SuppressWarnings("unchecked")
	public void testGetValueObjectForBooleanColumnWithAnyValueReturnsEmptyString() throws Exception {
		GuidelineGenerateParams guidelineGenerateParams = ObjectMother.createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(
				ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		((List<String>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(BooleanDataHelper.ANY_VALUE);
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_BOOLEAN);

		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		testGetValueObject(valueSlot, guidelineGenerateParams, "");
	}

	@SuppressWarnings("unchecked")
	public void testGetValueObjectForBooleanColumnWithTrueValueReturnsT() throws Exception {
		GuidelineGenerateParams guidelineGenerateParams = ObjectMother.createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(
				ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		((List<Boolean>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(Boolean.TRUE);
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_BOOLEAN);

		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		testGetValueObject(valueSlot, guidelineGenerateParams, RuleGeneratorHelper.AE_TRUE);
	}

	@SuppressWarnings("unchecked")
	public void testGetValueObjectForBooleanColumnWithFalseValueReturnsNil() throws Exception {
		GuidelineGenerateParams guidelineGenerateParams = ObjectMother.createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(
				ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		((List<Boolean>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(Boolean.FALSE);
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_BOOLEAN);

		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		testGetValueObject(valueSlot, guidelineGenerateParams, RuleGeneratorHelper.AE_NIL);
	}

	@SuppressWarnings("unchecked")
	public void testGetValueobjectForEnumListColumnSingleSelectWithEnumValueHappyCase() throws Exception {
		GuidelineGenerateParams guidelineGenerateParams = ObjectMother.createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(
				ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setIsMultiSelectAllowed(false);
		EnumValue enumValue = ObjectMother.createEnumValue();
		((List<Object>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(enumValue);

		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		testGetValueObject(valueSlot, guidelineGenerateParams, enumValue);
	}

	@SuppressWarnings("unchecked")
	public void testGetValueobjectForEnumListColumnSingleSelectWithEnumValueAndAttributeMapSetHappyCase() throws Exception {
		GuidelineGenerateParams guidelineGenerateParams = ObjectMother.createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(
				ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setIsMultiSelectAllowed(false);
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setAttributeMap(
				ObjectMother.createString() + "." + ObjectMother.createString());
		EnumValue enumValue = ObjectMother.createEnumValue();
		((List<Object>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(enumValue);

		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		testGetValueObject(valueSlot, guidelineGenerateParams, enumValue);
	}

	@SuppressWarnings("unchecked")
	public void testGetValueobjectForEnumListColumnSingleSelectWithStringHappyCase() throws Exception {
		GuidelineGenerateParams guidelineGenerateParams = ObjectMother.createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(
				ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setIsMultiSelectAllowed(false);
		String str = ObjectMother.createString();
		((List<Object>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(str);

		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		testGetValueObject(valueSlot, guidelineGenerateParams, str);
	}

	@SuppressWarnings("unchecked")
	public void testGetValueobjectForEnumListColumnMultiSelectWithEnumValuesHappyCase() throws Exception {
		GuidelineGenerateParams guidelineGenerateParams = ObjectMother.createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(
				ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setIsMultiSelectAllowed(true);
		EnumValues<EnumValue> enumValues = ObjectMother.attachEnumValue(ObjectMother.createEnumValues(), 1);
		((List<Object>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(enumValues);

		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		testGetValueObject(valueSlot, guidelineGenerateParams, enumValues);
	}

	@SuppressWarnings("unchecked")
	public void testGetValueobjectForEnumListColumnMultiSelectWithStringHappyCase() throws Exception {
		GuidelineGenerateParams guidelineGenerateParams = ObjectMother.createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(
				ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setIsMultiSelectAllowed(true);
		String str = ObjectMother.createString();
		((List<Object>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(str);

		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		Object obj = invokeGetValueObject(valueSlot, guidelineGenerateParams);
		assertEquals(str, ((EnumValues<?>) obj).get(0));
	}

	@SuppressWarnings("unchecked")
	public void testGetValueobjectWithStringOnlyUsesAttrMapIfEnumSourceIsSetToDomainAttr() throws Exception {
		DomainClass domainClass = ObjectMother.attachDomainAttributes(ObjectMother.createDomainClass(), 1);
		DomainManager.getInstance().addDomainClass(domainClass);
		DeploymentManager.getInstance().addEnumValueMap(
				domainClass.getName(),
				domainClass.getDomainAttributes().get(0).getName(),
				ObjectMother.attachEnumValue(ObjectMother.createEnumValues(), 2));

		GuidelineGenerateParams guidelineGenerateParams = ObjectMother.createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(
				ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_STRING);
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setIsMultiSelectAllowed(true);
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setAttributeMap(
				domainClass.getName() + "." + domainClass.getDomainAttributes().get(0).getName());
		String str = ObjectMother.createString();
		((List<Object>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(str);

		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		testGetValueObject(valueSlot, guidelineGenerateParams, str);
	}

	@SuppressWarnings("unchecked")
	public void testGetValueObjectForSymbolColumnHappyCase() throws Exception {
		String symbolStr = "symbol_" + ObjectMother.createInt();
		GuidelineGenerateParams guidelineGenerateParams = ObjectMother.createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(
				ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_SYMBOL);
		((List<Object>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(symbolStr);

		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		testGetValueObject(valueSlot, guidelineGenerateParams, symbolStr);
	}

	@SuppressWarnings("unchecked")
	public void testGetValueobjectWithColumnValueAndSlotTextHappyCase() throws Exception {
		GuidelineGenerateParams guidelineGenerateParams = ObjectMother.createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(
				ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		Double value = new Double(12345.6789);
		((List<Double>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(value);

		String slotText = "slot-" + ObjectMother.createString() + " {0} text";
		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1, slotText);
		testGetValueObject(valueSlot, guidelineGenerateParams, MessageFormat.format(
				slotText,
				new Object[] { RuleGeneratorHelper.floatFormatter.format(value) }));
	}

	public void testGetValueobjectWithStringValuePatternSlotHappyCase() throws Exception {
		StringValuePatternValueSlot stringValuePatternValueSlot = RuleObjectMother.createStringValuePatternValueSlot();
		testGetValueObject(stringValuePatternValueSlot, null, stringValuePatternValueSlot.getSlotValue());
	}

	private Object testGetValueObject(PatternValueSlot valueSlot, GuidelineGenerateParams generateParams, Object expectedValue)
			throws Exception {
		Object obj = invokeGetValueObject(valueSlot, generateParams);
		assertEquals(expectedValue, obj);
		return obj;
	}

	private Object invokeGetValueObject(PatternValueSlot valueSlot, GuidelineGenerateParams generateParams) throws Exception {
		Object obj = ReflectionUtil.executePrivate(operatorBasedLHSValueSlotHelper, "getValueObject", new Class[] {
				PatternValueSlot.class,
				GuidelineGenerateParams.class }, new Object[] { valueSlot, generateParams });
		return obj;
	}
}
