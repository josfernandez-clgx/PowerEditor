package com.mindbox.pe.client.pear;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.communication.pear.HttpRequest;
import com.mindbox.pe.communication.pear.PowerEditorInfoRequest;
import com.mindbox.pe.communication.pear.PowerEditorInfoResponse;

public class LoginFrameTest {
    
    private static final Logger LOG = Logger.getLogger(LoginFrameTest.class);

    public static void main(String[] args) throws Exception {
        LOG.info("main(): " + UtilBase.nameEqualsValueArray("args", args));

        if (3 != args.length) {
            System.err.println("Usage: peUrl peServletUrl pearServletUrl");
            System.exit(-1);
        }
      
        LoginInfo info = new LoginInfo(args[0], args[1], args[2]);
        PowerEditorInfoRequest request = new PowerEditorInfoRequest();
        PowerEditorInfoResponse response = (PowerEditorInfoResponse) HttpRequest.post(info.pearServletUrl, request);
        info.peInfo = response;
        LoginFrame frame = new LoginFrame(info);
        frame.setVisible(true);
    }

}
