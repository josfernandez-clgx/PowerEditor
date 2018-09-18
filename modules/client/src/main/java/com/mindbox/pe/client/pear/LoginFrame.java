package com.mindbox.pe.client.pear;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.communication.pear.HttpRequest;
import com.mindbox.pe.communication.pear.LoginUserRequest;
import com.mindbox.pe.communication.pear.LoginUserResponse;
import com.mindbox.pe.communication.pear.VerifyUserRequest;
import com.mindbox.pe.communication.pear.VerifyUserResponse;

public class LoginFrame extends JFrame {
    private static final long serialVersionUID = -6834538190485035692L;
    private static final Logger LOG = Logger.getLogger(LoginFrame.class);

    public class LoginActionListener implements ActionListener {

        private LoginFrame loginFrame;

        public LoginActionListener(LoginFrame loginFrame) {
            this.loginFrame = loginFrame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String message = "actionPerformed() not implemented";
            LOG.error(message);
        }

        public LoginFrame getLoginFrame() {
            return loginFrame;
        }

    }

    public class ChangePasswordWorker extends SwingWorker<Void, Void> {

        private LoginFrame loginFrame;
        private LoginInfo loginInfo;

        public ChangePasswordWorker(LoginFrame frame, LoginInfo info) {
            LOG.debug("ChangePasswordWorker()");
            this.loginFrame = frame;
            this.loginInfo = info;
        }

        @Override
        public Void doInBackground() {
            LOG.debug("doInBackground()");
            ChangePasswordFrame changePasswordFrame = new ChangePasswordFrame(info);
            loginFrame.setVisible(false);
            changePasswordFrame.setVisible(true);
            loginFrame.dispose();
            return null;
        }

        @Override
        public void done() {
        }

        public LoginFrame getLoginFrame() {
            return loginFrame;
        }

        public LoginInfo getLoginInfo() {
            return loginInfo;
        }
    }

    public class PearWorker extends SwingWorker<Void, Void> {

        private LoginFrame loginFrame;
        private LoginInfo loginInfo;
        private String password;

        public PearWorker(LoginFrame frame, LoginInfo info, String password) {
            this.loginFrame = frame;
            this.loginInfo = info;
            this.password = password;
        }

        @Override
        public Void doInBackground() throws Exception {
            PearLoadingFrame pearLoadingFrame = new PearLoadingFrame(getLoginInfo().peInfo.clientWindowTitle);
            pearLoadingFrame.setVisible(true);
            loginFrame.setVisible(false);
            loginFrame.dispose();

            LoginInfo info = getLoginInfo();
            String password = getPassword();
            LoginUserRequest request = new LoginUserRequest(info.username, password);
            LoginUserResponse response = (LoginUserResponse) HttpRequest.post(info.pearServletUrl, request);
            if (response.failed) {
                String message = "runPearWorker() LoginUser request failed, \"" + response.failureReason + '"';
                LOG.error(message);
                System.exit(-1);
            }
            info.sessionID = response.sessionID;
            PearFrame pearFrame = new PearFrame(info);
            pearFrame.setVisible(true);
            pearLoadingFrame.setVisible(false);
            pearLoadingFrame.dispose();
            return null;
        }

        @Override
        public void done() {
        }

        public LoginFrame getLoginFrame() {
            return loginFrame;
        }

        public LoginInfo getLoginInfo() {
            return loginInfo;
        }

        public String getPassword() {
            return password;
        }
    }

    private JPasswordField passwordField;
    private JTextField usernameField;

    private LoginInfo info;

