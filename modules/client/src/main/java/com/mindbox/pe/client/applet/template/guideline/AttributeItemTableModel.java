/*
 * Created on 2004. 8. 6.
 */
package com.mindbox.pe.client.applet.template.guideline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.model.template.ColumnAttributeItemDigest;

/**
 * @author kim
 * @author MindBox
 * @since PowerEditor 
 */
public final class AttributeItemTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private final List<ColumnAttributeItemDigest> valueList;

	public AttributeItemTableModel() {
		super();
		this.valueList = new ArrayList<ColumnAttributeItemDigest>();
	}

	public void addRow(ColumnAttributeItemDigest messageDigest) {
		addRow_internal(messageDigest);
		fireTableDataChanged();
	}

	private void addRow_internal(ColumnAttributeItemDigest messageDigest) {
		this.valueList.add(messageDigest);
	}

	public void updateRow(int row) {
		fireTableRowsUpdated(row, row);
	}

	public List<ColumnAttributeItemDigest> getData() {
		return Collections.unmodifiableList(valueList);
	}

	public void setData(List<ColumnAttributeItemDigest> digestList) {
		this.valueList.clear();
		for (Iterator<ColumnAttributeItemDigest> iter = digestList.iterator(); iter.hasNext();) {
			addRow_internal(iter.next());
		}
		fireTableDataChanged();
	}

	public void removeRow(ColumnAttributeItemDigest column) {
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

	public ColumnAttributeItemDigest getColumnAt(int row) {
		return valueList.get(row);
	}

	public String getColumnName(int col) {
		switch (col) {
		case 0:
			return ClientUtil.getInstance().getLabel("label.name.display");
		case 1:
			return ClientUtil.getInstance().getLabel("label.attribute");
		default:
			return "ERROR";
		}
	}

	public Class<String> getColumnClass(int col) {
		return String.class;
	}

	public Object getValueAt(int row, int col) {
		ColumnAttributeItemDigest digest = valueList.get(row);
		switch (col) {
		case 0:
			return digest.getDisplayValue();
		case 1:
			return digest.getName();
		default:
			return digest;
		}
	}

	public void setValueAt(Object value, int row, int col) {
		if (value instanceof String && row >= 0 && row < valueList.size()) {
			ColumnAttributeItemDigest digest = valueList.get(row);
			switch (col) {
			case 0:
				digest.setDisplayValue((String) value);
			case 1:
				digest.setName((String) value);
			default:
				throw new IllegalArgumentException("Invalid col: " + col);
			}
		}
	}

	public int indexOf(ColumnAttributeItemDigest column) {
		return valueList.indexOf(column);
	}

	public boolean isCellEditable(int row, int col) {
		return false;
	}

}
