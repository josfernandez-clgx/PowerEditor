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
public final class EntityType extends EnumerationBase {
	
	private static final long serialVersionUID = 200305191437000L;

	public static final EntityType TEMPLATE = new EntityType(6, "template");
	public static final EntityType USER_DATA = new EntityType(20,"user");
	public static final EntityType ROLE = new EntityType(21,"role");
	public static final EntityType PRIVILEGE = new EntityType(22,"privilege");
	public static final EntityType GUIDELINE_REPORT = new EntityType(30, "GuidelineReport");
	public static final EntityType GUIDELINE_ACTION   = new EntityType(31, "GuidelineAction");
	public static final EntityType GUIDELINE_TEST_CONDITION   = new EntityType(32, "GuidelineTestCondition");

	public static final EntityType DATE_SYNONYM       = new EntityType(50, "DateSynonym");
	public static final EntityType PARAMETER_TEMPLATE = new EntityType(61, "parameter template");
	public static final EntityType PARAMETER_GRID     = new EntityType(62, "parameter grid"); 
	public static final EntityType PROCESS_REQUEST    = new EntityType(81, "Process Request");
	public static final EntityType PROCESS_PHASE      = new EntityType(82, "Process Phase");
	
	public static final EntityType CBR_CASE_BASE	  = new EntityType(33, "CBR Case Base");
	public static final EntityType CBR_ATTRIBUTE_TYPE	  = new EntityType(34, "CBR Attribute Type");
	public static final EntityType CBR_SCORING_FUNCTION	  = new EntityType(35, "CBR Scoring Function");
	public static final EntityType CBR_CASE_CLASS	  = new EntityType(36, "CBR Case Class");
	public static final EntityType CBR_CASE_ACTION	  = new EntityType(37, "CBR Case Action");
	public static final EntityType CBR_ATTRIBUTE	  = new EntityType(38, "CBR Attribute");
	public static final EntityType CBR_VALUE_RANGE	  = new EntityType(39, "CBR Value Range");
	public static final EntityType CBR_CASE			  = new EntityType(40, "CBR Case");
	
	public static final EntityType ENUM_SOURCE	  = new EntityType(70, "Enumeration Source");
	
	
	public static final EntityType forID(int id) {
		switch(id) {
			case 6 : return TEMPLATE;
			case 20 : return USER_DATA;
			case 21 : return ROLE;
			case 22 : return PRIVILEGE;
			case 30 : return GUIDELINE_REPORT;
			case 31 : return GUIDELINE_ACTION;
			case 32 : return GUIDELINE_TEST_CONDITION;
			case 33 : return CBR_CASE_BASE;
			case 34 : return CBR_ATTRIBUTE_TYPE;
			case 35 : return CBR_SCORING_FUNCTION;
			case 36 : return CBR_CASE_CLASS;
			case 37 : return CBR_CASE_ACTION;
			case 38 : return CBR_ATTRIBUTE;
			case 39 : return CBR_VALUE_RANGE;
			case 40 : return CBR_CASE;
			case 50 : return DATE_SYNONYM;
			case 61 : return PARAMETER_TEMPLATE;
			case 62 : return PARAMETER_GRID;
			case 70 : return ENUM_SOURCE;
			case 81 : return PROCESS_REQUEST;
			case 82 : return PROCESS_PHASE;
			default:
				throw new IllegalArgumentException("Invalid entity type id: " + id);	
		}
	}
	
	private EntityType(int id, String name) {
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
