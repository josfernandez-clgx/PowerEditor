package com.mindbox.pe.client.pear;

import java.awt.Color;
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
import com.mindbox.pe.communication.pear.ChangePasswordRequest;
import com.mindbox.pe.communication.pear.ChangePasswordResponse;
import com.mindbox.pe.communication.pear.HttpRequest;

public class ChangePasswordFrame extends JFrame {
    private static final long serialVersionUID = 1899564110866757485L;
    private static final Logger LOG = Logger.getLogger(ChangePasswordFrame.class);

    public class ChangePasswordActionListener implements ActionListener {

        private ChangePasswordFrame frame;

        public ChangePasswordActionListener(ChangePasswordFrame frame) {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String message = "actionPerformed() not implemented";
            LOG.error(message);
        }

        public ChangePasswordFrame getChangePasswordFrame() {
            return frame;
        }
    }

    public class LoginWorker extends SwingWorker<Void, Void> {

        private ChangePasswordFrame changeFrame;
        private LoginInfo info;

        public LoginWorker(ChangePasswordFrame changeFrame, LoginInfo info) {
            this.changeFrame = changeFrame;
            this.info = info;
        }

        @Override
        public Void doInBackground() throws Exception {
            LoginFrame loginFrame = new LoginFrame(getInfo());
            changeFrame.setVisible(false);
            loginFrame.setVisible(true);
            changeFrame.dispose();
            return null;
        }

        @Override
        public void done() {
        }

        public ChangePasswordFrame getFrame() {
            return changeFrame;
        }

        public LoginInfo getInfo() {
            return info;
        }
    }

    private LoginInfo info;
    private JTextField usernameField;
    private JPasswordField newPasswordField;
    private JPasswordField oldPasswordField;
    private JPasswordField verifyPasswordField;

    public ChangePasswordFrame(LoginInfo info) {
        super(info.peInfo.clientWindowTitle);
        this.info = info;

        LOG.debug("ChangePasswordFrame() info=" + info.toString());

        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(1, 1, 1, 1);
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;

        JLabel usernameLabel = new JLabel("Username:", SwingConstants.RIGHT);
        usernameField = new JTextField(25);
        JLabel oldPasswordLabel = new JLabel("Old password:", SwingConstants.RIGHT);
        oldPasswordField = new JPasswordField(25);
        JLabel newPasswordLabel = new JLabel("New password:", SwingConstants.RIGHT);
        newPasswordField = new JPasswordField(25);
        JLabel verifyPasswordLabel = new JLabel("Verify password:", SwingConstants.RIGHT);
        verifyPasswordField = new JPasswordField(25);

        JPanel changePanel = new JPanel();
        changePanel.setLayout(new GridBagLayout());

        gbc.gridy = 0; gbc.gridx = 0; changePanel.add(usernameLabel, gbc);
        gbc.gridy = 0; gbc.gridx = 1; changePanel.add(usernameField, gbc);

        gbc.gridy = 1; gbc.gridx = 0; changePanel.add(oldPasswordLabel, gbc);
        gbc.gridy = 1; gbc.gridx = 1; changePanel.add(oldPasswordField, gbc);

        gbc.gridy = 2; gbc.gridx = 0; changePanel.add(newPasswordLabel, gbc);
        gbc.gridy = 2; gbc.gridx = 1; changePanel.add(newPasswordField, gbc);

        gbc.gridy = 3; gbc.gridx = 0; changePanel.add(verifyPasswordLabel, gbc);
        gbc.gridy = 3; gbc.gridx = 1; changePanel.add(verifyPasswordField, gbc);

        // Create and populate button panel

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());

        JButton changeButton = new JButton("Change password");
        JButton cancelButton = new JButton("Cancel");

        gbc.gridy = 0; gbc.gridx = 0; buttonPanel.add(changeButton, gbc);
        gbc.gridy = 0; gbc.gridx = 1; buttonPanel.add(cancelButton, gbc);

        // Add panels to the ChangePasswordFrame

        setLayout(new GridBagLayout());
        gbc.fill = GridBagConstraints.NONE;

        JLabel mindboxIcon = new JLabel(info.peInfo.mindboxIcon);
        JLabel changeLabel = new JLabel("Change Password", SwingConstants.CENTER);
        changeLabel.setForeground(Color.BLUE);
        UtilBase.resizeLabel(changeLabel, 2.0);
        JLabel requirementsLabel = new JLabel(UtilBase.addHtmlCenterTags(info.peInfo.passwordRequirements), SwingConstants.CENTER);
        UtilBase.resizeLabel(requirementsLabel, 0.75);

