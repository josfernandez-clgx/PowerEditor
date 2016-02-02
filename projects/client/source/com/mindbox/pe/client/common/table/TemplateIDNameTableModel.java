package com.mindbox.pe.client.common.table;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.model.GridTemplate;

/**
 * Template id name table model.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.1.0
 */
public final class TemplateIDNameTableModel extends IDNameObjectSelectionTableModel<GridTemplate> {

	static final String[] COLUMN_NAMES = new String[] { ClientUtil.getInstance().getLabel("label.name"),
			ClientUtil.getInstance().getLabel("label.id")};

	public TemplateIDNameTableModel() {
		super(COLUMN_NAMES);
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		if (dataList == null || dataList.size() < row) {
			ClientUtil.printError("getValueAt: Invalid entities: " + dataList);
			return null;
		}

		GridTemplate template = dataList.get(row);
		String name = template.getName() + " (" + template.getVersion() + ")";
		switch (col) {
		case 0:
			return name;
		case 1:
			return String.valueOf(template.getID());
		default:
			return template;
		}
	}

}