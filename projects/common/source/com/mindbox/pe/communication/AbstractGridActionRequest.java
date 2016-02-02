package com.mindbox.pe.communication;


/**
 * To retrieve grid summary (template lists) for a given template usage type.
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public abstract class AbstractGridActionRequest<T extends ResponseComm> extends AbstractTemplateIDRequest<T> {

	private static final long serialVersionUID = 2003071510004000L;

	protected AbstractGridActionRequest(String userID, String sessionID, int templateID) {
		super(userID, sessionID, templateID);
	}

}
