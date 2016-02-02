/*
 * Created on Jun 16, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.communication;

import com.mindbox.pe.model.EntityType;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public abstract class AbstractEntityNameActionRequest<T extends ResponseComm> extends SessionRequest<T> {

	private static final long serialVersionUID = 2003061611403000L;

	protected final String name;
	protected final EntityType entityType;

	/**
	 * @param userID
	 * @param sessionID
	 */
	protected AbstractEntityNameActionRequest(String userID, String sessionID, String name, EntityType entityType) {
		super(userID, sessionID);
		this.name = name;
		this.entityType = entityType;
	}

	public String getName() {
		return name;
	}
	
	public EntityType getEntityType() {
		return entityType;	
	}
}
