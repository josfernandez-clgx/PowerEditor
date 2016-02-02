package com.mindbox.pe.model;


/**
 * Declares constants.
 *
 * @author MindBox
 */
public final class Constants {
	
	public static final String UNSPECIFIED_DATE_SYNONYM_DESCRIPTION = "Open Ended";

	public static final String ENTITIES_EXPORT_FILE_NAME = "entities.xml";
	public static final String POLICIES_EXPORT_FILE_NAME = "policies.xml";
	
	public static final String APPLICATION_NAME = "Power Editor";
	public static final String SESSION_ID = "sessionId";

    public static final String VALUE_YES = "YES";
    public static final String VALUE_NO = "NO";

    public static final String KEY_TYPE_ANY = "any";
	public static final String KEY_TYPE_ENUM = "enum";
	public static final String KEY_TYPE_RANGE = "range";
	
	public static final String CELL_SELECTION_DEFAULT = "default";
	public static final String CELL_SELECTION_EXCLUDE_SINGLE = "enumExcludeSingle";
	public static final String CELL_SELECTION_EXCLUDE_MULTIPLE = "enumExcludeMultiple";
	public static final String CELL_SELECTION_INCLUDE_MULTIPLE = "enumIncludeSingle";
	public static final String RANGE_STYLE_VERBOSE = "verbose";
	public static final String RANGE_STYLE_SYMBOLIC = "symbolic";
	public static final String RANGE_STYLE_BRACKETED = "bracketed";
	
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

	private Constants() {}
}
