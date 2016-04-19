package com.mindbox.pe.client.applet.template;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.template.guideline.TemplateDetailPanel;
import com.mindbox.pe.client.common.selection.GuidelineTemplateSelectionPanel;
import com.mindbox.pe.client.common.tab.PowerEditorTabPanel;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.exceptions.CanceledException;
import com.mindbox.pe.model.template.GridTemplate;

/**
 * @author Geneho Kim
 * @author MindBox
 */
public class TemplateManagementTab extends JPanel implements PowerEditorTabPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private final TemplateDetailPanel detailPanel;
	private final GuidelineTemplateSelectionPanel selectionPanel;

	public TemplateManagementTab(boolean readOnly) throws ServerException {
		super();

		selectionPanel = new GuidelineTemplateSelectionPanel(false, false, readOnly);
		this.detailPanel = new TemplateDetailPanel(selectionPanel);
		selectionPanel.setGuidelineSelectionListener(detailPanel);

		JPanel buttonPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.LEFT));
		JButton[] buttons = detailPanel.getTemplateButtons();
		for (int i = 0; i < buttons.length; i++) {
			buttonPanel.add(buttons[i]);
		}
		JPanel leftPanel = UIFactory.createBorderLayoutPanel(0, 0);
		leftPanel.add(buttonPanel, BorderLayout.NORTH);
		leftPanel.add(selectionPanel, BorderLayout.CENTER);
		leftPanel.setBorder(UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel("label.title.guideline.template.selection")));

		JSplitPane splitPane = UIFactory.createSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, detailPanel);
		this.setLayout(new BorderLayout(0, 0));
		add(splitPane, BorderLayout.CENTER);
	}

	public boolean hasUnsavedChanges() {
		return detailPanel.hasUnsavedChanges();
	}

	public void saveChanges() throws CanceledException, ServerException {
		detailPanel.saveChanges();
	}

	public void editTemplate(GridTemplate template) throws CanceledException {
		selectionPanel.editTemplate(template);
	}

	public void discardChanges() {
		detailPanel.discardChanges();
	}

	public void reloadTemplates() throws ServerException {
		selectionPanel.reloadTemplates();
	}
}
