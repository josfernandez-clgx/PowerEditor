package com.mindbox.pe.model;

import java.io.ObjectStreamException;
import java.io.Serializable;

import com.mindbox.pe.common.UtilBase;


/**
 * Encapsulates EnumValue tag in a domain XML.
 * This is used by the domain digester.
 * @author Geneho
 * @since PowerEditor 3.2.0
 */
public class EnumValue implements Comparable<EnumValue>, Serializable {

	private static final long serialVersionUID = 2004060312062000L;

	// Some enumerated lists allow an empty or blank value as one of the valid options.
	public static final EnumValue BLANK;
	static {
		BLANK = new EnumValue();
		BLANK.setDisplayLabel(" "); // work-around to get JCombo to display BLANK correctly.
		BLANK.setDeployValue("");
	}

	private Integer deployID;
	private String deployValue;
	private String displayLabel;
	private boolean inactive;

	/**
	 * 
	 */
	public EnumValue() {
		super();
		inactive = false;
	}


	/**
	 * @return Returns the deployID.
	 */
	public Integer getDeployID() {
		return deployID;
	}

	/**
	 * @param deployID The deployID to set.
	 */
	public void setDeployID(Integer deployID) {
		this.deployID = deployID;
	}

	/**
	 * @return Returns the deployValue.
	 */
	public String getDeployValue() {
		return deployValue;
	}

	/**
	 * @param deployValue The deployValue to set.
	 */
	public void setDeployValue(String deployValue) {
		this.deployValue = deployValue;
	}

	/**
	 * @return Returns the displayLabel.
	 */
	public String getDisplayLabel() {
		return displayLabel;
	}

	/**
	 * @param displayLabel The displayLabel to set.
	 */
	public void setDisplayLabel(String displayLabel) {
		this.displayLabel = displayLabel;
	}

	public boolean isActive() {
		return !inactive;
	}

	/**
	 * 
	 * @return String The inactive flag.
	 */
	public String getInactive() {
		return String.valueOf(this.inactive);
	}

	/**
	 * @param inactive The inactive to set.
	 */
	public void setInactive(String inactive) {
		this.inactive = (UtilBase.isEmptyAfterTrim(inactive)
				? false
				: !(inactive.equalsIgnoreCase("no") || inactive.equalsIgnoreCase("false")));
	}

	public String toString() {
		return this == BLANK ? "" : (deployID == null ? deployValue : deployID.toString());
		// Otherwise Domain.xml is missing a required attribute value
	}

	public String debugString() {
		return "EnumValue[" + deployID + ": " + deployValue + "=" + displayLabel + ": " + inactive + "]";
	}

	/** 
	 * This implementation of compareTo is inconsistent with equals.  compareTo is based only on display label,
	 * whereas equals is based on all properties.  (n.b. This would have better been implemented as a 
	 * "public class EnumDisplayValueComparator implements Comparator")
	 */
	public int compareTo(EnumValue obj) {
		if (this == obj) return 0;
		return this.displayLabel.compareTo(obj.getDisplayLabel());
	}

	public boolean isValidForDomainEnumValue() {
		return !(deployID == null || displayLabel == null);
	}

	public boolean hasDeployID() {
		return deployID != null;
	}

	private Object readResolve() throws ObjectStreamException {
		return deployID == null && deployValue.equals("") ? BLANK : this;
	}

	/** 
	 * Property-based equals. i.e. If two instances have the same value for
	 * each and every property then they represent the same EnumValue.
	 * 
	 * Subclasses should override this method _if_ they add any property significant for equals semantics.
	 */
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (this == BLANK) {
			return false; // BLANK is only equal to itself, which would have been caught above.
		}
		if (o == null) {
			return false;
		}
		if (!o.getClass().getName().equals(this.getClass().getName())) { // safe for subclasses and multiple classloaders
			return false;
		}
		EnumValue that = (EnumValue) o;
		return UtilBase.isSame(this.deployID, that.deployID) && UtilBase.nullSafeEquals(this.deployValue, that.deployValue)
				&& UtilBase.nullSafeEquals(this.displayLabel, that.displayLabel) && this.isActive() == that.isActive();
	}

	public int hashCode() {
		int result = 13;
		result = 17 * result + (deployID == null ? 0 : deployID.hashCode());
		result = 17 * result + (deployValue == null ? 0 : deployValue.hashCode());
		result = 17 * result + (displayLabel == null ? 0 : displayLabel.hashCode());
		result = 17 * result + (isActive() ? 0 : 1);
		return super.hashCode();
	}
}
