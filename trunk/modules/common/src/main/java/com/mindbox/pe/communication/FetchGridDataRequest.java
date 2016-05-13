package com.mindbox.pe.communication;

import com.mindbox.pe.model.GuidelineContext;

/**
 * To retrieve grid summary (template lists) for a given template usage type.
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class FetchGridDataRequest extends AbstractGridActionWithContextRequest<GridDataResponse> {
	
	private static final long serialVersionUID = 2003071110002000L;

	public FetchGridDataRequest(String userID, String sessionID, int templateID, GuidelineContext[] contexts) {
		super(userID, sessionID, templateID, contexts);
	}

	public String toString() {
		return "FetchGridDataRequest[" + getTemplateID() + "]";
	}

}
