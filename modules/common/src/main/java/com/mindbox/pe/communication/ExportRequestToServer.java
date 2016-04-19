package com.mindbox.pe.communication;

import com.mindbox.pe.model.filter.GuidelineReportFilter;

/**
 * @author Vineet Khosla
 * @since 5.0.0
 */
public class ExportRequestToServer extends AbstractGuidelineReportFilterRequest<ExportRequestToServerResponse> {

	private static final long serialVersionUID = 3204235621151189605L;

	private final String fileName;

	public ExportRequestToServer(GuidelineReportFilter filter, String fileName, String userID, String sessionID) {
		super(userID, sessionID, filter);
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public String toString() {
		return "ExportRequestToServer[fitler=" + guidelineReportFilter;
	}
}
