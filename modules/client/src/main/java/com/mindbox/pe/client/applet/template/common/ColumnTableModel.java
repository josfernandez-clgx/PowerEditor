package com.mindbox.pe.client.applet.template.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.mindbox.pe.model.template.GridTemplateColumn;

/**
 * @author kim
 * @author MindBox
 * @since PowerEditor 
 */
public final class ColumnTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private final List<Object> valueList;

	public ColumnTableModel() {
		super();
		this.valueList = new ArrayList<Object>();
	}

	public void addRow(GridTemplateColumn column) {
		addRow_internal(column);
		fireTableDataChanged();
	}

	private void addRow_internal(GridTemplateColumn column) {
		this.valueList.add(column);
	}

	public void moveRow(int columnNo, boolean up) {
		Object data = this.valueList.remove(columnNo - 1);
		this.valueList.add((up ? columnNo - 2 : columnNo), data);
		fireTableDataChanged();
	}

	public void setData(List<GridTemplateColumn> columnList, boolean dataChanged) {
		this.valueList.clear();
		for (Iterator<GridTemplateColumn> iter = columnList.iterator(); iter.hasNext();) {
			addRow_internal(iter.next());
		}
		if (dataChanged) {
			fireTableDataChanged();
		}
	}

	public void removeRow(GridTemplateColumn column) {
		if (valueList.contains(column)) {
			valueList.remove(column);
		}
		fireTableDataChanged();
	}

	public void removeAllRows() {
		this.valueList.clear();
		fireTableDataChanged();
	}

	public int getRowCount() {
		return valueList.size();
	}

	public int getColumnCount() {
		return 2;
	}

	public GridTemplateColumn getColumnAt(int row) {
		return (GridTemplateColumn) valueList.get(row);
	}

	public String getColumnName(int col) {
		switch (col) {
		case 0:
			return "ID";
		case 1:
			return "Title";
		default:
			return "ERROR";
		}
	}

	public Class<?> getColumnClass(int col) {
		switch (col) {
		case 0:
			return Integer.class;
		default:
			return String.class;
		}
	}

	public Object getValueAt(int row, int col) {
		GridTemplateColumn column = (GridTemplateColumn) valueList.get(row);
		switch (col) {
		case 0:
			return new Integer(column.getID());
		case 1:
			return column.getTitle();
		default:
			return column;
		}
	}

	public int indexOf(GridTemplateColumn column) {
		return valueList.indexOf(column);
	}

	public boolean isCellEditable(int row, int col) {
		return false;
	}
}
