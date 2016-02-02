package com.mindbox.pe.common.config;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public final class ConfigUtil {

	private static final SimpleDateFormat xmlDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static final SimpleDateFormat xmlDateFormatReport = new SimpleDateFormat("MM/dd/yy HH:mm a");

	public static final String PROPERTY_TYPE_BOOLEAN = "boolean";
	public static final String PROPERTY_TYPE_INT = "integer";
	public static final String PROPERTY_TYPE_FLOAT = "float";
	public static final String PROPERTY_TYPE_LONG = "long";
	public static final String PROPERTY_TYPE_DOUBLE = "double";
	public static final String PROPERTY_TYPE_DATE = "date";

	public static final String PROPERTY_TYPE_SYMBOL = "symbol";
	public static final String PROPERTY_TYPE_STRING = "string";
	public static final String PROPERTY_TYPE_PERCENT = "percent";
	public static final String PROPERTY_TYPE_CURRENCY = "currency";
	public static final String PROPERTY_TYPE_ENUM = "enum";
	public static final String PROPERTY_TYPE_INTEGERLIST = "integerList";
	
	
	public static final String CONFIG_VALUE_YES = "YES";
	public static final String CONFIG_VALUE_NO = "NO";

	public static final String asGenericEntityPropertyType(String value) {
		if (value.equalsIgnoreCase(PROPERTY_TYPE_ENUM)) {
			return PROPERTY_TYPE_ENUM;
		}
		if (value.equalsIgnoreCase(PROPERTY_TYPE_BOOLEAN)) {
			return PROPERTY_TYPE_BOOLEAN;
		}
		if (value.equalsIgnoreCase(PROPERTY_TYPE_INT)) {
			return PROPERTY_TYPE_INT;
		}
		if (value.equalsIgnoreCase(PROPERTY_TYPE_INTEGERLIST)) {
			return PROPERTY_TYPE_INTEGERLIST;
		}
		if (value.equalsIgnoreCase(PROPERTY_TYPE_FLOAT)) {
			return PROPERTY_TYPE_FLOAT;
		}
		if (value.equalsIgnoreCase(PROPERTY_TYPE_LONG)) {
			return PROPERTY_TYPE_LONG;
		}
		if (value.equalsIgnoreCase(PROPERTY_TYPE_DOUBLE)) {
			return PROPERTY_TYPE_DOUBLE;
		}
		if (value.equalsIgnoreCase(PROPERTY_TYPE_DATE)) {
			return PROPERTY_TYPE_DATE;
		}
		if (value.equalsIgnoreCase(PROPERTY_TYPE_STRING)) {
			return PROPERTY_TYPE_STRING;
		}
		if (value.equalsIgnoreCase(PROPERTY_TYPE_SYMBOL)) {
			return PROPERTY_TYPE_SYMBOL;
		}
		if (value.equalsIgnoreCase(PROPERTY_TYPE_CURRENCY)) {
			return PROPERTY_TYPE_CURRENCY;
		}
		if (value.equalsIgnoreCase(PROPERTY_TYPE_PERCENT)) {
			return PROPERTY_TYPE_PERCENT;
		}
		throw new IllegalArgumentException("Invalid entity property type: " + value);
	}

	public static Date toDate(String dateXMLStr) {
		if (dateXMLStr == null || dateXMLStr.trim().length() < 1) {
			return null;
		}
		try {
			return xmlDateFormat.parse(dateXMLStr);
		}
		catch (Exception ex) {
			return null;
		}
	}

	public static String toDateXMLString(Date date) {
		if (date == null) {
			return "";
		}
		else {
			return xmlDateFormat.format(date);
		}
	}
	
	public static final boolean asBoolean(String value) {
		return (value != null && value.equalsIgnoreCase(CONFIG_VALUE_YES) || Boolean.valueOf(value).booleanValue());
	}
	
	private ConfigUtil() {}
	
	public static Date toXMLReportDate(String dateXMLStr) {
		if (dateXMLStr == null || dateXMLStr.trim().length() < 1) {
			return null;
		}
		try {
			return xmlDateFormatReport.parse(dateXMLStr);
		}
		catch (Exception ex) {
			return null;
		}
	}

	public static String toDateXMLReportString(Date date) {
		if (date == null) {
			return "";
		}
		else {
			return xmlDateFormatReport.format(date);
		}
	}
}