        gbc.gridy = 0; gbc.gridx = 0; add(mindboxIcon, gbc);
        gbc.gridy = 1; gbc.gridx = 0; add(changeLabel, gbc);
        gbc.gridy = 2; gbc.gridx = 0; add(new JPanel(), gbc); // Empty row
        gbc.gridy = 3; gbc.gridx = 0; add(requirementsLabel, gbc);
        gbc.gridy = 4; gbc.gridx = 0; add(new JPanel(), gbc); // Empty row
        gbc.gridy = 5; gbc.gridx = 0; add(changePanel, gbc);
        gbc.gridy = 6; gbc.gridx = 0; add(buttonPanel, gbc);
        gbc.gridy = 7; gbc.gridx = 0; add(new JPanel(), gbc); // Empty row

        JLabel copyrightLabel = new JLabel("Copyright (c) 2011-2018, CoreLogic Inc.  All rights reserved.");
        UtilBase.resizeLabel(copyrightLabel, 0.75);
        gbc.gridy = 8; gbc.gridx = 0; add(copyrightLabel, gbc);

        JLabel versionLabel = new JLabel(info.peInfo.version);
        UtilBase.resizeLabel(versionLabel, 0.75);
        gbc.gridy = 9; gbc.gridx = 0; add(versionLabel, gbc);

        // Set shortcut keys

        usernameLabel.setDisplayedMnemonic(KeyEvent.VK_U);
        usernameLabel.setLabelFor(usernameField);

        oldPasswordLabel.setDisplayedMnemonic(KeyEvent.VK_O);
        oldPasswordLabel.setLabelFor(oldPasswordField);

        newPasswordLabel.setDisplayedMnemonic(KeyEvent.VK_N);
        newPasswordLabel.setLabelFor(newPasswordField);

        verifyPasswordLabel.setDisplayedMnemonic(KeyEvent.VK_V);
        verifyPasswordLabel.setLabelFor(verifyPasswordField);

        changeButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.setMnemonic(KeyEvent.VK_A);

        // Set action listeners

        newPasswordField.addActionListener(new ChangePasswordActionListener(this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                getChangePasswordFrame().getVerifyPasswordField().requestFocus();
            }
        });

        verifyPasswordField.addActionListener(new ChangePasswordActionListener(this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                getChangePasswordFrame().doChange();
            }
        });

        changeButton.addActionListener(new ChangePasswordActionListener(this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                getChangePasswordFrame().doChange();
            }
        });

        cancelButton.addActionListener(new ChangePasswordActionListener(this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChangePasswordFrame frame = getChangePasswordFrame();
                LoginWorker worker = new LoginWorker(frame, frame.getInfo());
                worker.execute();
            }
        });

        // Set focus

        pack();
        usernameField.requestFocus();
    }

    public void doChange() {
        try {
            info.username = usernameField.getText();

            char[] oldPasswordChars = oldPasswordField.getPassword();
            if (0 == oldPasswordChars.length) {
                JOptionPane.showMessageDialog(this, "Old password cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            char[] newPasswordChars = newPasswordField.getPassword();
            if (0 == newPasswordChars.length) {
                JOptionPane.showMessageDialog(this, "New password cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            char[] verifyPasswordChars = verifyPasswordField.getPassword();
            if (0 == verifyPasswordChars.length) {
                JOptionPane.showMessageDialog(this, "Verify password cannot be empty", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String oldPassword = new String(oldPasswordChars);
            String newPassword = new String(newPasswordChars);
            String verifyPassword = new String(verifyPasswordChars);
            if (0 != newPassword.compareTo(verifyPassword)) {
                JOptionPane.showMessageDialog(this, "New and verify passwords do not match", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            ChangePasswordRequest request = new ChangePasswordRequest(info.username, oldPassword, newPassword);
            ChangePasswordResponse response = (ChangePasswordResponse) HttpRequest.post(info.pearServletUrl, request);
            if (!response.success) {
                JOptionPane.showMessageDialog(this, response.message, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LoginWorker worker = new LoginWorker(this, info);
            worker.execute();
        } catch (Exception e) {
            LOG.error("doChange()", e);
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public LoginInfo getInfo() {
        return info;
    }

    public JPasswordField getNewPasswordField() {
        return newPasswordField;
    }

    public JPasswordField getVerifyPasswordField() {
        return verifyPasswordField;
    }
}
