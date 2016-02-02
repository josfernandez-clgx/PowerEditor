/*
 * Created on 2004. 6. 28.
 */
package com.mindbox.pe.client.applet.admin.phase;

import java.awt.BorderLayout;

import com.mindbox.pe.client.common.PanelBase;


/**
 * 
 *
 * @author kim
 * @since PowerEditor 3.3.0 
 */
public class PhaseManagementPanel extends PanelBase {

	public PhaseManagementPanel(boolean readOnly) {
		initPanel(readOnly);
	}

	private void initPanel(boolean readOnly) {
		setLayout(new BorderLayout(1, 1));
		add(new PhaseTreePanel(readOnly), BorderLayout.CENTER);
	}
}
