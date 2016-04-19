package com.mindbox.pe.client.common.grid;

import java.awt.Component;
import java.text.MessageFormat;

import javax.swing.JTable;

import com.mindbox.pe.client.common.AbstractCellEditor;
import com.mindbox.pe.client.common.FloatTextField;
import com.mindbox.pe.common.UtilBase;

class FloatCellEditor extends AbstractCellEditor {

	private static final MessageFormat EDITOR_FORMAT = new MessageFormat("{0,number,###############0.###########}");

	static final String asEditorStringValue(Number value) {
		return value == null ? null : EDITOR_FORMAT.format(new Object[] { value });
	}

	private FloatTextField field;
	private Number value;

	public FloatCellEditor() {
		this(new FloatTextField(10, false));
	}

	protected FloatCellEditor(FloatTextField field) {
		this.field = field;
		setClickCountToStart(2);
	}

	protected void fireEditingStopped() {
		value = string2Double(field.getText());
		super.fireEditingStopped();
	}

	public Component getTableCellEditorComponent(JTable jtable, Object obj, boolean flag, int i, int j) {
		if (obj instanceof Number) {
			value = (Number) obj;
		}
		else {
			String str = (obj == null ? null : obj.toString());
			if (str == null) {
				value = null;
			}
			else {
				try {
					value = Double.valueOf(str);
				}
				catch (NumberFormatException ex) {
					value = null;
				}
			}
		}

		String textToSet = asEditorStringValue(value);
		field.setText(textToSet);
		return field;
	}

	public Object getCellEditorValue() {
		return value;
	}

	protected Double string2Double(String s) {
		String sScrubbed = s == null ? null : UtilBase.strip(s, ",");
		return UtilBase.isEmpty(sScrubbed) ? null : Double.valueOf(sScrubbed);
	}
}
