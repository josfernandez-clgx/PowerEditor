package com.mindbox.pe.model.filter;

import com.mindbox.pe.model.GenericEntityType;

public final class CloneGenericEntityFilter extends AbstractGenericEntitySearchFilter {

	private static final long serialVersionUID = 2006052590000L;

	private final int parentID;

	private final boolean forAllDescendents;

	public CloneGenericEntityFilter(GenericEntityType type, int parentID, boolean flag) {
		super(type);
		this.parentID = parentID;
		this.forAllDescendents = flag;
	}

	public int getParentID() {
		return parentID;
	}

	public boolean isForAllDescendents() {
		return forAllDescendents;
	}

	public String toString() {
		return "CloneGenericEntityFilter[" + parentID + "]" + super.toString();
	}
}
