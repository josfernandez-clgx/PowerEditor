package com.mindbox.pe.client.common.grid;

import javax.swing.table.DefaultTableCellRenderer;

import com.mindbox.pe.common.format.FloatRangeFormatter;
import com.mindbox.pe.model.table.FloatRange;

class FloatRangeCellRenderer extends DefaultTableCellRenderer {
	public FloatRangeCellRenderer(int precision) {
		this(new FloatRangeFormatter(precision));
	}

	public void setValue(Object o) {
		try {
			super.setValue(o instanceof String ? o : formatter.format((FloatRange) o));
		} catch (Exception exception) {
			exception.printStackTrace();
			super.setValue("Error");
		}
	}
	
	protected FloatRangeCellRenderer(FloatRangeFormatter formatter) {
		this.formatter = formatter;
	}

	private FloatRangeFormatter formatter;
}
