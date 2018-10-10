package com.mindbox.pe.server.servlet.handlers.pear;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.mindbox.pe.communication.pear.VerifyUserRequest;
import com.mindbox.pe.communication.pear.VerifyUserResponse;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.servlet.handlers.LoginAttempt;

public class VerifyUserHandler extends Handler {

    private static final Logger LOG = Logger.getLogger(VerifyUserHandler.class);

    public static VerifyUserResponse process(VerifyUserRequest request, HttpServletRequest servletRequest) throws Exception {
        LOG.debug("process(), request=" + request.toString());

        try {
            LoginAttempt attempt = new LoginAttempt(request.username, request.password);

            if (attempt.failed()) {
                return new VerifyUserResponse(attempt.failed(), attempt.getFailureReason(), 0, false, false);
            } else {
                return new VerifyUserResponse(attempt.failed(), attempt.getFailureReason(),
                        attempt.getDaysUntilPasswordExpires(), attempt.isPasswordChangeRequired(),
                        attempt.isPasswordExpiryNotificationRequired());
            }
        } catch (Exception e) {
            LOG.error("process()", e);
            throw new ServerException(e.getMessage());
        }
    }
}
