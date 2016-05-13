package com.mindbox.pe.common;


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

	// The KB_ELEMENT_TYPE* constants are used to populate the kb_element_type_id column of the MB_KB_AUDIT_DETAIL_DATA table
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
	public static final int KB_ELEMENT_TYPE_PROPERTY_NAME = 12;
	public static final int KB_ELEMENT_TYPE_BEFORE_NATIVE_VALUE = 13;
	public static final int KB_ELEMENT_TYPE_AFTER_NATIVE_VALUE = 14;
	public static final int KB_ELEMENT_TYPE_COLUMN_ID = 15;
	public static final int KB_ELEMENT_TYPE_INSERTED_VALUES_ID = 16;
	public static final int KB_ELEMENT_TYPE_REMOVED_VALUES_ID = 17;

	public static String getChangedTypeDescription(int typeID) {
		switch (typeID) {
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION:
			return "Guideline Activation";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_TEMPLATE:
			return "Guideline Template";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_ENTITY:
			return "Entity";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_CATEGORY:
			return "Category";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_ENTITY_CAT_RELATIONSHIP:
			return "Entity-To-Category Relationship";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION:
			return "Parameter Activation";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_TEMPLATE:
			return "Parameter Template";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_ENTITY_COMPATIBILITY:
			return "Entity Compatibility";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_DATE_SYNONYM:
			return "Date Synonym";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTION:
			return "Guideline Action";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_TEST_CONDITION:
			return "Test Condition";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_ROLE:
			return "Role";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_USER:
			return "User";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_PHASE:
			return "Phase";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_PROCESS:
			return "Process";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_CBR_ATTRIBUTE:
			return "CBR Attribute";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_CBR_CASE:
			return "CBR Case";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_CBR_CASE_BASE:
			return "CBR Case Base";
		default:
			return "[Invalid KBChanged Type: " + typeID + ']';
		}
	}

	public static String getElementTypeDescription(int elementTypeID) {
		switch (elementTypeID) {
		case KB_ELEMENT_TYPE_ENTITY_ID:
			return "Entity";
		case KB_ELEMENT_TYPE_CATEGORY_ID:
			return "Category";
		case KB_ELEMENT_TYPE_BEFORE_VALUE:
			return "Before Value";
		case KB_ELEMENT_TYPE_AFTER_VALUE:
			return "New Value";
		case KB_ELEMENT_TYPE_ROW_NUMBER:
			return "Row Number";
		case KB_ELEMENT_TYPE_COLUMN_NAME:
			return "Column Name";
		case KB_ELEMENT_TYPE_TEMPLATE_ID:
			return "Template";
		case KB_ELEMENT_TYPE_SOURCE_ID:
			return "Source";
		case KB_ELEMENT_TYPE_EFFECTIVE_DATESYNONYM_ID:
			return "Effective Date";
		case KB_ELEMENT_TYPE_EXPIRATION_DATESYNONYM_ID:
			return "Expiration Date";
		default:
			return "[Invalid element type id " + elementTypeID + "]";
		}
	}

	public static String getModTypeDescription(int kbModTypeID) {
		switch (kbModTypeID) {
		case KB_MOD_TYPE_GENERIC_ADD:
			return "Add";
		case KB_MOD_TYPE_GENERIC_REMOVE:
			return "Remove";
		case KB_MOD_TYPE_GENERIC_UPDATE:
			return "Update";
		case KB_MOD_TYPE_GENERIC_IMPORT:
			return "Import";
		case KB_MOD_TYPE_ADD_CONTEXT_ELEMENT:
			return "Add Context Element";
		case KB_MOD_TYPE_REMOVE_CONTEXT_ELEMENT:
			return "Remove Context Element";
		case KB_MOD_TYPE_ADD_GRID_ROW:
			return "Add Row";
		case KB_MOD_TYPE_REMOVE_GRID_ROW:
			return "Remove Row";
		case KB_MOD_TYPE_MODIFY_GRID_CELL:
			return "Modify Cell";
		case KB_MOD_TYPE_MODIFY_ENTITY_PROPERTY:
			return "Modify Entity Property";
		case KB_MOD_TYPE_ADD_ENTITY_CAT_RELATIONSHIP:
			return "Add Entity-To-Category Relationship";
		case KB_MOD_TYPE_REMOVE_ENTITY_CAT_RELATIONSHIP:
			return "Remove Entity-To-Category Relationship";
		case KB_MOD_TYPE_COPY_POLICIES:
			return "Copy Policies";
		case KB_MOD_TYPE_CUTOVER_POLICIES:
			return "Cutover Policies";
		case KB_MOD_TYPE_REMOVE_ALL_POLICIES:
			return "Remove All Policies";
		case KB_MOD_TYPE_MODIFY_COMMENTS:
			return "Modify Activation Comments";
		case KB_MOD_TYPE_MODIFY_EFFECTIVE_DATE:
			return "Modify Effective Date";
		case KB_MOD_TYPE_MODIFY_EXPIRATION_DATE:
			return "Modify Expiration Date";
		case KB_MOD_TYPE_MODIFY_STATUS:
			return "Modify Status";
		default:
			return "[Invalid KB Mod Type: " + kbModTypeID + "]";
		}
	}


	private AuditConstants() {
	}
}
