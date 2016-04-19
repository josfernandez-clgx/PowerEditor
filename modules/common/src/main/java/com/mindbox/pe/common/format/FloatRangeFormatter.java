package com.mindbox.pe.common.format;

import com.mindbox.pe.model.table.FloatRange;

public class FloatRangeFormatter {
	private FloatFormatter formatter;

	public FloatRangeFormatter(int precision) {
		formatter = new FloatFormatter(precision);
	}
	
	public final String format(FloatRange range) {
		if (range == null) {
			return "";
		}
		
		Double lo = range.getLowerValue();
		Double hi = range.getUpperValue();
		
		if (lo == null && hi == null) { return decorate(""); }

		StringBuilder buff = new StringBuilder();
		if (lo != null) {
			buff.append((range.isLowerValueInclusive() ? "[" : "("));
			buff.append(formatter.format(lo));
		}
		else {
			buff.append(" ");
		}
		buff.append("-");
		if (hi != null) {
			buff.append(formatter.format(hi));
			buff.append((range.isUpperValueInclusive() ? "]" : ")"));
		}
		else {
			buff.append(" ");
		}
		return decorate(buff.toString());
	}

	/** Override to decorate the formatted value.  note: The formatted value may be null. */
	protected String decorate(String formattedValue) {
		return formattedValue;
	}
}
