package com.mindbox.pe.communication;

import com.mindbox.pe.model.filter.GuidelineReportFilter;

public abstract class AbstractGuidelineReportFilterRequest<T extends ResponseComm> extends SessionRequest<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3656090025287504162L;

	protected final GuidelineReportFilter guidelineReportFilter;

	protected AbstractGuidelineReportFilterRequest(String userID, String sessionID, GuidelineReportFilter guidelineReportFilter) {
		super(userID, sessionID);
		this.guidelineReportFilter = guidelineReportFilter;
	}

	public GuidelineReportFilter getGuidelineReportFilter() {
		return guidelineReportFilter;
	}
}
