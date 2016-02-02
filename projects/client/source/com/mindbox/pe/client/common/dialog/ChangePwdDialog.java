/*
 * Created on Jun 10, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.client.common.dialog;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.model.admin.UserData;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public class ChangePwdDialog extends JPanel {

	/**
	 * Displays the Change password dialog for the specified user.
	 * @param frameBase the root frame
	 * @param user the user object
	 * @return <code>true</code> if password was changed successfully; <code>false</code>, otherwise
	 */
	public static boolean changePassword(UserData user) {

		ChangePwdDialog dialog = null;
		dialog = new ChangePwdDialog(user);
		dialog.currPwdField.requestFocus();
		int option = JOptionPane.showConfirmDialog(ClientUtil.getApplet(), dialog, "Change Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (option == JOptionPane.CANCEL_OPTION) {
			return false;
		}
		else {
			if (user.getCurrentPassword().equals(new String(dialog.currPwdField.getPassword()))) {
				ClientUtil.getInstance().showWarning("msg.warning.invalid.password");
				return false;
			}
			else {
				return true;
			}
		}
	}

	private final JPasswordField currPwdField, newPwdField;

	private ChangePwdDialog(UserData user) {
		super();
		currPwdField = UIFactory.createPasswordField();
		newPwdField = UIFactory.createPasswordField();
		layoutComponents(user.getUserID());
	}

	private void layoutComponents(String userID) {
		JPanel bPanel = UIFactory.createJPanel(new GridLayout(3, 2, 4, 4));
		bPanel.add(new JLabel("User ID:"));
		bPanel.add(new JLabel(userID));

		bPanel.add(new JLabel("Current Password:"));
		bPanel.add(currPwdField);

		bPanel.add(new JLabel("New Password:"));
		bPanel.add(newPwdField);

		setLayout(new BorderLayout(4, 4));
		add(bPanel, BorderLayout.CENTER);
	}

}