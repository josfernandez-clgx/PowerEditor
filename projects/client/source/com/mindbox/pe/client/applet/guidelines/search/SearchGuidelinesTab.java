package com.mindbox.pe.client.applet.guidelines.search;

import java.awt.BorderLayout;
import java.awt.Dimension;

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
public class SearchGuidelinesTab extends JPanel implements PowerEditorTabPanel {

	private final JSplitPane splitPane;
	private final GuidelineListPanel resultPanel;

	public SearchGuidelinesTab(boolean readOnly) {
		super(new BorderLayout(0, 0));

		this.resultPanel = new GuidelineListPanel(this, readOnly);
		GuidelineSearchPanel searchPanel = new GuidelineSearchPanel(resultPanel);
		resultPanel.setSearchPanel(searchPanel);
		resultPanel.setMinimumSize(new Dimension(6, 0));

		splitPane = UIFactory.createSplitPane(JSplitPane.HORIZONTAL_SPLIT, searchPanel, resultPanel);
		add(splitPane, BorderLayout.CENTER);

		splitPane.setDividerLocation((int) searchPanel.getPreferredSize().getWidth());
		splitPane.setResizeWeight(0.5);
	}

	int getLastDividerLocation() {
		return splitPane.getLastDividerLocation();
	}

	void setDividerLocation(int pos) {
		splitPane.setDividerLocation(pos);
	}

	public boolean hasUnsavedChanges() {
		return resultPanel.hasUnsavedChanges();
	}

	public void saveChanges() throws CanceledException, ServerException {
		resultPanel.saveChanges();
	}

	public void discardChanges() {
		resultPanel.discardChanges();
	}
}
