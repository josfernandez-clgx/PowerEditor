package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.NamedEntityResponselessActionRequest;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.SessionRequest;
import com.mindbox.pe.communication.SuccessResponse;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletActionException;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public final class NamedEntityResponselessActionRequestHandler extends AbstractSessionRequestHandler<NamedEntityResponselessActionRequest> {

	public ResponseComm handleRequest(NamedEntityResponselessActionRequest request, HttpServletRequest httpservletrequest) throws ServletActionException {
		int actionType = request.getActionType();
		User user = getUser(request.getUserID());
		switch (actionType) {
		case SessionRequest.ACTION_TYPE_LOCK:
			BizActionCoordinator.getInstance().lockEntity(request.getEntityType(), request.getName(), user);
			return new SuccessResponse();

		case SessionRequest.ACTION_TYPE_UNLOCK:
			BizActionCoordinator.getInstance().unlockEntity(request.getEntityType(), request.getName(), user);
			return new SuccessResponse();

		case SessionRequest.ACTION_TYPE_DELETE:
			BizActionCoordinator.getInstance().deleteEntity(request.getEntityType(), request.getName(), user);
			return new SuccessResponse();
		default:
			return generateErrorResponse(new ServletActionException("InvalidRequestError", "Unsupported action " + actionType));
		}
	}

}
