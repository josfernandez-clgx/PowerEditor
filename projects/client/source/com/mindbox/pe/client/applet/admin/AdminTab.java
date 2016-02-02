package com.mindbox.pe.client.applet.admin;

import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.admin.config.ConfigPanel;
import com.mindbox.pe.client.applet.admin.deploy.DeployTabPanel;
import com.mindbox.pe.client.applet.admin.imexport.ExportPanel;
import com.mindbox.pe.client.applet.admin.imexport.ImportPanel;
import com.mindbox.pe.client.applet.admin.role.RoleTabPanel;
import com.mindbox.pe.client.applet.admin.user.UserTabPanel;
import com.mindbox.pe.client.common.tab.PowerEditorTab;
import com.mindbox.pe.common.PrivilegeConstants;

public class AdminTab extends PowerEditorTab {

	private UserTabPanel mUserTabPanel;
	private RoleTabPanel mRoleTabPanel;

	public AdminTab(boolean readOnly) {

		mUserTabPanel = null;
		mRoleTabPanel = null;
		int i = 0;
		setFont(PowerEditorSwingTheme.tabFont);
		setFocusable(false);

		if (ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_DEPLOY)) {
			i++;
			addTab(ClientUtil.getInstance().getLabel("tab.deploy"), ClientUtil.getInstance().makeImageIcon("image.blank"), new JScrollPane(
					new DeployTabPanel()), ClientUtil.getInstance().getLabel("tab.tooltip.deploy"));
		}

		if (ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_EXPORT_DATA)) {
			i++;
			addTab(ClientUtil.getInstance().getLabel("tab.export"), ClientUtil.getInstance().makeImageIcon("image.blank"), new JScrollPane(
					new ExportPanel()), ClientUtil.getInstance().getLabel("tab.tooltip.export"));
			if (!readOnly) {
				i++;
				addTab(
						ClientUtil.getInstance().getLabel("tab.import"),
						ClientUtil.getInstance().makeImageIcon("image.blank"),
						new JScrollPane(new ImportPanel()),
						ClientUtil.getInstance().getLabel("tab.tooltip.import"));
			}
		}

		if (ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_MANAGE_CONFIGURATION)) {
			i++;
			addTab(
					ClientUtil.getInstance().getLabel("tab.config.manage"),
					ClientUtil.getInstance().makeImageIcon("image.blank"),
					new JScrollPane(new ConfigPanel()),
					ClientUtil.getInstance().getLabel("tab.tooltip.config.manage"));
		}

		final int MANAGE_USERS_INDEX = i;
		if (ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_MANAGE_USERS)
				&& !ClientUtil.getUserSession().getUserManagementConfig().isReadOnly()) {
			i++;
			addTab(
					ClientUtil.getInstance().getLabel("tab.user.manage"),
					ClientUtil.getInstance().makeImageIcon("image.blank"),
					mUserTabPanel = new UserTabPanel(readOnly),
					ClientUtil.getInstance().getLabel("tab.tooltip.user.manage"));
		}

		final int MANAGE_ROLES_INDEX = i;
		if (ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_MANAGE_USERS)) {
			i++;
			addTab(
					ClientUtil.getInstance().getLabel("tab.role.manage"),
					ClientUtil.getInstance().makeImageIcon("image.blank"),
					mRoleTabPanel = new RoleTabPanel(readOnly),
					ClientUtil.getInstance().getLabel("tab.tooltip.role.manage"));
		}

		addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent changeevent) {
				int j = getSelectedIndex();
				if (j == MANAGE_USERS_INDEX && mUserTabPanel != null) mUserTabPanel.load();
				if (j == MANAGE_ROLES_INDEX && mRoleTabPanel != null) mRoleTabPanel.load();
			}

		});
	}

	public void loadCurrentPanel() {
		if (getSelectedComponent() == mRoleTabPanel) {
			mRoleTabPanel.load();
		}
		else if (getSelectedComponent() == mUserTabPanel) {
			mUserTabPanel.load();
		}
	}
}
