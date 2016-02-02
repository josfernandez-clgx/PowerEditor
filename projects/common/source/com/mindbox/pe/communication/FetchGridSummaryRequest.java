package com.mindbox.pe.communication;

import com.mindbox.pe.model.GridSummary;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.TemplateUsageType;

/**
 * To retrieve grid summary (template lists) for a given template usage type.
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class FetchGridSummaryRequest extends SessionRequest<ListResponse<GridSummary>> {
	
	private static final long serialVersionUID = 2003052312002009L;

	private final GuidelineContext[] contexts;
	private final TemplateUsageType usageType;


	public FetchGridSummaryRequest(String userID, String sessionID, TemplateUsageType usageType, GuidelineContext[] contexts) {
		super(userID, sessionID);
		this.usageType = usageType;
		this.contexts = contexts;
	}

	public String toString() {
		return "FetchGridSummaryRequest[" + usageType + "]";
	}

	public GuidelineContext[] getContexts() {
		return contexts;
	}

	public TemplateUsageType getUsageType() {
		return usageType;
	}

}
