package com.mindbox.pe.client.common.grid;

import com.mindbox.pe.common.format.CurrencyFormatter;

class CurrencyCellRenderer extends FloatCellRenderer {
	public CurrencyCellRenderer(int precision) {
		super(new CurrencyFormatter(precision));
	}
}
