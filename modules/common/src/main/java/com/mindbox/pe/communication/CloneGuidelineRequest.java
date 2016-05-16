package com.mindbox.pe.communication;


/**
 * @author Geneho Kim
 * @since PowerEditor 
 */
public class CloneGuidelineRequest extends SessionRequest<AbstractSimpleResponse> {

	private static final long serialVersionUID = 200896917262591341L;

	private final int oldTemplateID, newTemplateID;

	/**
	 * @param userID userID
	 * @param sessionID sessionID
	 * @param oldTemplateID oldTemplateID
	 * @param newTemplateID newTemplateID
	 */
	public CloneGuidelineRequest(String userID, String sessionID, int oldTemplateID, int newTemplateID) {
		super(userID, sessionID);
		this.oldTemplateID = oldTemplateID;
		this.newTemplateID = newTemplateID;
	}

	public int getNewTemplateID() {
		return newTemplateID;
	}

	public int getOldTemplateID() {
		return oldTemplateID;
	}
}
