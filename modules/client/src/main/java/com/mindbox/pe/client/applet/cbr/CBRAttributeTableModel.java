package com.mindbox.pe.client.applet.cbr;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.common.table.IDNameDescriptionObjectSelectionTableModel;
import com.mindbox.pe.model.cbr.CBRAttribute;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class CBRAttributeTableModel extends IDNameDescriptionObjectSelectionTableModel<CBRAttribute> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private static final String[] COLUMNS = new String[] {
			ClientUtil.getInstance().getLabel("label.name"),
			ClientUtil.getInstance().getLabel("label.cbr.attribute.type"),
			ClientUtil.getInstance().getLabel("label.desc") };

	public CBRAttributeTableModel() {
		super(COLUMNS);
	}

	@Override
	public Object getValueAt(int row, int col) {
		CBRAttribute att = dataList.get(row);
		switch (col) {
		case 0:
			return att.getName();
		case 1:
			return att.getAttributeType().getName();
		case 2:
			return att.getDescription();
		}
		return att;
	}
}
