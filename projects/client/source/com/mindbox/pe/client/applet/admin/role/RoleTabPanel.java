package com.mindbox.pe.client.applet.admin.role;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.admin.AbstractFilterSelectEditPanel;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.admin.Role;

/**
 * Role management panel.
 * @since PowerEditor 1.0
 */
public class RoleTabPanel extends AbstractFilterSelectEditPanel {

	protected static String DELETE_CONFIRM_MSG = "RemoveRoleMsg";
	private static boolean initialized = false;

	public static void reset() {
		initialized = false;
	}

	private class RoleActionAdapter extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent actionevent) {
			Object obj = actionevent.getSource();
			Role role = getRoleSelectionTable().getSelectedRole();
			if (obj == getViewBtn())
				getRoleDetailsPanel().displayRoleDetails(role, true, false);
			else if (obj == getEditBtn())
				getRoleDetailsPanel().displayRoleDetails(role, false, false);
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

	private final boolean readOnly;

	public RoleTabPanel(boolean readOnly) {
		super(false);
		this.readOnly = readOnly;
		create(new RoleActionAdapter());
	}

	protected boolean isReadOnly() {
		return readOnly;
	}

	protected String getEditPermission() {
		return PrivilegeConstants.PRIV_MANAGE_USERS;
	}

	protected boolean allowRemove() {
		return true;
	}

	protected boolean allowNew() {
		return true;
	}

	RoleSelectionTable getRoleSelectionTable() {
		return (RoleSelectionTable) getSelectionTable();
	}

	private boolean deleteRole(Role role) {
		try {
			ClientUtil.getCommunicator().delete(role.getID(), EntityType.ROLE);
			return true;
		}
		catch (Exception exception) {
			ClientUtil.handleRuntimeException(exception);
			return false;
		}
	}

	protected JButton getCloneBtn() {
		return super.getCloneBtn();
	}

	protected PanelBase createFilterPanel(JTable jtable) {
		return null;
	}

	RoleDetailsPanel getRoleDetailsPanel() {
		return (RoleDetailsPanel) getDetailsPanel();
	}


	protected void handleDetailUpdate(PropertyChangeEvent propertychangeevent) {
		getRoleSelectionTable().updateRole((Role) propertychangeevent.getNewValue());
	}

	protected JButton getEditBtn() {
		return super.getEditBtn();
	}

	protected JButton getRemoveBtn() {
		return super.getRemoveBtn();
	}

	void removeSelectedRow() {
		ClientUtil.printInfo("Remove selected...");
		Role role = getRoleSelectionTable().getSelectedRole();
		if (role != null) {
			int i = JOptionPane.showConfirmDialog(
					ClientUtil.getApplet(),
					ClientUtil.getInstance().getMessage(DELETE_CONFIRM_MSG),
					ClientUtil.getInstance().getMessage("PleaseConfirmMsgTitle"),
					0);
			if (i == 0) {
				deleteRole(role);
				getRoleSelectionTable().removeRole(role);
				if (getDisplayRoleId() == role.getID()) {
					getRoleDetailsPanel().populateForm(null, true);
				}
				EntityModelCacheFactory.getInstance().removeRole(role);// remove role in client cache
			}
		}
	}

	protected JButton getViewBtn() {
		return super.getViewBtn();
	}

	public void load() {
		boolean reloadRoles = EntityModelCacheFactory.getInstance().isRelodRolesInRoleTabpanel();
		if (reloadRoles) {// reload takes preference because user hit reload button on manageUsers screen and reloaded all users and their roles
			List<Role> roleList = EntityModelCacheFactory.getInstance().getAllRoles();
			getRoleSelectionTable().setRoles(roleList);
			initialized = true;
			EntityModelCacheFactory.getInstance().setRelodRolesInRoleTabpanel(false);//set reload flag to false
			return;
		}
		if (initialized) {//coming back and no new roles were added
			return;
		}
		else {// coming for first time
			List<Role> roleList = EntityModelCacheFactory.getInstance().getAllRoles();
			getRoleSelectionTable().setRoles(roleList);
			initialized = true;
			return;
		}
	}

	protected JButton getNewBtn() {
		return super.getNewBtn();
	}

	protected JTable createSelectionTable() {
		return new RoleSelectionTable(new RoleSelectionTableModel());
	}

	void addNewRow() {
		ClientUtil.printInfo("New selected...");
		Role role = new Role(-1, "", null);
		getRoleDetailsPanel().displayRoleDetails(role, false, true);
	}

	private int getDisplayRoleId() {
		if (getRoleDetailsPanel().getDisplayRole() == null)
			return -1;
		else
			return getRoleDetailsPanel().getDisplayRole().getID();
	}

	void cloneSelectedRow() {
		Role role = getRoleSelectionTable().getSelectedRole();
		if (role == null) return;
		try {
			Role role1 = new Role(role);
			// TT 2128
			role1.setID(-1);
			getRoleDetailsPanel().displayRoleDetails(role1, false, true);
		}
		catch (Exception exception) {
			ClientUtil.handleRuntimeException(exception);
		}
	}

	protected PanelBase createDetailsPanel() {
		return new RoleDetailsPanel();
	}

	protected String getTableTitle() {
		return "label.title.user.roles";
	}

}
