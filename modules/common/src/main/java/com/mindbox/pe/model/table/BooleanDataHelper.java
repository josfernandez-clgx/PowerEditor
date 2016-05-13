package com.mindbox.pe.model.table;

import com.mindbox.pe.common.UtilBase;

public class BooleanDataHelper {

	public static final String TRUE_VALUE = "Yes";
	public static final String FALSE_VALUE = "No";
	public static final String ANY_VALUE = "Any Value";

	public static boolean isValidString(String str) {
		if (UtilBase.isEmpty(str)) {
			return true;
		}
		else {
			return TRUE_VALUE.equals(str) || FALSE_VALUE.equals(str) || ANY_VALUE.equals(str) || str.equalsIgnoreCase("true") || str.equalsIgnoreCase("false");
		}
	}

	public static Boolean mapToBooleanValue(String strValue, boolean blankAllowed) {
		if (strValue == null || strValue.trim().length() == 0) {
			return (blankAllowed ? null : Boolean.FALSE);
		}
		else if (strValue.equals(ANY_VALUE)) {
			return null;
		}
		return (strValue.equals(TRUE_VALUE) || Boolean.valueOf(strValue).booleanValue()) ? Boolean.TRUE : Boolean.FALSE;
	}

	public static String toStringValue(Boolean booleanValue, boolean blankAllowed) {
		if (booleanValue == null) {
			return blankAllowed ? ANY_VALUE : FALSE_VALUE;
		}
		else {
			return booleanValue.booleanValue() ? TRUE_VALUE : FALSE_VALUE;
		}
	}

	/**
	 * Handles true/false values. 
	 * This maps <code>true</code> to {@link #TRUE_VALUE} and <code>false</code> to {@link #FALSE_VALUE}.
	 * @param str string value to interpret
	 * @return string representation fit for boolean data spec
	 */
	public static String toStringValue(String str) {
		if (str.equalsIgnoreCase("TRUE")) {
			return TRUE_VALUE;
		}
		else if (str.equalsIgnoreCase("FALSE")) {
			return FALSE_VALUE;
		}
		else {
			return str;
		}
	}

	private BooleanDataHelper() {
	}
}
