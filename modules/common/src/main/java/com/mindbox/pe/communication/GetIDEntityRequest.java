package com.mindbox.pe.communication;

import com.mindbox.pe.model.PeDataType;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public class GetIDEntityRequest extends AbstractEntityIDActionRequest<SingleEntityResponse> {

	private static final long serialVersionUID = 2003061611403003L;


	/**
	 * @param userID userID
	 * @param sessionID sessionID
	 * @param entityID entityID
	 * @param entityType entityType
	 * @param lockEntity lockEntity
	 */
	public GetIDEntityRequest(String userID, String sessionID, int entityID, PeDataType entityType, boolean lockEntity) {
		super(userID, sessionID, entityID, entityType, lockEntity);
	}

}
