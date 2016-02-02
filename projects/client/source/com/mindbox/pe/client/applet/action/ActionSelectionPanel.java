package com.mindbox.pe.client.applet.action;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.entities.EntityManagementButtonPanel;
import com.mindbox.pe.client.common.selection.IDNameDescriptionObjectSelectionPanel;
import com.mindbox.pe.client.common.table.IDNameDescriptionObjectSelectionTable;
import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.rule.ActionTypeDefinition;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public final class ActionSelectionPanel extends
		IDNameDescriptionObjectSelectionPanel<ActionTypeDefinition, EntityManagementButtonPanel<ActionTypeDefinition>> {

	public ActionSelectionPanel(ActionDetailsPanel detailPanel, boolean readOnly) {
		super(
				ClientUtil.getInstance().getLabel("label.title.guideline.actions"),
				new IDNameDescriptionObjectSelectionTable<ActionSelectionTableModel, ActionTypeDefinition>(
						new ActionSelectionTableModel(),
						false),
				readOnly);
		this.buttonPanel.setDetailPanel(detailPanel);
	}

	public void setEnabledSelectionAwares(boolean enabled) {
		buttonPanel.setEnabledSelectionAwares(enabled);
	}

	protected void createButtonPanel() {
		this.buttonPanel = new EntityManagementButtonPanel<ActionTypeDefinition>(
				isReadOnly(),
				this,
				EntityType.GUIDELINE_ACTION,
				ClientUtil.getInstance().getLabel("label.guideline.action"),
				false,
				true);
		buttonPanel.setEditPrivilege(PrivilegeConstants.PRIV_MANAGE_GUIDELINE_ACTIONS);
	}

}
