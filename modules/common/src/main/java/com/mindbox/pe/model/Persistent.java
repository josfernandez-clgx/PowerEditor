/*
 * Created on Jun 16, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.model;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public interface Persistent {
	
	/**
	 * Unassigned ID. Value is -1.
	 */
	public static final int UNASSIGNED_ID = -1;

	/**
	 * Gets the unique of this.
	 * @return the id unique accross the same entity
	 */
	int getID();

}
