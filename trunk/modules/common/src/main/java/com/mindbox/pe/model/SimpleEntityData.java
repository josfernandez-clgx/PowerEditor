package com.mindbox.pe.model;


/**
 * Simple concrete implementation of {@link AbstractIDNameObject}.
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public final class SimpleEntityData extends AbstractIDNameObject {

	private static final long serialVersionUID = 2003061920001000L;
	
	public SimpleEntityData(int id, String name) {
		super(id, name);
	}

	public void setName(String name) {
		super.setName(name);
	}

	public String toString() {
		return getName();
	}

}
