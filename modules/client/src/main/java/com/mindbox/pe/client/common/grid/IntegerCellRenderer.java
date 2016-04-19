package com.mindbox.pe.client.common.grid;

import java.text.NumberFormat;

import javax.swing.table.DefaultTableCellRenderer;

class IntegerCellRenderer extends DefaultTableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public void setValue(Object obj) {
		if (obj == null || (obj instanceof String) && (((String) obj).length() == 0)) {
			super.setValue("");
			return;
		}
		try {
			NumberFormat numberformat = NumberFormat.getIntegerInstance();
			Long number;
			if (obj instanceof Long) {
				number = (Long) obj;
			}
			if (obj instanceof Number) {
				number = new Long(((Number) obj).longValue());
			}
			else {
				number = new Long(NumberFormat.getInstance().parse((String) obj).longValue());
			}

			super.setValue(obj != null ? ((Object) (numberformat.format(number))) : "");
		}
		catch (Exception exception) {
			exception.printStackTrace();
			super.setValue("Error");
		}
	}

}
