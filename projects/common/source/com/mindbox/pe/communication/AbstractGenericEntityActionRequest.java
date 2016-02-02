/*
 * Created on 2004. 4. 20.
 *
 */
package com.mindbox.pe.communication;

import com.mindbox.pe.model.GenericEntityType;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public abstract class AbstractGenericEntityActionRequest<T extends ResponseComm> extends SessionRequest<T> {

	protected final int entityID;
	protected final GenericEntityType type;

	/**
	 * @param userID
	 * @param sessionID
	 */
	public AbstractGenericEntityActionRequest(String userID, String sessionID, int entityID, GenericEntityType type) {
		super(userID, sessionID);
		this.entityID = entityID;
		this.type = type;
	}

	public int getEntityID() {
		return entityID;
	}

	public GenericEntityType getEntityType() {
		return type;	
	}
	
}
