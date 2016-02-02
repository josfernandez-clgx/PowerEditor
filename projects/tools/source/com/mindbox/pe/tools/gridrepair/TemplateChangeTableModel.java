package com.mindbox.pe.tools.gridrepair;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.common.table.AbstractSelectionTableModel;
import com.mindbox.pe.common.UtilBase;

/**
 *
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.1.0
 */
class TemplateChangeTableModel extends AbstractSelectionTableModel<TemplateColumnChangeSpec> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -855456795724887007L;

	static final String[] COLUMN_NAMES = new String[] { "Template ID", "Added Column Positions", "Delete Columns" };

	public TemplateChangeTableModel() {
		super(COLUMN_NAMES);
	}

	public Object getValueAt(int row, int col) {
		if (dataList == null || dataList.size() < row) {
			ClientUtil.printError("getValueAt: Invalid entities: " + dataList);
			return null;
		}

		TemplateColumnChangeSpec spec = (TemplateColumnChangeSpec) dataList.get(row);
		switch (col) {
		case 0:
			return String.valueOf(spec.getTemplateID());
		case 1:
			return UtilBase.toString(spec.addedColumnPositions());
		case 2:
			return UtilBase.toString(spec.removedColumns());
		default:
			return spec;
		}
	}

}
