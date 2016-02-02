package com.mindbox.pe.client.applet.admin;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.admin.phase.PhaseManagementPanel;
import com.mindbox.pe.client.applet.admin.request.RequestManagementPanel;
import com.mindbox.pe.client.common.tab.PowerEditorTab;

public class ProcessManagementTab extends PowerEditorTab {

	public ProcessManagementTab(boolean readOnly) {

		// TODO Kim: disable edits if readOnly is true

		setFont(PowerEditorSwingTheme.tabFont);
		setFocusable(false);

		if (ClientUtil.checkPermissionByPrivilegeName("ManagePhase")) {
			addTab(
					ClientUtil.getInstance().getLabel("tab.process.phase"),
					ClientUtil.getInstance().makeImageIcon("image.blank"),
					new PhaseManagementPanel(readOnly),
					ClientUtil.getInstance().getLabel("tab.tooltip.process.phase"));
		}

		if (ClientUtil.checkPermissionByPrivilegeName("ManageRequestType")) {
			addTab(
					ClientUtil.getInstance().getLabel("tab.process.request"),
					ClientUtil.getInstance().makeImageIcon("image.blank"),
					new RequestManagementPanel(readOnly),
					ClientUtil.getInstance().getLabel("tab.tooltip.process.request"));
		}

	}
}