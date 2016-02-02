package com.mindbox.pe.server.model;

/**
 * Contains information to uniquely identify a cell value of a grid.
 * Useful for cell specific update/retrieval.
 *
 */
public final class GridCellDetail {

	private int gridID;
	private int rowID;
	private String columnName;
	private Object cellValue;

	public Object getCellValue() {
		return cellValue;
	}

	public void setCellValue(Object cellValue) {
		this.cellValue = cellValue;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public int getGridID() {
		return gridID;
	}

	public void setGridID(int gridID) {
		this.gridID = gridID;
	}

	public int getRowID() {
		return rowID;
	}

	public void setRowID(int rowID) {
		this.rowID = rowID;
	}

	public String toString() {
		return "GridCellDetail[grid=" + gridID + "row=" + rowID + ",column=" + columnName + ",value=" + cellValue + ']';
	}
}
