package com.mindbox.pe.client.common.grid;

import java.awt.Component;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.mindbox.pe.client.common.EnumValueCellRenderer;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.EnumValue;

public class EnumCellRenderer extends DefaultTableCellRenderer {

	private List<EnumValue> enumValues;

	public EnumCellRenderer(ColumnDataSpecDigest columnDataSpecDigest) {
		this.enumValues = EnumCellEditor.fetchSortedEnumValueListIfConfigured(columnDataSpecDigest);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
		return super.getTableCellRendererComponent(
				table,
				EnumValueCellRenderer.getDisplayLabel(value, enumValues),
				isSelected,
				hasFocus,
				row,
				col);
	}
}
