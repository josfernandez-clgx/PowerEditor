package com.mindbox.pe.client.common.grid;

import com.mindbox.pe.client.common.FloatTextField;
import com.mindbox.pe.common.UtilBase;

class CurrencyCellEditor extends FloatCellEditor {
	public CurrencyCellEditor() {
		super(new FloatTextField(10, true));
	}

	protected Double string2Double(String s) {
		return super.string2Double((!UtilBase.isEmpty(s) && s.trim().charAt(0) == '$') ? s.substring(1) : s);
	}
}

