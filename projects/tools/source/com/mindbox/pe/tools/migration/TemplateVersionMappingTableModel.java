package com.mindbox.pe.tools.migration;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.common.table.AbstractSelectionTableModel;

/**
 * Model for template version mapping table.
 * 
 * @since PowerEditor 4.2.0
 */
class TemplateVersionMappingTableModel extends AbstractSelectionTableModel<TemplateVersionMap> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3718214198510192124L;

	/**
	 * Returns false for all rows.
	 */
	public boolean isCellEditable(int row, int col) {
		return col == 4 || col == 5;
	}

	public Object getValueAt(int row, int col) {
		if (dataList == null || dataList.size() < row) {
			ClientUtil.printError("getValueAt: Invalid entities: " + dataList);
			return null;
		}

		TemplateVersionMap map = getMapAt(row);
		switch (col) {
		case 0:
			return new Integer(map.getID());
		case 1:
			return map.getName();
		case 2:
			return map.getUsage();
		case 3:
			return map.getLabel();
		case 4:
			return map.getVersion();
		case 5:
			return (map.getParentID() > 0 ? new Integer(map.getParentID()) : null);
		default:
			return map;
		}
	}

	private final TemplateVersionMap getMapAt(int row) {
		return (TemplateVersionMap) dataList.get(row);
	}

	public String getColumnName(int arg0) {
		switch (arg0) {
		case 0:
			return "ID";
		case 1:
			return "Name";
		case 2:
			return "Usage";
		case 3:
			return "Activation Label";
		case 4:
			return "Version";
		case 5:
			return "Parent";
		}
		return String.valueOf(arg0);
	}

	public void setValueAt(Object value, int row, int col) {
		//System.out.println(">>> TemplateVersionMappingTableModel: setValueAt: " +row+","+col + " = " + value);
		if (row < 0 || row >= dataList.size() || value == null) return;
		TemplateVersionMap map = getMapAt(row);
		if (map != null) {
			switch (col) {
			case 4:
				map.setVersion((String) value);
				break;
			case 5:
				map.setParentID(((Integer) value).intValue());
				break;
			}
		}
	}

	public final void fireUpdateRow(int row) {
		super.fireTableRowsUpdated(row, row);
	}
}