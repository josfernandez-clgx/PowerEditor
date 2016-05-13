package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.GenericEntityResponselessActionRequest;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.SessionRequest;
import com.mindbox.pe.communication.SuccessResponse;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletActionException;

/**
 * @author Gene Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public final class GenericEntityResponselessActionRequestHandler extends
		AbstractSessionRequestHandler<GenericEntityResponselessActionRequest> {

	public ResponseComm handleRequest(GenericEntityResponselessActionRequest request, HttpServletRequest httpservletrequest) throws ServletActionException {
		User user = getUser(request.getUserID());

		int actionType = request.getActionType();
		switch (actionType) {
		case SessionRequest.ACTION_TYPE_LOCK:
			BizActionCoordinator.getInstance().lockGenericEntity(request.getEntityType(), request.getEntityID(), user);
			return new SuccessResponse();
		case SessionRequest.ACTION_TYPE_UNLOCK:
			BizActionCoordinator.getInstance().unlockGenericEntity(request.getEntityType(), request.getEntityID(), user);
			return new SuccessResponse();
		case SessionRequest.ACTION_TYPE_DELETE:
			BizActionCoordinator.getInstance().deleteGenericEntity(request.getEntityType(), request.getEntityID(), user);
			return new SuccessResponse();
		default:
			return generateErrorResponse(new ServletActionException("InvalidRequestError", "Unsupported action " + actionType));
		}
	}
}