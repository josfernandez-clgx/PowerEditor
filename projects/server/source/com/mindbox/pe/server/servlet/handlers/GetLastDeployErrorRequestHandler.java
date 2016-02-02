package com.mindbox.pe.server.servlet.handlers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.ErrorResponse;
import com.mindbox.pe.communication.GetLastDeployErrorRequest;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.StringResponse;
import com.mindbox.pe.server.cache.DeploymentManager;

public final class GetLastDeployErrorRequestHandler extends AbstractSessionRequestHandler<GetLastDeployErrorRequest> {

	public ResponseComm handleRequest(GetLastDeployErrorRequest request, HttpServletRequest httpservletrequest) {
		try {
			String errorStr = DeploymentManager.getInstance().getDeployErrorStr(request.getRunID());
			return new StringResponse(errorStr);
		}
		catch (IOException ex) {
			logger.error("Failed to read error string for " + request.getRunID(), ex);
			ErrorResponse response = new ErrorResponse(ErrorResponse.SERVER_ERROR, "msg.error.failure.error.log", new Object[] { ex.getMessage()});
			return response;
		}
	}
}