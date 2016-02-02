package com.mindbox.pe.tools.migration;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.common.table.AbstractSelectionTableModel;

/**
 * Model for guideline action parameter table.
 * 
 * @since PowerEditor 4.2.0
 */
class GuidelineActionParameterTableModel extends AbstractSelectionTableModel<GuidelineActionParameterRow> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3215887251558487925L;

	public Object getValueAt(int row, int col) {
		if (dataList == null || dataList.size() < row) {
			ClientUtil.printError("getValueAt: Invalid entities: " + dataList);
			return null;
		}
		GuidelineActionParameterRow data = getRowAt(row);
		switch (col) {
		case 0:
			return new Integer(data.getActionID());
		case 1:
			return new Integer(data.getParamID());
		case 2:
			return data.getName();
		case 3:
			return data.getDeployType();
		default:
			return data;
		}
	}

	private final GuidelineActionParameterRow getRowAt(int row) {
		return (GuidelineActionParameterRow) dataList.get(row);
	}

	public String getColumnName(int arg0) {
		switch (arg0) {
		case 0:
			return "Action ID";
		case 1:
			return "Parameter ID";
		case 2:
			return "Name";
		case 3:
			return "Deploy Type";
		}
		return super.getColumnName(arg0);
	}

	public final void fireUpdateRow(int row) {
		super.fireTableRowsUpdated(row, row);
	}
}