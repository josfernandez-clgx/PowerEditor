package com.mindbox.pe.communication;

import com.mindbox.pe.model.GuidelineContext;

/**
 * To retrieve grid summary (template lists) for a given template usage type.
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public abstract class AbstractGridActionWithContextRequest<T extends ResponseComm> extends AbstractGridActionRequest<T> {
	
	private static final long serialVersionUID = 2006062290000L;

	private final GuidelineContext[] contexts;

	protected AbstractGridActionWithContextRequest(String userID, String sessionID, int templateID, GuidelineContext[] contexts) {
		super(userID, sessionID, templateID);

		this.contexts = contexts;
	}

	public final GuidelineContext[] getContexts() {
		return contexts;
	}

}
