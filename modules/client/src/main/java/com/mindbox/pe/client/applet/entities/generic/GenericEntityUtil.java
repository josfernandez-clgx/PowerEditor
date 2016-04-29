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
import com.mindbox.pe.client.common.TypeEnumMultiSelectPanel;
import com.mindbox.pe.client.common.TypeEnumValueComboBox;
import com.mindbox.pe.client.common.dialog.MDateDateField;
import com.mindbox.pe.client.common.formatter.FormatterFactory;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.ui.NumberTextField;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.model.filter.GenericEntityFilterSpec;
import com.mindbox.pe.xsd.config.EntityProperty;
import com.mindbox.pe.xsd.config.EntityPropertyType;

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

		@Override
		public void actionPerformed(ActionEvent arg0) {
			updateField();
		}

		@Override
		public void changedUpdate(DocumentEvent arg0) {
			updateField();
		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			updateField();
		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			updateField();
		}

		private void updateField() {
			if (sourceComponent.isEnabled()) {
				targetField.setText(UtilBase.format(new Date(System.currentTimeMillis())));
			}
		}
	}

	static void addAutoUpdateListener(JComponent sourceComponent, JTextField targetField) {
		AutoUpdateChangeListener autoUpdateChangeListener = new AutoUpdateChangeListener(sourceComponent, targetField);
		if (sourceComponent instanceof JTextComponent) {
			((JTextComponent) sourceComponent).getDocument().addDocumentListener(autoUpdateChangeListener);
		}
		else if (sourceComponent instanceof JComboBox) {
			((JComboBox<?>) sourceComponent).addActionListener(autoUpdateChangeListener);
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
	static JComponent createEditComponent(EntityProperty propDef, boolean hasEmptyValue) {
		EntityPropertyType propType = propDef.getType();
		JComponent comp = null;
		switch (propType) {
		case BOOLEAN:
			comp = new JCheckBox("Yes");
			break;
		case CURRENCY:
			comp = new FloatTextField(20, true);
			break;
		case DATE:
			MDateDateField df = new MDateDateField(false, false, true);
			df.setValue(null);
			comp = df;
			break;
		case ENUM:
			final boolean isRequired = UtilBase.asBoolean(propDef.isIsRequired(), false);
			final boolean doSort = UtilBase.asBoolean(propDef.isSort(), false);
			if (UtilBase.asBoolean(propDef.isAllowMultiple(), false)) {
				if (!UtilBase.isEmpty(propDef.getAttributeMap())) {
					comp = UIFactory.createTypeEnumMultiSelectPanelForAttributeMap(propDef.getDisplayName(), propDef.getAttributeMap(), isRequired, doSort);
				}
				else {
					comp = UIFactory.createTypeEnumMultiSelectPanel(propDef.getDisplayName(), propDef.getEnumType(), isRequired, doSort);
				}
			}
			else {
				if (!UtilBase.isEmpty(propDef.getAttributeMap())) {
					comp = UIFactory.createTypeEnumComboBoxForAttributeMap(propDef.getAttributeMap(), hasEmptyValue, doSort);
				}
				else {
					comp = UIFactory.createTypeEnumComboBox(propDef.getEnumType(), hasEmptyValue, doSort);
				}
			}
			break;
		case INTEGER_LIST:
			comp = new IntegerListField(propDef.getDisplayName());
			break;
		case STRING:
			comp = new JTextField();
			break;
		case SYMBOL:
			comp = new JFormattedTextField(FormatterFactory.getSymbolFormatter());
			break;
		case DOUBLE:
			comp = new FloatTextField(20, false);
			break;
		case FLOAT:
			comp = new FloatTextField(10, false);
			break;
		case PERCENT:
			comp = new FloatTextField(10, false);
			break;
		case INTEGER:
			comp = new NumberTextField(10);
			break;
		case LONG:
			comp = new NumberTextField(20);
			break;
		}
		return comp;
	}

	static Class<?> getEditValueClass(EntityProperty propDef) {
		EntityPropertyType propType = propDef.getType();
		Class<?> valueClass = null;
		switch (propType) {
		case BOOLEAN:
			valueClass = Boolean.class;
			break;
		case CURRENCY:
			valueClass = Double.class;
			break;
		case DATE:
		case ENUM:
		case INTEGER_LIST:
		case STRING:
		case SYMBOL:
			valueClass = String.class;
			break;
		case DOUBLE:
			valueClass = Double.class;
			break;
		case FLOAT:
		case PERCENT:
			valueClass = Float.class;
			break;
		case INTEGER:
			valueClass = Integer.class;
			break;
		case LONG:
			valueClass = Long.class;
			break;
		}
		return (valueClass == null ? Object.class : valueClass);
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

	static Object getPropertyValueForTable(GenericEntity entity, EntityProperty propDef) {
		String propName = propDef.getName();
		final EntityPropertyType propType = propDef.getType();
		Object value = null;
		switch (propType) {
		case BOOLEAN:
			value = new Boolean(entity.getBooleanProperty(propName));
			break;
		case CURRENCY:
			value = new Double(entity.getDoubleProperty(propName));
			break;
		case DATE:
			if (entity.getDateProperty(propName) != null) {
				value = Constants.THREADLOCAL_FORMAT_DATE.get().format(entity.getDateProperty(propName));
			}
			break;
		case DOUBLE:
			value = new Double(entity.getDoubleProperty(propName));
			break;
		case ENUM:
			if (UtilBase.asBoolean(propDef.isAllowMultiple(), false)) {
				value = getEnumDisplayValue(propDef.getEnumType(), GenericEntity.toMultiEnumValues(entity.getStringProperty(propName)));
			}
			else {
				value = getEnumDisplayValue(propDef.getEnumType(), entity.getStringProperty(propName), true);
			}
			break;
		case FLOAT:
		case PERCENT:
			value = new Float(entity.getFloatProperty(propName));
			break;
		case INTEGER:
			value = new Integer(entity.getIntProperty(propName));
			break;
		case INTEGER_LIST:
			value = entity.getStringProperty(propName);
			break;
		case LONG:
			value = new Long(entity.getLongProperty(propName));
			break;
		case STRING:
		case SYMBOL:
			value = entity.getStringProperty(propName);
			break;
		}
		return (value == null ? "" : value);
	}

	static boolean hasEditComponentValue(JComponent component, EntityPropertyType propType) {
		switch (propType) {
		case BOOLEAN:
			return true;
		case DATE:
			return ((MDateDateField) component).getDate() != null;
		case ENUM:
			if (component instanceof TypeEnumMultiSelectPanel) {
				return ((TypeEnumMultiSelectPanel) component).hasSelectedValue();
			}
			else {
				return ((TypeEnumValueComboBox) component).getSelectedIndex() >= 0;
			}
		case INTEGER_LIST:
			return ((IntegerListField) component).hasValue();
		case DOUBLE:
		case CURRENCY:
		case FLOAT:
		case PERCENT:
			return ((FloatTextField) component).hasValue();
		case INTEGER:
		case LONG:
			return ((NumberTextField) component).hasValue();
		default:
			return !UtilBase.isEmptyAfterTrim(((JTextComponent) component).getText());
		}
	}

	static void setEditComponentValue(GenericEntity entity, String propName, JComponent component, EntityPropertyType propType) {
		if (entity == null) throw new NullPointerException("entity cannot be null");
		if (propName == null) throw new NullPointerException("propName cannot be null");
		boolean hasValue = entity.hasProperty(propName);
		switch (propType) {
		case BOOLEAN:
			((JCheckBox) component).setSelected(entity.getBooleanProperty(propName));
			break;
		case CURRENCY:
			if (hasValue) {
				((FloatTextField) component).setValue(entity.getDoubleProperty(propName));
			}
			else {
				((FloatTextField) component).setText(null);
			}
			break;
		case DATE:
			((MDateDateField) component).setValue(entity.getDateProperty(propName));
			break;
		case ENUM:
			if (component instanceof TypeEnumMultiSelectPanel) {
				((TypeEnumMultiSelectPanel) component).setSelectedValues(GenericEntity.toMultiEnumValues(entity.getStringProperty(propName)));
			}
			else {
				((TypeEnumValueComboBox) component).selectTypeEnumValue(entity.getStringProperty(propName));
			}
			break;
		case INTEGER_LIST:
			((IntegerListField) component).setValue(entity.getStringProperty(propName));
			break;
		case DOUBLE:
			if (hasValue) {
				((FloatTextField) component).setValue(entity.getDoubleProperty(propName));
			}
			else {
				((FloatTextField) component).setText(null);
			}
			break;
		case FLOAT:
			if (hasValue) {
				((FloatTextField) component).setValue(entity.getFloatProperty(propName));
			}
			else {
				((FloatTextField) component).setText(null);
			}
			break;
		case PERCENT:
			if (hasValue) {
				((FloatTextField) component).setValue(entity.getFloatProperty(propName));
			}
			else {
				((FloatTextField) component).setText(null);
			}
			break;
		case INTEGER:
			if (hasValue) {
				((NumberTextField) component).setValue(entity.getIntProperty(propName));
			}
			else {
				((NumberTextField) component).setText(null);
			}
			break;
		case LONG:
			if (hasValue) {
				((NumberTextField) component).setValue(entity.getLongProperty(propName));
			}
			else {
				((NumberTextField) component).setText(null);
			}
			break;
		case STRING:
			if (hasValue) {
				((JTextComponent) component).setText(entity.getStringProperty(propName));
			}

			else {
				((JTextComponent) component).setText(null);
			}
			break;
		case SYMBOL:
			if (hasValue) {
				((JTextComponent) component).setText(entity.getStringProperty(propName));
			}

			else {
				((JTextComponent) component).setText(null);
			}
			break;
		}
	}

	static void setEditComponentValue(GenericEntityFilterSpec filterSpec, String propName, JComponent component, EntityPropertyType propType) {
		Object value = filterSpec.getPropertyCriterion(propName);
		if (value == null) return;

		switch (propType) {
		case BOOLEAN:
			((JCheckBox) component).setSelected(((Boolean) value).booleanValue());
			break;
		case DATE:
			((MDateDateField) component).setValue((Date) value);
			break;
		case ENUM:
			((TypeEnumValueComboBox) component).selectTypeEnumValue(value.toString());
			break;
		case INTEGER_LIST:
			((IntegerListField) component).setValue(propName);
			break;
		case STRING:
			((JTextComponent) component).setText(value.toString());
			break;
		case SYMBOL:
			((JTextComponent) component).setText(value.toString());
			break;
		case CURRENCY:
		case DOUBLE:
			((FloatTextField) component).setValue(((Number) value).doubleValue());
			break;
		case FLOAT:
			((FloatTextField) component).setValue(((Number) value).floatValue());
			break;
		case PERCENT:
			((FloatTextField) component).setValue(((Number) value).floatValue());
			break;
		case INTEGER:
			((NumberTextField) component).setValue(((Number) value).intValue());
			break;
		case LONG:
			((NumberTextField) component).setValue(((Number) value).longValue());
			break;
		}
	}

	static void setPropertyFromEditComponent(GenericEntity entity, String propName, JComponent component, EntityPropertyType propType) {
		if (hasEditComponentValue(component, propType)) {
			switch (propType) {
			case BOOLEAN:
				entity.setProperty(propName, ((JCheckBox) component).isSelected());
				break;
			case CURRENCY:
				entity.setProperty(propName, toDoubleValue(((FloatTextField) component).getDoubleValue()));
				break;
			case DATE:
				entity.setProperty(propName, ((MDateDateField) component).getDate());
				break;
			case ENUM:
				if (component instanceof TypeEnumMultiSelectPanel) {
					entity.setProperty(propName, GenericEntity.toMultiEnumPropertyValue(((TypeEnumMultiSelectPanel) component).getSelectedValues()));
				}
				else {
					entity.setProperty(propName, ((TypeEnumValueComboBox) component).getSelectedEnumValueValue());
				}
				break;
			case INTEGER_LIST:
				entity.setProperty(propName, ((IntegerListField) component).getStringValue());
				break;
			case DOUBLE:
				entity.setProperty(propName, toDoubleValue(((FloatTextField) component).getDoubleValue()));
				break;
			case FLOAT:
				entity.setProperty(propName, toFloatValue(((FloatTextField) component).getValue()));
				break;
			case PERCENT:
				entity.setProperty(propName, toFloatValue(((FloatTextField) component).getValue()));
				break;
			case INTEGER:
				entity.setProperty(propName, toIntValue(((NumberTextField) component).getValue()));
				break;
			case LONG:
				entity.setProperty(propName, toLongValue(((NumberTextField) component).getLongValue()));
				break;
			case STRING:
				entity.setProperty(propName, ((JTextComponent) component).getText());
				break;
			case SYMBOL:
				entity.setProperty(propName, ((JTextComponent) component).getText());
				break;
			}
		}
		else {
			entity.clearProperty(propName);
		}
	}

	static void setPropertyFromEditComponent(GenericEntityFilterSpec filterSpec, String propName, JComponent component, EntityPropertyType propType) {
		if (propName == null || component == null) return;
		if (hasEditComponentValue(component, propType)) {
			switch (propType) {
			case BOOLEAN:
				filterSpec.setPropertyCriterion(propName, ((JCheckBox) component).isSelected());
				break;
			case CURRENCY:
			case DOUBLE:
				filterSpec.setPropertyCriterion(propName, toDoubleValue(((FloatTextField) component).getDoubleValue()));
				break;
			case DATE:
				filterSpec.setPropertyCriterion(propName, ((MDateDateField) component).getDate());
				break;
			case ENUM:
				filterSpec.setPropertyCriterion(propName, ((TypeEnumValueComboBox) component).getSelectedEnumValueValue());
				break;
			case INTEGER_LIST:
				filterSpec.setPropertyCriterion(propName, ((IntegerListField) component).getStringValue());
				break;
			case FLOAT:
			case PERCENT:
				filterSpec.setPropertyCriterion(propName, toFloatValue(((FloatTextField) component).getValue()));
				break;
			case INTEGER:
				filterSpec.setPropertyCriterion(propName, toIntValue(((NumberTextField) component).getValue()));
				break;
			case LONG:
				filterSpec.setPropertyCriterion(propName, toLongValue(((NumberTextField) component).getLongValue()));
				break;
			case STRING:
				filterSpec.setPropertyCriterion(propName, ((JTextComponent) component).getText());
				break;
			case SYMBOL:
				filterSpec.setPropertyCriterion(propName, ((JTextComponent) component).getText());
				break;
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

	private static int toIntValue(Integer obj) {
		return (obj == null ? 0 : obj.intValue());
	}

	private static long toLongValue(Long obj) {
		return (obj == null ? 0L : obj.longValue());
	}

	private GenericEntityUtil() {
	}

}