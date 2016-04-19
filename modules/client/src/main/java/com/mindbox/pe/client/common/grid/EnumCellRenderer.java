package com.mindbox.pe.client.common.grid;

import java.awt.Component;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.mindbox.pe.client.common.EnumValueCellRenderer;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;

public class EnumCellRenderer extends DefaultTableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private List<EnumValue> enumValues;

	public EnumCellRenderer(ColumnDataSpecDigest columnDataSpecDigest) {
		this.enumValues = EnumCellEditor.fetchSortedEnumValueListIfConfigured(columnDataSpecDigest);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
		return super.getTableCellRendererComponent(table, EnumValueCellRenderer.getDisplayLabel(value, enumValues), isSelected, hasFocus, row, col);
	}
}
