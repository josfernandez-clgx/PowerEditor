package com.mindbox.pe.client.applet.entities.generic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractListField;
import com.mindbox.pe.client.common.FloatTextField;
import com.mindbox.pe.client.common.IntegerListField;
import com.mindbox.pe.client.common.NumberTextField;
import com.mindbox.pe.client.common.TypeEnumMultiSelectPanel;
import com.mindbox.pe.client.common.TypeEnumValueComboBox;
import com.mindbox.pe.client.common.dialog.MDateDateField;
import com.mindbox.pe.client.common.formatter.FormatterFactory;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.common.config.EntityPropertyDefinition;
import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.model.filter.GenericEntityFilterSpec;

/**
 * Provides utiliy methods for managing UI components for generic entities.
 * 
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
final class GenericEntityUtil {

	private static class AutoUpdateChangeListener implements ActionListener, DocumentListener {
		private final JComponent sourceComponent;

		private final JTextField targetField;

		public AutoUpdateChangeListener(JComponent sourceComponent, JTextField targetField) {
			this.sourceComponent = sourceComponent;
			this.targetField = targetField;
		}

		public void actionPerformed(ActionEvent arg0) {
			updateField();
		}

		private void updateField() {
			if (sourceComponent.isEnabled()) {
				targetField.setText(UtilBase.format(new Date(System.currentTimeMillis())));
			}
		}

		public void insertUpdate(DocumentEvent arg0) {
			updateField();
		}

		public void removeUpdate(DocumentEvent arg0) {
			updateField();
		}

		public void changedUpdate(DocumentEvent arg0) {
			updateField();
		}
	}

	/**
	 * 
	 * @param enumType
	 * @param value
	 * @param returnValueOnNotFound
	 * @return the enum display value, if found; otherwise, returns <code>value</code> if
	 *         <code>returnValueOnNotFound</code> is <code>true</code>, if not, returns
	 *         <code>null</code>
	 */
	static String getEnumDisplayValue(String enumType, String value, boolean returnValueOnNotFound) {
		List<TypeEnumValue> enumValues = EntityModelCacheFactory.getInstance().getAllEnumValues(enumType);
		for (Iterator<TypeEnumValue> iter = enumValues.iterator(); iter.hasNext();) {
			TypeEnumValue element = iter.next();
			if (element.getName().equals(value)) {
				return element.getDisplayLabel();
			}
		}
		return (returnValueOnNotFound ? value : null);
	}

	static String getEnumDisplayValue(String enumType, List<String> values) {
		if (values == null || values.isEmpty()) return "";
		StringBuilder buff = new StringBuilder();
		for (int i = 0; i < values.size(); i++) {
			if (i > 0) buff.append(", ");
			buff.append(getEnumDisplayValue(enumType, values.get(i), true));
		}
		return buff.toString();
	}

	static Object getPropertyValueForTable(GenericEntity entity, EntityPropertyDefinition propDef) {
		String propName = propDef.getName();
		String propType = propDef.getType();
		Object value = null;
		if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_BOOLEAN)) {
			value = new Boolean(entity.getBooleanProperty(propName));
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_CURRENCY)) {
			value = new Double(entity.getDoubleProperty(propName));
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_DATE)) {
			if (entity.getDateProperty(propName) != null) {
				value = UIConfiguration.FORMAT_DATE.format(entity.getDateProperty(propName));
			}
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_DOUBLE)) {
			value = new Double(entity.getDoubleProperty(propName));
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_ENUM)) {
			if (propDef.allowMultiple()) {
				value = getEnumDisplayValue(propDef.getEnumType(), GenericEntity.toMultiEnumValues(entity.getStringProperty(propName)));
			}
			else {
				value = getEnumDisplayValue(propDef.getEnumType(), entity.getStringProperty(propName), true);
			}
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_FLOAT)) {
			value = new Float(entity.getFloatProperty(propName));
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_INT)) {
			value = new Integer(entity.getIntProperty(propName));
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_INTEGERLIST)) {
			value = entity.getStringProperty(propName);
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_LONG)) {
			value = new Long(entity.getLongProperty(propName));
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_PERCENT)) {
			value = new Float(entity.getFloatProperty(propName));
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_STRING)) {
			value = entity.getStringProperty(propName);
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_SYMBOL)) {
			value = entity.getStringProperty(propName);
		}
		return (value == null ? "" : value);
	}

	static Class<?> getEditValueClass(EntityPropertyDefinition propDef) {
		String propType = propDef.getType();
		Class<?> valueClass = null;
		if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_BOOLEAN)) {
			valueClass = Boolean.class;
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_CURRENCY)) {
			valueClass = Double.class;
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_DATE)) {
			valueClass = String.class;
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_DOUBLE)) {
			valueClass = Double.class;
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_ENUM)) {
			valueClass = String.class;
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_FLOAT)) {
			valueClass = Float.class;
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_INT)) {
			valueClass = Integer.class;
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_INTEGERLIST)) {
			valueClass = String.class;
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_LONG)) {
			valueClass = Long.class;
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_PERCENT)) {
			valueClass = Float.class;
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_STRING)) {
			valueClass = String.class;
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_SYMBOL)) {
			valueClass = String.class;
		}
		return (valueClass == null ? Object.class : valueClass);
	}

	static void addAutoUpdateListener(JComponent sourceComponent, JTextField targetField) {
		AutoUpdateChangeListener autoUpdateChangeListener = new AutoUpdateChangeListener(sourceComponent, targetField);
		if (sourceComponent instanceof JTextComponent) {
			((JTextComponent) sourceComponent).getDocument().addDocumentListener(autoUpdateChangeListener);
		}
		else if (sourceComponent instanceof JComboBox) {
			((JComboBox) sourceComponent).addActionListener(autoUpdateChangeListener);
		}
		else if (sourceComponent instanceof AbstractButton) {
			((AbstractButton) sourceComponent).addActionListener(autoUpdateChangeListener);
		}
		else if (sourceComponent instanceof AbstractListField) {
			((AbstractListField) sourceComponent).addDocumentListener(autoUpdateChangeListener);
		}
		else if (sourceComponent instanceof TypeEnumMultiSelectPanel) {
			((TypeEnumMultiSelectPanel) sourceComponent).addDocumentListener(autoUpdateChangeListener);
		}
	}

	/**
	 * Gets a new instance of editor Swing component for the specified generic entity property.
	 * 
	 * @param propDef
	 * @return Swing component for the specified property
	 */
	static JComponent createEditComponent(EntityPropertyDefinition propDef, boolean hasEmptyValue) {
		String propType = propDef.getType();
		JComponent comp = null;
		if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_BOOLEAN)) {
			comp = new JCheckBox("Yes");
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_CURRENCY)) {
			comp = new FloatTextField(20, true);
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_DATE)) {
			MDateDateField df = new MDateDateField(false, false, true);
			df.setValue(null);
			comp = df;
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_DOUBLE)) {
			comp = new FloatTextField(20, false);
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_ENUM)) {
			if (propDef.allowMultiple()) {
				if (propDef.isAttributeMapSet()) {
					comp = UIFactory.createTypeEnumMultiSelectPanelForAttributeMap(
							propDef.getDisplayName(),
							propDef.getAttributeMap(),
							propDef.isRequired(),
							propDef.sort());
				}
				else {
					comp = UIFactory.createTypeEnumMultiSelectPanel(
							propDef.getDisplayName(),
							propDef.getEnumType(),
							propDef.isRequired(),
							propDef.sort());
				}
			}
			else {
				if (propDef.isAttributeMapSet()) {
					comp = UIFactory.createTypeEnumComboBoxForAttributeMap(propDef.getAttributeMap(), hasEmptyValue, propDef.sort());
				}
				else {
					comp = UIFactory.createTypeEnumComboBox(propDef.getEnumType(), hasEmptyValue, propDef.sort());
				}
			}
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_FLOAT)) {
			comp = new FloatTextField(10, false);
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_INT)) {
			comp = new NumberTextField(10);
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_INTEGERLIST)) {
			comp = new IntegerListField(propDef.getDisplayName());
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_LONG)) {
			comp = new NumberTextField(20);
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_PERCENT)) {
			comp = new FloatTextField(10, false);
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_STRING)) {
			comp = new JTextField();
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_SYMBOL)) {
			comp = new JFormattedTextField(FormatterFactory.getSymbolFormatter());
		}
		return comp;
	}

	static void setEditComponentValue(GenericEntity entity, String propName, JComponent component, String propType) {
		if (entity == null) throw new NullPointerException("entity cannot be null");
		if (propName == null) throw new NullPointerException("propName cannot be null");
		boolean hasValue = entity.hasProperty(propName);
		if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_BOOLEAN)) {
			((JCheckBox) component).setSelected(entity.getBooleanProperty(propName));
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_CURRENCY)) {
			if (hasValue) {
				((FloatTextField) component).setValue(entity.getDoubleProperty(propName));
			}
			else {
				((FloatTextField) component).setText(null);
			}
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_DATE)) {
			((MDateDateField) component).setValue(entity.getDateProperty(propName));
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_DOUBLE)) {
			if (hasValue) {
				((FloatTextField) component).setValue(entity.getDoubleProperty(propName));
			}
			else {
				((FloatTextField) component).setText(null);
			}
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_ENUM)) {
			if (component instanceof TypeEnumMultiSelectPanel) {
				((TypeEnumMultiSelectPanel) component).setSelectedValues(GenericEntity.toMultiEnumValues(entity.getStringProperty(propName)));
			}
			else {
				((TypeEnumValueComboBox) component).selectTypeEnumValue(entity.getStringProperty(propName));
			}
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_FLOAT)) {
			if (hasValue) {
				((FloatTextField) component).setValue(entity.getFloatProperty(propName));
			}
			else {
				((FloatTextField) component).setText(null);
			}
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_INT)) {
			if (hasValue) {
				((NumberTextField) component).setValue(entity.getIntProperty(propName));
			}
			else {
				((NumberTextField) component).setText(null);
			}
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_INTEGERLIST)) {
			((IntegerListField) component).setValue(entity.getStringProperty(propName));
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_LONG)) {
			if (hasValue) {
				((NumberTextField) component).setValue(entity.getLongProperty(propName));
			}
			else {
				((NumberTextField) component).setText(null);
			}
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_PERCENT)) {
			if (hasValue) {
				((FloatTextField) component).setValue(entity.getFloatProperty(propName));
			}
			else {
				((FloatTextField) component).setText(null);
			}
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_STRING)) {
			if (hasValue) {
				((JTextComponent) component).setText(entity.getStringProperty(propName));
			}

			else {
				((JTextComponent) component).setText(null);
			}
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_SYMBOL)) {
			if (hasValue) {
				((JTextComponent) component).setText(entity.getStringProperty(propName));
			}

			else {
				((JTextComponent) component).setText(null);
			}
		}
	}

	static void setEditComponentValue(GenericEntityFilterSpec filterSpec, String propName, JComponent component, String propType) {
		Object value = filterSpec.getPropertyCriterion(propName);
		if (value == null) return;

		if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_BOOLEAN)) {
			((JCheckBox) component).setSelected(((Boolean) value).booleanValue());
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_CURRENCY)) {
			((FloatTextField) component).setValue(((Number) value).doubleValue());
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_DATE)) {
			((MDateDateField) component).setValue((Date) value);
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_DOUBLE)) {
			((FloatTextField) component).setValue(((Number) value).doubleValue());
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_ENUM)) {
			((TypeEnumValueComboBox) component).selectTypeEnumValue(value.toString());
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_FLOAT)) {
			((FloatTextField) component).setValue(((Number) value).floatValue());
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_INT)) {
			((NumberTextField) component).setValue(((Number) value).intValue());
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_INTEGERLIST)) {
			((IntegerListField) component).setValue(propName);
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_LONG)) {
			((NumberTextField) component).setValue(((Number) value).longValue());
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_PERCENT)) {
			((FloatTextField) component).setValue(((Number) value).floatValue());
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_STRING)) {
			((JTextComponent) component).setText(value.toString());
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_SYMBOL)) {
			((JTextComponent) component).setText(value.toString());
		}
	}

	static boolean hasEditComponentValue(JComponent component, String propType) {
		if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_BOOLEAN)) {
			return true;
		}
		else if (component instanceof FloatTextField) {
			return ((FloatTextField) component).hasValue();
		}
		else if (component instanceof NumberTextField) {
			return ((NumberTextField) component).hasValue();
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_DATE)) {
			return ((MDateDateField) component).getDate() != null;
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_ENUM)) {
			if (component instanceof TypeEnumMultiSelectPanel) {
				return ((TypeEnumMultiSelectPanel) component).hasSelectedValue();
			}
			else {
				return ((TypeEnumValueComboBox) component).getSelectedIndex() >= 0;
			}
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_INTEGERLIST)) {
			return ((IntegerListField) component).hasValue();
		}
		else {
			return !UtilBase.isEmptyAfterTrim(((JTextComponent) component).getText());
		}
	}

	static void setPropertyFromEditComponent(GenericEntity entity, String propName, JComponent component, String propType) {
		if (hasEditComponentValue(component, propType)) {
			if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_BOOLEAN)) {
				entity.setProperty(propName, ((JCheckBox) component).isSelected());
			}
			else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_CURRENCY)) {
				entity.setProperty(propName, toDoubleValue(((FloatTextField) component).getDoubleValue()));
			}
			else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_DATE)) {
				entity.setProperty(propName, ((MDateDateField) component).getDate());
			}
			else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_DOUBLE)) {
				entity.setProperty(propName, toDoubleValue(((FloatTextField) component).getDoubleValue()));
			}
			else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_ENUM)) {
				if (component instanceof TypeEnumMultiSelectPanel) {
					entity.setProperty(propName, GenericEntity.toMultiEnumPropertyValue(((TypeEnumMultiSelectPanel) component).getSelectedValues()));
				}
				else {
					entity.setProperty(propName, ((TypeEnumValueComboBox) component).getSelectedEnumValueValue());
				}
			}
			else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_FLOAT)) {
				entity.setProperty(propName, toFloatValue(((FloatTextField) component).getValue()));
			}
			else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_INT)) {
				entity.setProperty(propName, toIntValue(((NumberTextField) component).getValue()));
			}
			else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_INTEGERLIST)) {
				entity.setProperty(propName, ((IntegerListField) component).getStringValue());
			}

			else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_LONG)) {
				entity.setProperty(propName, toLongValue(((NumberTextField) component).getLongValue()));
			}
			else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_PERCENT)) {
				entity.setProperty(propName, toFloatValue(((FloatTextField) component).getValue()));
			}
			else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_STRING)) {
				entity.setProperty(propName, ((JTextComponent) component).getText());
			}
			else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_SYMBOL)) {
				entity.setProperty(propName, ((JTextComponent) component).getText());
			}
		}
		else {
			entity.clearProperty(propName);
		}
	}

	static void setPropertyFromEditComponent(GenericEntityFilterSpec filterSpec, String propName, JComponent component, String propType) {
		if (propName == null || component == null) return;
		if (hasEditComponentValue(component, propType)) {
			if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_BOOLEAN)) {
				filterSpec.setPropertyCriterion(propName, ((JCheckBox) component).isSelected());
			}
			else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_CURRENCY)) {
				filterSpec.setPropertyCriterion(propName, toDoubleValue(((FloatTextField) component).getDoubleValue()));
			}
			else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_DATE)) {
				filterSpec.setPropertyCriterion(propName, ((MDateDateField) component).getDate());
			}
			else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_DOUBLE)) {
				filterSpec.setPropertyCriterion(propName, toDoubleValue(((FloatTextField) component).getDoubleValue()));
			}
			else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_ENUM)) {
				filterSpec.setPropertyCriterion(propName, ((TypeEnumValueComboBox) component).getSelectedEnumValueValue());
			}
			else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_FLOAT)) {
				filterSpec.setPropertyCriterion(propName, toFloatValue(((FloatTextField) component).getValue()));
			}
			else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_INT)) {
				filterSpec.setPropertyCriterion(propName, toIntValue(((NumberTextField) component).getValue()));
			}
			else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_INTEGERLIST)) {
				filterSpec.setPropertyCriterion(propName, ((IntegerListField) component).getStringValue());
			}
			else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_LONG)) {
				filterSpec.setPropertyCriterion(propName, toLongValue(((NumberTextField) component).getLongValue()));
			}
			else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_PERCENT)) {
				filterSpec.setPropertyCriterion(propName, toFloatValue(((FloatTextField) component).getValue()));
			}
			else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_STRING)) {
				filterSpec.setPropertyCriterion(propName, ((JTextComponent) component).getText());
			}
			else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_SYMBOL)) {
				filterSpec.setPropertyCriterion(propName, ((JTextComponent) component).getText());
			}
		}
		else {
			filterSpec.setPropertyCriterion(propName, (String) null);
		}
	}

	private static double toDoubleValue(Double obj) {
		return (obj == null ? 0.0 : obj.doubleValue());
	}

	private static float toFloatValue(Float obj) {
		return (obj == null ? 0.0f : obj.floatValue());
	}

	private static long toLongValue(Long obj) {
		return (obj == null ? 0L : obj.longValue());
	}

	private static int toIntValue(Integer obj) {
		return (obj == null ? 0 : obj.intValue());
	}

	private GenericEntityUtil() {
	}

}