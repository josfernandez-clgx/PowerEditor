package com.mindbox.pe.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.table.GridCellValue;

/**
 * Grid Cell value container.
 * @author kim
 * @author MindBox
 * @since PowerEditor 4.0.0
 */
class GridValueContainer<C extends AbstractTemplateColumn> implements GridValueContainable, Serializable {

	private static final long serialVersionUID = 2004080970000L;
	
	private static final String toKey(int rowID, String columnName) {
		return rowID + '.' + columnName;
	}

	private static final boolean isKeyForColumn(String key, String columnName) {
		int index = key.indexOf(".");
		if (index < 0)
			return false;
		else {
			return columnName.equals(key.substring(index + 1));
		}
	}

	private static final int getRowIDFromKey(String key) {
		int index = key.indexOf(".");
		assert (index >= 0);
		return Integer.parseInt(key.substring(0, index));
	}

	private AbstractTemplateCore<C> template;
	private final Map<String, Object> cellValueMap;
	private int rowCount = 0;

	GridValueContainer(AbstractTemplateCore<C> template) {
		if (template == null) throw new IllegalArgumentException("template cannot be null");
		this.template = template;
		this.cellValueMap = new HashMap<String, Object>();
	}

	private String[] extractColumnNames() {
		List<String> list = new ArrayList<String>();
		for (int i = 1; i <= template.getNumColumns(); i++) {
			AbstractTemplateColumn element = template.getColumn(i);
			list.add(element.getName());
		}
		return list.toArray(new String[0]);
	}

	void repairForColumnNameChange(String oldName, String newName) {
		List<String> keyList = new ArrayList<String>();
		for (String key : cellValueMap.keySet()) {
			if (isKeyForColumn(key, oldName)) {
				keyList.add(key);
			}
		}
		for (String key : keyList) {
			Object value = cellValueMap.remove(key);
			cellValueMap.put(toKey(getRowIDFromKey(key), newName), value);
		}
	}

	public void setValue(int rowID, String columnName, Object value) {
		if (columnName != null) {
			cellValueMap.put(toKey(rowID, columnName), value);
			if (rowCount < rowID) rowCount = rowID;
		}
	}

	public void setValue(int rowID, int col, Object value) {
		setValue(rowID, getColumnNameFor(col), value);
	}

	public Object getCellValue(int row, String columnName) {
		if (columnName == null) {
			return null;
		}
		return cellValueMap.get(toKey(row, columnName));
	}

	void setTemplate(AbstractTemplateCore<C> template) {
		assert (template != null);
		this.template = template;
	}

	public int getNumRows() {
		return rowCount;
	}

	public void setNumRows(int i) {
		rowCount = i;
	}

	public boolean hasSameCellValues(GridValueContainable valueContainer) {
		if (this.rowCount != valueContainer.getNumRows()) {
			return false;
		}

		String[] columnNames = extractColumnNames();
		if (columnNames.length != valueContainer.getColumnNames().length) {
			return false;
		}

		for (int row = 1; row <= rowCount; row++) {
			for (int c = 0; c < columnNames.length; c++) {
				if (!UtilBase.isSameGridCellValue(getCellValue(row, columnNames[c]), valueContainer.getCellValue(row, columnNames[c]))) {
					return false;
				}
			}
		}
		return true;
	}

	// Assume the specified valueContainable is for the same template
	@Override
	public boolean hasSameRow(int rowInThis, String[] columnNames, int rowInTarget, GridValueContainable valueContainable) {
		if (rowInThis > rowCount || rowInTarget > valueContainable.getNumRows()) return false;
		for (int c = 0; c < columnNames.length; c++) {
			if (!UtilBase.isSameGridCellValue(getCellValue(rowInThis, columnNames[c]), valueContainable.getCellValue(rowInTarget, columnNames[c]))) {
				return false;
			}
		}
		return true;
	}

	private String getColumnNameFor(int col) {
		AbstractTemplateColumn column = template.getColumn(col);
		if (column == null) return null;
		return column.getName();
	}

	public Object getCellValueObject(int row, int col, Object defaultValue) throws InvalidDataException {
		Object value = getCellValue(row, getColumnNameFor(col));
		return (value == null ? defaultValue : value);
	}

	public Object[][] getDataObjects() {
		Object[][] values = new Object[rowCount][template.getNumColumns()];
		for (int r = 0; r < rowCount; r++) {
			for (int c = 0; c < template.getNumColumns(); c++) {
				try {
					values[r][c] = getCellValueObject(r + 1, c + 1, null);
				}
				catch (InvalidDataException e) {
					values[r][c] = null;
				}
			}
		}
		return values;
	}

	public void clearValues() {
		this.rowCount = 0;
		this.cellValueMap.clear();
	}

	synchronized void setDataList(List<List<Object>> list) {
		if (list == null) {
			clearValues();
		}
		else {
			for (int i = 0; i < list.size(); i++) {
				List<Object> rowValueList = list.get(i);
				for (int j = 0; j < rowValueList.size(); j++) {
					setValue(i + 1, j + 1, rowValueList.get(j));
				}
			}
			this.rowCount = list.size();
		}
	}

	@Override
	public boolean isEmpty() {
		return (rowCount == 0 || cellValueMap.isEmpty());
	}

	@Override
	public boolean isEmptyRow(int row) {
		if (row > rowCount) return true;
		String[] columnNames = extractColumnNames();
		for (String column : columnNames) {
			Object cellValue = getCellValue(row, column);
			if (cellValue != null && !UtilBase.isEmpty(cellValue.toString())) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public synchronized void copyCellValue(GridValueContainable source) {
		if (source instanceof GridValueContainer) {
			this.cellValueMap.clear();
			for (Map.Entry<String, Object> entry : ((GridValueContainer<C>) source).cellValueMap.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				if (value instanceof GridCellValue) {
					GridCellValue copiedValue = ((GridCellValue) value).copy();
					this.cellValueMap.put(key, copiedValue);
				}
				else {
					this.cellValueMap.put(key, value);
				}
			}
		}
		else {
			this.rowCount = source.getNumRows();
			String[] columnNames = extractColumnNames();
			for (int i = 0; i < rowCount; i++) {
				for (int j = 0; j < columnNames.length; j++) {
					Object sourceObj = source.getCellValue(i + 1, columnNames[j]);
					if (sourceObj instanceof GridCellValue) {
						setValue(i + 1, columnNames[j], ((GridCellValue) sourceObj).copy());
					}
					else {
						setValue(i + 1, columnNames[j], sourceObj);
					}
				}
			}
		}
	}

	public String[] getColumnNames() {
		return extractColumnNames();
	}

	public final List<String> getRuleIDColumnNames() {
		return template == null ? null : template.getRuleIDColumnNames();
	}
}