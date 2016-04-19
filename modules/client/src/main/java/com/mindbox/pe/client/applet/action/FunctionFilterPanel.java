package com.mindbox.pe.client.applet.action;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.mindbox.pe.client.applet.entities.EntityManagementButtonPanel;
import com.mindbox.pe.client.common.filter.IDNameDescriptionObjectFilterPanel;
import com.mindbox.pe.client.common.selection.AbstractSelectionPanel;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.rule.FunctionTypeDefinition;

public final class FunctionFilterPanel<T extends FunctionTypeDefinition> extends IDNameDescriptionObjectFilterPanel<T, EntityManagementButtonPanel<T>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;


	public FunctionFilterPanel(AbstractSelectionPanel<T, EntityManagementButtonPanel<T>> selectionPanel, PeDataType et) {
		super(selectionPanel, et, false);
	}

	protected void addComponents(GridBagLayout bag, GridBagConstraints c) {
		super.addComponents(bag, c);
	}

	protected void clearSearchFields() {
		super.clearSearchFields();
	}

}
