package com.mindbox.pe.server.servlet.handlers;

import static com.mindbox.pe.common.UtilBase.isEmptyAfterTrim;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.LogoutRequest;
import com.mindbox.pe.communication.RedirectResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.SuccessResponse;
import com.mindbox.pe.server.audit.AuditLogger;
import com.mindbox.pe.server.cache.LockManager;
import com.mindbox.pe.server.cache.SessionManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.model.User;

public final class LogoutRequestHandler extends AbstractActionRequestHandler<LogoutRequest> {

	public ResponseComm handleRequest(LogoutRequest logoutrequestcomm, HttpServletRequest request) {
		String s = logoutrequestcomm.getUserID();
		User user = getUser(s);

		SessionManager sessionmanager = SessionManager.getInstance();
		sessionmanager.terminateSession(request.getSession().getId());

		LockManager.getInstance().unlockAll(user);

		AuditLogger.getInstance().logLogOff(user.getUserID());

		String logoutUrl = ConfigurationManager.getInstance().getSessionConfiguration().getLogoutUrlToUse();
		if (!isEmptyAfterTrim(logoutUrl)) {
			return new RedirectResponse(logoutUrl);
		}
		else {
			return new SuccessResponse();
		}
	}

}