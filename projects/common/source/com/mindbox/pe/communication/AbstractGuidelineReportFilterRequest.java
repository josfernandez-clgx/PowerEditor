package com.mindbox.pe.communication;

import com.mindbox.pe.model.filter.GuidelineReportFilter;

public abstract class AbstractGuidelineReportFilterRequest<T extends ResponseComm> extends SessionRequest<T> {

	protected final GuidelineReportFilter guidelineReportFilter;

	protected AbstractGuidelineReportFilterRequest(String userID, String sessionID, GuidelineReportFilter guidelineReportFilter) {
		super(userID, sessionID);
		this.guidelineReportFilter = guidelineReportFilter;
	}

	public GuidelineReportFilter getGuidelineReportFilter() {
		return guidelineReportFilter;
	}
}
