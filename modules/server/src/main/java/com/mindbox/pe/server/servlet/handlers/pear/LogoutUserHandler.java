package com.mindbox.pe.server.servlet.handlers.pear;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.mindbox.pe.communication.pear.LogoutUserRequest;
import com.mindbox.pe.communication.pear.LogoutUserResponse;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.model.User;

public class LogoutUserHandler extends Handler {

    private static final Logger LOG = Logger.getLogger(LogoutUserHandler.class);

    public static LogoutUserResponse process(LogoutUserRequest request, HttpServletRequest servletRequest) throws Exception {
        LOG.info("process(), request=" + request.toString());
        try {
            User user = SecurityCacheManager.getInstance().getUser(request.username);
            BizActionCoordinator.getInstance().performLogoff(request.sessionID, user);
            return new LogoutUserResponse(false, null);
        } catch (Exception e) {
            LOG.error("process()", e);
            return new LogoutUserResponse(true, e.getMessage());
        }
    }
}
