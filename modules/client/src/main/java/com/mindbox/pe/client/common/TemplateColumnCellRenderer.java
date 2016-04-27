package com.mindbox.pe.client.common;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.mindbox.pe.model.template.AbstractTemplateColumn;

public class TemplateColumnCellRenderer extends DefaultListCellRenderer implements ListCellRenderer<Object> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private String getDisplayValue(Object value) {
		if (value == null) return "";
		if (value instanceof AbstractTemplateColumn)
			return String.format("%s (%d)", ((AbstractTemplateColumn) value).getTitle(), ((AbstractTemplateColumn) value).getColumnNumber());
		return value.toString();
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		return super.getListCellRendererComponent(list, getDisplayValue(value), index, isSelected, cellHasFocus);
	}
}
