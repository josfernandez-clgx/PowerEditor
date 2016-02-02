package com.mindbox.pe.client.common.table;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.model.IDNameObject;

/**
 * Model for IDName Object selection table.
 * 
 * @since PowerEditor 1.0
 */
public class IDNameObjectSelectionTableModel<D extends IDNameObject> extends AbstractSelectionTableModel<D> {

	/**
	 * Create new IDName Object selection table model with the specified column names.
	 * 
	 * @param columnNames
	 */
	public IDNameObjectSelectionTableModel(String... columnNames) {
		super(columnNames);
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (dataList == null || dataList.size() < row) {
			ClientUtil.printError("getValueAt: Invalid entities: " + dataList);
			return null;
		}

		IDNameObject nameObject = dataList.get(row);
		switch (col) {
		case 0:
			return nameObject.getName();
		default:
			return nameObject;
		}
	}

	public final void fireUpdateRow(int row) {
		super.fireTableRowsUpdated(row, row);
	}
	
	public final int getIndexOf(int id) {
		for (int i = 0; i < dataList.size(); i++) {
			if (dataList.get(i).getID() == id) return i;
		}
		return -1;
	}
	
	public final D getDataWithID(int id) {
		for (D data : dataList) {
			if (data.getID() == id) return data;
		}
		return null;
	}
}
