package com.mindbox.pe.communication;

import com.mindbox.pe.model.grid.ParameterGrid;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.2.0
 */
public class FetchParameterGridRequest extends SessionRequest<ListResponse<ParameterGrid>> {

	private static final long serialVersionUID = 2004010516420000L;
	
	private final int templateID; 
	
	/**
	 * @param userID
	 * @param sessionID
	 */
	public FetchParameterGridRequest(String userID, String sessionID, int templateID) {
		super(userID, sessionID);
		this.templateID = templateID;
	}

	public int getTemplateID() {
		return templateID;
	}
	
}
