package com.mindbox.pe.client.applet.cbr;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.tab.PowerEditorTab;
import com.mindbox.pe.client.common.tab.PowerEditorTabPanel;
import com.mindbox.pe.client.common.table.IDNameDescriptionObjectSelectionTable;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.CBRCase;
import com.mindbox.pe.model.CBRCaseBase;
import com.mindbox.pe.model.exceptions.CanceledException;

/**
 * @author deklerk
 * @since PowerEditor 4.1.0
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CBRCaseBaseTab extends PowerEditorTab implements PowerEditorTabPanel {

	private CBRCaseBase caseBase = null;
	private CBRCaseBaseAttributesManagementPanel cbAttPanel;
	private CBRCaseManagementPanel casePanel;

	/**
	 * 
	 */
	public CBRCaseBaseTab(CBRCaseBase cb, boolean readOnly) {
		super();
		caseBase = cb;
		UIFactory.setLookAndFeel(this);
		setFont(PowerEditorSwingTheme.boldFont);

		// add casebase management tab
		cbAttPanel = new CBRCaseBaseAttributesManagementPanel(caseBase, readOnly);
		addTab(
				ClientUtil.getInstance().getLabel("tab.cbr.casebases.manage"),
				ClientUtil.getInstance().makeImageIcon("image.blank"),
				cbAttPanel,
				ClientUtil.getInstance().getLabel("tab.tooltip.cbr.casebases.manage"));
		// add case management tab
		CBRCaseTableModel tableModel = new CBRCaseTableModel();
		CBRCaseDetailPanel detailPanel = new CBRCaseDetailPanel(cb);
		IDNameDescriptionObjectSelectionTable<CBRCaseTableModel, CBRCase> table = new IDNameDescriptionObjectSelectionTable<CBRCaseTableModel, CBRCase>(
				tableModel,
				false);
		CBRCaseSelectionPanel selectionPanel = new CBRCaseSelectionPanel(
				ClientUtil.getInstance().getLabel("label.title.cbr.cases"),
				table,
				detailPanel,
				readOnly);
		CBRCaseFilterPanel filterPanel = new CBRCaseFilterPanel(selectionPanel, false, caseBase);
		detailPanel.setSelectionPanel(selectionPanel);
		casePanel = new CBRCaseManagementPanel(filterPanel, selectionPanel, detailPanel);
		addTab(
				ClientUtil.getInstance().getLabel("tab.cbr.cases.manage"),
				ClientUtil.getInstance().makeImageIcon("image.blank"),
				casePanel,
				ClientUtil.getInstance().getLabel("tab.tooltip.cbr.cases.manage"));
		this.addChangeListener(filterPanel);
		this.addChangeListener(detailPanel);
	}

	public CBRCaseBase getCBRCaseBase() {
		return caseBase;
	}

	public boolean hasUnsavedChanges() {
		return cbAttPanel.hasUnsavedChanges() || casePanel.hasUnsavedChanges();
	}

	public void saveChanges() throws CanceledException, ServerException {
		if (cbAttPanel.hasUnsavedChanges()) cbAttPanel.saveChanges();
		if (casePanel.hasUnsavedChanges()) casePanel.saveChanges();
	}

	public void discardChanges() {
		if (cbAttPanel.hasUnsavedChanges()) cbAttPanel.discardChanges();
		if (casePanel.hasUnsavedChanges()) casePanel.discardChanges();
	}
}
