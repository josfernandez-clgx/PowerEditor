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
public class GenericEntityResponselessActionRequest extends AbstractGenericEntityActionRequest<AbstractSimpleResponse> {

	private static final long serialVersionUID = 4786782978419052492L;

	private final int actionType;

	/**
	 * 
	 * @param userID
	 * @param sessionID
	 * @param entityID
	 * @param type
	 * @param actionType
	 */
	public GenericEntityResponselessActionRequest(
		String userID,
		String sessionID,
		int entityID,
		GenericEntityType type,
		int actionType) {
		super(userID, sessionID, entityID, type);
		this.actionType = actionType;
	}

	public int getActionType() {
		return actionType;
	}

}