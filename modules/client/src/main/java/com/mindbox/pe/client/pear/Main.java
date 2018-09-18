package com.mindbox.pe.client.pear;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.communication.pear.HttpRequest;
import com.mindbox.pe.communication.pear.LoginUserRequest;
import com.mindbox.pe.communication.pear.LoginUserResponse;
import com.mindbox.pe.communication.pear.PowerEditorInfoRequest;
import com.mindbox.pe.communication.pear.PowerEditorInfoResponse;

public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        LOG.debug("main() " + UtilBase.nameEqualsValueArray("args", args));
        if (5 != args.length) {
            System.err.println("Usage: peUrl peServletUrl pearServletUrl username password");
            System.exit(-1);
        }

        LoginInfo info = new LoginInfo(args[0], args[1], args[2]);
        info.username = args[3];
        String password = args[4];

        try {
            PowerEditorInfoResponse peResponse = (PowerEditorInfoResponse) HttpRequest.post(info.pearServletUrl, new PowerEditorInfoRequest());
            info.peInfo = peResponse;

            LoginUserRequest loginRequest = new LoginUserRequest(info.username, password);
            LoginUserResponse loginResponse = (LoginUserResponse) HttpRequest.post(info.pearServletUrl, loginRequest);
            LOG.error("main() response=" + loginResponse.toString());
            if (loginResponse.failed) {
                System.exit(-1);
            } else {
                info.sessionID = loginResponse.sessionID;
            }
        } catch (Exception e) {
            LOG.error("main() exception=" + e.getMessage());
        }

        PearFrame frame = new PearFrame(info);
        frame.setVisible(true);
    }
}
