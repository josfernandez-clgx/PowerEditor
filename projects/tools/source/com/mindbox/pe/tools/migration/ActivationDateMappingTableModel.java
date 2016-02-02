package com.mindbox.pe.tools.migration;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.common.table.AbstractSelectionTableModel;
import com.mindbox.pe.common.config.UIConfiguration;

/**
 * Model for guideline date mapping table.
 * 
 * @since PowerEditor 4.2.0
 */
class ActivationDateMappingTableModel extends AbstractSelectionTableModel<GuidelineDateMap> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6004335578459858126L;

	/**
	 * Returns false for all rows.
	 */
	public boolean isCellEditable(int row, int col) {
		return col == 2 || col == 1;
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (dataList == null || dataList.size() < row) {
			ClientUtil.printError("getValueAt: Invalid entities: " + dataList);
			return null;
		}

		GuidelineDateMap map = getMapAt(row);
		switch (col) {
		case 0:
			return UIConfiguration.FORMAT_DATE_TIME_SEC.format(map.getDate());
		case 1:
			return map.getName();
		case 2:
			return map.getDescription();
		default:
			return map;
		}
	}

	private final GuidelineDateMap getMapAt(int row) {
		return (GuidelineDateMap) dataList.get(row);
	}

	@Override
	public String getColumnName(int arg0) {
		switch (arg0) {
		case 0:
			return "Date";
		case 1:
			return "Name";
		case 2:
			return "Description";
		}
		return super.getColumnName(arg0);
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		if (row < 0 || row >= dataList.size() || value == null || !(value instanceof String)) return;
		GuidelineDateMap map = getMapAt(row);
		if (map != null) {
			switch (col) {
			case 1:
				map.setName((String) value);
				break;
			case 2:
				map.setDescription(((String) value));
				break;
			}
		}
	}

	public final void fireUpdateRow(int row) {
		super.fireTableRowsUpdated(row, row);
	}
}