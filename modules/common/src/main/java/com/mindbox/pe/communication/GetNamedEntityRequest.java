package com.mindbox.pe.communication;

import com.mindbox.pe.model.PeDataType;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public class GetNamedEntityRequest extends AbstractEntityNameActionRequest<SingleEntityResponse> {

	private static final long serialVersionUID = 2351105608889216105L;

	/**
	 * @param userID userID
	 * @param sessionID sessionID
	 * @param name name
	 * @param entityType entityType
	 */
	public GetNamedEntityRequest(String userID, String sessionID, String name, PeDataType entityType) {
		super(userID, sessionID, name, entityType);
	}

}
