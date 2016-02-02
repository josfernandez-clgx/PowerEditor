package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.CloneCBRRequest;
import com.mindbox.pe.communication.CloneCBRResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletActionException;

/**
 * 
 * @author Inna Nill
 * @author MindBox, LLC
 * @since PowerEditor 4.1.0
 */
public final class CloneCBRRequestHandler extends AbstractSessionRequestHandler<CloneCBRRequest> {

	public ResponseComm handleRequest(CloneCBRRequest request, HttpServletRequest httpservletrequest) throws ServletActionException {
		User user = getUser(request.getUserID());
		logger.debug("  oldCaseBaseID = " + request.getOldCaseBaseID());
		logger.debug("  newCaseBaseName = " + request.getNewCaseBaseName());
		int newCaseBaseID = BizActionCoordinator.getInstance().cloneCBR(request.getOldCaseBaseID(), request.getNewCaseBaseName(), user);
		logger.info("clone cbr request returned " + newCaseBaseID);

		return new CloneCBRResponse(newCaseBaseID);
	}

}
