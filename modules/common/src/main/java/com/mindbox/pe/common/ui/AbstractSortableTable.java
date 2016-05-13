package com.mindbox.pe.common.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * Table that sorts rows.
 * Extend this to provide a JTable that sorts rows.
 */
public abstract class AbstractSortableTable<M extends AbstractSelectionTableModel<D>, D> extends JTable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	/**
	 * Equivalent to <code>AbstractSortableTable(tableModel, 0)</code>.
	 * @param tableModel tableModel
	 */
	protected AbstractSortableTable(M tableModel) {
		this(tableModel, 0);
	}

	protected AbstractSortableTable(M tableModel, int initalSortColumn) {
		super(tableModel);
		initColumns(tableModel.getColumnNames());

		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableModel);
		setRowSorter(sorter);
		sorter.toggleSortOrder(initalSortColumn);
	}

	public final void add(D object) {
		if (object == null) throw new NullPointerException("object cannot be null");
		getSelectionTableModel().addData(object);
	}

	@SuppressWarnings("unchecked")
	public D getDateObjectAt(int rowInView) {
		return (D) getModel().getValueAt(convertRowIndexToModel(rowInView), -1);
	}

	@SuppressWarnings("unchecked")
	public final D getSelectedDataObject() {
		int row = getSelectedRow();
		if (row >= 0) {
			return (D) getModel().getValueAt(convertRowIndexToModel(row), -1);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public final List<D> getSelectedDataObjects() {
		int[] rows = getSelectedRows();
		List<D> list = new ArrayList<D>();
		for (int i = 0; i < rows.length; i++) {
			list.add((D) getModel().getValueAt(convertRowIndexToModel(rows[i]), -1));
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public final M getSelectionTableModel() {
		return (M) getModel();
	}

	/**
	 * Sets column header's text with the specified column names.
	 * Override this to reset preferred width of each column.
	 * @param columnNames columnNames
	 */
	protected void initColumns(String[] columnNames) {
		int colCount = columnNames.length;
		DefaultTableColumnModel defaulttablecolumnmodel = new DefaultTableColumnModel();
		for (int i = 0; i < colCount; i++) {
			TableColumn tablecolumn = new TableColumn(i);
			tablecolumn.setHeaderValue(columnNames[i]);
			defaulttablecolumnmodel.addColumn(tablecolumn);
		}
		setColumnModel(defaulttablecolumnmodel);
	}

	public final synchronized void refresh() {
		getSelectionTableModel().refreshData();
	}

	// for date sysnonym column support
	public final synchronized void refresh(boolean showDateNames) {
		getSelectionTableModel().setShowDateNames(showDateNames);
		getSelectionTableModel().refreshData();
	}

	public final void remove(D object) {
		if (object == null) throw new NullPointerException("object cannot be null");
		getSelectionTableModel().removeData(object);
	}

	public final void selectOneRow(int rowInView) {
		setRowSelectionInterval(rowInView, rowInView);
	}

	public final void setDataList(List<D> dataList) {
		getSelectionTableModel().setDataList(dataList);
	}

	public final void updateRow(int rowInView) {
		if (rowInView > -1 && rowInView < getModel().getRowCount()) {
			int rowInModel = convertRowIndexToModel(rowInView);
			getSelectionTableModel().fireTableRowsUpdated(rowInModel, rowInModel);
		}
	}

}
