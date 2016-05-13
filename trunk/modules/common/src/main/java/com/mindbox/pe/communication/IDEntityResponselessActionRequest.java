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
public class IDEntityResponselessActionRequest extends AbstractEntityIDActionRequest<AbstractSimpleResponse> {

	private static final long serialVersionUID = 6326012410790978776L;

	private final int actionType;

	/**
	 * @param userID
	 * @param sessionID
	 * @param entityID
	 * @param entityType
	 * @param actionType defined in {@link SessionRequest} 
	 */
	public IDEntityResponselessActionRequest(String userID, String sessionID, int entityID, PeDataType entityType, int actionType) {
		super(userID, sessionID, entityID, entityType, false);
		this.actionType= actionType;
	}

	public int getActionType() {
		return actionType;
	}

}
