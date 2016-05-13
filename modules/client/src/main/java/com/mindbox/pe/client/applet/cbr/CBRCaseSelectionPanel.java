package com.mindbox.pe.client.applet.cbr;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.entities.EntityManagementButtonPanel;
import com.mindbox.pe.client.common.selection.IDNameDescriptionObjectSelectionPanel;
import com.mindbox.pe.client.common.table.IDNameDescriptionObjectSelectionTable;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.cbr.CBRCase;

public class CBRCaseSelectionPanel extends IDNameDescriptionObjectSelectionPanel<CBRCase, EntityManagementButtonPanel<CBRCase>> {
	private static final long serialVersionUID = -3951228734910107454L;

	public CBRCaseSelectionPanel(String title, IDNameDescriptionObjectSelectionTable<CBRCaseTableModel, CBRCase> selectionTable, CBRCaseDetailPanel detailPanel, boolean readOnly) {
		super(title, selectionTable, readOnly);
		buttonPanel.setDetailPanel(detailPanel);
	}

	@Override
	protected void createButtonPanel() {
		this.buttonPanel = new EntityManagementButtonPanel<CBRCase>(isReadOnly(), this, PeDataType.CBR_CASE, ClientUtil.getInstance().getLabel("label.cbr.case"), false, true);
	}

	@Override
	public void discardChanges() {
		((EntityManagementButtonPanel<?>) this.buttonPanel).discardChanges();
	}

	@Override
	public void setEnabledSelectionAwares(boolean enabled) {
		((EntityManagementButtonPanel<?>) this.buttonPanel).setEnabledSelectionAwares(enabled);
	}
}
