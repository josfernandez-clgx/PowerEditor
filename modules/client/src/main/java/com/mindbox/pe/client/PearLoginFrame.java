package com.mindbox.pe.client;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PearLoginFrame extends JFrame implements ActionListener, DocumentListener {

	/**
	 * Generated Serial Number
	 */
	private static final long serialVersionUID = 107149976230411451L;

	private boolean doLoginFlag = false;
	private String userID = "";
	private String password = "";
	private String peUrl = null;

	private char echoChar = '*';

	Container loginFrameContainerPane = getContentPane();

	// labels
	JLabel userLabel = new JLabel("UserID");
	JLabel passwordLabel = new JLabel("Password");

	// text fields
	JTextField userTextField = new JTextField(20);
	JPasswordField passwordTextField = new JPasswordField(20);

	// check box
	JCheckBox showPassword = new JCheckBox("Show Password");

	// buttons
	JButton loginButton = new JButton("Login");
	JButton cancelButton = new JButton("Cancel");

	/**
	 * define constructor
	 *
	 */
	public PearLoginFrame() {
		setLayoutManager();
		setPositionsAndSizes();
		addContainerComponents();
		// addFrameMenuBar();
		addActionEvents();
		addDocListeners();
	}

	public void setPeUrl(String peUrl) {
		this.peUrl = peUrl;
	}

	public void setLayoutManager() {
		loginFrameContainerPane.setLayout(null);
	}

	/**
	 * set position of components
	 */
	public void setPositionsAndSizes() {
		// labels
		userLabel.setBounds(20, 10, 80, 25);
		passwordLabel.setBounds(20, 40, 80, 25);

		// text fields
		userTextField.setBounds(110, 10, 160, 25);
		passwordTextField.setBounds(110, 40, 160, 25);
		echoChar = passwordTextField.getEchoChar();

		// check boxes
		showPassword.setBounds(110, 70, 150, 30);

		// buttons
		loginButton.setMnemonic(KeyEvent.VK_L);
		loginButton.setBounds(25, 110, 80, 25);
		loginButton.setEnabled(false);

		cancelButton.setMnemonic(KeyEvent.VK_C);
		cancelButton.setBounds(180, 110, 80, 25);
	}

	/**
	 * add components to the frame container pane
	 */
	public void addContainerComponents() {
		// labels
		loginFrameContainerPane.add(userLabel);
		loginFrameContainerPane.add(passwordLabel);

		// text fields
		loginFrameContainerPane.add(userTextField);
		loginFrameContainerPane.add(passwordTextField);

		// check boxes
		loginFrameContainerPane.add(showPassword);

		// buttons
		loginFrameContainerPane.add(loginButton);
		loginFrameContainerPane.add(cancelButton);
	}

	/**
	 * add action listeners to components
	 */
	public void addActionEvents() {
		loginButton.addActionListener(this);
		cancelButton.addActionListener(this);
		showPassword.addActionListener(this);
	}

	public void addDocListeners() {
		userTextField.getDocument().addDocumentListener(this);
		passwordTextField.getDocument().addDocumentListener(this);
	}


	public void addFrameMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu;
		JMenuItem menuItem;

		menu = new JMenu("Additional Options");
		menu.setMnemonic(KeyEvent.VK_A);

		menuBar.add(menu);

		menuItem = new JMenuItem("Check Version", KeyEvent.VK_V);
		menuItem.setActionCommand("VERSIONINFO");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Change Password...", KeyEvent.VK_P);
		menuItem.setActionCommand("PASSWORDCHAGE");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		this.setJMenuBar(menuBar);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equalsIgnoreCase("VERSIONINFO")) {
			if (peUrl == null) {
				JOptionPane.showMessageDialog(this, "** PowerEditor URL not passed **");
			}
			// showVersionInfo();
		}

		if (e.getActionCommand().equalsIgnoreCase("PASSWORDCHAGE")) {
			// JOptionPane.showMessageDialog(this, "** Change User's Password **");
			showNewChangePasswordUI();
		}

		// login button action handling
		if (e.getSource() == loginButton) {
			userID = userTextField.getText();
			password = passwordTextField.getText();
			// set the flag to trigger login
			setDoLoginFlag (true);
		}

		// cancel button action handling
		if (e.getSource() == cancelButton) {
			//exit the entire application
			System.exit(0);
		}

		// show password check box action handling
		if (e.getSource() == showPassword) {
			if (showPassword.isSelected()) {
				passwordTextField.setEchoChar((char) 0);
			}
			else {
				passwordTextField.setEchoChar(echoChar);
			}
		}

	}

	private void showNewChangePasswordUI() {
		// instantiate Change Password frame
		PearChangePasswordFrame pearChangePasswordFrame = new PearChangePasswordFrame();

		this.setAlwaysOnTop(false);

		// set login frame properties
		pearChangePasswordFrame.setTitle("Change Password");
		pearChangePasswordFrame.setAlwaysOnTop(true);
		pearChangePasswordFrame.setLocation(500, 500);
		pearChangePasswordFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pearChangePasswordFrame.setResizable(false);

		// pearLoginFrame.setPeUrl(peUrl);

		pearChangePasswordFrame.setVisible(true);
		pearChangePasswordFrame.requestFocus();

		// wait until submit or cancel is triggered
		while (!pearChangePasswordFrame.getDoSubmitFlag()) {
		}
		pearChangePasswordFrame.setAlwaysOnTop(false);
		this.setAlwaysOnTop(true);

		// shut down the change password frame
		// pearChangePasswordFrame.dispose();
		pearChangePasswordFrame.setEnabled(false);
		pearChangePasswordFrame.setVisible(false);

	}

	public void showVersionInfo() {
		try {
			URL url = new URL("http://localhost:8080/powereditor_598_PEAR/com.mindbox.pe.server.tag.GetAppVersionTag");
			URLConnection urlConn = url.openConnection();
		}
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		toggleLoginButton();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		toggleLoginButton();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		toggleLoginButton();
	}

	/**
	 * add functionality to toggle the login button on
	 * and off based on values in user and password text fields.
	 * if both have values then enable login button else disable it
	 */
	@SuppressWarnings("deprecation")
	public void toggleLoginButton() {
		if (userTextField.getText().isEmpty() || passwordTextField.getText().isEmpty()) {
			loginButton.setEnabled(false);
		}
		else {
			loginButton.setEnabled(true);
		}
	}

	public String getUserID() {
		return userID;
	}

	public String getPassword() {
		return password;
	}

	public synchronized boolean getDoLoginFlag() {
		return doLoginFlag;
	}

	public synchronized void setDoLoginFlag(boolean value) {
		doLoginFlag = value;
	}
}
