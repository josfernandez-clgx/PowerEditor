package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.BooleanResponse;
import com.mindbox.pe.communication.CheckForUniqueNameRequest;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;

public class CheckForUniqueNameRequestHandler extends AbstractSessionRequestHandler<CheckForUniqueNameRequest> {

	public ResponseComm handleRequest(CheckForUniqueNameRequest request, HttpServletRequest httpservletrequest) {
		boolean result = BizActionCoordinator.getInstance().isUniqueName(request.getEntityType(), request.getName());
		return new BooleanResponse(result);
	}

}
