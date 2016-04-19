package com.mindbox.pe.server.servlet.handlers;

import static com.mindbox.pe.common.LogUtil.logDebug;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.mindbox.pe.communication.LoginRequest;
import com.mindbox.pe.communication.LoginResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.server.audit.AuditLogger;
import com.mindbox.pe.server.cache.SessionManager;
import com.mindbox.pe.server.model.PowerEditorSession;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ResourceUtil;
import com.mindbox.pe.server.servlet.ServletActionException;

public final class LoginRequestHandler extends AbstractActionRequestHandler<LoginRequest> {

	private final Logger logger = Logger.getLogger(LoginRequestHandler.class);

	public ResponseComm handleRequest(LoginRequest loginReq, HttpServletRequest httpservletrequest) {
		try {
			LoginAttempt loginAttempt = new LoginAttempt(loginReq.getLoginUserId(), loginReq.getLoginPassword());
			logDebug(logger, "Checking login attempt %s", loginAttempt);

			if (loginAttempt.failed()) {
				logDebug(logger, "Authentication failed: %s", loginAttempt.getFailureReason());
				return LoginResponse.createFailureInstance(loginAttempt.getFailureReason());
			}

			if (loginAttempt.isPasswordChangeRequired()) {
				return LoginResponse.createPasswordNeedsResetInstance(ResourceUtil.getInstance().getResource("change.pwd.needReset"));
			}

			// login succeeded and user doesn't have to change his password, so create a session
			User user = loginAttempt.getUser();

			PowerEditorSession session = new PowerEditorSession(httpservletrequest.getSession(), user.getUserID());
			SessionManager.getInstance().registerSession(session);

			ResponseComm loginResponse = loginAttempt.isPasswordExpiryNotificationRequired() ? LoginResponse.createSuccessWithPasswordExpiryNoticeInstance(
					session.getSessionId(),
					loginAttempt.getDaysUntilPasswordExpires()) : LoginResponse.createSuccessInstance(session.getSessionId());

			AuditLogger.getInstance().logLogIn(user.getUserID());
			return loginResponse;
		}
		catch (ServletActionException ex) {
			logger.error("Failed to create session: " + ex.getResourceKey(), ex);
			return LoginResponse.createFailureInstance(ResourceUtil.getInstance().getResource(ex.getResourceKey()));
		}
		catch (Exception ex) {
			logger.error("Failed to create session", ex);
			String resourceStr = ResourceUtil.getInstance().getResource("msg.error.generic.service");
			return LoginResponse.createFailureInstance((resourceStr == null ? ex.getMessage() : MessageFormat.format(resourceStr, new Object[] { ex.getMessage() })));
		}

	}
}
