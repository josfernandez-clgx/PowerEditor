package com.mindbox.pe.model.assckey;

import java.io.Serializable;

import com.mindbox.pe.model.Associable;

import net.sf.oval.constraint.Min;

/**
 * Immutable abstract implementation of {@link AssociationKey}.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public abstract class AbstractAssociationKey implements AssociationKey, Serializable {

	private static final long serialVersionUID = 2003061317413000L;

	@Min(value = 1)
	private int associableID;

	/**
	 * @param associableID associableID
	 */
	public AbstractAssociationKey(int associableID) {
		if (associableID == Associable.UNASSIGNED_ID) throw new IllegalArgumentException("Invalid associable id: " + associableID);
		this.associableID = associableID;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		else if (this == obj) {
			return true;
		}
		if (obj instanceof AssociationKey) {
			return this.associableID == ((AssociationKey) obj).getAssociableID();
		}
		else {
			return false;
		}
	}

	@Override
	public final int getAssociableID() {
		return associableID;
	}

	@Override
	public int hashCode() {
		return associableID;
	}

	/**
	 * To allow mutable extensions.
	 * @param id id
	 * @since 5.1.0
	 */
	void setAssociableID(int id) {
		this.associableID = id;
	}

	@Override
	public String toString() {
		return super.toString() + ":id=" + associableID;
	}
}
