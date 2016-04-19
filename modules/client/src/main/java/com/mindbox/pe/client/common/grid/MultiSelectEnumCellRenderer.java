package com.mindbox.pe.client.common.grid;

import java.awt.Component;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.common.EnumValueCellRenderer;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.table.EnumValues;

public class MultiSelectEnumCellRenderer extends DefaultTableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public static String toDisplayString(EnumValues<?> enumValues) {
		return toDisplayString(enumValues, true);
	}

	private static String toDisplayString(EnumValues<?> enumValues, boolean includeExclusionPrefix) {
		if (enumValues == null) return "";

		StringBuilder result = new StringBuilder(getExlusionPrefix(enumValues, includeExclusionPrefix));
		for (Iterator<?> inputValIter = enumValues.iterator(); inputValIter.hasNext();) {
			result.append(EnumValueCellRenderer.getDisplayLabel(inputValIter.next()));
			if (inputValIter.hasNext()) {
				result.append(',');
			}
		}
		return result.toString();
	}

	private static String getExlusionPrefix(EnumValues<?> enumValues, boolean includeExclusionPrefix) {
		return includeExclusionPrefix && enumValues.isSelectionExclusion() && !enumValues.isEmpty() ? EnumValues.OLD_EXCLUSION_PREFIX : "";
	}


	@SuppressWarnings("unchecked")
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
		setIcon(getNotIcon(value));

		Object displayValue = value instanceof EnumValues ? toDisplayString((EnumValues<EnumValue>) value, false) // 'false' because we're using an icon to indicate 'Not'
				: value;

		return super.getTableCellRendererComponent(table, displayValue, isSelected, hasFocus, row, col);
	}

	private Icon getNotIcon(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof EnumValues) {
			EnumValues<?> enumValues = (EnumValues<?>) value;
			return enumValues.isSelectionExclusion() ? ClientUtil.getInstance().makeImageIcon("image.not") : null;
		}

		if (value instanceof String) {
			String s = (String) value;
			return s.length() > EnumValues.EXCLUSION_PREFIX.length() // string must have an enum value, not just the NOT prefix
					&& (s.startsWith(EnumValues.EXCLUSION_PREFIX) || s.startsWith(EnumValues.OLD_EXCLUSION_PREFIX)) ? ClientUtil.getInstance().makeImageIcon("image.not") : null;
		}

		return null;
	}
}