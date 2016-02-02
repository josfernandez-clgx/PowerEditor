package com.mindbox.pe.server.audit;

/**
 * Defines constants for audit module.
 * @author kim
 *
 */
public final class AuditConstants {
	
	public static final String PARAMETER_USAGE_TYPE = "PARAMETER";

	public static final String FAILURE_MESSAGE_PREFIX = "Failed to log ";
	
	// NOTE: DO NOT modify the value of existing constants, when adding a new one.
	
	// The KB_CHANGED_ELEMENT_TYPE* constants are used to populate the kb_changed_type_id column of the MB_KB_AUDIT_MASTER table.
	public static final int KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION = 1;
	public static final int KB_CHANGED_ELMENT_TYPE_GUIDELINE_TEMPLATE = 2;
	public static final int KB_CHANGED_ELMENT_TYPE_ENTITY = 3;
	public static final int KB_CHANGED_ELMENT_TYPE_CATEGORY = 4;
	public static final int KB_CHANGED_ELMENT_TYPE_ENTITY_CAT_RELATIONSHIP = 5;
	public static final int KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION = 6;
	public static final int KB_CHANGED_ELMENT_TYPE_PARAMETER_TEMPLATE = 7;
	public static final int KB_CHANGED_ELMENT_TYPE_ENTITY_COMPATIBILITY = 8;
	public static final int KB_CHANGED_ELMENT_TYPE_DATE_SYNONYM = 9;
	public static final int KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTION = 10;
	public static final int KB_CHANGED_ELMENT_TYPE_TEST_CONDITION = 11;
	public static final int KB_CHANGED_ELMENT_TYPE_ROLE = 12;
	public static final int KB_CHANGED_ELMENT_TYPE_USER = 13;
	public static final int KB_CHANGED_ELMENT_TYPE_PHASE = 14;
	public static final int KB_CHANGED_ELMENT_TYPE_PROCESS = 15;
	public static final int KB_CHANGED_ELMENT_TYPE_CBR_ATTRIBUTE = 16;
	public static final int KB_CHANGED_ELMENT_TYPE_CBR_CASE = 17;
	public static final int KB_CHANGED_ELMENT_TYPE_CBR_CASE_BASE = 18;

	// The KB_MOD_TYPE* constants are used to poluate the kb_mod_type_id column of the MB_KB_AUDIT_DETAIL table.
	public static final int KB_MOD_TYPE_GENERIC_ADD = 1;
	public static final int KB_MOD_TYPE_GENERIC_REMOVE = 2;
	public static final int KB_MOD_TYPE_GENERIC_UPDATE = 3;
	public static final int KB_MOD_TYPE_GENERIC_IMPORT = 4;
	public static final int KB_MOD_TYPE_ADD_CONTEXT_ELEMENT = 5;
	public static final int KB_MOD_TYPE_REMOVE_CONTEXT_ELEMENT = 6;
	public static final int KB_MOD_TYPE_ADD_GRID_ROW = 7;
	public static final int KB_MOD_TYPE_REMOVE_GRID_ROW = 8;
	public static final int KB_MOD_TYPE_MODIFY_GRID_CELL = 9;
	public static final int KB_MOD_TYPE_MODIFY_ENTITY_PROPERTY = 10;
	public static final int KB_MOD_TYPE_ADD_ENTITY_CAT_RELATIONSHIP = 11;
	public static final int KB_MOD_TYPE_REMOVE_ENTITY_CAT_RELATIONSHIP = 12;
	public static final int KB_MOD_TYPE_COPY_POLICIES = 13;
	public static final int KB_MOD_TYPE_CUTOVER_POLICIES = 14;
	public static final int KB_MOD_TYPE_REMOVE_ALL_POLICIES = 15;
	public static final int KB_MOD_TYPE_MODIFY_COMMENTS = 16;
	public static final int KB_MOD_TYPE_MODIFY_EFFECTIVE_DATE = 17;
	public static final int KB_MOD_TYPE_MODIFY_EXPIRATION_DATE = 18;
	public static final int KB_MOD_TYPE_MODIFY_STATUS = 19;
	
	// The KB_MOD_TYPE* constants are used to populate the kb_element_type_id column of the MB_KB_AUDIT_DETAIL_DATA table
	public static final int KB_ELEMENT_TYPE_ENTITY_ID = 1;
	public static final int KB_ELEMENT_TYPE_CATEGORY_ID = 2;
	public static final int KB_ELEMENT_TYPE_BEFORE_VALUE = 3;
	public static final int KB_ELEMENT_TYPE_AFTER_VALUE = 4;
	public static final int KB_ELEMENT_TYPE_ROW_NUMBER = 5;
	public static final int KB_ELEMENT_TYPE_COLUMN_NAME = 6;
	public static final int KB_ELEMENT_TYPE_TEMPLATE_ID = 7;
	public static final int KB_ELEMENT_TYPE_SOURCE_ID = 8;
	public static final int KB_ELEMENT_TYPE_EFFECTIVE_DATESYNONYM_ID = 9;
	public static final int KB_ELEMENT_TYPE_EXPIRATION_DATESYNONYM_ID = 10;
	public static final int KB_ELEMENT_TYPE_STATUS = 11;
	
	private AuditConstants() {}
}
