package com.mindbox.pe.model;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * Template Usage Type constants. 
 * This is a type-safe enum.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public final class TemplateUsageType implements Serializable {

	private static final long serialVersionUID = 20030606124070000L;

	private static Map<String,TemplateUsageType> knownTypes = new HashMap<String,TemplateUsageType>();

	public static TemplateUsageType createInstance(String value, String displayName, String privilege) {
		if (value == null || value.length() == 0) {
			throw new IllegalArgumentException("value must be specified");
		}

		if (knownTypes.containsKey(value)) {
			return knownTypes.get(value);
		}
		else {
			TemplateUsageType newOne = new TemplateUsageType(value, displayName, privilege);
			knownTypes.put(value, newOne);
			return newOne;
		}
	}

	public static TemplateUsageType valueOf(String value) {
		if (value == null) throw new NullPointerException("value cannot be null");
		if (knownTypes.containsKey(value)) {
			return knownTypes.get(value);
		}
		else {
			throw new IllegalArgumentException("Invalid template usage type: " + value);
		}
	}

	public static TemplateUsageType[] getAllInstances() {
		return knownTypes.values().toArray(new TemplateUsageType[0]);
	}
	
	private final String value;
	private final String displayName;
	private final String privilegeName;

	private TemplateUsageType(String value, String displayName, String privilegeName) {
		this.value = value;
		this.displayName = ((displayName == null || displayName.length() == 0) ? value : displayName);
		this.privilegeName = privilegeName;
	}

	private Object readResolve() throws ObjectStreamException {
		try {
			return createInstance(this.value, this.displayName, this.privilegeName);
		}
		catch (Exception ex) {
			throw new InvalidObjectException(ex.getMessage());
		}
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		else if (obj instanceof TemplateUsageType) {
			return this.value.equals(((TemplateUsageType) obj).value);
		}
		else {
			return false;
		}
	}

	/**
	 * 
	 * @return the display name of this usage
	 * @since PowerEditor 3.3.0
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * 
	 * @return the privilege of this usage
	 * @since PowerEditor 3.3.0
	 */
	public String getPrivilege() {
		return privilegeName;
	}

	public int hashCode() {
		return value.hashCode();
	}

	public String toString() {
		return value;
	}
}