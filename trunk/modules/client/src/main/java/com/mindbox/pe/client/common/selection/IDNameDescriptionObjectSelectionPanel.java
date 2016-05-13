package com.mindbox.pe.client.common.selection;

import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.client.common.table.IDNameDescriptionObjectSelectionTable;
import com.mindbox.pe.model.AbstractIDNameDescriptionObject;

/**
 *  
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public abstract class IDNameDescriptionObjectSelectionPanel<D extends AbstractIDNameDescriptionObject, B extends ButtonPanel> extends IDNameObjectSelectionPanel<D, B> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public IDNameDescriptionObjectSelectionPanel(String title, IDNameDescriptionObjectSelectionTable<?, D> selectionTable, boolean readOnly) {
		super(title, selectionTable, readOnly);
	}

	// for 1934, 2094
	public boolean hasChangesInDetails() {
		return this.buttonPanel.hasUnsavedChanges();
	}

	// for 1934, 2094
	public boolean saveChangesInDetails() {
		checkReadOnly();

		return this.buttonPanel.saveChanges();
	}

	// for 1934, 2094
	public void discardChangesInDetails() {
		this.buttonPanel.discardChanges();
	}
}
