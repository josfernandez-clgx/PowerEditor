package com.mindbox.pe.server.servlet.handlers;

import static com.mindbox.pe.common.LogUtil.logInfo;
import static com.mindbox.pe.common.LogUtil.logWarn;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.communication.ErrorResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.SessionRequest;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.cache.SessionManager;

/**
 * use template pattern
 * 
 * 1. final method "handleRequest", does cast of requestcomm arg to expected type and handles authentication/authorization
 * 2. add to subclasses, doHandleRequest and getPrivilegeString
 * @author davies
 *
 */
public abstract class AbstractSessionRequestHandler<T extends SessionRequest<?>> extends AbstractActionRequestHandler<T> {

	@Override
	public ResponseComm serviceRequest(T request, HttpServletRequest httpServletRequest) {
		// Authenticate session
		ErrorResponse errorResponse = null;
		errorResponse = authenticate(request, httpServletRequest);
		if (errorResponse != null) {
			return errorResponse;
		}

		// Authorize if applicable
		errorResponse = authorize(request);
		if (errorResponse != null) {
			return errorResponse;
		}

		return super.serviceRequest(request, httpServletRequest);
	}

	/** 
	 * Override to return a valid privilege string.
	 * This returns <code>null</code>.
	 */
	protected String getRequiredPrivilegeName(T request) {
		return null;
	}

	protected final boolean isAuthorized(T request, String privilegeName) throws ServerException {
		return SecurityCacheManager.getInstance().authorize(request.getUserID(), privilegeName);
	}

	private ErrorResponse authenticate(T request, HttpServletRequest httpServletRequest) {
		try {
			if (!SessionManager.getInstance().hasSession(request.getSessionID()) && !httpServletRequest.isRequestedSessionIdValid()) {
				logWarn(logger, "HttpSession has expired; terminating PE session...");

				SessionManager.getInstance().terminateSession(httpServletRequest.getSession().getId());

				return new ErrorResponse(ErrorResponse.AUTHENTICATION_FAILURE, "AuthenticationFailureMsg", null);
			}
			else if (!SessionManager.getInstance().authenticateSession(request.getUserID(), request.getSessionID())) {
				logWarn(logger, "Could not authenticate [%s] for session [%s]", request.getUserID(), request.getSessionID());

				return new ErrorResponse(ErrorResponse.AUTHENTICATION_FAILURE, "AuthenticationFailureMsg", null);
			}
			else {
				return null;
			}
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return new ErrorResponse(
					ErrorResponse.SERVER_ERROR,
					"msg.error.generic.service",
					new Object[] { "Failed to authenticate session: " + ex.getMessage() });
		}
	}

	private ErrorResponse authorize(T request) {
		String privilegeName = getRequiredPrivilegeName(request);
		if (UtilBase.isEmpty(privilegeName)) return null;
		try {
			if (!isAuthorized(request, privilegeName)) {
				logInfo(logger, "User %s is not authorized for [%s]", request.getUserID(), privilegeName);
				return generateAuthorizationFailureResponse();
			}
			return null;
		}
		catch (ServerException e) {
			return new ErrorResponse(ErrorResponse.SERVER_ERROR, e.getMessage());
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return new ErrorResponse(
					ErrorResponse.SERVER_ERROR,
					"msg.error.generic.service",
					new Object[] { "Failed to authorize session: " + ex.getMessage() });
		}
	}

}