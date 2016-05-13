package com.mindbox.pe.common.format;

import com.mindbox.pe.common.UtilBase;

public class CurrencyRangeFormatter extends FloatRangeFormatter {
	public CurrencyRangeFormatter(int precision) {
		super(precision);
	}

	/**
	 * Possible formattedValues:
	 * ""
	 * " -hi}"
	 * "{lo- "
	 * "{lo-hi}"
	 * 
	 * Where '{' and '}' are either '(' or '[', and ')' or ']', respectively,
	 * and "lo" and "hi" are the range boundary values.
	 */
	protected String decorate(String formattedValue) {
		int separatorIndex = getSeparatorPosition(formattedValue);

		if (separatorIndex == -1) {
			return formattedValue; // ""
		}
		
		StringBuilder buffer = new StringBuilder();

		char firstChar = formattedValue.charAt(0);
		char lastChar = formattedValue.charAt(formattedValue.length()-1);
		
		boolean hasLo = firstChar == '(' || firstChar == '[';
		boolean hasHi = lastChar == ')' || lastChar == ']';
		
		if (hasLo) {
			buffer.append(firstChar);

			buffer.append('$');
			buffer.append(formattedValue.substring(1, separatorIndex + 1));
			
			if (hasHi) { // "{lo-hi}"
				buffer.append('$');
				buffer.append(formattedValue.substring(separatorIndex + 1, formattedValue.length()));
			} else { // "{lo- "
				buffer.append(' ');
			}
		} else { // " -hi}"
				buffer.append(" -$");
				buffer.append(formattedValue.substring(separatorIndex + 1, formattedValue.length()));
		}
		
		return buffer.toString();
	}

	/* Range is separated by '-', but the lo may be negative, so, the first '-' may not be the field separator */
	private int getSeparatorPosition(String formattedValue) {
		if (UtilBase.isEmpty(formattedValue)) {
			return -1;
		}

		// if no lo val, then first '-'
		int indexOfFirstMinusSymbol = formattedValue.indexOf('-');
		char firstChar = formattedValue.charAt(0);
		boolean hasLo = firstChar == '(' || firstChar == '[';
		if (!hasLo) {
			return indexOfFirstMinusSymbol;
		}

		// else deal with possible negative lo val
		char[] chars = formattedValue.toCharArray();
		int indexOfFirstDigit = -1;
		for (int i = 0; i < chars.length; i++) {
			if (Character.isDigit(chars[i])) {
				indexOfFirstDigit = i;
				break;
			}
		}
		if (indexOfFirstDigit < indexOfFirstMinusSymbol) {
			return indexOfFirstMinusSymbol;
		}
		return formattedValue.indexOf('-', indexOfFirstMinusSymbol + 1);
	}
}
