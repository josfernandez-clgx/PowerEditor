package com.mindbox.pe.client.common;

import java.awt.Component;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.model.EnumValue;

/**
 * List cell renderer for {@link com.mindbox.pe.model.EnumValue} objects.
 * @since PowerEditor 4.5.2
 */
public class EnumValueCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = -3951228734910107454L;

	public static String getDisplayLabel(EnumValue enumValue) {
		return enumValue == null ? "" : enumValue.getDisplayLabel();
	}

	/**
	 * @param value Either an EnumValue object (b/c the table model holds EnumValue objects)
	 * or an EnumValue's displayLabel String (b/c the cell editor list of values is a list of display value strings).
	 * In either case we want to display the displayLabel.
	 */
	public static String getDisplayLabel(Object value) {
		if (value == null) {
			return "";
		}
		return value instanceof EnumValue ? ((EnumValue) value).getDisplayLabel() : (String) value;
	}

	/** 
	 * Similar to {@link #getDisplayLabel(Object)}, but if <code>value</code> is an EnumValue's deployId then
	 * returns the display label for the identified EnumValue instance in the <code>validEnumValues</code> list.
	 */
	public static String getDisplayLabel(Object value, List<EnumValue> validEnumValues) {
		return getDisplayLabel(idToEnumValue(value, validEnumValues));
	}

	/** 
	 * If <code>value</code> is a String containing the Id of one of the EnumValues in <code>validEnumValues</code>
	 * then return that instance of EnumValue, otherwise return <code>value</code> as is.
	 */
	private static Object idToEnumValue(Object value, List<EnumValue> validEnumValues) {
		if (validEnumValues != null && value instanceof String) {
			for (EnumValue enumVal : validEnumValues) {
				if (enumVal.hasDeployID() && value.equals(enumVal.getDeployID().toString())) {
					return enumVal;
				}
				else if (!enumVal.hasDeployID() && value.equals(enumVal.getDeployValue())) {
					return enumVal;
				}
			}
		}
		return value;
	}

	private List<EnumValue> enumValues;

	public EnumValueCellRenderer() {
		setOpaque(true);
	}

	public EnumValueCellRenderer(List<EnumValue> enumValues) {
		this();
		this.enumValues = enumValues;
	}

	public EnumValueCellRenderer(String imageKey) {
		this();
		if (imageKey != null) {
			setIcon(ClientUtil.getInstance().makeImageIcon(imageKey));
		}
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Object> arg0, Object value, int index, boolean isSelected, boolean arg4) {
		return super.getListCellRendererComponent(arg0, getDisplayLabel(value, enumValues), index, isSelected, arg4);
	}
}