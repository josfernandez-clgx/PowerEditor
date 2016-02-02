package com.mindbox.pe.client.applet.cbr;

import com.mindbox.pe.client.applet.entities.EntityManagementButtonPanel;
import com.mindbox.pe.client.applet.entities.EntityManagementPanel;
import com.mindbox.pe.client.common.detail.AbstractDetailPanel;
import com.mindbox.pe.client.common.filter.AbstractFilterPanel;
import com.mindbox.pe.client.common.selection.AbstractSelectionPanel;
import com.mindbox.pe.client.common.tab.PowerEditorTabPanel;
import com.mindbox.pe.model.CBRCase;
import com.mindbox.pe.model.EntityType;

/**
 * @author deklerk
 * @since PowerEditor 4.1.0
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CBRCaseManagementPanel extends EntityManagementPanel<CBRCase, EntityManagementButtonPanel<CBRCase>> implements
		PowerEditorTabPanel {

	/**
	 * 
	 */
	public CBRCaseManagementPanel(AbstractFilterPanel<CBRCase, EntityManagementButtonPanel<CBRCase>> filterPanel,
			AbstractSelectionPanel<CBRCase, EntityManagementButtonPanel<CBRCase>> selectionPanel,
			AbstractDetailPanel<CBRCase, EntityManagementButtonPanel<CBRCase>> workspace) {
		super(EntityType.CBR_CASE, filterPanel, selectionPanel, workspace);
	}
}
