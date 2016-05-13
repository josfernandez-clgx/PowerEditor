/*
 * Created on 2005. 9. 2.
 *
 */
package com.mindbox.pe.communication;

import com.mindbox.pe.model.PeDataType;


/**
 * Delete template request.
 * @author Geneho Kim
 * @since PowerEditor 4.3.7
 */
public class DeleteTemplateRequest extends IDEntityResponselessActionRequest {

	private static final long serialVersionUID = 20050902700000L;
	
	private final boolean deleteGuidelines;
	
	/**
	 * @param userID
	 * @param sessionID
	 * @param templateID the template ID
	 * @param deleteGuidelines indicates whether or not to delete guidelines for the specified template
	 */
	public DeleteTemplateRequest(String userID, String sessionID, int templateID, boolean deleteGuidelines) {
		super(userID, sessionID, templateID, PeDataType.TEMPLATE, SessionRequest.ACTION_TYPE_DELETE);
		this.deleteGuidelines = deleteGuidelines;
	}
	
	public boolean isDeleteGuidelinesOn() {
		return deleteGuidelines;
	}
}
