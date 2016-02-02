/*
 * Created on Jan 26, 2006
 *
 */
package com.mindbox.pe.client.applet.report;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.common.tab.PowerEditorTabPanel;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.exceptions.CanceledException;


/**
 * Report Tab.
 * @author Geneho Kim
 * @since PowerEditor 4.4.0
 */
public class ReportTab extends JPanel implements PowerEditorTabPanel {

	private final JTabbedPane tab;

	public ReportTab() {
		tab = new JTabbedPane();
		tab.setFont(PowerEditorSwingTheme.tabFont);
		initPanel();
	}

	private void initPanel() {
		tab.addTab("Custom Reports", new CustomReportPanel());
		setLayout(new BorderLayout());
		add(tab, BorderLayout.CENTER);
	}

	public boolean hasUnsavedChanges() {
		return false;
	}

	public void saveChanges() throws CanceledException, ServerException {
		// noop
	}

	public void discardChanges() {
		// noop
	}

}
