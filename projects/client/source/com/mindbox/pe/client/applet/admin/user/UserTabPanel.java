package com.mindbox.pe.client.applet.admin.user;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.admin.AbstractFilterSelectEditPanel;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.model.admin.UserData;

public class UserTabPanel extends AbstractFilterSelectEditPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2909947502110423990L;

	protected static String DELETE_CONFIRM_MSG = "RemoveUserMsg";
	private static boolean initialized = false;

	public static void reset() {
		initialized = false;
	}

	private class EnableUserL extends ReloadL {
		public void performAction(ActionEvent actionevent) {
			try {
				UserData userData = getUserSelectionTable().getSelectedUser();
				if (userData != null) {
					ClientUtil.getCommunicator().enableUser(userData.getUserID());

					super.performAction(actionevent);
				}
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
		}
	}

	private class ReloadL extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent actionevent) {
			try {
				List<UserData> users = ClientUtil.getCommunicator().reloadUserData();
				getUserSelectionTable().setUsers(users);
				EntityModelCacheFactory.getInstance().reloadRolesFromServer();
				initialized = true;
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
		}
	}

	private class UserActionAdapter extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent actionevent) {
			Object obj = actionevent.getSource();
			UserData userdata = getUserSelectionTable().getSelectedUser();
			if (obj == getViewBtn())
				getUserDetailsPanel().displayUserDetails(userdata, true, false);
			else if (obj == getEditBtn())
				getUserDetailsPanel().displayUserDetails(userdata, false, false);
			else if (obj == getRemoveBtn())
				removeSelectedRow();
			else if (obj == getCloneBtn())
				cloneSelectedRow();
			else if (obj == getNewBtn())
				addNewRow();
			else
				ClientUtil.printWarning("Unknown Action Event= " + actionevent);
		}
	}

	private final JButton refreshButton;
	private final JButton enableButton;
	private final boolean readOnly;

	public UserTabPanel(boolean readOnly) {
		super(true);
		this.readOnly = readOnly;
		create(new UserActionAdapter());
		if (!readOnly && !ClientUtil.getUserSession().getUserManagementConfig().isAllowDelete()) {
			getRemoveBtn().setEnabled(false);
		}
		refreshButton = UIFactory.createJButton("button.reload", null, new ReloadL(), "button.tooltip.reload.user");
		super.addButton(refreshButton);

		if (!readOnly && ClientUtil.getUserSession().getUiConfiguration().isAllowDisableEnableUser()) {
			enableButton = UIFactory.createJButton("button.enable", null, new EnableUserL(), null);
			enableButton.setEnabled(false);
			super.addButton(enableButton);
		}
		else {
			enableButton = null;
		}
	}

	@Override
	protected void updateButtonStatesInternal(boolean hasSelection) {
		if (enableButton != null && hasSelection) {
			UserData userData = getUserSelectionTable().getSelectedUser();
			enableButton.setEnabled((userData != null && userData.isDisabled()));
		}
	}

	protected boolean isReadOnly() {
		return readOnly || ClientUtil.getUserSession().getUserManagementConfig().isReadOnly();
	}

	protected String getEditPermission() {
		return PrivilegeConstants.PRIV_MANAGE_USERS;
	}

	private boolean deleteUser(UserData userdata) {
		try {
			ClientUtil.getCommunicator().deleteUser(userdata);
			return true;
		}
		catch (Exception exception) {
			ClientUtil.handleRuntimeException(exception);
			return false;
		}
	}

	protected String getRefreshProperty() {
		return "SelUpdated";
	}

	protected PanelBase createFilterPanel(JTable jtable) {
		return null;
	}

	protected boolean allowRemove() {
		return ClientUtil.getUserSession().getUserManagementConfig().isAllowDelete();
	}

	protected boolean allowNew() {
		return ClientUtil.getUserSession().getUserManagementConfig().isReadOnly();
	}

	protected void handleDetailUpdate(PropertyChangeEvent propertychangeevent) {
		getUserSelectionTable().updateUser((UserData) propertychangeevent.getNewValue());
	}

	void removeSelectedRow() {
		// The readOnly field is set using KB Date Filter set in PE config file
		// The user management config's isReadOnly() method determines if PE DB/LDAP configuration allows user modifications
		if (readOnly && ClientUtil.getUserSession().getUserManagementConfig().isReadOnly()) return;
		UserData userdata = getUserSelectionTable().getSelectedUser();
		if (userdata != null) {
			int i = JOptionPane.showConfirmDialog(
					ClientUtil.getApplet(),
					ClientUtil.getInstance().getMessage(DELETE_CONFIRM_MSG),
					ClientUtil.getInstance().getMessage("PleaseConfirmMsgTitle"),
					0);
			if (i == 0) {
				deleteUser(userdata);
				getUserSelectionTable().removeUser(userdata);
				getUserDetailsPanel().populateForm(null, true);
			}
		}
	}

	protected JButton getViewBtn() {
		return super.getViewBtn();
	}

	public synchronized void load() {
		if (initialized) {
			return;
		}
		else {
			List<UserData> users = EntityModelCacheFactory.getInstance().getAllUsers();
			getUserSelectionTable().setUsers(users);
			initialized = true;
		}
	}

	protected JTable createSelectionTable() {
		return new UserSelectionTable(new UserSelectionTableModel());
	}

	UserSelectionTable getUserSelectionTable() {
		return (UserSelectionTable) getSelectionTable();
	}

	void addNewRow() {
		UserData userdata = new UserData("New", "", "", null, true, 0, null);
		getUserDetailsPanel().displayUserDetails(userdata, false, true);
	}

	void cloneSelectedRow() {
		UserData userdata = getUserSelectionTable().getSelectedUser();
		if (userdata == null) return;
		try {
			UserData userdata1 = new UserData(userdata);
			getUserDetailsPanel().displayUserDetails(userdata1, false, true);
		}
		catch (Exception exception) {
			ClientUtil.handleRuntimeException(exception);
		}
	}

	protected PanelBase createDetailsPanel() {
		return new UserDetailsPanel();
	}

	UserDetailsPanel getUserDetailsPanel() {
		return (UserDetailsPanel) getDetailsPanel();
	}

	protected String getTableTitle() {
		return "label.title.users";
	}
}
