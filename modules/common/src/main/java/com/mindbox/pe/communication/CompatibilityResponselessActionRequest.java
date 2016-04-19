package com.mindbox.pe.communication;

import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public class CompatibilityResponselessActionRequest extends SessionRequest<AbstractSimpleResponse> {

	private static final long serialVersionUID = -7214646112247171318L;
	
	private final int actionType;
	private final GenericEntityCompatibilityData data;

	/**
	 * 
	 * @param userID
	 * @param sessionID
	 * @param data
	 * @param actionType
	 */
	public CompatibilityResponselessActionRequest(String userID, String sessionID, GenericEntityCompatibilityData data,
			int actionType) {
		super(userID, sessionID);
		this.actionType = actionType;
		this.data = data;
	}

	public int getActionType() {
		return actionType;
	}

	public GenericEntityCompatibilityData getData() {
		return data;
	}

}