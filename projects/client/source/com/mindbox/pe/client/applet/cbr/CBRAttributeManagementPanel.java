package com.mindbox.pe.client.applet.cbr;

import com.mindbox.pe.client.applet.entities.EntityManagementButtonPanel;
import com.mindbox.pe.client.applet.entities.EntityManagementPanel;
import com.mindbox.pe.client.common.detail.AbstractDetailPanel;
import com.mindbox.pe.client.common.filter.AbstractFilterPanel;
import com.mindbox.pe.client.common.selection.AbstractSelectionPanel;
import com.mindbox.pe.client.common.tab.PowerEditorTabPanel;
import com.mindbox.pe.model.CBRAttribute;
import com.mindbox.pe.model.EntityType;

/**
 * CBR attribute management panel.
 * @author deklerk
 * @since PowerEditor 4.1.0
 */
public class CBRAttributeManagementPanel extends EntityManagementPanel<CBRAttribute, EntityManagementButtonPanel<CBRAttribute>> implements
		PowerEditorTabPanel {


	/**
	 * Creates a new CBR attribute management panel with the specified panels.
	 * @param filterPanel filter panel
	 * @param selectionPanel selection panel
	 * @param workspace workspace (for displaying details of a CBR attribute)
	 */
	public CBRAttributeManagementPanel(AbstractFilterPanel<CBRAttribute, EntityManagementButtonPanel<CBRAttribute>> filterPanel,
			AbstractSelectionPanel<CBRAttribute, EntityManagementButtonPanel<CBRAttribute>> selectionPanel,
			AbstractDetailPanel<CBRAttribute, EntityManagementButtonPanel<CBRAttribute>> workspace) {
		super(EntityType.CBR_ATTRIBUTE, filterPanel, selectionPanel, workspace);
	}


}
