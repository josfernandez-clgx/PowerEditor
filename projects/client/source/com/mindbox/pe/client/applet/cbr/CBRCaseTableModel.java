package com.mindbox.pe.client.applet.cbr;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.common.table.IDNameDescriptionObjectSelectionTableModel;
import com.mindbox.pe.model.CBRCase;

/**
 * 
 * @author Gene Kim

 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class CBRCaseTableModel extends IDNameDescriptionObjectSelectionTableModel<CBRCase> {

	private static final String[] COLUMNS = new String[]{
			ClientUtil.getInstance().getLabel("label.name"),
			ClientUtil.getInstance().getLabel("label.desc")};
	
	public CBRCaseTableModel() {
		super(COLUMNS);
	}

	@Override
	public Object getValueAt(int row, int col) {
		CBRCase c = dataList.get(row); 
		switch (col) {
			case 0 : return c.getName();
			case 1 : return c.getDescription();
		}
		return c;
	}
}
