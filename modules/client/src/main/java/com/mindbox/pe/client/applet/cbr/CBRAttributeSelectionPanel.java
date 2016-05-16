package com.mindbox.pe.client.applet.cbr;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.entities.EntityManagementButtonPanel;
import com.mindbox.pe.client.common.selection.IDNameDescriptionObjectSelectionPanel;
import com.mindbox.pe.client.common.table.IDNameDescriptionObjectSelectionTable;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.cbr.CBRAttribute;

/**
 * @author deklerk
 * @since PowerEditor 4.1.0
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CBRAttributeSelectionPanel extends IDNameDescriptionObjectSelectionPanel<CBRAttribute, EntityManagementButtonPanel<CBRAttribute>> {
	private static final long serialVersionUID = -3951228734910107454L;

	public CBRAttributeSelectionPanel(String title, IDNameDescriptionObjectSelectionTable<CBRAttributeTableModel, CBRAttribute> selectionTable, CBRAttributeDetailPanel detailPanel,
			boolean readOnly) {
		super(title, selectionTable, readOnly);
		buttonPanel.setDetailPanel(detailPanel);
	}

	@Override
	protected void createButtonPanel() {
		this.buttonPanel = new EntityManagementButtonPanel<CBRAttribute>(
				isReadOnly(),
				this,
				PeDataType.CBR_ATTRIBUTE,
				ClientUtil.getInstance().getLabel("label.cbr.attribute"),
				false,
				true);
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
