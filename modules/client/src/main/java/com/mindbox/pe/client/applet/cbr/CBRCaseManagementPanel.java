package com.mindbox.pe.client.applet.cbr;

import com.mindbox.pe.client.applet.entities.EntityManagementButtonPanel;
import com.mindbox.pe.client.applet.entities.EntityManagementPanel;
import com.mindbox.pe.client.common.detail.AbstractDetailPanel;
import com.mindbox.pe.client.common.filter.AbstractFilterPanel;
import com.mindbox.pe.client.common.selection.AbstractSelectionPanel;
import com.mindbox.pe.client.common.tab.PowerEditorTabPanel;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.cbr.CBRCase;

public class CBRCaseManagementPanel extends EntityManagementPanel<CBRCase, EntityManagementButtonPanel<CBRCase>> implements PowerEditorTabPanel {
	private static final long serialVersionUID = -3951228734910107454L;

	public CBRCaseManagementPanel(AbstractFilterPanel<CBRCase, EntityManagementButtonPanel<CBRCase>> filterPanel,
			AbstractSelectionPanel<CBRCase, EntityManagementButtonPanel<CBRCase>> selectionPanel, AbstractDetailPanel<CBRCase, EntityManagementButtonPanel<CBRCase>> workspace) {
		super(PeDataType.CBR_CASE, filterPanel, selectionPanel, workspace);
	}
}
