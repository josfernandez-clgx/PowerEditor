package com.mindbox.pe.communication;

import com.mindbox.pe.model.GuidelineContext;

/**
 * To update context of grids.
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 4.2.0
 */
public class FetchFullGuidelineContextRequest extends AbstractGridActionWithContextRequest<ListResponse<GuidelineContext>> {

	private static final long serialVersionUID = 20050215350009L;

	public FetchFullGuidelineContextRequest(String userID, String sessionID, int templateID, GuidelineContext[] subContexts) {
		super(userID, sessionID, templateID, subContexts);
	}

	public String toString() {
		return "FetchFullGuidelineContextRequest[" + getTemplateID() + ",contexts=" + super.getContexts() + "]";
	}

	/**
	 * Identical to <code>getContexts()</code>.
	 * 
	 * @return the sub-contexts as an array of guideline context objects
	 */
	public GuidelineContext[] getSubContexts() {
		return super.getContexts();
	}
}