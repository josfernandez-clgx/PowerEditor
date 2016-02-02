package com.mindbox.pe.client.common;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.mindbox.pe.model.AbstractTemplateColumn;

public class TemplateColumnCellRenderer extends DefaultListCellRenderer implements ListCellRenderer {

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		return super.getListCellRendererComponent(list, getDisplayValue(value), index, isSelected, cellHasFocus);
	}

	private String getDisplayValue(Object value) {
		if (value == null) return "";
		if (value instanceof AbstractTemplateColumn)
			return String.format("%s (%d)", ((AbstractTemplateColumn) value).getTitle(), ((AbstractTemplateColumn) value).getColumnNumber());
		return value.toString();
	}
}
