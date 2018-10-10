package com.mindbox.pe.client.pear;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.mindbox.pe.client.applet.PearApplet;
import com.mindbox.pe.communication.pear.HttpRequest;
import com.mindbox.pe.communication.pear.LogoutUserRequest;
import com.mindbox.pe.communication.pear.LogoutUserResponse;

public class PearFrame extends JFrame {
    private static final long serialVersionUID = 5654610715062840948L;
    private static final Logger LOG = Logger.getLogger(PearFrame.class);

    public class PearWindowListener implements WindowListener {

        private PearFrame frame;

        public PearWindowListener(PearFrame frame) {
            this.frame = frame;
        }

        @Override
        public void windowClosing(WindowEvent e) {
            LOG.debug("windowClosing() e=" + e);
            int option = JOptionPane.showConfirmDialog(getPearFrame(), "Are you sure you want to exit?  Unsaved changed may be lost.", "Confirm exit", JOptionPane.YES_NO_OPTION);
            if (0 == option) {
                try {
                    LoginInfo info = getPearFrame().getLoginInfo();
                    LogoutUserRequest request = new LogoutUserRequest(info.sessionID, info.username);
                    LogoutUserResponse response = (LogoutUserResponse) HttpRequest.post(info.pearServletUrl, request);
                    if (response.failed) {
                        LOG.fatal("Logout failure: " + response.failureReason);
                        System.exit(-1);
                    } else {
                        System.exit(0);
                    }
                } catch (Exception ex) {
                    LOG.fatal("windowClosing()", ex);
                    System.exit(-1);
                }
            }
        }

        public PearFrame getPearFrame() {
            return frame;
        }

        @Override
        public void windowActivated(WindowEvent arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void windowClosed(WindowEvent arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void windowDeactivated(WindowEvent arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void windowDeiconified(WindowEvent arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void windowIconified(WindowEvent arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void windowOpened(WindowEvent arg0) {
            // TODO Auto-generated method stub

        }
    }

    private LoginInfo info;

    private static String generateTitle(LoginInfo info) {
        StringBuffer sb = new StringBuffer(info.peInfo.clientWindowTitle);
        sb.append(" - signed in as ");
        sb.append(info.username);
        return sb.toString();
    }

    public PearFrame(LoginInfo info) throws Exception {
        super(generateTitle(info));
        this.info = info;

        LOG.debug("PearFrame() info=" + info.toString());

        GraphicsEnvironment graphics_environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] graphics_devices = graphics_environment.getScreenDevices();
        GraphicsConfiguration graphics_configuration = graphics_devices[0].getDefaultConfiguration();
        Rectangle graphics_rectangle = graphics_configuration.getBounds();
        setSize(graphics_rectangle.width - 6, graphics_rectangle.height - 58);
        getContentPane().setLayout(new GridLayout(1, 1));

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new PearWindowListener(this));

        PearAppletStub stub = new PearAppletStub(info.peUrl + '/');
        stub.setParameter("ssid", info.sessionID);
        stub.setParameter("server", info.peServletUrl);
        stub.setParameter("logoffURL", "/powereditor/logout.jsp");

        PearApplet applet = new PearApplet();
        applet.setStub(stub);
        applet.init();
        applet.start();

        getContentPane().add(applet);
    }

    private LoginInfo getLoginInfo() {
        return info;
    }
}
