package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.ClearFailedLoginCounterRequest;
import com.mindbox.pe.communication.ErrorResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.StringResponse;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.cache.SessionManager;
import com.mindbox.pe.server.model.PowerEditorSession;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletActionException;

public class ClearFailedLoginCounterRequestHandler extends AbstractActionRequestHandler<ClearFailedLoginCounterRequest> {

	public ResponseComm handleRequest(final ClearFailedLoginCounterRequest request, final HttpServletRequest httpservletrequest) throws ServerException {
		final PowerEditorSession session = SessionManager.getInstance().getSession(request.getSessionID());
		try {
			if (session != null) {
				String userID = session.getUserID();
				User actingUser = SecurityCacheManager.getInstance().getUser(userID);
				if (actingUser == null) {
					return new ErrorResponse("ServerError", "No user " + userID + " found");
				}

				BizActionCoordinator.getInstance().clearFailedLoginCounter(request.getUserID(), actingUser);

				return new StringResponse(String.format("Successfullly cleared failed login counter of the user."));
			}

			else {
				throw new ServletActionException("ServerError", "Invalid Request: Invalid session");
			}
		}
		catch (ServletActionException ex) {
			return generateErrorResponse(ex);
		}
	}


}
