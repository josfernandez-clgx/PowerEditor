package com.mindbox.pe.server.servlet.handlers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.communication.ByteArrayResponse;
import com.mindbox.pe.communication.ErrorResponse;
import com.mindbox.pe.communication.PrintRequest;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;

public final class PrintRequestHandler extends AbstractSessionRequestHandler<PrintRequest> {

	@Override
	protected String getRequiredPrivilegeName(PrintRequest request) {
		return PrivilegeConstants.PRIV_VIEW_REPORT;
	}

	public ResponseComm handleRequest(PrintRequest printRequest, HttpServletRequest httpservletrequest) throws ServerException {
		try {
			ByteArrayResponse response = new ByteArrayResponse(BizActionCoordinator.getInstance().generateGuidelineReports(
					printRequest.getReportSpec(),
					printRequest.getGuidelineList()));
			return response;
		}
		catch (IOException ex) {
			logger.error("Failed to generate guideline reports", ex);
			return new ErrorResponse("msg.error.failure.report.guideline", ex.getMessage());
		}
	}
}