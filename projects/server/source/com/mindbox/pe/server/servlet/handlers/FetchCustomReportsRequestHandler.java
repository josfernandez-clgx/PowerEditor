package com.mindbox.pe.server.servlet.handlers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.communication.ErrorResponse;
import com.mindbox.pe.communication.FetchCustomReportsRequest;
import com.mindbox.pe.communication.ListResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;

public final class FetchCustomReportsRequestHandler extends AbstractSessionRequestHandler<FetchCustomReportsRequest> {

	@Override
	protected String getRequiredPrivilegeName(FetchCustomReportsRequest request) {
		return PrivilegeConstants.PRIV_VIEW_REPORT;
	}

	public ResponseComm handleRequest(FetchCustomReportsRequest fecthRequest, HttpServletRequest httpservletrequest) throws ServerException {
		try {
			ListResponse<String> response = new ListResponse<String>(BizActionCoordinator.getInstance().getCustomReportNames());
			return response;
		}
		catch (IOException ex) {
			logger.error("IO Error on fetch custom report", ex);
			return new ErrorResponse("msg.error.failure.report.guideline", ex.getMessage());
		}
	}
}