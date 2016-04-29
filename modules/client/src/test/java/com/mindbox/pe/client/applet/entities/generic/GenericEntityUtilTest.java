package com.mindbox.pe.client.applet.entities.generic;

import static com.mindbox.pe.client.ClientTestObjectMother.createEntityPropertyDefinition;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static com.mindbox.pe.unittest.UnitTestHelper.getDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.common.FloatTextField;
import com.mindbox.pe.client.common.IntegerListField;
import com.mindbox.pe.client.common.TypeEnumValueComboBox;
import com.mindbox.pe.client.common.dialog.MDateDateField;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.common.ui.NumberTextField;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.model.filter.GenericEntityFilterSpec;
import com.mindbox.pe.xsd.config.EntityProperty;
import com.mindbox.pe.xsd.config.EntityPropertyType;

public class GenericEntityUtilTest extends AbstractClientTestBase {
	private GenericEntityFilterSpec filterSpec;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		filterSpec = new GenericEntityFilterSpec(entityType2, 1, "name");
	}

	@Override
	@After
	public void tearDown() throws Exception {
		filterSpec = null;
		super.tearDown();
	}

	@Test
	public void testCreateEditComponentBoolean() throws Exception {
		JCheckBox comp = (JCheckBox) GenericEntityUtil.createEditComponent(createEntityPropertyDefinition(EntityPropertyType.BOOLEAN), false);
		assertEquals("Yes", comp.getText());
		assertFalse(comp.isSelected());
	}

	@Test
	public void testCreateEditComponentCurrency() throws Exception {
		FloatTextField comp = (FloatTextField) GenericEntityUtil.createEditComponent(createEntityPropertyDefinition(EntityPropertyType.CURRENCY), false);
		assertEquals(20, comp.getColumns());
		assertTrue(((Boolean) ReflectionUtil.getPrivate(comp, "forCurrency")).booleanValue());
		assertEquals("", comp.getText());
	}

	@Test
	public void testCreateEditComponentDate() throws Exception {
		MDateDateField comp = (MDateDateField) GenericEntityUtil.createEditComponent(createEntityPropertyDefinition(EntityPropertyType.DATE), false);
		assertFalse(((Boolean) ReflectionUtil.getPrivate(comp, "forTime")).booleanValue());
		assertFalse(((Boolean) ReflectionUtil.getPrivate(comp, "showClearButton")).booleanValue());
		assertTrue(((Boolean) ReflectionUtil.getPrivate(comp, "allowSecondsInDate")).booleanValue());
		assertNull(comp.getValue());
	}

	@Test
	public void testCreateEditComponentDouble() throws Exception {
		FloatTextField comp = (FloatTextField) GenericEntityUtil.createEditComponent(createEntityPropertyDefinition(EntityPropertyType.CURRENCY), false);
		assertEquals(20, comp.getColumns());
		assertTrue(((Boolean) ReflectionUtil.getPrivate(comp, "forCurrency")).booleanValue());
		assertEquals("", comp.getText());
	}

	@Test
	public void testCreateEditComponentFloat() throws Exception {
		FloatTextField comp = (FloatTextField) GenericEntityUtil.createEditComponent(createEntityPropertyDefinition(EntityPropertyType.FLOAT), false);
		assertEquals(10, comp.getColumns());
		assertFalse(((Boolean) ReflectionUtil.getPrivate(comp, "forCurrency")).booleanValue());
		assertEquals("", comp.getText());
	}

	@Test
	public void testCreateEditComponentInt() throws Exception {
		NumberTextField comp = (NumberTextField) GenericEntityUtil.createEditComponent(createEntityPropertyDefinition(EntityPropertyType.INTEGER), false);
		assertEquals(10, comp.getColumns());
		assertEquals("", comp.getText());
	}

	@Test
	public void testCreateEditComponentIntegerList() throws Exception {
		EntityProperty entProp = createEntityPropertyDefinition(EntityPropertyType.INTEGER_LIST);
		IntegerListField comp = (IntegerListField) GenericEntityUtil.createEditComponent(entProp, false);
		assertEquals(entProp.getDisplayName(), ReflectionUtil.getPrivate(comp, "dialogTitle"));
	}

	@Test
	public void testCreateEditComponentLong() throws Exception {
		NumberTextField comp = (NumberTextField) GenericEntityUtil.createEditComponent(createEntityPropertyDefinition(EntityPropertyType.LONG), false);
		assertEquals(20, comp.getColumns());
		assertEquals("", comp.getText());
	}

	@Test
	public void testCreateEditComponentPercent() throws Exception {
		FloatTextField comp = (FloatTextField) GenericEntityUtil.createEditComponent(createEntityPropertyDefinition(EntityPropertyType.PERCENT), false);
		assertEquals(10, comp.getColumns());
		assertFalse(((Boolean) ReflectionUtil.getPrivate(comp, "forCurrency")).booleanValue());
		assertEquals("", comp.getText());
	}

	@Test
	public void testCreateEditComponentString() throws Exception {
		JTextField comp = (JTextField) GenericEntityUtil.createEditComponent(createEntityPropertyDefinition(EntityPropertyType.STRING), false);
		assertEquals("", comp.getText());
	}

	@Test
	public void testHasEditComponentValueWithBooleanTypeReturnsAlwaysTrue() throws Exception {
		JCheckBox component = new JCheckBox();
		component.setSelected(false);
		assertTrue(GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.BOOLEAN));
		component.setSelected(true);
		assertTrue(GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.BOOLEAN));
	}

	@Test
	public void testHasEditComponentValueWithCurrencyTypeHappyCase() throws Exception {
		FloatTextField component = new FloatTextField(100, true);
		component.setText(null);
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.CURRENCY));
		component.setText(" ");
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.CURRENCY));
		component.setValue(10.25);
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.CURRENCY));
	}

	@Test
	public void testHasEditComponentValueWithDateTypeHappyCase() throws Exception {
		MDateDateField component = new MDateDateField(true, false);
		component.setValue(null);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.DATE));
		component.setValue(getDate(2006, 10, 10));
		assertTrue(GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.DATE));
	}

	@Test
	public void testHasEditComponentValueWithDoubleTypeHappyCase() throws Exception {
		FloatTextField component = new FloatTextField(100, false);
		component.setText(null);
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.DOUBLE));
		component.setText(" ");
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.DOUBLE));
		component.setValue(10.25);
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.DOUBLE));
	}

	@Test
	public void testHasEditComponentValueWithEnumTypeHappyCase() throws Exception {
		DefaultComboBoxModel<TypeEnumValue> comboBoxModel = new DefaultComboBoxModel<TypeEnumValue>();
		TypeEnumValue typeEnumValue = new TypeEnumValue(1, "value", "Value");
		comboBoxModel.addElement(typeEnumValue);
		TypeEnumValueComboBox component = new TypeEnumValueComboBox(comboBoxModel);
		component.selectTypeEnumValue(null);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.ENUM));
		component.setSelectedItem(null);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.ENUM));
		component.selectTypeEnumValue(1);
		assertTrue(GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.ENUM));
	}

	@Test
	public void testHasEditComponentValueWithFloatTypeHappyCase() throws Exception {
		FloatTextField component = new FloatTextField(100, false);
		component.setText(null);
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.FLOAT));
		component.setText(" ");
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.FLOAT));
		component.setValue(10.25);
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.FLOAT));
	}

	@Test
	public void testHasEditComponentValueWithIntegerListTypeHappyCase() throws Exception {
		IntegerListField component = new IntegerListField("IntegerList");
		component.setValue((int[]) null);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.INTEGER_LIST));
		component.setValue("");
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.INTEGER_LIST));
		component.setValue(new int[] { 2 });
		assertTrue(GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.INTEGER_LIST));
	}

	@Test
	public void testHasEditComponentValueWithIntegerTypeHappyCase() throws Exception {
		NumberTextField component = new NumberTextField(100);
		component.setText(null);
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.INTEGER));
		component.setText(" ");
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.INTEGER));
		component.setValue(1000);
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.INTEGER));
	}

	@Test
	public void testHasEditComponentValueWithLongTypeHappyCase() throws Exception {
		NumberTextField component = new NumberTextField(1200);
		component.setText(null);
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.LONG));
		component.setText(" ");
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.LONG));
		component.setValue(1000);
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.LONG));
	}

	@Test
	public void testHasEditComponentValueWithNullComponentThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				GenericEntityUtil.class,
				"hasEditComponentValue",
				new Class[] { JComponent.class, EntityPropertyType.class },
				new Object[] { null, EntityPropertyType.STRING });
	}

	@Test
	public void testHasEditComponentValueWithNullTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				GenericEntityUtil.class,
				"hasEditComponentValue",
				new Class[] { JComponent.class, EntityPropertyType.class },
				new Object[] { new JTextField(), null });
	}

	@Test
	public void testHasEditComponentValueWithPercentTypeHappyCase() throws Exception {
		FloatTextField component = new FloatTextField(100, false);
		component.setText(null);
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.PERCENT));
		component.setText(" ");
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.PERCENT));
		component.setValue(10.25);
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.PERCENT));
	}

	@Test
	public void testHasEditComponentValueWithStringTypeHappyCase() throws Exception {
		JTextField component = new JTextField();
		component.setText(null);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.STRING));
		component.setText(" ");
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.STRING));
		component.setText("x");
		assertTrue(GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.STRING));
	}

	@Test
	public void testHasEditComponentValueWithSymbolTypeHappyCase() throws Exception {
		JTextField component = new JTextField();
		component.setText(null);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.SYMBOL));
		component.setText(" ");
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.SYMBOL));
		component.setText("x");
		assertTrue(GenericEntityUtil.hasEditComponentValue(component, EntityPropertyType.SYMBOL));
	}

	@Test
	public void testSetEditComponentValueForGenericEntityWithNoPropertyValueOfBooleanTypeSetsComponentToFalse() throws Exception {
		EntityPropertyType propType = EntityPropertyType.BOOLEAN;
		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		entity.clearProperty(propType.value());

		JCheckBox component = new JCheckBox();
		GenericEntityUtil.setEditComponentValue(entity, "assumable", component, propType);
		assertTrue(GenericEntityUtil.hasEditComponentValue(component, propType));
		assertFalse(component.isSelected());
	}

	@Test
	public void testSetEditComponentValueForGenericEntityWithNoPropertyValueOfCurrencyTypeClearsComponent() throws Exception {
		EntityPropertyType propType = EntityPropertyType.CURRENCY;
		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		FloatTextField component = new FloatTextField(200, true);
		component.setValue(0.25);
		GenericEntityUtil.setEditComponentValue(entity, "hazard.insurance.amount", component, propType);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, propType));
	}

	@Test
	public void testSetEditComponentValueForGenericEntityWithNoPropertyValueOfDateTypeClearsComponent() throws Exception {
		EntityPropertyType propType = EntityPropertyType.DATE;
		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		entity.clearProperty(propType.value());
		MDateDateField component = new MDateDateField(true, false);
		component.setValue(getDate(2006, 10, 10));
		GenericEntityUtil.setEditComponentValue(entity, "activation.date", component, propType);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, propType));
	}

	@Test
	public void testSetEditComponentValueForGenericEntityWithNoPropertyValueOfDoubleTypeClearsComponent() throws Exception {
		EntityPropertyType propType = EntityPropertyType.DOUBLE;
		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		entity.clearProperty(propType.value());
		FloatTextField component = new FloatTextField(200, false);
		component.setValue(0.25);
		GenericEntityUtil.setEditComponentValue(entity, "arm.later.adjust.cap", component, propType);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, propType));
	}

	@Test
	public void testSetEditComponentValueForGenericEntityWithNoPropertyValueOfEnumTypeClearsComponent() throws Exception {
		EntityPropertyType propType = EntityPropertyType.ENUM;
		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		entity.clearProperty(propType.value());
		DefaultComboBoxModel<TypeEnumValue> comboBoxModel = new DefaultComboBoxModel<TypeEnumValue>();
		TypeEnumValue typeEnumValue = new TypeEnumValue(1, "value", "Value");
		comboBoxModel.addElement(typeEnumValue);
		TypeEnumValueComboBox component = new TypeEnumValueComboBox(comboBoxModel);
		component.selectTypeEnumValue("value");
		GenericEntityUtil.setEditComponentValue(entity, "product.amortization_type", component, propType);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, propType));
	}

	@Test
	public void testSetEditComponentValueForGenericEntityWithNoPropertyValueOfFloatTypeClearsComponent() throws Exception {
		EntityPropertyType propType = EntityPropertyType.FLOAT;
		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		entity.clearProperty(propType.value());
		FloatTextField component = new FloatTextField(200, false);
		component.setValue(0.25);
		GenericEntityUtil.setEditComponentValue(entity, "arm.first.payment.cap", component, propType);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, propType));
	}

	@Test
	public void testSetEditComponentValueForGenericEntityWithNoPropertyValueOfIntListTypeClearsComponent() throws Exception {
		EntityPropertyType propType = EntityPropertyType.INTEGER_LIST;
		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		entity.clearProperty(propType.value());
		IntegerListField component = new IntegerListField("IntList");
		component.setValue(new int[] { 2, 3 });
		GenericEntityUtil.setEditComponentValue(entity, "amortization.terms", component, propType);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, propType));
	}

	@Test
	public void testSetEditComponentValueForGenericEntityWithNoPropertyValueOfIntTypeClearsComponent() throws Exception {
		EntityPropertyType propType = EntityPropertyType.INTEGER;
		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		entity.clearProperty(propType.value());
		NumberTextField component = new NumberTextField(200);
		component.setValue(12345);
		GenericEntityUtil.setEditComponentValue(entity, "late.charge.type", component, propType);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, propType));
	}

	@Test
	public void testSetEditComponentValueForGenericEntityWithNoPropertyValueOfLongTypeClearsComponent() throws Exception {
		EntityPropertyType propType = EntityPropertyType.LONG;
		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		entity.clearProperty(propType.value());
		NumberTextField component = new NumberTextField(200);
		component.setValue(12345);
		GenericEntityUtil.setEditComponentValue(entity, "deferred.limit", component, propType);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, propType));
	}

	@Test
	public void testSetEditComponentValueForGenericEntityWithNoPropertyValueOfPercentTypeClearsComponent() throws Exception {
		EntityPropertyType propType = EntityPropertyType.PERCENT;
		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		entity.clearProperty(propType.value());
		FloatTextField component = new FloatTextField(200, false);
		component.setValue(0.25);
		GenericEntityUtil.setEditComponentValue(entity, "late.charge.percent", component, propType);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, propType));
	}

	@Test
	public void testSetEditComponentValueForGenericEntityWithNoPropertyValueOfStringTypeClearsComponent() throws Exception {
		EntityPropertyType propType = EntityPropertyType.STRING;
		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		entity.clearProperty(propType.value());
		JTextField component = new JTextField();
		component.setText("value");
		GenericEntityUtil.setEditComponentValue(entity, "description", component, propType);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, propType));
	}

	@Test
	public void testSetEditComponentValueForGenericEntityWithNoPropertyValueOfSymbolTypeClearsComponent() throws Exception {
		EntityPropertyType propType = EntityPropertyType.SYMBOL;
		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		entity.clearProperty(propType.value());
		JTextField component = new JTextField();
		component.setText("value");
		GenericEntityUtil.setEditComponentValue(entity, "pricing.group", component, propType);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, propType));
	}

	@Test
	public void testSetEditComponentValueForGenericEntityWithNullComponentThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				GenericEntityUtil.class,
				"setEditComponentValue",
				new Class[] { GenericEntity.class, String.class, JComponent.class, EntityPropertyType.class },
				new Object[] { new GenericEntity(1, entityType1, "name"), "description", null, EntityPropertyType.STRING });
	}

	@Test
	public void testSetEditComponentValueForGenericEntityWithNullEntityThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				GenericEntityUtil.class,
				"setEditComponentValue",
				new Class[] { GenericEntity.class, String.class, JComponent.class, EntityPropertyType.class },
				new Object[] { null, "description", new JTextField(), EntityPropertyType.STRING });
	}

	@Test
	public void testSetEditComponentValueForGenericEntityWithNullPropertyNameThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				GenericEntityUtil.class,
				"setEditComponentValue",
				new Class[] { GenericEntity.class, String.class, JComponent.class, EntityPropertyType.class },
				new Object[] { new GenericEntity(1, entityType1, "name"), null, new JTextField(), EntityPropertyType.STRING });
	}

	@Test
	public void testSetEditComponentValueForGenericEntityWithNullTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				GenericEntityUtil.class,
				"setEditComponentValue",
				new Class[] { GenericEntity.class, String.class, JComponent.class, EntityPropertyType.class },
				new Object[] { new GenericEntity(1, entityType1, "name"), "description", new JTextField(), null });
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithBooleanTypeSetsPropertyCriterion() throws Exception {
		JCheckBox component = new JCheckBox();
		component.setSelected(false);

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "assumable", component, EntityPropertyType.BOOLEAN);
		Object obj = filterSpec.getPropertyCriterion("assumable");
		assertEquals(Boolean.FALSE, obj);
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithCurrencyTypeSetsPropertyCriterion() throws Exception {
		FloatTextField component = new FloatTextField(200, true);
		component.setValue(1234000.5678);

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "hazard.insurance.amount", component, EntityPropertyType.CURRENCY);
		Object obj = filterSpec.getPropertyCriterion("hazard.insurance.amount");
		assertEquals(new Double(1234000.5678), obj);
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithDateTypeSetsPropertyCriterion() throws Exception {
		MDateDateField component = new MDateDateField(true, false);
		component.setValue(getDate(2006, 10, 10, 15, 23, 44));

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "activation.date", component, EntityPropertyType.DATE);
		Object obj = filterSpec.getPropertyCriterion("activation.date");
		assertEquals(getDate(2006, 10, 10, 15, 23, 44), obj);
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithDoubleTypeSetsPropertyCriterion() throws Exception {
		FloatTextField component = new FloatTextField(200, false);
		component.setValue(1234000.5678);

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "arm.later.adjust.cap", component, EntityPropertyType.DOUBLE);
		Object obj = filterSpec.getPropertyCriterion("arm.later.adjust.cap");
		assertEquals(new Double(1234000.5678), obj);
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithEnumTypeSetsPropertyCriterion() throws Exception {
		DefaultComboBoxModel<TypeEnumValue> comboBoxModel = new DefaultComboBoxModel<TypeEnumValue>();
		TypeEnumValue typeEnumValue = new TypeEnumValue(1, "value", "Value");
		comboBoxModel.addElement(typeEnumValue);
		TypeEnumValueComboBox component = new TypeEnumValueComboBox(comboBoxModel);

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "product.amortization_type", component, EntityPropertyType.ENUM);
		Object obj = filterSpec.getPropertyCriterion("product.amortization_type");
		assertEquals("value", obj);
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithFloatTypeSetsPropertyCriterion() throws Exception {
		FloatTextField component = new FloatTextField(200, false);
		component.setValue(1234.5678f);

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "arm.first.payment.cap", component, EntityPropertyType.FLOAT);
		Object obj = filterSpec.getPropertyCriterion("arm.first.payment.cap");
		assertEquals(new Float(1234.5678f), obj);
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithIntListTypeSetsPropertyCriterion() throws Exception {
		IntegerListField component = new IntegerListField("title");
		component.setValue(new int[] { 200, 300, 400 });

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "amortization.terms", component, EntityPropertyType.INTEGER_LIST);
		Object obj = filterSpec.getPropertyCriterion("amortization.terms");
		assertEquals("200,300,400", obj);
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithIntTypeSetsPropertyCriterion() throws Exception {
		NumberTextField component = new NumberTextField(200);
		component.setValue(23456);

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "late.charge.type", component, EntityPropertyType.INTEGER);
		Object obj = filterSpec.getPropertyCriterion("late.charge.type");
		assertEquals(new Integer(23456), obj);
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithLongTypeSetsPropertyCriterion() throws Exception {
		NumberTextField component = new NumberTextField(200);
		component.setValue(2345600099L);

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "deferred.limit", component, EntityPropertyType.LONG);
		Object obj = filterSpec.getPropertyCriterion("deferred.limit");
		assertEquals(new Long(2345600099L), obj);
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNoValueOfBooleanTypeNeversClearsPropery() throws Exception {
		JCheckBox component = new JCheckBox();
		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "assumable", component, EntityPropertyType.BOOLEAN);
		assertEquals(Boolean.FALSE, filterSpec.getPropertyCriterion("assumable"));

		component.setSelected(true);
		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "assumable", component, EntityPropertyType.BOOLEAN);
		assertEquals(Boolean.TRUE, filterSpec.getPropertyCriterion("assumable"));
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNoValueOfCurrencyTypeClearsCriteria() throws Exception {
		filterSpec.setPropertyCriterion("hazard.insurance.amount", "value");
		FloatTextField component = new FloatTextField(200, true);
		component.setText(null);
		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "hazard.insurance.amount", component, EntityPropertyType.CURRENCY);
		assertNull(filterSpec.getPropertyCriterion("hazard.insurance.amount"));
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNoValueOfDateTypeClearsCriteria() throws Exception {
		filterSpec.setPropertyCriterion("activation.date", getDate(2005, 12, 31, 12, 34, 56));
		MDateDateField component = new MDateDateField(true, false);
		component.setValue(null);
		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "activation.date", component, EntityPropertyType.DATE);
		assertNull(filterSpec.getPropertyCriterion("activation.date"));
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNoValueOfDoubleTypeClearsCriteria() throws Exception {
		filterSpec.setPropertyCriterion("arm.later.adjust.cap", "value");
		FloatTextField component = new FloatTextField(200, false);
		component.setText(null);
		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "arm.later.adjust.cap", component, EntityPropertyType.DOUBLE);
		assertNull(filterSpec.getPropertyCriterion("arm.later.adjust.cap"));
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNoValueOfEnumTypeClearsCriteria() throws Exception {
		filterSpec.setPropertyCriterion("product.amortization_type", "value");

		DefaultComboBoxModel<TypeEnumValue> comboBoxModel = new DefaultComboBoxModel<TypeEnumValue>();
		TypeEnumValue typeEnumValue = new TypeEnumValue(1, "value", "Value");
		comboBoxModel.addElement(typeEnumValue);
		TypeEnumValueComboBox component = new TypeEnumValueComboBox(comboBoxModel);
		component.setSelectedIndex(-1);

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "product.amortization_type", component, EntityPropertyType.ENUM);
		assertNull(filterSpec.getPropertyCriterion("product.amortization_type"));
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNoValueOfFloatTypeClearsCriteria() throws Exception {
		filterSpec.setPropertyCriterion("arm.first.payment.cap", "value");
		FloatTextField component = new FloatTextField(200, false);
		component.setText(null);
		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "arm.first.payment.cap", component, EntityPropertyType.FLOAT);
		assertNull(filterSpec.getPropertyCriterion("arm.first.payment.cap"));
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNoValueOfIntListTypeClearsCriteria() throws Exception {
		filterSpec.setPropertyCriterion("amortization.terms", "value");
		IntegerListField component = new IntegerListField("IntList");
		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "amortization.terms", component, EntityPropertyType.INTEGER_LIST);
		assertNull(filterSpec.getPropertyCriterion("amortization.terms"));
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNoValueOfIntTypeClearsCriteria() throws Exception {
		filterSpec.setPropertyCriterion("product.amortization_type", "value");
		NumberTextField component = new NumberTextField(2000);
		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "product.amortization_type", component, EntityPropertyType.INTEGER);
		assertNull(filterSpec.getPropertyCriterion("product.amortization_type"));
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNoValueOfLongTypeClearsCriteria() throws Exception {
		filterSpec.setPropertyCriterion("deferred.limit", "value");
		NumberTextField component = new NumberTextField(2000);
		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "deferred.limit", component, EntityPropertyType.LONG);
		assertNull(filterSpec.getPropertyCriterion("deferred.limit"));
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNoValueOfPercentTypeClearsCriteria() throws Exception {
		filterSpec.setPropertyCriterion("late.charge.percent", "value");
		FloatTextField component = new FloatTextField(200, false);
		component.setText(null);
		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "late.charge.percent", component, EntityPropertyType.PERCENT);
		assertNull(filterSpec.getPropertyCriterion("late.charge.percent"));
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNoValueOfStringTypeClearsCriteria() throws Exception {
		filterSpec.setPropertyCriterion("description", "value");
		JTextField component = new JTextField();
		component.setText(null);
		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "description", component, EntityPropertyType.STRING);
		assertNull(filterSpec.getPropertyCriterion("description"));
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNoValueOfSymbolTypeClearsCriteria() throws Exception {
		filterSpec.setPropertyCriterion("pricing.group", "value");
		JTextField component = new JTextField();
		component.setText(null);
		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "pricing.group", component, EntityPropertyType.SYMBOL);
		assertNull(filterSpec.getPropertyCriterion("pricing.group"));
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNullFilterSpecThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				GenericEntityUtil.class,
				"setPropertyFromEditComponent",
				new Class[] { GenericEntityFilterSpec.class, String.class, JComponent.class, EntityPropertyType.class },
				new Object[] { null, "description", new JTextField(), EntityPropertyType.STRING });
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNullTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				GenericEntityUtil.class,
				"setPropertyFromEditComponent",
				new Class[] { GenericEntityFilterSpec.class, String.class, JComponent.class, EntityPropertyType.class },
				new Object[] { filterSpec, "description", new JTextField(), null });
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithPercentTypeSetsPropertyCriterion() throws Exception {
		FloatTextField component = new FloatTextField(200, false);
		component.setValue(0.1245f);

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "late.charge.percent", component, EntityPropertyType.PERCENT);
		Object obj = filterSpec.getPropertyCriterion("late.charge.percent");
		assertEquals(new Float(0.1245f), obj);
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithStringTypeSetsPropertyCriterion() throws Exception {
		JTextField component = new JTextField();
		component.setText("some_value");

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "pricing.group", component, EntityPropertyType.SYMBOL);
		Object obj = filterSpec.getPropertyCriterion("pricing.group");
		assertEquals("some_value", obj);
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithSymbolTypeSetsPropertyCriterion() throws Exception {
		JTextField component = new JTextField();
		component.setText("some value");

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "description", component, EntityPropertyType.STRING);
		Object obj = filterSpec.getPropertyCriterion("description");
		assertEquals("some value", obj);
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityWithNoValueOfBooleanTypeNeversClearsPropery() throws Exception {
		GenericEntity entity = new GenericEntity(1, entityType2, "name");

		JCheckBox component = new JCheckBox();
		GenericEntityUtil.setPropertyFromEditComponent(entity, "assumable", component, EntityPropertyType.BOOLEAN);
		assertTrue(entity.hasProperty("assumable"));

		component.setSelected(true);
		GenericEntityUtil.setPropertyFromEditComponent(entity, "assumable", component, EntityPropertyType.BOOLEAN);
		assertTrue(entity.hasProperty("assumable"));
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityWithNoValueOfCurrencyTypeClearsProperty() throws Exception {
		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		entity.setProperty("hazard.insurance.amount", "value");
		FloatTextField component = new FloatTextField(200, true);
		component.setText(null);
		GenericEntityUtil.setPropertyFromEditComponent(entity, "hazard.insurance.amount", component, EntityPropertyType.CURRENCY);
		assertFalse(entity.hasProperty("hazard.insurance.amount"));
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityWithNoValueOfDateTypeClearsProperty() throws Exception {
		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		entity.setProperty("activation.date", getDate(2005, 12, 31, 12, 34, 56));

		MDateDateField component = new MDateDateField(true, false);
		component.setValue(null);
		GenericEntityUtil.setPropertyFromEditComponent(entity, "activation.date", component, EntityPropertyType.DATE);
		assertFalse(entity.hasProperty("activation.date"));
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityWithNoValueOfDoubleTypeClearsProperty() throws Exception {
		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		entity.setProperty("arm.later.adjust.cap", "value");
		FloatTextField component = new FloatTextField(200, false);
		component.setText(null);
		GenericEntityUtil.setPropertyFromEditComponent(entity, "arm.later.adjust.cap", component, EntityPropertyType.DOUBLE);
		assertFalse(entity.hasProperty("arm.later.adjust.cap"));
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityWithNoValueOfEnumTypeClearsProperty() throws Exception {
		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		entity.setProperty("product.amortization_type", "value");

		DefaultComboBoxModel<TypeEnumValue> comboBoxModel = new DefaultComboBoxModel<TypeEnumValue>();
		TypeEnumValue typeEnumValue = new TypeEnumValue(1, "value", "Value");
		comboBoxModel.addElement(typeEnumValue);
		TypeEnumValueComboBox component = new TypeEnumValueComboBox(comboBoxModel);
		component.setSelectedIndex(-1);

		GenericEntityUtil.setPropertyFromEditComponent(entity, "product.amortization_type", component, EntityPropertyType.ENUM);
		assertFalse(entity.hasProperty("product.amortization_type"));
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityWithNoValueOfFloatTypeClearsProperty() throws Exception {
		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		entity.setProperty("arm.first.payment.cap", "value");
		FloatTextField component = new FloatTextField(200, false);
		component.setText(null);
		GenericEntityUtil.setPropertyFromEditComponent(entity, "arm.first.payment.cap", component, EntityPropertyType.FLOAT);
		assertFalse(entity.hasProperty("arm.first.payment.cap"));
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityWithNoValueOfIntListTypeClearsProperty() throws Exception {
		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		entity.setProperty("amortization.terms", "value");
		IntegerListField component = new IntegerListField("IntList");
		GenericEntityUtil.setPropertyFromEditComponent(entity, "amortization.terms", component, EntityPropertyType.INTEGER_LIST);
		assertFalse(entity.hasProperty("amortization.terms"));
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityWithNoValueOfIntTypeClearsProperty() throws Exception {
		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		entity.setProperty("product.amortization_type", "value");

		NumberTextField component = new NumberTextField(2000);
		GenericEntityUtil.setPropertyFromEditComponent(entity, "product.amortization_type", component, EntityPropertyType.INTEGER);
		assertFalse(entity.hasProperty("product.amortization_type"));
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityWithNoValueOfLongTypeClearsProperty() throws Exception {
		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		entity.setProperty("deferred.limit", "value");

		NumberTextField component = new NumberTextField(2000);
		GenericEntityUtil.setPropertyFromEditComponent(entity, "deferred.limit", component, EntityPropertyType.LONG);
		assertFalse(entity.hasProperty("deferred.limit"));
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityWithNoValueOfPercentTypeClearsProperty() throws Exception {
		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		entity.setProperty("late.charge.percent", "value");
		FloatTextField component = new FloatTextField(200, false);
		component.setText(null);
		GenericEntityUtil.setPropertyFromEditComponent(entity, "late.charge.percent", component, EntityPropertyType.PERCENT);
		assertFalse(entity.hasProperty("late.charge.percent"));
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityWithNoValueOfStringTypeClearsProperty() throws Exception {
		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		entity.setProperty("description", "value");

		JTextField component = new JTextField();
		component.setText(null);
		GenericEntityUtil.setPropertyFromEditComponent(entity, "description", component, EntityPropertyType.STRING);
		assertFalse(entity.hasProperty("description"));
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityWithNoValueOfSymbolTypeClearsProperty() throws Exception {
		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		entity.setProperty("pricing.group", "value");

		JTextField component = new JTextField();
		component.setText(null);
		GenericEntityUtil.setPropertyFromEditComponent(entity, "pricing.group", component, EntityPropertyType.SYMBOL);
		assertFalse(entity.hasProperty("pricing.group"));
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityWithNullComponentThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				GenericEntityUtil.class,
				"setPropertyFromEditComponent",
				new Class[] { GenericEntity.class, String.class, JComponent.class, EntityPropertyType.class },
				new Object[] { new GenericEntity(1, entityType1, "name"), "description", null, EntityPropertyType.STRING });
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityWithNullEntityThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				GenericEntityUtil.class,
				"setPropertyFromEditComponent",
				new Class[] { GenericEntity.class, String.class, JComponent.class, EntityPropertyType.class },
				new Object[] { null, "description", new JTextField(), EntityPropertyType.STRING });
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityWithNullPropertyNameThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				GenericEntityUtil.class,
				"setPropertyFromEditComponent",
				new Class[] { GenericEntity.class, String.class, JComponent.class, EntityPropertyType.class },
				new Object[] { new GenericEntity(1, entityType1, "name"), null, new JTextField(), EntityPropertyType.STRING });
	}

	@Test
	public void testSetPropertyFromEditComponentForGenericEntityWithNullTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				GenericEntityUtil.class,
				"setPropertyFromEditComponent",
				new Class[] { GenericEntity.class, String.class, JComponent.class, EntityPropertyType.class },
				new Object[] { new GenericEntity(1, entityType1, "name"), "description", new JTextField(), null });
	}
}
