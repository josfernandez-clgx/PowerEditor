package com.mindbox.pe.server.servlet.handlers.pear;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.mindbox.pe.communication.pear.SessionInfoRequest;
import com.mindbox.pe.communication.pear.SessionInfoResponse;
import com.mindbox.pe.server.cache.SessionManager;
import com.mindbox.pe.server.config.ConfigurationManager;

public class SessionInfoHandler extends Handler {

    private static final Logger LOG = Logger.getLogger(SessionInfoHandler.class);

    public static SessionInfoResponse process(SessionInfoRequest request, HttpServletRequest servletRequest) {
        LOG.debug("Process(): request=" + request.toString());

        int current = SessionManager.getInstance().countSessions();
        int maximum = ConfigurationManager.getInstance().getPowerEditorConfiguration().getServer().getSession().getMaxUserSessions().intValue();
        String map = SessionManager.getInstance().mapToString();

        SessionInfoResponse response = new SessionInfoResponse(current, maximum, map);
        LOG.debug("Process(): response=" + response.toString());
        return response;
    }
}
