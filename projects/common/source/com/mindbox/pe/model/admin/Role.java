package com.mindbox.pe.model.admin;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.model.AbstractIDNameObject;
import com.mindbox.pe.model.Auditable;

public class Role extends AbstractIDNameObject implements Comparable<AbstractIDNameObject>, Auditable {

	private static final long serialVersionUID = 2003061613438001L;

	public Role(int id, String name, List<Privilege> list) {
		super(id, name);
		privliegeList = new LinkedList<Privilege>();
		if (list != null) privliegeList.addAll(list);
	}

	public Role(Role source) {
		this(source.getID(), source.getName(), source.privliegeList);
	}

	public Auditable deepCopy() {
		return new Role(this);
	}

	public String getAuditDescription() {
		return toString();
	}
	
	public String toString() {
		return "role '" + getName() + "'";
	}

	public void setPrivileges(List<Privilege> list) {
		privliegeList.clear();
		privliegeList.addAll(list);
	}

	public List<Privilege> getPrivileges() {
		return Collections.unmodifiableList(privliegeList);
	}

	public void addPrivilege(Privilege privilege) {
		if (!privliegeList.contains(privilege)) {
			this.privliegeList.add(privilege);
		}
	}

	public boolean hasPrivilege(String privilegeName) {
		for (Privilege privilege : privliegeList) {
			if (privilege.getName().equals(privilegeName)) {
				return true;
			}
		}
		return false;
	}
	
	public int compareTo(AbstractIDNameObject obj) throws ClassCastException {
		Role role = (Role) obj;
		return getName().compareTo(role.getName());
	}

	public boolean equals(Object obj) {
		return (obj instanceof Role) && super.equals(obj);
	}

	public void setName(String s) {
		super.setName(s);
	}
	
	private List<Privilege> privliegeList;

}