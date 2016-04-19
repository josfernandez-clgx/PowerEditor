package com.mindbox.pe.client.applet.action;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.entities.EntityManagementButtonPanel;
import com.mindbox.pe.client.common.selection.IDNameDescriptionObjectSelectionPanel;
import com.mindbox.pe.client.common.table.IDNameDescriptionObjectSelectionTable;
import com.mindbox.pe.client.common.table.IDNameDescriptionObjectSelectionTableModel;
import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.rule.TestTypeDefinition;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public final class TestSelectionPanel extends IDNameDescriptionObjectSelectionPanel<TestTypeDefinition, EntityManagementButtonPanel<TestTypeDefinition>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public TestSelectionPanel(TestDetailsPanel detailPanel, boolean readOnly) {
		super(
				ClientUtil.getInstance().getLabel("label.title.guideline.tests"),
				new IDNameDescriptionObjectSelectionTable<IDNameDescriptionObjectSelectionTableModel<TestTypeDefinition>, TestTypeDefinition>(
						new IDNameDescriptionObjectSelectionTableModel<TestTypeDefinition>(new String[] {
								ClientUtil.getInstance().getLabel("label.name"),
								ClientUtil.getInstance().getLabel("label.desc") }),
						false), readOnly);
		this.buttonPanel.setDetailPanel(detailPanel);
	}

	public void setEnabledSelectionAwares(boolean enabled) {
		buttonPanel.setEnabledSelectionAwares(enabled);
	}

	protected void createButtonPanel() {
		this.buttonPanel = new EntityManagementButtonPanel<TestTypeDefinition>(
				isReadOnly(),
				this,
				PeDataType.GUIDELINE_TEST_CONDITION,
				ClientUtil.getInstance().getLabel("label.guideline.test"),
				false,
				true);
		buttonPanel.setEditPrivilege(PrivilegeConstants.PRIV_MANAGE_GUIDELINE_ACTIONS);
	}
}
