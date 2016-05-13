package com.mindbox.pe.client.common.table;

import com.mindbox.pe.model.AbstractIDNameDescriptionObject;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class IDNameDescriptionObjectSelectionTable<M extends IDNameDescriptionObjectSelectionTableModel<D>, D extends AbstractIDNameDescriptionObject> extends IDNameObjectSelectionTable<M, D> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	/**
	 * @param tableModel
	 * @param canSelectMultiple
	 */
	public IDNameDescriptionObjectSelectionTable(M tableModel, boolean canSelectMultiple) {
		super(tableModel, canSelectMultiple);
	}

}
