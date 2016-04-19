package com.mindbox.pe.communication;


/**
 * Abstract request that holds a template id.
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 4.3.7
 */
public abstract class AbstractTemplateIDRequest<T extends ResponseComm> extends SessionRequest<T> {
	
	private static final long serialVersionUID = 20050923400001L;

	private final int templateID;

	protected AbstractTemplateIDRequest(String userID, String sessionID, int templateID) {
		super(userID, sessionID);

		if (templateID < 0) throw new IllegalArgumentException("Invalid template ID");
		this.templateID = templateID;
	}

	public final int getTemplateID() {
		return templateID;
	}

}
