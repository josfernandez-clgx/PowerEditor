/*
 * Created on Jun 13, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.model;

import java.io.Serializable;

import com.mindbox.pe.common.validate.oval.PositiveOrUnassigned;

/**
 * Object with ID. 
 * This is an abstract implementation of {@link Associable}
 * that all other entit classes extend.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public abstract class AbstractIDObject implements Associable, Serializable {

	private static final long serialVersionUID = 200306132300000L;

	@PositiveOrUnassigned
	private int id;

	/**
	 * Constructs a new ID object with unassigned id.
	 * 
	 */
	protected AbstractIDObject() {
		this(UNASSIGNED_ID);
	}

	/**
	 * Constucts a new ID object with the specified id.
	 * @param id the id
	 */
	protected AbstractIDObject(int id) {
		this.id = id;
	}

	public final int getID() {
		return id;
	}

	public final void setID(int newID) {
		this.id = newID;
	}

	
	/**
	 * Added for digester and Struts property getter support.
	 * This returns the same value as the <code>getID()</code> method does.
	 * @return the id of this; identical to <code>getID()</code> method.
	 */
	public int getId() {
		return getID();
	}

	/**
	 * Sets the id of this from the specified string.
	 * Added for digest support.
	 * @param value the string representation of id
	 * @since PowerEditor 3.2.0
	 */
	public final void setIdString(String value) {
		try {
			this.id = Integer.parseInt(value);
		}
		catch (Exception ex) {
		}
	}

	public boolean equals(Object arg) {
		if (arg == null) { return false; }
		if (this == arg) { return true; }
		if (arg instanceof AbstractIDObject) {
			return id == ((AbstractIDObject) arg).id;
		}
		else {
			return false;
		}
	}

	public int hashCode() {
		return id;
	}

	public String toString() {
		return String.valueOf(id);
	}

}