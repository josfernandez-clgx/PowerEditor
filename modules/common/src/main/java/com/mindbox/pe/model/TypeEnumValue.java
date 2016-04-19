/*
 * Created on 2005. 5. 23.
 *
 */
package com.mindbox.pe.model;


/**
 * Type Enum Value object.
 * Represents a row in MB_TYPE_ENUM table.
 * @author Geneho Kim
 * @since PowerEditor 4.3.1
 */
public final class TypeEnumValue extends AbstractIDNameObject {

	private static final long serialVersionUID = 2005052300001L;
	
	/**
	 * Status system type.
	 */
	public static final String TYPE_STATUS = "system.status";
	
	private final String displayLabel;
	
	public TypeEnumValue(int id, String value, String dispLabel) {
		super(id, value);
		this.displayLabel = dispLabel;
	}
	
	public String getDisplayLabel() {
		return displayLabel;
	}
	
	/**
	 * Identical to <code>getName()</code>.
	 * @return the value of this
	 */
	public String getValue() {
		return super.getName();
	}

}
