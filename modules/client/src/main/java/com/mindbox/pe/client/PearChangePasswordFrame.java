package com.mindbox.pe.client;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PearChangePasswordFrame extends JFrame implements ActionListener, DocumentListener {

	/**
	 * Generated Serial Number
	 */
	private static final long serialVersionUID = 5433951919087000439L;

	private boolean doSubmitFlag = false;
	private String userID = "";
	private String oldPassword = "";
	private String newPassword = "";
	private String confirmPassword = "";
	private String peUrl = null;

	private char echoChar = '*';

	Container cpFrameContainerPane = getContentPane();

	// labels
	JLabel userLabel = new JLabel("UserID:");
	JLabel oldPasswordLabel = new JLabel("Old Password:");
	JLabel newPasswordLabel = new JLabel("New Password:");
	JLabel confirmPasswordLabel = new JLabel("Confirm New Password:");

	// text fields
	JTextField userTextField = new JTextField(20);
	JPasswordField oldPasswordTextField = new JPasswordField(20);
	JPasswordField newPasswordTextField = new JPasswordField(20);
	JPasswordField confirmPasswordTextField = new JPasswordField(20);

	// check box
	JCheckBox showPassword = new JCheckBox("Show Passwords");
	JToggleButton showOldPassword = new JToggleButton();
	JToggleButton showNewPassword = new JToggleButton();
	JToggleButton showConfirmPassword = new JToggleButton();


	// buttons
	JButton submitButton = new JButton("Submit");
	JButton cancelButton = new JButton("Cancel");

	/**
	 * define constructor
	 * 
	 */
	public PearChangePasswordFrame() {
		setLayoutManager();
		setPositionsAndSizes();
		addContainerComponents();
		addActionEvents();
		addDocListeners();
	}

	public void setPeUrl(String peUrl) {
		this.peUrl = peUrl;
	}

	public void setLayoutManager() {
		cpFrameContainerPane.setLayout(null);
	}

	/**
	 * set position of components
	 */
	public void setPositionsAndSizes() {
		// Frame
		this.setBounds(10, 10, 350, 260);

		// labels
		userLabel.setBounds(10, 10, 80, 25);
		oldPasswordLabel.setBounds(10, 40, 90, 25);
		newPasswordLabel.setBounds(10, 70, 120, 25);
		confirmPasswordLabel.setBounds(10, 100, 140, 25);

		// text fields
		userTextField.setBounds(160, 10, 160, 25);
		oldPasswordTextField.setBounds(160, 40, 160, 25);
		newPasswordTextField.setBounds(160, 70, 160, 25);
		confirmPasswordTextField.setBounds(160, 100, 160, 25);

		echoChar = oldPasswordTextField.getEchoChar();

		// check boxes
		showPassword.setBounds(110, 130, 150, 30);
		showOldPassword.setBounds(322, 45, 15, 15);
		showNewPassword.setBounds(322, 75, 15, 15);
		showConfirmPassword.setBounds(322, 105, 15, 15);

		// buttons
		submitButton.setMnemonic(KeyEvent.VK_S);
		submitButton.setBounds(45, 175, 80, 25);
		submitButton.setEnabled(false);

		cancelButton.setMnemonic(KeyEvent.VK_C);
		cancelButton.setBounds(200, 175, 80, 25);
	}

	/**
	 * add components to the frame container pane
	 */
	public void addContainerComponents() {
		// labels
		cpFrameContainerPane.add(userLabel);
		cpFrameContainerPane.add(oldPasswordLabel);
		cpFrameContainerPane.add(newPasswordLabel);
		cpFrameContainerPane.add(confirmPasswordLabel);

		// text fields
		cpFrameContainerPane.add(userTextField);
		cpFrameContainerPane.add(oldPasswordTextField);
		cpFrameContainerPane.add(newPasswordTextField);
		cpFrameContainerPane.add(confirmPasswordTextField);

		// check boxes
		cpFrameContainerPane.add(showPassword);

		// frameContainerPane.add(showOldPassword);
		// frameContainerPane.add(showNewPassword);
		// frameContainerPane.add(showConfirmPassword);

		// buttons
		cpFrameContainerPane.add(submitButton);
		cpFrameContainerPane.add(cancelButton);
	}

	/**
	 * add action listeners to components
	 */
	public void addActionEvents() {
		submitButton.addActionListener(this);
		cancelButton.addActionListener(this);
		showPassword.addActionListener(this);
		showOldPassword.addActionListener(this);
	}

	public void addDocListeners() {
		userTextField.getDocument().addDocumentListener(this);
		oldPasswordTextField.getDocument().addDocumentListener(this);
		newPasswordTextField.getDocument().addDocumentListener(this);
		confirmPasswordTextField.getDocument().addDocumentListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent e) {

		// submit button action handling
		if (e.getSource() == submitButton) {
			boolean allGood = true;
			String failedMsg = "";

			userID = userTextField.getText();
			oldPassword = oldPasswordTextField.getText();
			newPassword = newPasswordTextField.getText();
			confirmPassword = confirmPasswordTextField.getText();

			if (oldPassword.equals(newPassword)) {
				allGood = false;
				failedMsg = "New password cannot be the same as old password. Please retype";
				newPasswordTextField.setText("");
				confirmPasswordTextField.setText("");
			}
			else if (!newPassword.equals(confirmPassword)) {
				allGood = false;
				failedMsg = "New passwords don't match. Please retype";
				newPasswordTextField.setText("");
				confirmPasswordTextField.setText("");
			}

			if (allGood) {
				// set the flag to trigger submission
				doSubmitFlag = true;
			}
			else {
				userID = "";
				oldPassword = "";
				newPassword = "";
				confirmPassword = "";
				JOptionPane.showMessageDialog(this, "" + failedMsg);
			}

		}

		// cancel button action handling
		if (e.getSource() == cancelButton) {
			//exit the entire application
			// System.exit(0);

			//TODO : change this to logic for exiting more gracefully
			doSubmitFlag = true;
		}

		// show password check box action handling
		if (e.getSource() == showPassword) {
			if (showPassword.isSelected()) {
				oldPasswordTextField.setEchoChar((char) 0);
				newPasswordTextField.setEchoChar((char) 0);
				confirmPasswordTextField.setEchoChar((char) 0);
			}
			else {
				oldPasswordTextField.setEchoChar(echoChar);
				newPasswordTextField.setEchoChar(echoChar);
				confirmPasswordTextField.setEchoChar(echoChar);
			}
		}

	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		toggleSubmitButton();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		toggleSubmitButton();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		toggleSubmitButton();
	}

	/**
	 * add functionality to toggle the submit button on 
	 * and off based on values in user and password text fields.
	 * if all have values then enable login button else disable it
	 */
	@SuppressWarnings("deprecation")
	public void toggleSubmitButton() {
		if (userTextField.getText().isEmpty() || oldPasswordTextField.getText().isEmpty() || newPasswordTextField.getText().isEmpty()
				|| confirmPasswordTextField.getText().isEmpty()) {
			submitButton.setEnabled(false);
		}
		else {
			submitButton.setEnabled(true);
		}
	}

	public String getUserID() {
		return userID;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public String getConfirmNewPassword() {
		return confirmPassword;
	}

	public boolean getDoSubmitFlag() {
		return doSubmitFlag;
	}

}

