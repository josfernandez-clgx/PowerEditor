package com.mindbox.pe.common.format;

public class CurrencyFormatter extends FloatFormatter {
	public CurrencyFormatter(int precision) {
		super(precision);
	}
	
	// TODO Kim, 2007-06-01: this decorator pattern assumes that PE uses US locale. 
	//        For i18support, this must be modified to use NumberFormat.getCurrentyInstace(),
	//        in order to use Java's built-in currency support.
	//
	protected String decorate(String formattedValue) {
		return formattedValue == null ? null : '$' + formattedValue;
	}
}
