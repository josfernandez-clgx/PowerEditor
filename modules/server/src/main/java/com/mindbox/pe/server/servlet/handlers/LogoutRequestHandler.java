package com.mindbox.pe.server.servlet.handlers;

import static com.mindbox.pe.common.UtilBase.isEmptyAfterTrim;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.LogoutRequest;
import com.mindbox.pe.communication.RedirectResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.SuccessResponse;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.model.User;

public final class LogoutRequestHandler extends AbstractActionRequestHandler<LogoutRequest> {

	public ResponseComm handleRequest(final LogoutRequest logoutrequestcomm, final HttpServletRequest request) {
		final String userId = logoutrequestcomm.getUserID();
		final User user = getUser(userId);

		BizActionCoordinator.getInstance().performLogoff(request.getSession().getId(), user);

		final String logoutUrl = ConfigurationManager.getInstance().getLogoutUrlToUse();
		if (!isEmptyAfterTrim(logoutUrl)) {
			return new RedirectResponse(logoutUrl);
		}
		else {
			return new SuccessResponse();
		}
	}

}