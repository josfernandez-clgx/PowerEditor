package com.mindbox.pe.model.domain;

import java.io.Serializable;

/**
 * Domain Class link.
 * Used by Domain digester.
 * @author Geneho
 * @since PowerEditor 1.0
 */
public class DomainClassLink implements Serializable {

	private static final long serialVersionUID = -3447362247013354354L;

	private String parentName;
	private String childName;
	private String deployValue; // link name. @since PowerEditor 4.2.0, configuration in domain.xml file.
	private boolean isSingleton;

	public DomainClassLink() {
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof DomainClassLink) {
			return this.parentName.equals(((DomainClassLink) obj).parentName) && this.childName.equals(((DomainClassLink) obj).childName);
		}
		else {
			return false;
		}
	}

	public String getChildName() {
		return childName;
	}

	public String getDeployValueName() {
		return deployValue;
	}

	public String getParentName() {
		return parentName;
	}

	@Override
	public int hashCode() {
		return (parentName + "." + childName).hashCode();
	}

	public boolean isSingleton() {
		return isSingleton;
	}

	public void setChildName(String s) {
		childName = s;
	}

	public void setDeployValueName(String s) {
		deployValue = s;
	}

	/**
	 * Sets multiplicity flag.
	 * @param value value
	 */
	public void setHasMultiplicity(String value) {
		isSingleton = !(value != null && (value.equals("1") || Boolean.valueOf(value).booleanValue()));
	}

	public void setParentName(String s) {
		parentName = s;
	}

	@Override
	public String toString() {
		return "DomainClassLink[" + getParentName() + "->" + getChildName() + ",dv=" + getDeployValueName() + " -> " + "]";
	}
}