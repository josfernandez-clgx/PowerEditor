/*
 * Created on 2004. 2. 12.
 *
 */
package com.mindbox.pe.client.common.formatter;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class Validator {

	private static final String REGEX_VALID_NAME = "[a-zA-Z_0-9]+";
	private static final String REGEX_VALID_TIME = "[0-9]{0,2}(:[0-9]{0,2}(:[0-9]{0,2})?)?";
	private static final String REGEX_VALID_DATE_TIME = "^([0-9/]+)( )?([0-9:]+)?$";

	public static boolean isValidClassName(String name) {
		if (name == null || name.length() == 0) {
			return false;
		}
		else {
			return name.matches(REGEX_VALID_NAME);
		}
	}

	public static boolean isValidTime(String time) {
		if (time == null || time.length() == 0) {
			return false;
		}
		else {
			return time.matches(REGEX_VALID_TIME);
		}
	}

	public static boolean isValidDateTime(String str) {
		if (str == null || str.length() == 0) {
			return true;
		}
		else {
			return str.matches(REGEX_VALID_DATE_TIME);
		}
	}

	private Validator() {
	}
}
