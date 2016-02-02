package com.mindbox.pe.client.applet.admin.request;

import java.awt.BorderLayout;

import com.mindbox.pe.client.common.PanelBase;

/**
 * Request management panel.
 *
 * @author kim
 * @since PowerEditor 3.3.0
 */
public class RequestManagementPanel extends PanelBase {

	public RequestManagementPanel(boolean readOnly) {
		initPanel(readOnly);
	}

	private void initPanel(boolean readOnly) {
		RequestListPanel listPanel = RequestListPanel.createInstance(readOnly);

		setLayout(new BorderLayout());
		add(listPanel, BorderLayout.CENTER);
	}
}