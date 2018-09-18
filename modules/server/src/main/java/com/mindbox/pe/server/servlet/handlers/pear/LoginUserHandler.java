package com.mindbox.pe.server.servlet.handlers.pear;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.mindbox.pe.communication.pear.LoginUserRequest;
import com.mindbox.pe.communication.pear.LoginUserResponse;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.cache.SessionManager;
import com.mindbox.pe.server.model.PowerEditorSession;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.handlers.LoginAttempt;

public class LoginUserHandler extends Handler {

    private static final Logger LOG = Logger.getLogger(LoginUserHandler.class);

    public static LoginUserResponse process(LoginUserRequest request, HttpServletRequest servletRequest) throws Exception {
        LOG.info("process(): request=" + request.toString());
        try {
            LoginAttempt attempt = new LoginAttempt(request.getUsername(), request.getPassword());
            if (attempt.failed()) {
                return new LoginUserResponse(true, attempt.getFailureReason(), 0, false, false, null);
            }
            User user = attempt.getUser();
            PowerEditorSession session = new PowerEditorSession(servletRequest.getSession(), user.getUserID());
            SessionManager.getInstance().registerSession(session);
            LoginUserResponse response = new LoginUserResponse(false, attempt.getFailureReason(), attempt.getDaysUntilPasswordExpires(), attempt.isPasswordChangeRequired(), attempt.isPasswordExpiryNotificationRequired(), session.getSessionId());
            LOG.info("process(): response=" + response.toString());
            return response;
        } catch (Exception e) {
            LOG.error("process()", e);
            throw new ServerException(e.getMessage());
        }
    }
}
