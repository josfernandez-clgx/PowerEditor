package com.mindbox.pe.client.pear;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.communication.pear.HttpRequest;
import com.mindbox.pe.communication.pear.PowerEditorInfoRequest;
import com.mindbox.pe.communication.pear.PowerEditorInfoResponse;

public class MainGUI {
    private static final Logger LOG = Logger.getLogger(MainGUI.class);

    public static void main(String[] args) throws Exception {
        LOG.debug("main() " + UtilBase.nameEqualsValueArray("args", args));

        if (3 != args.length) {
            System.err.println("Usage: peUrl peServletUrl pearServletUrl");
            System.exit(-1);
        }

        LoginInfo info = new LoginInfo(args[0], args[1], args[2]);

        try {
            PowerEditorInfoResponse response = (PowerEditorInfoResponse) HttpRequest.post(info.pearServletUrl, new PowerEditorInfoRequest());
            info.peInfo = response;
            LoginFrame frame = new LoginFrame(info);
            frame.setVisible(true);
        } catch (Exception e) {
            String message = e.getMessage();
            LOG.fatal(message);
            JOptionPane.showMessageDialog(null, message, "Connection error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
