package com.mindbox.pe.client.common.grid;

import java.awt.Component;
import java.util.Date;
import java.util.EventObject;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.common.AbstractCellEditor;
import com.mindbox.pe.client.common.dialog.MDateDateField;
import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.model.ColumnDataSpecDigest;

public class DateCellEditor extends AbstractCellEditor implements TableCellRenderer {

	private static final boolean isNullEmptyString(Object obj) {
		if (obj == null) {
			return true;
		}
		else if (obj instanceof String) {
			return ((String) obj).length() == 0;
		}
		else {
			return false;
		}
	}

	private final MDateDateField dateEntryField;
	private boolean isViewOnly;
	private final JLabel label;
	private final boolean forDateTime;

	public DateCellEditor(ColumnDataSpecDigest columnDataSpecDigest, boolean viewOnly) {
		this.forDateTime = columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_DATE_TIME);
		this.dateEntryField = new MDateDateField(false, false, forDateTime);//   (dataSpec instanceof DateTimeDataSpec ? true : false));
		this.isViewOnly = viewOnly;
		this.label = new JLabel("");
		label.setOpaque(true);
	}

	protected void fireEditingStopped() {
		super.fireEditingStopped();
	}

	public boolean stopCellEditing() {
		dateEntryField.setValue(dateEntryField.formatToDate(dateEntryField.getText()));
		return super.stopCellEditing();
	}

	public Component getTableCellEditorComponent(JTable jtable, Object value, boolean flag, int i, int j) {
		try {
			if (isNullEmptyString(value)) {
				dateEntryField.setValue(null);
			}
			else {
				if (!(value instanceof Date)) {
					if (forDateTime) {
						dateEntryField.setValue(UIConfiguration.FORMAT_DATE_TIME_SEC.parse(value.toString()));
					}
					else {
						dateEntryField.setValue(UIConfiguration.FORMAT_DATE.parse(value.toString()));
					}
				}
				else
					dateEntryField.setValue((Date) value);
			}
		}
		catch (Exception exception) {
			ClientUtil.getLogger().warn("getTableCellEditorComponent: Invalid date value: " + value);
		}
		return dateEntryField;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		if (value != null && value instanceof Date) {
			if (forDateTime) {
				label.setText(UIConfiguration.FORMAT_DATE_TIME_SEC.format((Date) value));
			}
			else {
				label.setText(UIConfiguration.FORMAT_DATE.format((Date) value));
			}
		}
		else {
			label.setText("");
		}
		label.setBackground((isSelected ? table.getSelectionBackground() : table.getBackground()));
		label.setForeground((isSelected ? table.getSelectionForeground() : table.getForeground()));
		return label;
	}

	public boolean isCellEditable(EventObject eventobject) {
		return !isViewOnly;
	}

	public Object getCellEditorValue() {
		Date date = dateEntryField.getDate();
		return date;
	}

}