    public LoginFrame(LoginInfo info) throws Exception {
        super(info.peInfo.clientWindowTitle);
        this.info = info;

        LOG.debug("LoginFrame() info=" + info.toString());

        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel usernameLabel = new JLabel("Username:", SwingConstants.RIGHT);
        usernameField = new JTextField(25);
        JLabel passwordLabel = new JLabel("Password:", SwingConstants.RIGHT);
        passwordField = new JPasswordField(25);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(1, 1, 1, 1);
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        gbc.gridy = 2; gbc.gridx = 0; loginPanel.add(new JPanel(), gbc); // Empty row
        gbc.gridy = 3; gbc.gridx = 0; loginPanel.add(usernameLabel, gbc);
        gbc.gridy = 3; gbc.gridx = 1; loginPanel.add(usernameField, gbc);
        gbc.gridy = 4; gbc.gridx = 0; loginPanel.add(passwordLabel, gbc);
        gbc.gridy = 4; gbc.gridx = 1; loginPanel.add(passwordField, gbc);
        gbc.gridy = 5; gbc.gridx = 0; loginPanel.add(new JPanel(), gbc); // Empty row

        JButton loginButton = new JButton("Login");
        JButton changePasswordButton = new JButton("Change password");

        Dimension buttonDimension = new Dimension(150, 25);
        loginButton.setPreferredSize(buttonDimension);
        changePasswordButton.setPreferredSize(buttonDimension);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0; gbc.gridx = 0; buttonPanel.add(loginButton, gbc);
        gbc.gridy = 0; gbc.gridx = 1; buttonPanel.add(changePasswordButton, gbc);

        JLabel mindboxIcon = new JLabel(info.peInfo.mindboxIcon);
        JLabel signinLabel = new JLabel("PowerEditor Sign In", SwingConstants.CENTER);
        signinLabel.setForeground(Color.BLUE);
        UtilBase.resizeLabel(signinLabel, 2.0);

        this.setLayout(new GridBagLayout());
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 0; gbc.gridx = 0; add(mindboxIcon, gbc);
        gbc.gridy = 1; gbc.gridx = 0; add(signinLabel, gbc);
        gbc.gridy = 2; gbc.gridx = 0; add(new JPanel(), gbc); // Empty row
        gbc.gridy = 3; gbc.gridx = 0; add(loginPanel, gbc);
        gbc.gridy = 4; gbc.gridx = 0; add(buttonPanel, gbc);
        gbc.gridy = 5; gbc.gridx = 0; add(new JPanel(), gbc); // Empty row

        JLabel warningLabel = new JLabel(UtilBase.addHtmlCenterTags(info.peInfo.accessWarning), SwingConstants.CENTER);
        UtilBase.resizeLabel(warningLabel, 0.75);

        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 2; add(warningLabel, gbc);
        gbc.gridy = 7; gbc.gridx = 0; gbc.gridwidth = 1; add(new JPanel(), gbc); // Empty row

        JLabel copyrightLabel = new JLabel("Copyright (c) 2011-2018, CoreLogic Inc.  All rights reserved.");
        UtilBase.resizeLabel(copyrightLabel, 0.75);
        gbc.gridy = 8; gbc.gridx = 0; add(copyrightLabel, gbc);

        JLabel versionLabel = new JLabel(info.peInfo.version);
        UtilBase.resizeLabel(versionLabel, 0.75);
        gbc.gridy = 9; gbc.gridx = 0; add(versionLabel, gbc);

        usernameField.requestFocus();

        usernameLabel.setDisplayedMnemonic(KeyEvent.VK_U);
        usernameLabel.setLabelFor(usernameField);

        passwordLabel.setDisplayedMnemonic(KeyEvent.VK_P);
        passwordLabel.setLabelFor(passwordField);

        loginButton.setMnemonic(KeyEvent.VK_L);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        changePasswordButton.setMnemonic(KeyEvent.VK_C);
        changePasswordButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField.addActionListener(new LoginActionListener(this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                getLoginFrame().getPasswordField().requestFocus();
            }
        });

        passwordField.addActionListener(new LoginActionListener(this) {
            @Override
            public void actionPerformed(ActionEvent ev) {
                getLoginFrame().doVerify();
            }
        });

        loginButton.addActionListener(new LoginActionListener(this) {
            @Override
            public void actionPerformed(ActionEvent ev) {
                getLoginFrame().doVerify();
            }
        });

        changePasswordButton.addActionListener(new LoginActionListener(this) {
            @Override
            public void actionPerformed(ActionEvent ev) {
                runChangePasswordWorker();
            }
        });

        pack();
    }

    private void doVerify() {
        try {
            info.username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            LOG.debug(String.format("doVerify() username=\"" + info.username + "\", password=\"" + password + '"'));

            VerifyUserRequest request = new VerifyUserRequest(info.username, password);
            VerifyUserResponse response = (VerifyUserResponse) HttpRequest.post(info.pearServletUrl, request);
            if (response.failed) {
                throw new Exception(response.failureReason);
            }
            if (!response.passwordChangeRequired && !response.passwordExpirationNotificationRequired) {
                runPearWorker(password);
            } else if (response.passwordChangeRequired) {
                runChangePasswordWorker();
            } else {
                String message = String.format("Your password will expire in %d days", response.daysUntilPasswordExpires);
                Object choices[] = { "Yes, change password now", "No, continue with login" };
                int selection = JOptionPane.showOptionDialog(this, message, "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
                if (0 == selection) {
                    runChangePasswordWorker();
                } else {
                    runPearWorker(password);
                }
            }
        } catch (Exception ex) {
            String message = ex.getMessage();
            LOG.error(message);
            JOptionPane.showMessageDialog(null, message, "Login error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JPasswordField getPasswordField(){
        return passwordField;
    }

    public JTextField getUsernameField(){
        return usernameField;
    }

    private void runChangePasswordWorker() {
        ChangePasswordWorker worker = new ChangePasswordWorker(this, info);
        worker.execute();
    }

    private void runPearWorker(String password) {
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        PearWorker worker = new PearWorker(this, info, password);
        worker.execute ();
    }
}
