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
public abstract class AbstractEntityNameActionRequest<T extends ResponseComm> extends SessionRequest<T> {

	private static final long serialVersionUID = 2003061611403000L;

	protected final String name;
	protected final PeDataType entityType;

	/**
	 * @param userID userID
	 * @param sessionID sessionID
	 * @param name name
	 * @param entityType entityType
	 */
	protected AbstractEntityNameActionRequest(String userID, String sessionID, String name, PeDataType entityType) {
		super(userID, sessionID);
		this.name = name;
		this.entityType = entityType;
	}

	public PeDataType getEntityType() {
		return entityType;
	}

	public String getName() {
		return name;
	}
}
