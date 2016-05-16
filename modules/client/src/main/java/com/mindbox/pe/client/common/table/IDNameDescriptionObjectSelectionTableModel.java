package com.mindbox.pe.client.common.table;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.model.AbstractIDNameDescriptionObject;

/**
 * Model for IDName Object selection table.
 * 
 * @since PowerEditor 1.0
 */
public class IDNameDescriptionObjectSelectionTableModel<D extends AbstractIDNameDescriptionObject> extends IDNameObjectSelectionTableModel<D> {

	private static final long serialVersionUID = -3951228734910107454L;

	/**
	 * Create new IDName Object selection table model with the specified column names.
	 * @param columnNames columnNames
	 */
	public IDNameDescriptionObjectSelectionTableModel(String... columnNames) {
		super(columnNames);
		if (columnNames == null || columnNames.length < 2) {
			throw new IllegalArgumentException("Invalid column names: length must be greather than 2");
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (dataList == null || dataList.size() < row) {
			ClientUtil.printError("getValueAt: Invalid entities: " + dataList);
			return null;
		}

		AbstractIDNameDescriptionObject value = dataList.get(row);
		switch (col) {
		case 0:
			return value.getName();
		case 1:
			return value.getDescription();
		default:
			return value;
		}
	}

}
