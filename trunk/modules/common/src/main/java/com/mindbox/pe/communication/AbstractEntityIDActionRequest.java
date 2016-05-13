/*
 * Created on Jun 16, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.communication;

import com.mindbox.pe.model.PeDataType;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public abstract class AbstractEntityIDActionRequest<T extends ResponseComm> extends SessionRequest<T> {

	private static final long serialVersionUID = 2003061611403000L;

	protected final int entityID;
	protected final PeDataType entityType;
	protected final boolean lock;

	/**
	 * @param userID
	 * @param sessionID
	 */
	protected AbstractEntityIDActionRequest(String userID, String sessionID, int entityID, PeDataType entityType, boolean lockEntity) {
		super(userID, sessionID);
		this.entityID = entityID;
		this.entityType = entityType;
		this.lock = lockEntity;
	}
	
	public int getEntityID() {
		return entityID;
	}

	public PeDataType getEntityType() {
		return entityType;	
	}
	
	public boolean doLockEntity() {
		return lock;
	}
}
