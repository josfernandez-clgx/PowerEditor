package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.EnableUserRequest;
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

public final class EnableUserRequestHandler extends AbstractActionRequestHandler<EnableUserRequest> {

	public ResponseComm handleRequest(EnableUserRequest request, HttpServletRequest httpservletrequest) throws ServerException {
		PowerEditorSession session = SessionManager.getInstance().getSession(request.getSessionID());
		try {
			if (session != null) {
				String userID = session.getUserID();
				User actingUser = SecurityCacheManager.getInstance().getUser(userID);
				if (actingUser == null) {
					return new ErrorResponse("ServerError", "No user " + userID + " found");
				}

				BizActionCoordinator.getInstance().enableUser(request.getUserID(), actingUser);

				return new StringResponse(String.format("Successfullly enabled the user."));
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