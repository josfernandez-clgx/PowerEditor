package com.mindbox.pe.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.mindbox.pe.xsd.config.RangeStyleType;


/**
 * Declares constants.
 *
 * @author MindBox
 */
public final class Constants {

	public static final String COPY_TEXT = " - Copy";

	public static final String UNSPECIFIED_DATE_SYNONYM_DESCRIPTION = "Open Ended";

	public static final String ENTITIES_EXPORT_FILE_NAME = "entities.xml";
	public static final String POLICIES_EXPORT_FILE_NAME = "policies.xml";

	public static final String APPLICATION_NAME = "Power Editor";
	public static final String SESSION_ID = "sessionId";

	public static final String VALUE_YES = "YES";
	public static final String VALUE_NO = "NO";

	public static final String ACTIVE_STATUS = "Active";
	public static final String INACTIVE_STATUS = "Inactive";
	public static final String LOCKOUT_STATUS = "Lockout";
	public static final String USER_STATUSES[] = { ACTIVE_STATUS, INACTIVE_STATUS, LOCKOUT_STATUS };

	public static final String DRAFT_STATUS = "Draft";

	public static final String DISABLED_STATUS = "Disabled";

	public static final String DATE_FORMAT = "MM/dd/yyyy hh:mm aaa";
	public static final String AUTHENTICATION_FAILURE_MSG = "AuthenticationFailureMsg";
	public static final String AUTHORIZATION_FAILURE_MSG = "AuthorizationFailureMsg";
	public static final String LOCK_FAILURE_MSG = "LockFailureMsg";
	public static final String INVALID_USERID_PSWD_MSG = "InvalidLoginMsg";
	public static final String SESSION_LIMIT_EXCEEDED_MSG = "SessionLimitExceededMsg";

	public static final String CATEGORY_PATH_DELIMITER = "->";
	public static final String CATEGORY_PATH_DELIMITER_REPORT = ".";

	public static final int CBR_NULL_DATA_EQUIVALENT_VALUE = -9999;
	public static final double CBR_NULL_DOUBLE_VALUE = -9999.0;

	public static final String DEFAULT_ENUM_DELIM = ", ";
	public static final String DEFAULT_ENUM_FINAL_DELIM = " or ";
	public static final String DEFAULT_ENUM_PREFIX = "";

	public static final RangeStyleType DEFAULT_RANGE_STYLE = RangeStyleType.VERBOSE;

	public static final String CONFIG_VALUE_YES = "YES";
	public static final String CONFIG_VALUE_NO = "NO";

	public static final long DAY_ADJUSTMENT = 24L * 60L * 60L * 1000L;

	public static final String FORMAT_STR_DATE = "MM/dd/yyyy";
	public static final String FORMAT_STR_DATE_TIME_MIN = "MM/dd/yyyy HH:mm";
	public static final String FORMAT_STR_DATE_TIME_SEC = "MM/dd/yyyy HH:mm:ss";
	public static final String FORMAT_STR_YYYY_MM_DD_TIME_SEC = "yyyy-MM-dd HH:mm:ss";
	public static final String FORMAT_STR_TIME_SEC = "HH:mm:ss";

	public static final ThreadLocal<DateFormat> THREADLOCAL_FORMAT_DATE = new ThreadLocal<DateFormat>() {
		protected DateFormat initialValue() {
			return new SimpleDateFormat(Constants.FORMAT_STR_DATE);
		}
	};

	public static final ThreadLocal<DateFormat> THREADLOCAL_FORMAT_DATE_TIME_MIN = new ThreadLocal<DateFormat>() {
		protected DateFormat initialValue() {
			return new SimpleDateFormat(Constants.FORMAT_STR_DATE_TIME_MIN);
		}
	};

	public static final ThreadLocal<DateFormat> THREADLOCAL_FORMAT_DATE_TIME_SEC = new ThreadLocal<DateFormat>() {
		protected DateFormat initialValue() {
			return new SimpleDateFormat(Constants.FORMAT_STR_DATE_TIME_SEC);
		}
	};

	public static final ThreadLocal<DateFormat> THREADLOCAL_FORMAT_YYYY_MM_DD_TIME_SEC = new ThreadLocal<DateFormat>() {
		protected DateFormat initialValue() {
			return new SimpleDateFormat(Constants.FORMAT_STR_YYYY_MM_DD_TIME_SEC);
		}
	};

	// TODO Replace with enum from XSD
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
	public static final String DEFAULT_CONDITIONAL_DELIMITER = ", ";
	public static final String DEFAULT_CONDITIONAL_FINAL_DELIMITER = ", ";


	private Constants() {
	}
}
