package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.communication.ReportRequest;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.StringResponse;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;

public final class ReportRequestHandler extends AbstractSessionRequestHandler<ReportRequest> {

	@Override
	protected String getRequiredPrivilegeName(ReportRequest request) {
		return PrivilegeConstants.PRIV_VIEW_REPORT;
	}

	public ResponseComm handleRequest(ReportRequest printRequest, HttpServletRequest httpservletrequest) throws ServerException {
		StringResponse response = new StringResponse(BizActionCoordinator.getInstance().generateReportURL(
				httpservletrequest,
				printRequest.getReportSpec(),
				printRequest.getGuidelineList()));
		return response;
	}
}