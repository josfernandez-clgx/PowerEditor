package com.mindbox.pe.client.applet.entities;

import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.client.common.ThreeTierPanel;
import com.mindbox.pe.client.common.detail.AbstractDetailPanel;
import com.mindbox.pe.client.common.filter.AbstractFilterPanel;
import com.mindbox.pe.client.common.selection.AbstractSelectionPanel;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.IDNameObject;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class EntityManagementPanel<T extends IDNameObject, B extends ButtonPanel> extends ThreeTierPanel<T, B> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	/**
	 * @param filterPanel
	 * @param selectionPanel
	 * @param workspace
	 */
	public EntityManagementPanel(PeDataType entityType, AbstractFilterPanel<T, B> filterPanel, AbstractSelectionPanel<T, B> selectionPanel, AbstractDetailPanel<T, B> workspace) {
		super(entityType, filterPanel, selectionPanel, workspace);

		selectionPanel.setEnabledSelectionAwares(false);
	}
}
