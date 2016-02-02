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
public class NamedEntityResponselessActionRequest extends AbstractEntityNameActionRequest<AbstractSimpleResponse> {

	private static final long serialVersionUID = 4101525660301485676L;

	private final int actionType;
	
	/**
	 * @param userID
	 * @param sessionID
	 * @param name
	 * @param entityType
	 * @param actionType defined in {@link SessionRequest} 
	 */
	public NamedEntityResponselessActionRequest(String userID, String sessionID, String name, EntityType entityType, int actionType) {
		super(userID, sessionID, name, entityType);
		this.actionType= actionType;
	}

	public int getActionType() {
		return actionType;
	}
}
