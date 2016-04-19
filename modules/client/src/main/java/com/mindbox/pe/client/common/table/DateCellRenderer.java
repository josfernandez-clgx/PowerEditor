/*
 * Created on 2004. 12. 9
 */
package com.mindbox.pe.client.common.table;

import java.awt.Color;
import java.awt.Component;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.model.Constants;

/**
 * Date cell renderer.
 * This is both a renderer for both lists and tables.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 4.2.0
 */
public final class DateCellRenderer extends JLabel implements ListCellRenderer, TableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public DateCellRenderer() {
		super();
		setOpaque(true);
	}

	private void setDisplayString(Object value, boolean isSelected) {
		if (value instanceof Date) {
			setText(Constants.THREADLOCAL_FORMAT_DATE_TIME_SEC.get().format((Date) value));
		}
		else {
			setText((value == null ? "" : value.toString()));
		}
		setBackground(isSelected ? PowerEditorSwingTheme.primary3 : Color.white);
	}

	public Component getListCellRendererComponent(JList arg0, Object value, int index, boolean isSelected, boolean arg4) {
		setDisplayString(value, isSelected);
		return this;
	}

	public Component getTableCellRendererComponent(JTable arg0, Object value, boolean isSelected, boolean arg3, int arg4, int arg5) {
		setDisplayString(value, isSelected);
		return this;
	}

}