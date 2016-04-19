package com.mindbox.pe.client.common;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.mindbox.pe.model.ExternalEnumSourceDetail;

public class ExternalEnumSourceDetailCellRenderer extends DefaultListCellRenderer implements ListCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		return super.getListCellRendererComponent(list, getDisplayValue(value), index, isSelected, cellHasFocus);
	}

	private String getDisplayValue(Object value) {
		if (value == null) return "";
		if (value instanceof ExternalEnumSourceDetail) return ((ExternalEnumSourceDetail) value).getName();
		return value.toString();
	}
}
