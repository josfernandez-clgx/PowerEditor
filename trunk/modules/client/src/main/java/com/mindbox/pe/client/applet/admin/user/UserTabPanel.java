package com.mindbox.pe.client.applet.admin.user;

import static com.mindbox.pe.common.LogUtil.logInfo;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.admin.AbstractFilterSelectEditPanel;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.admin.UserData;

public class UserTabPanel extends AbstractFilterSelectEditPanel {

	private class ClearFailedLoginsL extends ReloadL {
		public void performAction(ActionEvent actionevent) {
			try {
				UserData userData = getUserSelectionTable().getSelectedUser();
				if (userData != null) {
					ClientUtil.getCommunicator().clearFailedLoginCounter(userData.getUserID());
					super.performAction(actionevent);
				}
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
		}
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
			refreshButton.setEnabled(false);
			try {
				boolean proceed = true;
				final UserDetailsPanel userDetailsPanel = UserDetailsPanel.class.cast(getDetailsPanel());
				if (userDetailsPanel.hasUnsavedChanges()) {
					if (!userDetailsPanel.confirmProceed()) {
						proceed = false;
					}
				}

				if (proceed) {
					initialized = false;
					load();
				}
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
			finally {
				refreshButton.setEnabled(true);
			}
		}
	}

	private class UserActionAdapter extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent actionevent) {
			Object obj = actionevent.getSource();
			UserData userdata = getUserSelectionTable().getSelectedUser();
			if (obj == getViewBtn()) {
				getUserDetailsPanel().displayUserDetails(userdata, true, false);
			}
			else if (obj == getEditBtn()) {
				getUserDetailsPanel().displayUserDetails(userdata, false, false);
			}
			else if (obj == getRemoveBtn()) {
				removeSelectedRow();
			}
			else if (obj == getCloneBtn()) {
				cloneSelectedRow();
			}
			else if (obj == getNewBtn()) {
				addNewRow();
			}
			else {
				ClientUtil.printWarning("Unknown Action Event= " + actionevent);
			}
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2909947502110423990L;

	protected static String DELETE_CONFIRM_MSG = "RemoveUserMsg";

	private static boolean initialized = false;

	public static void reset() {
		initialized = false;
	}

	private final JButton refreshButton;
	private final JButton enableButton;
	private final JButton clearFailedLoginsButton;
	private final boolean readOnly;
	private final boolean allowCopy;

	public UserTabPanel(final boolean readOnly, final boolean allowCopy) {
		super(true);
		this.readOnly = readOnly;
		this.allowCopy = allowCopy;
		create(new UserActionAdapter());
		if (!readOnly && !ClientUtil.getUserManagementConfig().isAllowDelete()) {
			getRemoveBtn().setEnabled(false);
		}
		refreshButton = UIFactory.createJButton("button.reload", null, new ReloadL(), "button.tooltip.reload.user");
		super.addButton(refreshButton);

		if (!readOnly && UtilBase.asBoolean(ClientUtil.getUserInterfaceConfig().isAllowDisableEnableUser(), false)) {
			enableButton = UIFactory.createJButton("button.enable", null, new EnableUserL(), null);
			enableButton.setEnabled(false);
			super.addButton(enableButton);
		}
		else {
			enableButton = null;
		}
		if (readOnly) {
			clearFailedLoginsButton = null;
		}
		else {
			this.clearFailedLoginsButton = UIFactory.createJButton("button.clear.failed.logins", null, new ClearFailedLoginsL(), null);
			super.addButton(clearFailedLoginsButton);
		}
	}

	void addNewRow() {
		UserData userdata = new UserData("New", "", "", null, true, 0, null, null);
		getUserDetailsPanel().displayUserDetails(userdata, false, true);
	}

	@Override
	protected boolean allowCopy() {
		return allowCopy;
	}

	@Override
	protected boolean allowNew() {
		return !ClientUtil.getUserManagementConfig().isReadOnly();
	}

	@Override
	protected boolean allowRemove() {
		return ClientUtil.getUserManagementConfig().isAllowDelete();
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

	@Override
	protected PanelBase createDetailsPanel() {
		return new UserDetailsPanel();
	}

	@Override
	protected PanelBase createFilterPanel(JTable jtable) {
		return null;
	}

	@Override
	protected JTable createSelectionTable() {
		return new UserSelectionTable(new UserSelectionTableModel());
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

	@Override
	protected String getEditPermission() {
		return PrivilegeConstants.PRIV_MANAGE_USERS;
	}

	@Override
	protected String getRefreshProperty() {
		return "SelUpdated";
	}

	@Override
	protected String getTableTitle() {
		return "label.title.users";
	}

	UserDetailsPanel getUserDetailsPanel() {
		return (UserDetailsPanel) getDetailsPanel();
	}

	UserSelectionTable getUserSelectionTable() {
		return (UserSelectionTable) getSelectionTable();
	}

	@Override
	protected JButton getViewBtn() {
		return super.getViewBtn();
	}

	@Override
	protected void handleDetailUpdate(PropertyChangeEvent propertychangeevent) {
		getUserSelectionTable().updateUser((UserData) propertychangeevent.getNewValue());
	}

	@Override
	protected boolean isEditAllowed() {
		final UserData userData = getUserSelectionTable().getSelectedUser();
		return super.isEditAllowed() && (userData == null || !userData.getUserID().equals(ClientUtil.getUserSession().getUserID()));
	}

	@Override
	protected boolean isReadOnly() {
		return readOnly || ClientUtil.getUserManagementConfig().isReadOnly();
	}

	public void load() {
		if (!initialized) {
			if (SwingUtilities.isEventDispatchThread()) {
				loadInternal();
			}
			else {
				logInfo(ClientUtil.getLogger(), "[UserTabPanel] not in EDT: runnig in EDT...");
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							loadInternal();
						}
					});
				}
				catch (Exception e) {
					ClientUtil.handleRuntimeException(e);
				}
			}
		}
	}

	private void loadInternal() {
		removeSelectionListener();
		try {
			final List<UserData> users = EntityModelCacheFactory.getInstance().getAllUsers();
			getUserSelectionTable().setUsers(users);
			UserDetailsPanel.class.cast(getDetailsPanel()).reloadRoles();
		}
		catch (Exception e) {
			ClientUtil.handleRuntimeException(e);
		}
		finally {
			addSelectionListener();
			initialized = true;
		}
	}

	void removeSelectedRow() {
		// The readOnly field is set using KB Date Filter set in PE config file
		// The user management config's isReadOnly() method determines if PE DB/LDAP configuration allows user modifications
		if (readOnly && ClientUtil.getUserManagementConfig().isReadOnly()) return;
		UserData userdata = getUserSelectionTable().getSelectedUser();
		if (userdata != null) {
			int i = JOptionPane.showConfirmDialog(ClientUtil.getApplet(), ClientUtil.getInstance().getMessage(DELETE_CONFIRM_MSG), ClientUtil.getInstance().getMessage("PleaseConfirmMsgTitle"), 0);
			if (i == 0) {
				deleteUser(userdata);
				getUserSelectionTable().removeUser(userdata);
				getUserDetailsPanel().populateForm(null, true);
			}
		}
	}

	@Override
	protected void updateButtonStatesInternal(boolean hasSelection) {
		if (enableButton != null && hasSelection) {
			UserData userData = getUserSelectionTable().getSelectedUser();
			enableButton.setEnabled((userData != null && userData.isDisabled()));
			clearFailedLoginsButton.setEnabled((userData != null));
		}
	}
}
