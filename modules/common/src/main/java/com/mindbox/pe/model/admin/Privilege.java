package com.mindbox.pe.model.admin;

import com.mindbox.pe.model.AbstractIDNameObject;

public class Privilege extends AbstractIDNameObject implements Cloneable, Comparable<AbstractIDNameObject> {

	private static final long serialVersionUID = 2003061613438000L;

	private String displayString;
	private int privilegeType;

	public Privilege(int id, String name, String displayName, int privilegeType) {
		super(id, name);
		this.displayString = displayName;
		this.privilegeType = privilegeType;
	}

	public int getPrivilegeType() {
		return privilegeType;
	}

	public void setPrivilegeType(int privilege_type) {
		this.privilegeType = privilege_type;
	}

	public void setDisplayString(String s) {
		displayString = s;
	}

	public String getDisplayString() {
		return displayString;
	}

	public int compareTo(AbstractIDNameObject obj) throws ClassCastException {
		Privilege privilege = (Privilege) obj;
		return getName().compareTo(privilege.getName());
	}

	public boolean equals(Object obj) {
		return (obj instanceof Privilege && super.equals(obj));
	}

	public void setName(String s) {
		super.setName(s);
	}

	public String toString() {
		return getDisplayString();
	}

	public String toStringComplete() {
		return (super.toString() + " [display_string=" + getDisplayString() + ", privilegeType=" + getPrivilegeType() + "]");
	}

}
