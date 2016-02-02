package com.mindbox.pe.tools;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mindbox.pe.common.PasswordOneWayHashUtil;
import com.mindbox.pe.server.config.Password;

/** GUI for encrypting passwords to be included in PE config files. */
public class PasswordTool {
	public static void main(String[] args) {
		new PasswordTool();
	}
	
	private PasswordTool() {
		JFrame frame = new JFrame("Power Editor Password Encryption Tool");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel mainPanel = new JPanel(new GridLayout(4, 1));
		frame.getContentPane().add(mainPanel);
		
		// widgets
		final JPasswordField inputField = new JPasswordField(30);
		final JCheckBox hideCheckBox = new JCheckBox("Hide", true);
		final JTextField outputField = new JTextField(30);
		final JButton encryptButton = new JButton("Encrypt");
		final JButton resetButton = new JButton("Reset");
		
		final ButtonGroup chooseEncryptionType = new ButtonGroup();
		final JRadioButton pwdServerConfig = new JRadioButton("Password In Server Config");
		chooseEncryptionType.add(pwdServerConfig);
		final JRadioButton pwdMD5 = new JRadioButton("MD5 One Way Hash");
		chooseEncryptionType.add(pwdMD5);
		

		// password input
		JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		inputPanel.add(new JLabel("Password:           ")).setForeground(Color.BLUE);
		inputField.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent arg0) {
				encryptButton.setEnabled(inputField.getPassword().length > 0);
			}
		});
		inputPanel.add(inputField);
		hideCheckBox.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent event) {
				inputField.setEchoChar(hideCheckBox.isSelected() ? '*' : (char)0);
			}
		});
		inputPanel.add(hideCheckBox);
		mainPanel.add(inputPanel);
		
		// encryption type chooser
		JPanel encryptionTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		encryptionTypePanel.add(new JLabel("Encryption Type: ")).setForeground(Color.BLUE);
		encryptionTypePanel.add(pwdServerConfig);
		encryptionTypePanel.add(pwdMD5);
		pwdServerConfig.setSelected(true);
		mainPanel.add(encryptionTypePanel);
		
		// encrypted output
		JPanel outputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		outputPanel.add(new JLabel("Encrypted:           ")).setForeground(Color.BLUE);
		outputField.setEnabled(false);
		outputPanel.add(outputField);
		mainPanel.add(outputPanel);
		
		// action buttons
		JPanel buttonPanel = new JPanel();
		encryptButton.setEnabled(false);
		encryptButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				String clearTextPwd = new String(inputField.getPassword());
				if (pwdServerConfig.isSelected()) {
					Password pwd = Password.fromClearText(clearTextPwd);
					outputField.setText(pwd.getEncrypted());
				}
				else {
					try {
						String oneWayHashString = PasswordOneWayHashUtil.convertToOneWayHash(
								clearTextPwd,
								PasswordOneWayHashUtil.HASH_ALGORITHM_MD5);
						outputField.setText(oneWayHashString);
					}
					catch (Exception e) {
						outputField.setText(e.getMessage());
					}
				}
				outputField.setEnabled(true);
				inputField.setEnabled(false);
				encryptButton.setEnabled(false);
				resetButton.setEnabled(true);
			}
		});
		buttonPanel.add(encryptButton);
		resetButton.setEnabled(false);
		resetButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				outputField.setText(null);
				inputField.setText(null);
				outputField.setEnabled(false);
				inputField.setEnabled(true);
				resetButton.setEnabled(false);
			}
		});
		buttonPanel.add(resetButton);
		mainPanel.add(buttonPanel);
		
		// go
		frame.pack();
		frame.setVisible(true);
	}
	
}
