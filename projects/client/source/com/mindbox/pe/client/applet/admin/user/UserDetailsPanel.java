package com.mindbox.pe.client.applet.admin.user;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.IClientConstants;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.CheckList;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.tab.PowerEditorTabPanel;
import com.mindbox.pe.common.DateUtil;
import com.mindbox.pe.common.PasswordOneWayHashUtil;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.admin.UserData;
import com.mindbox.pe.model.admin.UserPassword;
import com.mindbox.pe.model.exceptions.CanceledException;

public final class UserDetailsPanel extends PanelBase implements IClientConstants, PowerEditorTabPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3587038626188307428L;
	
	protected static String ID_FORM_LBL = "label.userid";
	protected static String PASSWORD_FORM_LBL = "label.password";
	protected static String NAME_FORM_LBL = "label.name";
	protected static String STATUS_FORM_LBL = "label.status";
	private static String ROLES_FORM_LBL = "label.roles";
	protected static String DETAILS_TITLE_LBL = "label.title.user.details";
	protected static String DAYS_SINCE_PWD_CHANGE_LBL = "label.title.days.since.pwd.change";
	protected static String FAILED_LOGIN_ATTEMPTS_LBL = "label.title.user.failed.login.attempts";
	
	class UserDetailsAdapter extends AbstractThreadedActionAdapter {
		public void performAction(java.awt.event.ActionEvent actionevent) {
			Object obj = actionevent.getSource();
			if (obj == saveUserButton) {
				if (mUser != null && isDirty())
					saveDisplayUser();
				else
					ClientUtil.printInfo("Save not required!");
			}
			else {
				ClientUtil.printWarning("Unknown Action Event= " + actionevent);
			}
		}
	}

	private boolean isUpdateAllowed = false;
	private JTextField idField;
	private JTextField nameField;
	private JTextField passwordField;
	private JTextField daysPasswordChangeField;
	private JTextField failedLoginAttemptsField;
	private JComboBox statusField;
	private final CheckList roleListField;
	private JButton saveUserButton;
	protected UserData mUser;
	protected boolean viewOnly;
	private boolean isNew;

	public UserDetailsPanel() {
		super();
		idField = new JTextField(10);
		nameField = new JTextField(10);
		passwordField = new JPasswordField();
		daysPasswordChangeField = new JTextField(10);
		failedLoginAttemptsField = new JTextField(10);
		statusField = UIFactory.createComboBox();
		roleListField = new CheckList();
		saveUserButton = null;
		mUser = null;
		viewOnly = true;
		isNew = false;
		initComponents();
		addComponents();
		setEnabled(false);
		isUpdateAllowed = !ClientUtil.getUserSession().getUserManagementConfig().isReadOnly();
	}

	public boolean hasUnsavedChanges() {
		return isDirty();
	}

	public void saveChanges() throws CanceledException, ServerException {
		saveUser_aux();
	}

	protected void populateForm(UserData userdata, boolean flag) {
		mUser = userdata;
		viewOnly = flag;
		if (userdata == null) {
			clearForm();
			viewOnly = true;
			isNew = false;
			setEnabled(!flag);
			return;
		}
		idField.setText(userdata.getUserID());		
		passwordField.setText(userdata.getCurrentPassword());
		nameField.setText(userdata.getName());
		statusField.setSelectedItem(userdata.getStatus());
		
		int daysSinceChange = 0;
		// check against empty password history
		if (!isNew && !userdata.getPasswordHistory().isEmpty()) {
			Date passwordChangeDate = ((UserPassword) userdata.getPasswordHistory().get(0)).getPasswordChangeDate();
			if (passwordChangeDate != null) daysSinceChange = DateUtil.daysSince(passwordChangeDate);
		}
		daysPasswordChangeField.setText(String.valueOf(daysSinceChange));
		if (daysSinceChange >= ClientUtil.getUserSession().getPasswordExpirationDays()) {
			daysPasswordChangeField.setForeground(Color.RED);
		}
		else {
			daysPasswordChangeField.setForeground(Color.BLACK);
		}

		failedLoginAttemptsField.setText(String.valueOf(userdata.getFailedLoginCounter()));
		if (userdata.getFailedLoginCounter() >= ClientUtil.getUserSession().getMaxFailedLoginAttempts()) {
			failedLoginAttemptsField.setForeground(Color.RED);
		}
		else {
			failedLoginAttemptsField.setForeground(Color.BLACK);
		}		

		List<Role> list = userdata.getRoles();
		roleListField.clearSelection();
		//		get the latest role list model from cache since user may have added a new role or deleted an old role
		roleListField.setModel(EntityModelCacheFactory.getInstance().getRoleListModel());
		if (list != null) {
			for (Iterator<Role> iterator = list.iterator(); iterator.hasNext();) {
				Role role = iterator.next();
				roleListField.setSelectedValue(role, true);
			}
		}
		idField.setEditable(isNew());
	}

	protected void uploadFromGUI() {
		mUser.setName(nameField.getText());
		mUser.setUserID(idField.getText());
		
        if (!passwordField.getText().equals(mUser.getCurrentPassword())) {//admin reset password.
			mUser.setCurrentPassword(PasswordOneWayHashUtil.convertToOneWayHash(
					passwordField.getText(),
					PasswordOneWayHashUtil.HASH_ALGORITHM_MD5), ClientUtil.getUserSession().getHistoryLookBack());
			mUser.setPasswordChangeRequired(true);
			mUser.setFailedLoginCounter(0);
		}
        
		String s = (String) statusField.getSelectedItem();
		if (!s.equals(mUser.getStatus())) {
			mUser.setStatus(s);
		}

		Object[] selections = roleListField.getSelectedValues();
		LinkedList<Role> linkedlist = new LinkedList<Role>();
		for (int i = 0; i < selections.length; i++) {
			Role role = (Role) selections[i];
			linkedlist.add(role);
		}
		mUser.setRoles(linkedlist);
	}

	private boolean confirmProceed() {
		Boolean result = ClientUtil.getInstance().showSaveDiscardCancelDialog();
		if (result == null) {
			return false;
		}
		else {
			if (result.booleanValue()) {
				saveDisplayUser();
			}
			return true;
		}
	}

	public UserData getDisplayUser() {
		return mUser;
	}

	public void discardChanges() {
		clearForm();
		viewOnly = true;
		isNew = false;
		setEnabled(false);
	}

	private void clearForm() {
		mUser = null;
		idField.setText("");
		passwordField.setText("");
		daysPasswordChangeField.setText("");
		nameField.setText("");
		statusField.setSelectedIndex(0);
		failedLoginAttemptsField.setText("");
		roleListField.getSelectionModel().clearSelection();
	}

	private void addComponents() {
		TitledBorder titledborder = UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel(DETAILS_TITLE_LBL));
		setBorder(titledborder);

		JPanel jpanel = UIFactory.createJPanel();
		jpanel.setLayout(new java.awt.FlowLayout(FlowLayout.LEFT, 2, 2));
		jpanel.add(saveUserButton);

		GridBagLayout bag = new GridBagLayout();
		setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(3, 3, 3, 3);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(this, bag, c, jpanel);

		PanelBase.addFormSeparator(this, bag, c);

		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, UIFactory.createFormLabel(ID_FORM_LBL, true));

		c.weightx = 1.0;
		PanelBase.addComponent(this, bag, c, idField);

		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, UIFactory.createFormLabel(PASSWORD_FORM_LBL, true));

		//c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(this, bag, c, passwordField);
		
		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, UIFactory.createFormLabel(DAYS_SINCE_PWD_CHANGE_LBL, true));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(this, bag, c, daysPasswordChangeField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, UIFactory.createFormLabel(NAME_FORM_LBL, true));

		c.weightx = 1.0;
		PanelBase.addComponent(this, bag, c, nameField);

		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, UIFactory.createFormLabel(STATUS_FORM_LBL, true));

		//c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(this, bag, c, statusField);
		
		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, UIFactory.createFormLabel(FAILED_LOGIN_ATTEMPTS_LBL, true));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;

		PanelBase.addComponent(this, bag, c, failedLoginAttemptsField);

		c.weightx = 0.0;
		c.gridwidth = 1;
		PanelBase.addComponent(this, bag, c, UIFactory.createFormLabel(ROLES_FORM_LBL, "Roles", SwingConstants.TOP, true));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.gridheight = 1;
		c.weighty = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
        
        JScrollPane rolesPane = new JScrollPane(roleListField);
        UIFactory.setLookAndFeel(rolesPane);
		PanelBase.addComponent(this, bag, c, rolesPane);
	}

	boolean saveDisplayUser() {
		try {
			saveUser_aux();
			return true;
		}
		catch (Exception exception) {
			ClientUtil.handleRuntimeException(exception);
			return false;
		}
	}

	private void saveUser_aux() throws ServerException {
			uploadFromGUI();
			ClientUtil.getCommunicator().save(mUser, false);			
			isNew = false; // It's not new anymore.
			firePropertyChange("DetailUpdated", null, mUser);
			populateForm(mUser,true);
			setEnabled(false);
	}

	protected boolean isDirty() {
		if (mUser == null) { return false; }
		if (viewOnly) return false;
		if (isNew()) {
			return true;
		}
		else {
			boolean flag = !mUser.getName().equals(nameField.getText()) || !idField.getText().equals(mUser.getUserID())
					|| !passwordField.getText().equals(mUser.getCurrentPassword()) || !statusField.getSelectedItem().equals(mUser.getStatus())
					|| isRolesDirty();
			return flag;
		}
	}

	private boolean isRolesDirty() {
		boolean flag = false;
		Object aobj[] = roleListField.getSelectedValues();
		List<Role> list = mUser.getRoles();
		if (list == null || list.size() == 0) return aobj != null && aobj.length > 0;
		if (aobj.length != list.size()) return true;
		for (int i = 0; i < aobj.length; i++) {
			Role role = (Role) aobj[i];
			if (list.contains(role)) continue;
			flag = true;
			break;
		}

		return flag;
	}

	private boolean cancelEdit(String s) {
		try {
			ClientUtil.getCommunicator().unlockUser(s);
			return true;
		}
		catch (Exception exception) {
			ClientUtil.handleRuntimeException(exception);
			return false;
		}
	}

	protected void initComponents() {
		UserDetailsAdapter userdetailsadapter = new UserDetailsAdapter();

		saveUserButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.save"), "image.btn.small.save", userdetailsadapter, null);

		UIFactory.populateComboBox(statusField, Constants.USER_STATUSES);

		roleListField.setModel(EntityModelCacheFactory.getInstance().getRoleListModel());
		roleListField.setSelectionMode(2);
		roleListField.setVisibleRowCount(8);
	}

	private boolean lockUserOnServer(UserData userdata) {
		try {
			ClientUtil.getCommunicator().lockUser(userdata.getUserID());
			return true;
		}
		catch (Exception exception) {
			ClientUtil.handleRuntimeException(exception);
			return false;
		}
	}

	public void setEnabled(boolean flag) {
		boolean enabled = isUpdateAllowed && flag;
		passwordField.setEditable(ClientUtil.getUserSession().getUserManagementConfig().isPasswordChangeable() && enabled);
		daysPasswordChangeField.setEditable(false);
		failedLoginAttemptsField.setEditable(false);
		statusField.setEnabled(ClientUtil.getUserSession().getUserManagementConfig().isStatusChangeable() && enabled);
		roleListField.setEnabled(ClientUtil.getUserSession().getUserManagementConfig().isRoleChangeable() && enabled);
		nameField.setEditable(ClientUtil.getUserSession().getUserManagementConfig().isNameChangeable() && enabled);
		saveUserButton.setEnabled(flag);
		super.setEnabled(enabled);
	}

	private boolean isNew() {
		return isNew;
	}

	public void displayUserDetails(UserData userdata, boolean forViewOnly, boolean newUser) {
		unlockPrivUserOnServer();
		if (userdata != null) {
			if (getDisplayUser() != null) {
				if (userdata.getUserID().equals(getDisplayUser().getUserID())) {
					ClientUtil.printInfo("Same UserData!");
					if (forViewOnly == this.viewOnly) return;
				}
				if (isDirty() && !confirmProceed()) return;
				if (!this.viewOnly && !cancelEdit(getDisplayUser().getUserID())) {
					ClientUtil.printError("Unable to Cancel Edit");
					return;
				}
			}
			isNew = newUser;
			if (!isNew() && !forViewOnly) {
				if (!lockUserOnServer(userdata)) return;
			}
			populateForm(userdata, forViewOnly);
			setEnabled(!forViewOnly);
		}
		else {
			JOptionPane.showMessageDialog(
					ClientUtil.getApplet(),
					ClientUtil.getInstance().getMessage("SelectItemFirstMsg"),
					ClientUtil.getInstance().getMessage("WarningMsgTitle"),
					2);
			clearForm();
		}
	}
	
	private void unlockPrivUserOnServer() {
		if (getDisplayUser() != null) {
			try {
				ClientUtil.getCommunicator().unlockUser(getDisplayUser().getUserID());
			}
			catch (Exception exception) {
				ClientUtil.handleRuntimeException(exception);
			}
		}		
	}
}
