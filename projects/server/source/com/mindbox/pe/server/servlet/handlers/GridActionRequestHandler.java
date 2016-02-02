package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.ErrorResponse;
import com.mindbox.pe.communication.GridActionRequest;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.SuccessResponse;
import com.mindbox.pe.server.bizlogic.GridActionCoordinator;
import com.mindbox.pe.server.model.LockException;
import com.mindbox.pe.server.model.User;

public class GridActionRequestHandler extends AbstractSessionRequestHandler<GridActionRequest> {

	public ResponseComm handleRequest(GridActionRequest request, HttpServletRequest httpservletrequest) throws LockException {
		User user = getUser(request.getUserID());
		if (request.getActionType() == GridActionRequest.ACTION_TYPE_LOCK) {
			GridActionCoordinator.getInstance().lockGrid(request.getTemplateID(), request.getContexts(), user);
		}
		else if (request.getActionType() == GridActionRequest.ACTION_TYPE_UNLOCK) {
			GridActionCoordinator.getInstance().unlockGrid(request.getTemplateID(), request.getContexts(), user);
		}
		else {
			return new ErrorResponse("InvalidRequestError", "Invalid action type: " + request.getActionType());
		}
		return new SuccessResponse();
	}

}