package com.mindbox.pe.model;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;

/**
 * Entity type enumeration.
 * This is a type-safe enumeration. For equality, just check for identity. 
 * That is, you can do 
 * <pre>
   if (EntityType.DATE_SYNONYM == myType) {
   ...
   </pre>
 * @since PowerEditor 1.0
 */
public final class PeDataType extends EnumerationBase {

	private static final long serialVersionUID = 200305191437000L;

	public static final PeDataType TEMPLATE = new PeDataType(6, "template");
	public static final PeDataType USER_DATA = new PeDataType(20, "user");
	public static final PeDataType ROLE = new PeDataType(21, "role");
	public static final PeDataType PRIVILEGE = new PeDataType(22, "privilege");
	public static final PeDataType GUIDELINE_REPORT = new PeDataType(30, "GuidelineReport");
	public static final PeDataType GUIDELINE_ACTION = new PeDataType(31, "GuidelineAction");
	public static final PeDataType GUIDELINE_TEST_CONDITION = new PeDataType(32, "GuidelineTestCondition");

	public static final PeDataType DATE_SYNONYM = new PeDataType(50, "DateSynonym");
	public static final PeDataType PARAMETER_TEMPLATE = new PeDataType(61, "parameter template");
	public static final PeDataType PARAMETER_GRID = new PeDataType(62, "parameter grid");
	public static final PeDataType PROCESS_REQUEST = new PeDataType(81, "Process Request");
	public static final PeDataType PROCESS_PHASE = new PeDataType(82, "Process Phase");

	public static final PeDataType CBR_CASE_BASE = new PeDataType(33, "CBR Case Base");
	public static final PeDataType CBR_ATTRIBUTE_TYPE = new PeDataType(34, "CBR Attribute Type");
	public static final PeDataType CBR_SCORING_FUNCTION = new PeDataType(35, "CBR Scoring Function");
	public static final PeDataType CBR_CASE_CLASS = new PeDataType(36, "CBR Case Class");
	public static final PeDataType CBR_CASE_ACTION = new PeDataType(37, "CBR Case Action");
	public static final PeDataType CBR_ATTRIBUTE = new PeDataType(38, "CBR Attribute");
	public static final PeDataType CBR_VALUE_RANGE = new PeDataType(39, "CBR Value Range");
	public static final PeDataType CBR_CASE = new PeDataType(40, "CBR Case");

	public static final PeDataType ENUM_SOURCE = new PeDataType(70, "Enumeration Source");


	public static final PeDataType forID(int id) {
		switch (id) {
		case 6:
			return TEMPLATE;
		case 20:
			return USER_DATA;
		case 21:
			return ROLE;
		case 22:
			return PRIVILEGE;
		case 30:
			return GUIDELINE_REPORT;
		case 31:
			return GUIDELINE_ACTION;
		case 32:
			return GUIDELINE_TEST_CONDITION;
		case 33:
			return CBR_CASE_BASE;
		case 34:
			return CBR_ATTRIBUTE_TYPE;
		case 35:
			return CBR_SCORING_FUNCTION;
		case 36:
			return CBR_CASE_CLASS;
		case 37:
			return CBR_CASE_ACTION;
		case 38:
			return CBR_ATTRIBUTE;
		case 39:
			return CBR_VALUE_RANGE;
		case 40:
			return CBR_CASE;
		case 50:
			return DATE_SYNONYM;
		case 61:
			return PARAMETER_TEMPLATE;
		case 62:
			return PARAMETER_GRID;
		case 70:
			return ENUM_SOURCE;
		case 81:
			return PROCESS_REQUEST;
		case 82:
			return PROCESS_PHASE;
		default:
			throw new IllegalArgumentException("Invalid entity type id: " + id);
		}
	}

	private PeDataType(int id, String name) {
		super(id, name);
	}

	private Object readResolve() throws ObjectStreamException {
		try {
			return forID(this.id);
		}
		catch (IllegalArgumentException ex) {
			throw new InvalidObjectException(ex.getMessage());
		}
	}
}
