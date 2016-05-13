package com.mindbox.pe.client.common.grid;

import javax.swing.table.DefaultTableCellRenderer;

import com.mindbox.pe.common.format.FloatFormatter;

class FloatCellRenderer extends DefaultTableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public FloatCellRenderer(int precision) {
		this(new FloatFormatter(precision));
	}

	public void setValue(Object o) {
		try {
			super.setValue(o instanceof String ? o : formatter.format((Number) o));
		}
		catch (Exception exception) {
			exception.printStackTrace();
			super.setValue("Error");
		}
	}

	protected FloatCellRenderer(FloatFormatter formatter) {
		this.formatter = formatter;
	}

	private FloatFormatter formatter;
}
