/*
 * Created on 2004. 8. 6.
 */
package com.mindbox.pe.client.applet.template.guideline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.model.ColumnMessageFragmentDigest;

/**
 * @author kim
 * @author MindBox
 * @since PowerEditor 
 */
public final class ColumnMessageFragmentTableModel extends AbstractTableModel {

	private final List<ColumnMessageFragmentDigest> valueList;

	public ColumnMessageFragmentTableModel() {
		super();
		this.valueList = new ArrayList<ColumnMessageFragmentDigest>();
	}

	public void addRow(ColumnMessageFragmentDigest messageDigest) {
		addRow_internal(messageDigest);
		fireTableDataChanged();
	}

	private void addRow_internal(ColumnMessageFragmentDigest messageDigest) {
		this.valueList.add(messageDigest);
	}

	public void updateRow(int row) {
		fireTableRowsUpdated(row, row);
		fireTableDataChanged();
	}

	public void setData(List<ColumnMessageFragmentDigest> digestList) {
		this.valueList.clear();
		for (Iterator<ColumnMessageFragmentDigest> iter = digestList.iterator(); iter.hasNext();) {
			addRow_internal(iter.next());
		}
		fireTableDataChanged();
	}

	public void removeRow(ColumnMessageFragmentDigest column) {
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
		return 7;
	}

	public ColumnMessageFragmentDigest getColumnAt(int row) {
		return valueList.get(row);
	}

	public String getColumnName(int col) {
		switch (col) {
		case 0:
			return ClientUtil.getInstance().getLabel("label.type");
		case 1:
			return ClientUtil.getInstance().getLabel("label.cell.selection");
		case 2:
			return ClientUtil.getInstance().getLabel("label.range.style");
		case 3:
			return ClientUtil.getInstance().getLabel("label.delim.enum");
		case 4:
			return ClientUtil.getInstance().getLabel("label.delim.enum.final");
		case 5:
			return ClientUtil.getInstance().getLabel("label.prefix.enum");
		case 6:
			return ClientUtil.getInstance().getLabel("label.message.text");
		default:
			return "ERROR";
		}
	}

	public Class<String> getColumnClass(int col) {
		return String.class;
	}

	public Object getValueAt(int row, int col) {
		ColumnMessageFragmentDigest digest = valueList.get(row);
		switch (col) {
		case 0:
			return digest.getType();
		case 1:
			return digest.getCellSelection();
		case 2:
			return digest.getRangeStyle();
		case 3:
			return digest.getEnumDelimiter();
		case 4:
			return digest.getEnumFinalDelimiter();
		case 5:
			return digest.getEnumPrefix();
		case 6:
			return digest.getText();
		default:
			return digest;
		}
	}

	public int indexOf(ColumnMessageFragmentDigest column) {
		return valueList.indexOf(column);
	}

	public boolean isCellEditable(int row, int col) {
		return false;
	}

}

