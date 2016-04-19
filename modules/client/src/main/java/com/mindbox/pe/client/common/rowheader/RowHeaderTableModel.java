package com.mindbox.pe.client.common.rowheader;

import javax.swing.table.AbstractTableModel;

class RowHeaderTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public void setRowCount(int i) {
		mRowCount = i;
		fireTableDataChanged();
	}

	public int getRowCount() {
		return mRowCount;
	}

	public int getColumnCount() {
		return 1;
	}

	RowHeaderTableModel() {
		mRowCount = 0;
	}

	public boolean isCellEditable(int i, int j) {
		return false;
	}

	public Object getValueAt(int i, int j) {
		return new Integer(i + 1);
	}

	private int mRowCount;
}
