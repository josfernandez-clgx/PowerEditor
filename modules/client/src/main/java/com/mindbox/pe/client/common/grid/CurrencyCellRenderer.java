package com.mindbox.pe.client.common.grid;

import com.mindbox.pe.common.format.CurrencyFormatter;

class CurrencyCellRenderer extends FloatCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public CurrencyCellRenderer(int precision) {
		super(new CurrencyFormatter(precision));
	}
}
