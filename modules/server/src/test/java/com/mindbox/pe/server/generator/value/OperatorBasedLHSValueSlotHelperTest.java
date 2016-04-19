package com.mindbox.pe.server.generator.value;

import static com.mindbox.pe.server.ServerTestObjectMother.attachColumnDataSpecDigest;
import static com.mindbox.pe.server.ServerTestObjectMother.attachDomainAttributes;
import static com.mindbox.pe.server.ServerTestObjectMother.attachEnumValue;
import static com.mindbox.pe.server.ServerTestObjectMother.createDomainClass;
import static com.mindbox.pe.server.ServerTestObjectMother.createEnumValue;
import static com.mindbox.pe.server.ServerTestObjectMother.createEnumValues;
import static com.mindbox.pe.server.ServerTestObjectMother.createGridTemplateColumn;
import static com.mindbox.pe.server.ServerTestObjectMother.createGuidelineGenerateParams;
import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static org.junit.Assert.assertEquals;

import java.text.MessageFormat;
import java.util.List;

import org.junit.Test;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.table.BooleanDataHelper;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.server.AbstractTestWithTestConfig;
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

	private OperatorBasedLHSValueSlotHelper operatorBasedLHSValueSlotHelper;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		operatorBasedLHSValueSlotHelper = new OperatorBasedLHSValueSlotHelper(ConfigurationManager.getInstance().getRuleGenerationConfigHelper(null));
	}

	@Override
	public void tearDown() throws Exception {
		DomainManager.getInstance().startLoading();
		super.tearDown();
	}

	@Test
	public void testGetValueObjectForBooleanColumnWithNullValueReturnsNull() throws Exception {
		GuidelineGenerateParams guidelineGenerateParams = createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_BOOLEAN);

		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		testGetValueObject(valueSlot, guidelineGenerateParams, null);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueObjectForBooleanColumnWithAnyValueReturnsEmptyString() throws Exception {
		GuidelineGenerateParams guidelineGenerateParams = createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		((List<String>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(BooleanDataHelper.ANY_VALUE);
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_BOOLEAN);

		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		testGetValueObject(valueSlot, guidelineGenerateParams, "");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueObjectForBooleanColumnWithTrueValueReturnsT() throws Exception {
		GuidelineGenerateParams guidelineGenerateParams = createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		((List<Boolean>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(Boolean.TRUE);
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_BOOLEAN);

		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		testGetValueObject(valueSlot, guidelineGenerateParams, RuleGeneratorHelper.AE_TRUE);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueObjectForBooleanColumnWithFalseValueReturnsNil() throws Exception {
		GuidelineGenerateParams guidelineGenerateParams = createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		((List<Boolean>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(Boolean.FALSE);
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_BOOLEAN);

		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		testGetValueObject(valueSlot, guidelineGenerateParams, RuleGeneratorHelper.AE_NIL);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueobjectForEnumListColumnSingleSelectWithEnumValueHappyCase() throws Exception {
		GuidelineGenerateParams guidelineGenerateParams = createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setIsMultiSelectAllowed(false);
		EnumValue enumValue = createEnumValue();
		((List<Object>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(enumValue);

		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		testGetValueObject(valueSlot, guidelineGenerateParams, enumValue);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueobjectForEnumListColumnSingleSelectWithEnumValueAndAttributeMapSetHappyCase() throws Exception {
		GuidelineGenerateParams guidelineGenerateParams = createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setIsMultiSelectAllowed(false);
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setAttributeMap(createString() + "." + createString());
		EnumValue enumValue = createEnumValue();
		((List<Object>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(enumValue);

		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		testGetValueObject(valueSlot, guidelineGenerateParams, enumValue);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueobjectForEnumListColumnSingleSelectWithStringHappyCase() throws Exception {
		GuidelineGenerateParams guidelineGenerateParams = createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setIsMultiSelectAllowed(false);
		String str = createString();
		((List<Object>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(str);

		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		testGetValueObject(valueSlot, guidelineGenerateParams, str);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueobjectForEnumListColumnMultiSelectWithEnumValuesHappyCase() throws Exception {
		GuidelineGenerateParams guidelineGenerateParams = createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setIsMultiSelectAllowed(true);
		EnumValues<EnumValue> enumValues = attachEnumValue(createEnumValues(), 1);
		((List<Object>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(enumValues);

		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		testGetValueObject(valueSlot, guidelineGenerateParams, enumValues);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueobjectForEnumListColumnMultiSelectWithStringHappyCase() throws Exception {
		GuidelineGenerateParams guidelineGenerateParams = createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setIsMultiSelectAllowed(true);
		String str = createString();
		((List<Object>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(str);

		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		Object obj = invokeGetValueObject(valueSlot, guidelineGenerateParams, null);
		assertEquals(str, ((EnumValues<?>) obj).get(0));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueobjectWithStringOnlyUsesAttrMapIfEnumSourceIsSetToDomainAttr() throws Exception {
		DomainClass domainClass = attachDomainAttributes(createDomainClass(), 1);
		DomainManager.getInstance().addDomainClass(domainClass);
		DeploymentManager.getInstance().addEnumValueMap(domainClass.getName(), domainClass.getDomainAttributes().get(0).getName(), attachEnumValue(createEnumValues(), 2));

		GuidelineGenerateParams guidelineGenerateParams = createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_STRING);
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setIsMultiSelectAllowed(true);
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setAttributeMap(domainClass.getName() + "." + domainClass.getDomainAttributes().get(0).getName());
		String str = createString();
		((List<Object>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(str);

		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		testGetValueObject(valueSlot, guidelineGenerateParams, str);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueObjectForSymbolColumnHappyCase() throws Exception {
		String symbolStr = "symbol_" + createInt();
		GuidelineGenerateParams guidelineGenerateParams = createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		guidelineGenerateParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_SYMBOL);
		((List<Object>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(symbolStr);

		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1);
		testGetValueObject(valueSlot, guidelineGenerateParams, symbolStr);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueobjectWithColumnValueAndSlotTextHappyCase() throws Exception {
		GuidelineGenerateParams guidelineGenerateParams = createGuidelineGenerateParams();
		guidelineGenerateParams.getTemplate().addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, guidelineGenerateParams.getUsage())));
		Double value = new Double(12345.6789);
		((List<Double>) ReflectionUtil.getPrivate(guidelineGenerateParams, "rowData")).add(value);

		String slotText = "slot-" + createString() + " {0} text";
		ColumnReferencePatternValueSlot valueSlot = RuleObjectMother.createColumnReferencePatternValueSlot(1, slotText);
		testGetValueObject(
				valueSlot,
				guidelineGenerateParams,
				MessageFormat.format(slotText, new Object[] { RuleGeneratorHelper.getFormatterFactoryForCurrentThread().getFloatFormatter(null).format(value) }));
	}

	@Test
	public void testGetValueobjectWithStringValuePatternSlotHappyCase() throws Exception {
		StringValuePatternValueSlot stringValuePatternValueSlot = RuleObjectMother.createStringValuePatternValueSlot();
		testGetValueObject(stringValuePatternValueSlot, null, stringValuePatternValueSlot.getSlotValue());
	}

	private Object testGetValueObject(PatternValueSlot valueSlot, GuidelineGenerateParams generateParams, Object expectedValue) throws Exception {
		Object obj = invokeGetValueObject(valueSlot, generateParams, null);
		assertEquals(expectedValue, obj);
		return obj;
	}

	private Object invokeGetValueObject(PatternValueSlot valueSlot, GuidelineGenerateParams generateParams, final Integer precision) throws Exception {
		Object obj = ReflectionUtil.executePrivate(
				operatorBasedLHSValueSlotHelper,
				"getValueObject",
				new Class[] { PatternValueSlot.class, GuidelineGenerateParams.class, Integer.class },
				new Object[] { valueSlot, generateParams, precision });
		return obj;
	}
}
