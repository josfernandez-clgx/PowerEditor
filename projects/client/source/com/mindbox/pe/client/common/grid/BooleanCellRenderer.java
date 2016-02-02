package com.mindbox.pe.client.common.grid;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.mindbox.pe.model.table.BooleanDataHelper;


/**
 * Boolean cell renderer.
 */
public class BooleanCellRenderer extends DefaultTableCellRenderer {

	private final boolean blankAllowed;

	public BooleanCellRenderer(boolean blankAllowed) {
		this.blankAllowed = blankAllowed;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean arg3, int arg4, int arg5) {
		Component comp = super.getTableCellRendererComponent(table, value, isSelected, arg3, arg4, arg5);
		if (value instanceof Boolean) {
			setValue(BooleanDataHelper.toStringValue((Boolean) value, blankAllowed));
		}
		else if (value instanceof String) {
			setValue(BooleanDataHelper.toStringValue((String) value));
		}
		else {
			setValue((value == null ? null : value.toString()));
		}
		return comp;
	}
}