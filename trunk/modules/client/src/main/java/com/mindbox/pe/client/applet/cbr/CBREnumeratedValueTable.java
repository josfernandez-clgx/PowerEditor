package com.mindbox.pe.client.applet.cbr;

import java.awt.Insets;

import javax.swing.ListSelectionModel;

import com.mindbox.pe.common.ui.AbstractSortableTable;
import com.mindbox.pe.model.cbr.CBREnumeratedValue;

public final class CBREnumeratedValueTable extends AbstractSortableTable<CBREnumeratedValueTableModel, CBREnumeratedValue> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public CBREnumeratedValueTable(CBREnumeratedValueTableModel tableModel) {
		super(tableModel);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		initTable(tableModel.getColumnNames());
	}

	public CBREnumeratedValue getCBREnumeratedValueAt(int rowInView) {
		return (CBREnumeratedValue) getModel().getValueAt(convertRowIndexToModel(rowInView), -1);
	}

	public Insets getInsets() {
		return new Insets(1, 2, 1, 1);
	}

	protected void displaySelectedRowDetails() {
		getSelectedRow();
	}

	@Override
	protected void initColumns(String[] columnNames) {
		super.initColumns(columnNames);
		if (columnNames.length > 0) {
			getColumnModel().getColumn(0).setPreferredWidth(150);
		}
	}

	protected void initTable(String[] columnNames) {
		setAutoResizeMode(2);
		setRowSelectionAllowed(true);
		setRowHeight(22);
		getSelectionTableModel().setDataList(new java.util.ArrayList<CBREnumeratedValue>());
	}

}
