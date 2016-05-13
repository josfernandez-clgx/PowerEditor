package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.CloneGuidelineRequest;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.SuccessResponse;
import com.mindbox.pe.server.bizlogic.GridActionCoordinator;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletActionException;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public final class CloneGuidelineRequestHandler extends AbstractSessionRequestHandler<CloneGuidelineRequest> {

	public ResponseComm handleRequest(CloneGuidelineRequest request, HttpServletRequest httpservletrequest) throws ServletActionException {
		User user = getUser(request.getUserID());
		logger.debug("  oldTemplate = " + request.getOldTemplateID());
		logger.debug("  newTemplate = " + request.getNewTemplateID());
		GridActionCoordinator.getInstance().cloneGuidelines(request.getOldTemplateID(), request.getNewTemplateID(), user);
		return new SuccessResponse();
	}

}
