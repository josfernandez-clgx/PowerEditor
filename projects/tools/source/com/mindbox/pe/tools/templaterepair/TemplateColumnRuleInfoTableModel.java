package com.mindbox.pe.tools.templaterepair;

import java.util.Arrays;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.common.table.AbstractSelectionTableModel;

/**
 *
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.1.0
 */
class TemplateColumnRuleInfoTableModel extends AbstractSelectionTableModel<TemplateColumnRuleInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -679192724466362182L;
	
	static final String[] COLUMN_NAMES = new String[] { "Template ID", "Column No", "Template Name", "Template Version", "Template Usage"};


	public TemplateColumnRuleInfoTableModel() {
		super(COLUMN_NAMES);
	}

	public TemplateColumnRuleInfo[] getValues() {
		return (TemplateColumnRuleInfo[]) dataList.toArray(new TemplateColumnRuleInfo[0]);
	}

	public void setValues(TemplateColumnRuleInfo[] values) {
		dataList.clear();
		dataList.addAll(Arrays.asList(values));
		fireTableDataChanged();
	}

	public Object getValueAt(int row, int col) {
		if (dataList == null || dataList.size() < row) {
			ClientUtil.printError("getValueAt: Invalid entities: " + dataList);
			return null;
		}

		TemplateColumnRuleInfo info = (TemplateColumnRuleInfo) dataList.get(row);
		switch (col) {
		case 0:
			return new Integer(info.getID());
		case 1:
			return new Integer(info.getColumnID());
		case 2:
			return info.getName();
		case 3:
			return info.getVersion();
		case 4:
			return info.getUsage();
		default:
			return info;
		}
	}

}