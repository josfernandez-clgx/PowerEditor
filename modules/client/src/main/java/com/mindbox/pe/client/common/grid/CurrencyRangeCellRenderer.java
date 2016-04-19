package com.mindbox.pe.client.common.grid;

import com.mindbox.pe.common.format.CurrencyRangeFormatter;

class CurrencyRangeCellRenderer extends FloatRangeCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public CurrencyRangeCellRenderer(int precision) {
		super(new CurrencyRangeFormatter(precision));
	}
}
