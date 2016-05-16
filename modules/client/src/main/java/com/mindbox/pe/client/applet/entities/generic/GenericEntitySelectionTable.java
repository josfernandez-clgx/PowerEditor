package com.mindbox.pe.client.applet.entities.generic;

import com.mindbox.pe.client.common.table.IDNameObjectSelectionTable;
import com.mindbox.pe.model.GenericEntity;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class GenericEntitySelectionTable extends IDNameObjectSelectionTable<GenericEntitySelectionTableModel, GenericEntity> {

	private static final long serialVersionUID = -3951228734910107454L;

	public GenericEntitySelectionTable(GenericEntitySelectionTableModel tableModel, boolean canSelectMultiple) {
		super(tableModel, canSelectMultiple);
	}

	@Override
	protected void initColumns(String[] columnNames) {
		super.initColumns(columnNames);
		if (columnNames.length > 2) {
			getColumnModel().getColumn(0).setPreferredWidth(25);
			getColumnModel().getColumn(1).setPreferredWidth(160);
			getColumnModel().getColumn(2).setPreferredWidth(160);
		}
	}

}
