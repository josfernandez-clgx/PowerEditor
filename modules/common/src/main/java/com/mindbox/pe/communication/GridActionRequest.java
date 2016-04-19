package com.mindbox.pe.communication;

import com.mindbox.pe.model.GuidelineContext;

/**
 * To retrieve grid summary (template lists) for a given template usage type.
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class GridActionRequest extends AbstractGridActionWithContextRequest<AbstractSimpleResponse> {

	private static final long serialVersionUID = 2003071410002000L;

	private final int actionType;

	public GridActionRequest(String userID, String sessionID, int templateID, GuidelineContext[] contexts, int actionType) {
		super(userID, sessionID, templateID, contexts);
		this.actionType = actionType;
	}

	public String toString() {
		return "GridActionRequest[" + getTemplateID() + ",action=" + actionType + "]";
	}

	public int getActionType() {
		return actionType;
	}
}
