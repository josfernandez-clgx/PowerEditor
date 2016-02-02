/*
 * Created on 2003. 12. 11.
 *
 */
package com.mindbox.pe.client.applet.parameters;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.tab.PowerEditorTabPanel;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.exceptions.CanceledException;

/**
 * @author Geneho Kim
 * @author MindBox
 */
public class ParameterManagerTab extends JPanel implements PowerEditorTabPanel {

	private final JSplitPane splitPane;
	private final ParameterDetailPanel detailPanel;

	public ParameterManagerTab(boolean readOnly) {
		super();

		detailPanel = new ParameterDetailPanel(this, readOnly);
		ParameterTemplateSelectionPanel selectionPanel = new ParameterTemplateSelectionPanel(detailPanel);
		detailPanel.setSelectionPanel(selectionPanel);

		splitPane = UIFactory.createSplitPane(JSplitPane.HORIZONTAL_SPLIT, selectionPanel, detailPanel);
		this.setLayout(new BorderLayout(0, 0));
		add(splitPane, BorderLayout.CENTER);
		splitPane.setDividerLocation(230);
	}

	int getLastDividerLocation() {
		return splitPane.getLastDividerLocation();
	}

	void setDividerLocation(int pos) {
		splitPane.setDividerLocation(pos);
	}

	public boolean hasUnsavedChanges() {
		return detailPanel.hasUnsavedChanges();
	}

	public void saveChanges() throws CanceledException, ServerException {
		detailPanel.saveChanges();
	}

	public void discardChanges() {
		detailPanel.discardChanges();
	}
}
