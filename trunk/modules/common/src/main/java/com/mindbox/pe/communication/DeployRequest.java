package com.mindbox.pe.communication;

import com.mindbox.pe.model.filter.GuidelineReportFilter;

public class DeployRequest extends AbstractGuidelineReportFilterRequest<DeployResponse> {

	private static final long serialVersionUID = 2003052312002007L;

	private final boolean exportPolicies;

	public DeployRequest(String userID, String sessionID, GuidelineReportFilter filter, boolean exportPolicies) {
		super(userID, sessionID, filter);
		this.exportPolicies = exportPolicies;
	}

	public boolean isExportPolicies() {
		return exportPolicies;
	}
}
