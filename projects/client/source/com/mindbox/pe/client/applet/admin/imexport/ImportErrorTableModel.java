package com.mindbox.pe.client.applet.admin.imexport;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.common.validate.MessageDetail;

/**
 * Template id name table model.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.1.0
 */
final class ImportErrorTableModel extends AbstractTableModel {

	private static final String[] COLUMN_NAMES = new String[] {
			ClientUtil.getInstance().getLabel("label.message"),
			ClientUtil.getInstance().getLabel("label.item") };

	private final List<MessageDetail> dataList = new ArrayList<MessageDetail>();

	public ImportErrorTableModel() {
	}

	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (dataList == null || dataList.size() < row) {
			ClientUtil.printError("getValueAt: Invalid data: " + dataList);
			return null;
		}

		MessageDetail messageDetail = dataList.get(row);
		switch (col) {
		case 0:
			return messageDetail.getMessage();
		case 1:
			return messageDetail.getContext();
		default:
			return messageDetail;
		}
	}

	@Override
	public int getRowCount() {
		return dataList.size();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public void setData(List<MessageDetail> violations) {
		dataList.clear();
		dataList.addAll(violations);
	}
}