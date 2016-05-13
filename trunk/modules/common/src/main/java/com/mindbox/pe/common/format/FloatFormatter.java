package com.mindbox.pe.common.format;

import java.text.NumberFormat;

public class FloatFormatter {

	public static final int DEFAULT_PRECISION = 2;
	public static final int MIN_PRECISION = 0;
	public static final int NO_PRECISION = -1;

	private static final int MAX_FRACTION_FOR_NO_PRECISION = 32;

	public static boolean isValidPrecision(int precision) {
		return precision == NO_PRECISION || precision >= MIN_PRECISION;
	}

	private NumberFormat formatter;

	public FloatFormatter(int precision) {
		formatter = NumberFormat.getInstance();
		// TT 1879: only set fraction digits if precision is set
		if (precision >= MIN_PRECISION) {
			formatter.setMaximumFractionDigits(precision);
			formatter.setMinimumFractionDigits(precision);
		}
		else {
			formatter.setMaximumFractionDigits(MAX_FRACTION_FOR_NO_PRECISION);
		}
	}

	public final String format(Number n) {
		return decorate(n == null ? null : formatter.format(n));
	}

	/** Override to decorate the formatted value.  note: The formatted value may be null. */
	protected String decorate(String formattedValue) {
		return formattedValue;
	}
}
