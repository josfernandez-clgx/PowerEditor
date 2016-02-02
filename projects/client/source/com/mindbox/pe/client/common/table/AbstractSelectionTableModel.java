package com.mindbox.pe.client.common.table;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.model.DateSynonym;


/**
 * Model for selection table.
 * This is not thread-safe.
 * @since PowerEditor 5.5.1
 */
public abstract class AbstractSelectionTableModel<D> extends AbstractTableModel {

	protected final List<D> dataList;
	private final String[] columnNames;
	private boolean showDateNames = true;

	protected AbstractSelectionTableModel(String... columnNames) {
		super();
		this.columnNames = columnNames;
		this.dataList = new ArrayList<D>();
	}

	@Override
	public final int getColumnCount() {
		return getColumnNames().length;
	}

	@Override
	public Class<?> getColumnClass(int i) {
		return String.class;
	}

	@Override
	public final int getRowCount() {
		return ((dataList == null) ? 0 : dataList.size());
	}

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public final boolean containsData(D val) {
		return dataList.contains(val);
	}

	public final void addData(D data) {
		if (data != null && !dataList.contains(data)) {
			dataList.add(data);
			fireTableDataChanged();
		}
	}

	public final void addDataList(List<D> dataList) {
		boolean added = false;
		for (D row : dataList) {
			if (!this.dataList.contains(row)) {
				this.dataList.add(row);
				added = true;
			}
		}
		if (added) fireTableDataChanged();
	}

	public final void removeData(D data) {
		if (data != null && dataList.contains(data)) {
			dataList.remove(data);
			fireTableDataChanged();
		}
	}

	public final void removeDataAt(int row) {
		dataList.remove(row);
		fireTableDataChanged();
	}

	public void removeDataList(List<D> dataList) {
		boolean removed = false;
		for (D row : dataList) {
			if (this.dataList.contains(row)) {
				this.dataList.remove(row);
				removed = true;
			}
		}
		if (removed) fireTableDataChanged();
	}

	public final void clearDataList() {
		dataList.clear();
		fireTableDataChanged();
	}

	public final List<D> getDataList() {
		return Collections.unmodifiableList(dataList);
	}

	public void setDataList(List<D> data) {
		dataList.clear();
		if (data != null) {
			dataList.addAll(data);
		}
		fireTableDataChanged();
	}

	public void refreshData() {
		fireTableDataChanged();
	}

	// for Date synonym column support
	protected final String toDisplayString(DateSynonym dateSynonym) {
		return toDisplayString(dateSynonym, UIConfiguration.FORMAT_DATE_TIME_SEC);
	}

	protected final String toDisplayString(DateSynonym dateSynonym, SimpleDateFormat dateFormat) {
		if (dateSynonym == null) return "";
		if (dateFormat == null) dateFormat = UIConfiguration.FORMAT_YYYY_MM_DD_TIME_SEC;

		return (showDateNames ? dateSynonym.getName() : (dateSynonym.getDate() == null ? "" : dateFormat.format(dateSynonym.getDate())));
	}

	public final void setShowDateNames(boolean showDateNames) {
		this.showDateNames = showDateNames;
	}
}
