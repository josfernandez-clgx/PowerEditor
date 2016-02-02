package com.mindbox.pe.client.common.grid;

import com.mindbox.pe.common.format.CurrencyRangeFormatter;

class CurrencyRangeCellRenderer extends FloatRangeCellRenderer {
	public CurrencyRangeCellRenderer(int precision) {
		super(new CurrencyRangeFormatter(precision));
	}
}
