/*
 * Created on May 19, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.model;

import java.io.Serializable;

/**
 * Type-safe enumeration base class. This is immutable.
 * Provides integer ID and name fields.
 * Name is returned by the {@link #toString} method.
 * @since PowerEditor 1.0
 */
public abstract class EnumerationBase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3656090025287504162L;

	protected final int id;
	protected final String name;

	protected EnumerationBase(int id, String name) {
		if (name == null) {
			throw new NullPointerException("name cannot be null");
		}
		this.id = id;
		this.name = name;
	}

	public boolean equals(Object obj) {
		if (obj instanceof EnumerationBase) {
			return id == ((EnumerationBase) obj).id;
		}
		else {
			return false;
		}
	}

	/**
	 * Gets the internal ID of this entity type.
	 * @return the id
	 */
	public int getID() {
		return id;
	}

	public String toString() {
		return name;
	}

	public int hashCode() {
		return id;
	}

	public String getName() {
		return name;
	}
}
