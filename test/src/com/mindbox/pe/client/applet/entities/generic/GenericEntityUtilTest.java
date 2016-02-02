package com.mindbox.pe.client.applet.entities.generic;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.common.FloatTextField;
import com.mindbox.pe.client.common.IntegerListField;
import com.mindbox.pe.client.common.NumberTextField;
import com.mindbox.pe.client.common.TypeEnumValueComboBox;
import com.mindbox.pe.client.common.dialog.MDateDateField;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.common.config.EntityPropertyDefinition;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.model.filter.GenericEntityFilterSpec;

public class GenericEntityUtilTest extends AbstractClientTestBase {
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GenericEntityUtilTest Tests");
		suite.addTestSuite(GenericEntityUtilTest.class);
		return suite;
	}

	private GenericEntityFilterSpec filterSpec;

	public GenericEntityUtilTest(String name) {
		super(name);
	}

	public void testHasEditComponentValueWithNullComponentThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				GenericEntityUtil.class,
				"hasEditComponentValue",
				new Class[] { JComponent.class, String.class },
				new Object[] { null, "type" });
	}

	public void testHasEditComponentValueWithNullTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				GenericEntityUtil.class,
				"hasEditComponentValue",
				new Class[] { JComponent.class, String.class },
				new Object[] { new JTextField(), null });
	}

	public void testHasEditComponentValueWithBooleanTypeReturnsAlwaysTrue() throws Exception {
		JCheckBox component = new JCheckBox();
		component.setSelected(false);
		assertTrue(GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_BOOLEAN));
		component.setSelected(true);
		assertTrue(GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_BOOLEAN));
	}

	public void testHasEditComponentValueWithCurrencyTypeHappyCase() throws Exception {
		FloatTextField component = new FloatTextField(100, true);
		component.setText(null);
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_CURRENCY));
		component.setText(" ");
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_CURRENCY));
		component.setValue(10.25);
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_CURRENCY));
	}

	public void testHasEditComponentValueWithDateTypeHappyCase() throws Exception {
		MDateDateField component = new MDateDateField(true, false);
		component.setValue(null);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_DATE));
		component.setValue(getDate(2006, 10, 10));
		assertTrue(GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_DATE));
	}

	public void testHasEditComponentValueWithDoubleTypeHappyCase() throws Exception {
		FloatTextField component = new FloatTextField(100, false);
		component.setText(null);
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_DOUBLE));
		component.setText(" ");
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_DOUBLE));
		component.setValue(10.25);
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_DOUBLE));
	}

	public void testHasEditComponentValueWithEnumTypeHappyCase() throws Exception {
		DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
		TypeEnumValue typeEnumValue = new TypeEnumValue(1, "value", "Value");
		comboBoxModel.addElement(typeEnumValue);
		TypeEnumValueComboBox component = new TypeEnumValueComboBox(comboBoxModel);
		component.selectTypeEnumValue(null);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_ENUM));
		component.setSelectedItem(null);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_ENUM));
		component.selectTypeEnumValue(1);
		assertTrue(GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_ENUM));
	}

	public void testHasEditComponentValueWithFloatTypeHappyCase() throws Exception {
		FloatTextField component = new FloatTextField(100, false);
		component.setText(null);
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_FLOAT));
		component.setText(" ");
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_FLOAT));
		component.setValue(10.25);
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_FLOAT));
	}

	public void testHasEditComponentValueWithIntegerTypeHappyCase() throws Exception {
		NumberTextField component = new NumberTextField(100);
		component.setText(null);
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_INT));
		component.setText(" ");
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_INT));
		component.setValue(1000);
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_INT));
	}

	public void testHasEditComponentValueWithIntegerListTypeHappyCase() throws Exception {
		IntegerListField component = new IntegerListField("IntegerList");
		component.setValue((int[]) null);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_INTEGERLIST));
		component.setValue("");
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_INTEGERLIST));
		component.setValue(new int[] { 2 });
		assertTrue(GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_INTEGERLIST));
	}

	public void testHasEditComponentValueWithLongTypeHappyCase() throws Exception {
		NumberTextField component = new NumberTextField(1200);
		component.setText(null);
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_LONG));
		component.setText(" ");
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_LONG));
		component.setValue(1000);
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_LONG));
	}

	public void testHasEditComponentValueWithPercentTypeHappyCase() throws Exception {
		FloatTextField component = new FloatTextField(100, false);
		component.setText(null);
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_PERCENT));
		component.setText(" ");
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_PERCENT));
		component.setValue(10.25);
		assertTrue(component.hasValue() == GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_PERCENT));
	}

	public void testHasEditComponentValueWithStringTypeHappyCase() throws Exception {
		JTextField component = new JTextField();
		component.setText(null);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_STRING));
		component.setText(" ");
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_STRING));
		component.setText("x");
		assertTrue(GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_STRING));
	}

	public void testHasEditComponentValueWithSymbolTypeHappyCase() throws Exception {
		JTextField component = new JTextField();
		component.setText(null);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_SYMBOL));
		component.setText(" ");
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_SYMBOL));
		component.setText("x");
		assertTrue(GenericEntityUtil.hasEditComponentValue(component, ConfigUtil.PROPERTY_TYPE_SYMBOL));
	}

	public void testSetPropertyFromEditComponentForGenericEntityWithNullEntityThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(GenericEntityUtil.class, "setPropertyFromEditComponent", new Class[] {
				GenericEntity.class,
				String.class,
				JComponent.class,
				String.class }, new Object[] { null, "description", new JTextField(), ConfigUtil.PROPERTY_TYPE_STRING });
	}

	public void testSetPropertyFromEditComponentForGenericEntityWithNullPropertyNameThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(GenericEntityUtil.class, "setPropertyFromEditComponent", new Class[] {
				GenericEntity.class,
				String.class,
				JComponent.class,
				String.class }, new Object[] {
				new GenericEntity(1, GenericEntityType.forName("channel"), "name"),
				null,
				new JTextField(),
				ConfigUtil.PROPERTY_TYPE_STRING });
	}

	public void testSetPropertyFromEditComponentForGenericEntityWithNullComponentThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(GenericEntityUtil.class, "setPropertyFromEditComponent", new Class[] {
				GenericEntity.class,
				String.class,
				JComponent.class,
				String.class }, new Object[] {
				new GenericEntity(1, GenericEntityType.forName("channel"), "name"),
				"description",
				null,
				ConfigUtil.PROPERTY_TYPE_STRING });
	}

	public void testSetPropertyFromEditComponentForGenericEntityWithNullTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(GenericEntityUtil.class, "setPropertyFromEditComponent", new Class[] {
				GenericEntity.class,
				String.class,
				JComponent.class,
				String.class }, new Object[] {
				new GenericEntity(1, GenericEntityType.forName("channel"), "name"),
				"description",
				new JTextField(),
				null });
	}

	public void testSetPropertyFromEditComponentForGenericEntityWithNoValueOfBooleanTypeNeversClearsPropery() throws Exception {
		GenericEntity entity = new GenericEntity(1, GenericEntityType.forName("product"), "name");

		JCheckBox component = new JCheckBox();
		GenericEntityUtil.setPropertyFromEditComponent(entity, "assumable", component, ConfigUtil.PROPERTY_TYPE_BOOLEAN);
		assertTrue(entity.hasProperty("assumable"));

		component.setSelected(true);
		GenericEntityUtil.setPropertyFromEditComponent(entity, "assumable", component, ConfigUtil.PROPERTY_TYPE_BOOLEAN);
		assertTrue(entity.hasProperty("assumable"));
	}

	public void testSetPropertyFromEditComponentForGenericEntityWithNoValueOfDateTypeClearsProperty() throws Exception {
		GenericEntity entity = new GenericEntity(1, GenericEntityType.forName("product"), "name");
		entity.setProperty("activation.date", getDate(2005, 12, 31, 12, 34, 56));

		MDateDateField component = new MDateDateField(true, false);
		component.setValue(null);
		GenericEntityUtil.setPropertyFromEditComponent(entity, "activation.date", component, ConfigUtil.PROPERTY_TYPE_DATE);
		assertFalse(entity.hasProperty("activation.date"));
	}

	public void testSetPropertyFromEditComponentForGenericEntityWithNoValueOfEnumTypeClearsProperty() throws Exception {
		GenericEntity entity = new GenericEntity(1, GenericEntityType.forName("product"), "name");
		entity.setProperty("product.amortization_type", "value");

		DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
		TypeEnumValue typeEnumValue = new TypeEnumValue(1, "value", "Value");
		comboBoxModel.addElement(typeEnumValue);
		TypeEnumValueComboBox component = new TypeEnumValueComboBox(comboBoxModel);
		component.setSelectedIndex(-1);

		GenericEntityUtil.setPropertyFromEditComponent(entity, "product.amortization_type", component, ConfigUtil.PROPERTY_TYPE_ENUM);
		assertFalse(entity.hasProperty("product.amortization_type"));
	}

	public void testSetPropertyFromEditComponentForGenericEntityWithNoValueOfIntTypeClearsProperty() throws Exception {
		GenericEntity entity = new GenericEntity(1, GenericEntityType.forName("product"), "name");
		entity.setProperty("product.amortization_type", "value");

		NumberTextField component = new NumberTextField(2000);
		GenericEntityUtil.setPropertyFromEditComponent(entity, "product.amortization_type", component, ConfigUtil.PROPERTY_TYPE_INT);
		assertFalse(entity.hasProperty("product.amortization_type"));
	}

	public void testSetPropertyFromEditComponentForGenericEntityWithNoValueOfLongTypeClearsProperty() throws Exception {
		GenericEntity entity = new GenericEntity(1, GenericEntityType.forName("product"), "name");
		entity.setProperty("deferred.limit", "value");

		NumberTextField component = new NumberTextField(2000);
		GenericEntityUtil.setPropertyFromEditComponent(entity, "deferred.limit", component, ConfigUtil.PROPERTY_TYPE_LONG);
		assertFalse(entity.hasProperty("deferred.limit"));
	}

	public void testSetPropertyFromEditComponentForGenericEntityWithNoValueOfIntListTypeClearsProperty() throws Exception {
		GenericEntity entity = new GenericEntity(1, GenericEntityType.forName("product"), "name");
		entity.setProperty("amortization.terms", "value");
		IntegerListField component = new IntegerListField("IntList");
		GenericEntityUtil.setPropertyFromEditComponent(entity, "amortization.terms", component, ConfigUtil.PROPERTY_TYPE_INTEGERLIST);
		assertFalse(entity.hasProperty("amortization.terms"));
	}

	public void testSetPropertyFromEditComponentForGenericEntityWithNoValueOfCurrencyTypeClearsProperty() throws Exception {
		GenericEntity entity = new GenericEntity(1, GenericEntityType.forName("product"), "name");
		entity.setProperty("hazard.insurance.amount", "value");
		FloatTextField component = new FloatTextField(200, true);
		component.setText(null);
		GenericEntityUtil.setPropertyFromEditComponent(entity, "hazard.insurance.amount", component, ConfigUtil.PROPERTY_TYPE_CURRENCY);
		assertFalse(entity.hasProperty("hazard.insurance.amount"));

	}

	public void testSetPropertyFromEditComponentForGenericEntityWithNoValueOfDoubleTypeClearsProperty() throws Exception {
		GenericEntity entity = new GenericEntity(1, GenericEntityType.forName("product"), "name");
		entity.setProperty("arm.later.adjust.cap", "value");
		FloatTextField component = new FloatTextField(200, false);
		component.setText(null);
		GenericEntityUtil.setPropertyFromEditComponent(entity, "arm.later.adjust.cap", component, ConfigUtil.PROPERTY_TYPE_DOUBLE);
		assertFalse(entity.hasProperty("arm.later.adjust.cap"));
	}

	public void testSetPropertyFromEditComponentForGenericEntityWithNoValueOfFloatTypeClearsProperty() throws Exception {
		GenericEntity entity = new GenericEntity(1, GenericEntityType.forName("product"), "name");
		entity.setProperty("arm.first.payment.cap", "value");
		FloatTextField component = new FloatTextField(200, false);
		component.setText(null);
		GenericEntityUtil.setPropertyFromEditComponent(entity, "arm.first.payment.cap", component, ConfigUtil.PROPERTY_TYPE_FLOAT);
		assertFalse(entity.hasProperty("arm.first.payment.cap"));
	}

	public void testSetPropertyFromEditComponentForGenericEntityWithNoValueOfPercentTypeClearsProperty() throws Exception {
		GenericEntity entity = new GenericEntity(1, GenericEntityType.forName("product"), "name");
		entity.setProperty("late.charge.percent", "value");
		FloatTextField component = new FloatTextField(200, false);
		component.setText(null);
		GenericEntityUtil.setPropertyFromEditComponent(entity, "late.charge.percent", component, ConfigUtil.PROPERTY_TYPE_PERCENT);
		assertFalse(entity.hasProperty("late.charge.percent"));
	}

	public void testSetPropertyFromEditComponentForGenericEntityWithNoValueOfStringTypeClearsProperty() throws Exception {
		GenericEntity entity = new GenericEntity(1, GenericEntityType.forName("product"), "name");
		entity.setProperty("description", "value");

		JTextField component = new JTextField();
		component.setText(null);
		GenericEntityUtil.setPropertyFromEditComponent(entity, "description", component, ConfigUtil.PROPERTY_TYPE_STRING);
		assertFalse(entity.hasProperty("description"));
	}

	public void testSetPropertyFromEditComponentForGenericEntityWithNoValueOfSymbolTypeClearsProperty() throws Exception {
		GenericEntity entity = new GenericEntity(1, GenericEntityType.forName("product"), "name");
		entity.setProperty("pricing.group", "value");

		JTextField component = new JTextField();
		component.setText(null);
		GenericEntityUtil.setPropertyFromEditComponent(entity, "pricing.group", component, ConfigUtil.PROPERTY_TYPE_SYMBOL);
		assertFalse(entity.hasProperty("pricing.group"));
	}

	public void testSetEditComponentValueForGenericEntityWithNullEntityThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(GenericEntityUtil.class, "setEditComponentValue", new Class[] {
				GenericEntity.class,
				String.class,
				JComponent.class,
				String.class }, new Object[] { null, "description", new JTextField(), ConfigUtil.PROPERTY_TYPE_STRING });
	}

	public void testSetEditComponentValueForGenericEntityWithNullPropertyNameThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(GenericEntityUtil.class, "setEditComponentValue", new Class[] {
				GenericEntity.class,
				String.class,
				JComponent.class,
				String.class }, new Object[] {
				new GenericEntity(1, GenericEntityType.forName("channel"), "name"),
				null,
				new JTextField(),
				ConfigUtil.PROPERTY_TYPE_STRING });
	}

	public void testSetEditComponentValueForGenericEntityWithNullComponentThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(GenericEntityUtil.class, "setEditComponentValue", new Class[] {
				GenericEntity.class,
				String.class,
				JComponent.class,
				String.class }, new Object[] {
				new GenericEntity(1, GenericEntityType.forName("channel"), "name"),
				"description",
				null,
				ConfigUtil.PROPERTY_TYPE_STRING });
	}

	public void testSetEditComponentValueForGenericEntityWithNullTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(GenericEntityUtil.class, "setEditComponentValue", new Class[] {
				GenericEntity.class,
				String.class,
				JComponent.class,
				String.class }, new Object[] {
				new GenericEntity(1, GenericEntityType.forName("channel"), "name"),
				"description",
				new JTextField(),
				null });
	}

	public void testSetEditComponentValueForGenericEntityWithNoPropertyValueOfBooleanTypeSetsComponentToFalse() throws Exception {
		String propType = ConfigUtil.PROPERTY_TYPE_BOOLEAN;
		GenericEntity entity = new GenericEntity(1, GenericEntityType.forName("product"), "name");
		entity.clearProperty(propType);

		JCheckBox component = new JCheckBox();
		GenericEntityUtil.setEditComponentValue(entity, "assumable", component, propType);
		assertTrue(GenericEntityUtil.hasEditComponentValue(component, propType));
		assertFalse(component.isSelected());
	}

	public void testSetEditComponentValueForGenericEntityWithNoPropertyValueOfCurrencyTypeClearsComponent() throws Exception {
		String propType = ConfigUtil.PROPERTY_TYPE_CURRENCY;
		GenericEntity entity = new GenericEntity(1, GenericEntityType.forName("product"), "name");
		FloatTextField component = new FloatTextField(200, true);
		component.setValue(0.25);
		GenericEntityUtil.setEditComponentValue(entity, "hazard.insurance.amount", component, propType);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, propType));
	}

	public void testSetEditComponentValueForGenericEntityWithNoPropertyValueOfDoubleTypeClearsComponent() throws Exception {
		String propType = ConfigUtil.PROPERTY_TYPE_DOUBLE;
		GenericEntity entity = new GenericEntity(1, GenericEntityType.forName("product"), "name");
		entity.clearProperty(propType);
		FloatTextField component = new FloatTextField(200, false);
		component.setValue(0.25);
		GenericEntityUtil.setEditComponentValue(entity, "arm.later.adjust.cap", component, propType);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, propType));
	}

	public void testSetEditComponentValueForGenericEntityWithNoPropertyValueOfFloatTypeClearsComponent() throws Exception {
		String propType = ConfigUtil.PROPERTY_TYPE_FLOAT;
		GenericEntity entity = new GenericEntity(1, GenericEntityType.forName("product"), "name");
		entity.clearProperty(propType);
		FloatTextField component = new FloatTextField(200, false);
		component.setValue(0.25);
		GenericEntityUtil.setEditComponentValue(entity, "arm.first.payment.cap", component, propType);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, propType));
	}

	public void testSetEditComponentValueForGenericEntityWithNoPropertyValueOfPercentTypeClearsComponent() throws Exception {
		String propType = ConfigUtil.PROPERTY_TYPE_PERCENT;
		GenericEntity entity = new GenericEntity(1, GenericEntityType.forName("product"), "name");
		entity.clearProperty(propType);
		FloatTextField component = new FloatTextField(200, false);
		component.setValue(0.25);
		GenericEntityUtil.setEditComponentValue(entity, "late.charge.percent", component, propType);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, propType));
	}

	public void testSetEditComponentValueForGenericEntityWithNoPropertyValueOfIntTypeClearsComponent() throws Exception {
		String propType = ConfigUtil.PROPERTY_TYPE_INT;
		GenericEntity entity = new GenericEntity(1, GenericEntityType.forName("product"), "name");
		entity.clearProperty(propType);
		NumberTextField component = new NumberTextField(200);
		component.setValue(12345);
		GenericEntityUtil.setEditComponentValue(entity, "late.charge.type", component, propType);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, propType));
	}

	public void testSetEditComponentValueForGenericEntityWithNoPropertyValueOfLongTypeClearsComponent() throws Exception {
		String propType = ConfigUtil.PROPERTY_TYPE_LONG;
		GenericEntity entity = new GenericEntity(1, GenericEntityType.forName("product"), "name");
		entity.clearProperty(propType);
		NumberTextField component = new NumberTextField(200);
		component.setValue(12345);
		GenericEntityUtil.setEditComponentValue(entity, "deferred.limit", component, propType);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, propType));
	}

	public void testSetEditComponentValueForGenericEntityWithNoPropertyValueOfIntListTypeClearsComponent() throws Exception {
		String propType = ConfigUtil.PROPERTY_TYPE_INTEGERLIST;
		GenericEntity entity = new GenericEntity(1, GenericEntityType.forName("product"), "name");
		entity.clearProperty(propType);
		IntegerListField component = new IntegerListField("IntList");
		component.setValue(new int[] { 2, 3 });
		GenericEntityUtil.setEditComponentValue(entity, "amortization.terms", component, propType);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, propType));
	}

	public void testSetEditComponentValueForGenericEntityWithNoPropertyValueOfDateTypeClearsComponent() throws Exception {
		String propType = ConfigUtil.PROPERTY_TYPE_DATE;
		GenericEntity entity = new GenericEntity(1, GenericEntityType.forName("product"), "name");
		entity.clearProperty(propType);
		MDateDateField component = new MDateDateField(true, false);
		component.setValue(getDate(2006, 10, 10));
		GenericEntityUtil.setEditComponentValue(entity, "activation.date", component, propType);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, propType));
	}

	public void testSetEditComponentValueForGenericEntityWithNoPropertyValueOfEnumTypeClearsComponent() throws Exception {
		String propType = ConfigUtil.PROPERTY_TYPE_ENUM;
		GenericEntity entity = new GenericEntity(1, GenericEntityType.forName("product"), "name");
		entity.clearProperty(propType);
		DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
		TypeEnumValue typeEnumValue = new TypeEnumValue(1, "value", "Value");
		comboBoxModel.addElement(typeEnumValue);
		TypeEnumValueComboBox component = new TypeEnumValueComboBox(comboBoxModel);
		component.selectTypeEnumValue("value");
		GenericEntityUtil.setEditComponentValue(entity, "product.amortization_type", component, propType);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, propType));
	}

	public void testSetEditComponentValueForGenericEntityWithNoPropertyValueOfStringTypeClearsComponent() throws Exception {
		String propType = ConfigUtil.PROPERTY_TYPE_STRING;
		GenericEntity entity = new GenericEntity(1, GenericEntityType.forName("product"), "name");
		entity.clearProperty(propType);
		JTextField component = new JTextField();
		component.setText("value");
		GenericEntityUtil.setEditComponentValue(entity, "description", component, propType);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, propType));
	}

	public void testSetEditComponentValueForGenericEntityWithNoPropertyValueOfSymbolTypeClearsComponent() throws Exception {
		String propType = ConfigUtil.PROPERTY_TYPE_SYMBOL;
		GenericEntity entity = new GenericEntity(1, GenericEntityType.forName("product"), "name");
		entity.clearProperty(propType);
		JTextField component = new JTextField();
		component.setText("value");
		GenericEntityUtil.setEditComponentValue(entity, "pricing.group", component, propType);
		assertFalse(GenericEntityUtil.hasEditComponentValue(component, propType));
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNullFilterSpecThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(GenericEntityUtil.class, "setPropertyFromEditComponent", new Class[] {
				GenericEntityFilterSpec.class,
				String.class,
				JComponent.class,
				String.class }, new Object[] { null, "description", new JTextField(), ConfigUtil.PROPERTY_TYPE_STRING });
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNullTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(GenericEntityUtil.class, "setPropertyFromEditComponent", new Class[] {
				GenericEntityFilterSpec.class,
				String.class,
				JComponent.class,
				String.class }, new Object[] { filterSpec, "description", new JTextField(), null });
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNoValueOfBooleanTypeNeversClearsPropery() throws Exception {
		JCheckBox component = new JCheckBox();
		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "assumable", component, ConfigUtil.PROPERTY_TYPE_BOOLEAN);
		assertEquals(Boolean.FALSE, filterSpec.getPropertyCriterion("assumable"));

		component.setSelected(true);
		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "assumable", component, ConfigUtil.PROPERTY_TYPE_BOOLEAN);
		assertEquals(Boolean.TRUE, filterSpec.getPropertyCriterion("assumable"));
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNoValueOfDateTypeClearsCriteria() throws Exception {
		filterSpec.setPropertyCriterion("activation.date", getDate(2005, 12, 31, 12, 34, 56));
		MDateDateField component = new MDateDateField(true, false);
		component.setValue(null);
		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "activation.date", component, ConfigUtil.PROPERTY_TYPE_DATE);
		assertNull(filterSpec.getPropertyCriterion("activation.date"));
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNoValueOfEnumTypeClearsCriteria() throws Exception {
		filterSpec.setPropertyCriterion("product.amortization_type", "value");

		DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
		TypeEnumValue typeEnumValue = new TypeEnumValue(1, "value", "Value");
		comboBoxModel.addElement(typeEnumValue);
		TypeEnumValueComboBox component = new TypeEnumValueComboBox(comboBoxModel);
		component.setSelectedIndex(-1);

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "product.amortization_type", component, ConfigUtil.PROPERTY_TYPE_ENUM);
		assertNull(filterSpec.getPropertyCriterion("product.amortization_type"));
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNoValueOfIntTypeClearsCriteria() throws Exception {
		filterSpec.setPropertyCriterion("product.amortization_type", "value");
		NumberTextField component = new NumberTextField(2000);
		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "product.amortization_type", component, ConfigUtil.PROPERTY_TYPE_INT);
		assertNull(filterSpec.getPropertyCriterion("product.amortization_type"));
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNoValueOfLongTypeClearsCriteria() throws Exception {
		filterSpec.setPropertyCriterion("deferred.limit", "value");
		NumberTextField component = new NumberTextField(2000);
		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "deferred.limit", component, ConfigUtil.PROPERTY_TYPE_LONG);
		assertNull(filterSpec.getPropertyCriterion("deferred.limit"));
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNoValueOfIntListTypeClearsCriteria() throws Exception {
		filterSpec.setPropertyCriterion("amortization.terms", "value");
		IntegerListField component = new IntegerListField("IntList");
		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "amortization.terms", component, ConfigUtil.PROPERTY_TYPE_INTEGERLIST);
		assertNull(filterSpec.getPropertyCriterion("amortization.terms"));
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNoValueOfCurrencyTypeClearsCriteria() throws Exception {
		filterSpec.setPropertyCriterion("hazard.insurance.amount", "value");
		FloatTextField component = new FloatTextField(200, true);
		component.setText(null);
		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "hazard.insurance.amount", component, ConfigUtil.PROPERTY_TYPE_CURRENCY);
		assertNull(filterSpec.getPropertyCriterion("hazard.insurance.amount"));
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNoValueOfDoubleTypeClearsCriteria() throws Exception {
		filterSpec.setPropertyCriterion("arm.later.adjust.cap", "value");
		FloatTextField component = new FloatTextField(200, false);
		component.setText(null);
		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "arm.later.adjust.cap", component, ConfigUtil.PROPERTY_TYPE_DOUBLE);
		assertNull(filterSpec.getPropertyCriterion("arm.later.adjust.cap"));
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNoValueOfFloatTypeClearsCriteria() throws Exception {
		filterSpec.setPropertyCriterion("arm.first.payment.cap", "value");
		FloatTextField component = new FloatTextField(200, false);
		component.setText(null);
		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "arm.first.payment.cap", component, ConfigUtil.PROPERTY_TYPE_FLOAT);
		assertNull(filterSpec.getPropertyCriterion("arm.first.payment.cap"));
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNoValueOfPercentTypeClearsCriteria() throws Exception {
		filterSpec.setPropertyCriterion("late.charge.percent", "value");
		FloatTextField component = new FloatTextField(200, false);
		component.setText(null);
		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "late.charge.percent", component, ConfigUtil.PROPERTY_TYPE_PERCENT);
		assertNull(filterSpec.getPropertyCriterion("late.charge.percent"));
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNoValueOfStringTypeClearsCriteria() throws Exception {
		filterSpec.setPropertyCriterion("description", "value");
		JTextField component = new JTextField();
		component.setText(null);
		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "description", component, ConfigUtil.PROPERTY_TYPE_STRING);
		assertNull(filterSpec.getPropertyCriterion("description"));
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithNoValueOfSymbolTypeClearsCriteria() throws Exception {
		filterSpec.setPropertyCriterion("pricing.group", "value");
		JTextField component = new JTextField();
		component.setText(null);
		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "pricing.group", component, ConfigUtil.PROPERTY_TYPE_SYMBOL);
		assertNull(filterSpec.getPropertyCriterion("pricing.group"));
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithBooleanTypeSetsPropertyCriterion() throws Exception {
		JCheckBox component = new JCheckBox();
		component.setSelected(false);

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "assumable", component, ConfigUtil.PROPERTY_TYPE_BOOLEAN);
		Object obj = filterSpec.getPropertyCriterion("assumable");
		assertEquals(Boolean.FALSE, obj);
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithDateTypeSetsPropertyCriterion() throws Exception {
		MDateDateField component = new MDateDateField(true, false);
		component.setValue(getDate(2006, 10, 10, 15, 23, 44));

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "activation.date", component, ConfigUtil.PROPERTY_TYPE_DATE);
		Object obj = filterSpec.getPropertyCriterion("activation.date");
		assertEquals(getDate(2006, 10, 10, 15, 23, 44), obj);
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithEnumTypeSetsPropertyCriterion() throws Exception {
		DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
		TypeEnumValue typeEnumValue = new TypeEnumValue(1, "value", "Value");
		comboBoxModel.addElement(typeEnumValue);
		TypeEnumValueComboBox component = new TypeEnumValueComboBox(comboBoxModel);

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "product.amortization_type", component, ConfigUtil.PROPERTY_TYPE_ENUM);
		Object obj = filterSpec.getPropertyCriterion("product.amortization_type");
		assertEquals("value", obj);
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithIntListTypeSetsPropertyCriterion() throws Exception {
		IntegerListField component = new IntegerListField("title");
		component.setValue(new int[] { 200, 300, 400 });

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "amortization.terms", component, ConfigUtil.PROPERTY_TYPE_INTEGERLIST);
		Object obj = filterSpec.getPropertyCriterion("amortization.terms");
		assertEquals("200,300,400", obj);
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithCurrencyTypeSetsPropertyCriterion() throws Exception {
		FloatTextField component = new FloatTextField(200, true);
		component.setValue(1234000.5678);

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "hazard.insurance.amount", component, ConfigUtil.PROPERTY_TYPE_CURRENCY);
		Object obj = filterSpec.getPropertyCriterion("hazard.insurance.amount");
		assertEquals(new Double(1234000.5678), obj);
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithDoubleTypeSetsPropertyCriterion() throws Exception {
		FloatTextField component = new FloatTextField(200, false);
		component.setValue(1234000.5678);

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "arm.later.adjust.cap", component, ConfigUtil.PROPERTY_TYPE_DOUBLE);
		Object obj = filterSpec.getPropertyCriterion("arm.later.adjust.cap");
		assertEquals(new Double(1234000.5678), obj);
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithIntTypeSetsPropertyCriterion() throws Exception {
		NumberTextField component = new NumberTextField(200);
		component.setValue(23456);

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "late.charge.type", component, ConfigUtil.PROPERTY_TYPE_INT);
		Object obj = filterSpec.getPropertyCriterion("late.charge.type");
		assertEquals(new Integer(23456), obj);
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithLongTypeSetsPropertyCriterion() throws Exception {
		NumberTextField component = new NumberTextField(200);
		component.setValue(2345600099L);

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "deferred.limit", component, ConfigUtil.PROPERTY_TYPE_LONG);
		Object obj = filterSpec.getPropertyCriterion("deferred.limit");
		assertEquals(new Long(2345600099L), obj);
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithFloatTypeSetsPropertyCriterion() throws Exception {
		FloatTextField component = new FloatTextField(200, false);
		component.setValue(1234.5678f);

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "arm.first.payment.cap", component, ConfigUtil.PROPERTY_TYPE_FLOAT);
		Object obj = filterSpec.getPropertyCriterion("arm.first.payment.cap");
		assertEquals(new Float(1234.5678f), obj);
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithPercentTypeSetsPropertyCriterion() throws Exception {
		FloatTextField component = new FloatTextField(200, false);
		component.setValue(0.1245f);

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "late.charge.percent", component, ConfigUtil.PROPERTY_TYPE_PERCENT);
		Object obj = filterSpec.getPropertyCriterion("late.charge.percent");
		assertEquals(new Float(0.1245f), obj);
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithSymbolTypeSetsPropertyCriterion() throws Exception {
		JTextField component = new JTextField();
		component.setText("some value");

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "description", component, ConfigUtil.PROPERTY_TYPE_STRING);
		Object obj = filterSpec.getPropertyCriterion("description");
		assertEquals("some value", obj);
	}

	public void testSetPropertyFromEditComponentForGenericEntityFilterSpecWithStringTypeSetsPropertyCriterion() throws Exception {
		JTextField component = new JTextField();
		component.setText("some_value");

		GenericEntityUtil.setPropertyFromEditComponent(filterSpec, "pricing.group", component, ConfigUtil.PROPERTY_TYPE_SYMBOL);
		Object obj = filterSpec.getPropertyCriterion("pricing.group");
		assertEquals("some_value", obj);
	}

	public void testCreateEditComponentBoolean() throws Exception {
		JCheckBox comp = (JCheckBox) GenericEntityUtil.createEditComponent(
				ObjectMother.createEntityPropertyDefinition(ConfigUtil.PROPERTY_TYPE_BOOLEAN),
				false);
		assertEquals("Yes", comp.getText());
		assertFalse(comp.isSelected());
	}

	public void testCreateEditComponentCurrency() throws Exception {
		FloatTextField comp = (FloatTextField) GenericEntityUtil.createEditComponent(
				ObjectMother.createEntityPropertyDefinition(ConfigUtil.PROPERTY_TYPE_CURRENCY),
				false);
		assertEquals(20, comp.getColumns());
		assertTrue(((Boolean) ReflectionUtil.getPrivate(comp, "forCurrency")).booleanValue());
		assertEquals("", comp.getText());
	}

	public void testCreateEditComponentDate() throws Exception {
		MDateDateField comp = (MDateDateField) GenericEntityUtil.createEditComponent(
				ObjectMother.createEntityPropertyDefinition(ConfigUtil.PROPERTY_TYPE_DATE),
				false);
		assertFalse(((Boolean) ReflectionUtil.getPrivate(comp, "forTime")).booleanValue());
		assertFalse(((Boolean) ReflectionUtil.getPrivate(comp, "showClearButton")).booleanValue());
		assertTrue(((Boolean) ReflectionUtil.getPrivate(comp, "allowSecondsInDate")).booleanValue());
		assertNull(comp.getValue());
	}

	public void testCreateEditComponentDouble() throws Exception {
		FloatTextField comp = (FloatTextField) GenericEntityUtil.createEditComponent(
				ObjectMother.createEntityPropertyDefinition(ConfigUtil.PROPERTY_TYPE_CURRENCY),
				false);
		assertEquals(20, comp.getColumns());
		assertTrue(((Boolean) ReflectionUtil.getPrivate(comp, "forCurrency")).booleanValue());
		assertEquals("", comp.getText());
	}

	// TODO: davies, Aug 23, 2006: finish these tests after 4.5.0 release
	//	static JComponent createEditComponent(EntityPropertyDefinition propDef, boolean hasEmptyValue) {
	//		... 
	//	    else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_ENUM)) {
	//			if (propDef.isAttributeMapSet()) {
	//				comp = UIFactory.createTypeEnumComboBoxForAttributeMap(propDef.getAttributeMap(), hasEmptyValue, propDef.isSort());
	//			}
	//			else {
	//				comp = UIFactory.createTypeEnumComboBox(propDef.getEnumType(), hasEmptyValue, propDef.isSort());
	//			}
	//		}
	//
	//	public void testCreateEditComponentEnum_AttributeMap_EmptyValue_Sorted() throws Exception {
	//	TypeEnumValueComboBox comp = (TypeEnumValueComboBox) GenericEntityUtil.createEditComponent(ObjectMother.createEntityPropertyDefinitionEnum(ConfigUtil.PROPERTY_TYPE_ENUM), true);
	//	assertEquals(20, comp.getColumns());
	//	assertTrue(((Boolean) ReflectionUtil.getPrivate(comp, "forCurrency")).booleanValue());
	//}
	//
	//	public void testCreateEditComponentEnum_AttributeMap_NoEmptyValue_NotSorted() throws Exception {
	//	TypeEnumValueComboBox comp = (TypeEnumValueComboBox) GenericEntityUtil.createEditComponent(ObjectMother.createEntityPropertyDefinitionEnum(ConfigUtil.PROPERTY_TYPE_ENUM), true);
	//	assertEquals(20, comp.getColumns());
	//	assertTrue(((Boolean) ReflectionUtil.getPrivate(comp, "forCurrency")).booleanValue());
	//}
	//
	//	public void testCreateEditComponentEnum_NoAttributeMap_EmptyValue_Sorted() throws Exception {
	//	TypeEnumValueComboBox comp = (TypeEnumValueComboBox) GenericEntityUtil.createEditComponent(ObjectMother.createEntityPropertyDefinitionEnum(ConfigUtil.PROPERTY_TYPE_ENUM), true);
	//	assertEquals(20, comp.getColumns());
	//	assertTrue(((Boolean) ReflectionUtil.getPrivate(comp, "forCurrency")).booleanValue());
	//}
	//
	//	public void testCreateEditComponentEnum_NoAttributeMap_NoEmptyValue_NotSorted() throws Exception {
	//	TypeEnumValueComboBox comp = (TypeEnumValueComboBox) GenericEntityUtil.createEditComponent(ObjectMother.createEntityPropertyDefinitionEnum(ConfigUtil.PROPERTY_TYPE_ENUM), true);
	//	assertEquals(20, comp.getColumns());
	//	assertTrue(((Boolean) ReflectionUtil.getPrivate(comp, "forCurrency")).booleanValue());
	//}
	//
	//  ObjectMother methods...
	//	public static EntityPropertyDefinition createEntityPropertyDefinition_Enum() {
	//		return createEntityPropertyDefinition_Enum(true, true, true, true);
	//	}
	//
	//	public static EntityPropertyDefinition createEntityPropertyDefinition_Enum(boolean attributeMapSet, boolean sorted, boolean required, boolean searchable) {
	//		EntityPropertyDefinition entityPropDef = createEntityPropertyDefinition(ConfigUtil.PROPERTY_TYPE_ENUM, required, searchable);
	//		entityPropDef.setAttributeMap(ATTRIBUTE_MAP); // createDomainClass() (including adding it to DomainModel.domainClassList), return "<DomainModel>.<DomainAttribute>, see DomainModel.getDomainAttribute
	//		entityPropDef.setSort(sorted ? ConfigUtil.CONFIG_VALUE_YES : ConfigUtil.CONFIG_VALUE_NO);
	//		return entityPropDef;
	//	}

	//	else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_SYMBOL)) {
	//		comp = new JFormattedTextField(FormatterFactory.getSymbolFormatter());
	//	}

	public void testCreateEditComponentFloat() throws Exception {
		FloatTextField comp = (FloatTextField) GenericEntityUtil.createEditComponent(
				ObjectMother.createEntityPropertyDefinition(ConfigUtil.PROPERTY_TYPE_FLOAT),
				false);
		assertEquals(10, comp.getColumns());
		assertFalse(((Boolean) ReflectionUtil.getPrivate(comp, "forCurrency")).booleanValue());
		assertEquals("", comp.getText());
	}

	public void testCreateEditComponentInt() throws Exception {
		NumberTextField comp = (NumberTextField) GenericEntityUtil.createEditComponent(
				ObjectMother.createEntityPropertyDefinition(ConfigUtil.PROPERTY_TYPE_INT),
				false);
		assertEquals(10, comp.getColumns());
		assertEquals("", comp.getText());
	}

	public void testCreateEditComponentIntegerList() throws Exception {
		EntityPropertyDefinition entProp = ObjectMother.createEntityPropertyDefinition(ConfigUtil.PROPERTY_TYPE_INTEGERLIST);
		IntegerListField comp = (IntegerListField) GenericEntityUtil.createEditComponent(entProp, false);
		assertEquals(entProp.getDisplayName(), ReflectionUtil.getPrivate(comp, "dialogTitle"));
	}

	public void testCreateEditComponentLong() throws Exception {
		NumberTextField comp = (NumberTextField) GenericEntityUtil.createEditComponent(
				ObjectMother.createEntityPropertyDefinition(ConfigUtil.PROPERTY_TYPE_LONG),
				false);
		assertEquals(20, comp.getColumns());
		assertEquals("", comp.getText());
	}

	public void testCreateEditComponentPercent() throws Exception {
		FloatTextField comp = (FloatTextField) GenericEntityUtil.createEditComponent(
				ObjectMother.createEntityPropertyDefinition(ConfigUtil.PROPERTY_TYPE_PERCENT),
				false);
		assertEquals(10, comp.getColumns());
		assertFalse(((Boolean) ReflectionUtil.getPrivate(comp, "forCurrency")).booleanValue());
		assertEquals("", comp.getText());
	}

	public void testCreateEditComponentString() throws Exception {
		JTextField comp = (JTextField) GenericEntityUtil.createEditComponent(
				ObjectMother.createEntityPropertyDefinition(ConfigUtil.PROPERTY_TYPE_STRING),
				false);
		assertEquals("", comp.getText());
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		filterSpec = new GenericEntityFilterSpec(GenericEntityType.forName("product"), 1, "name");
	}

	protected void tearDown() throws Exception {
		filterSpec = null;
		config.resetConfiguration();
		super.tearDown();
	}
}
