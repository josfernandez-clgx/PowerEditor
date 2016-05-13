package com.mindbox.pe.model.grid;

import java.util.List;

import com.mindbox.pe.model.exceptions.InvalidDataException;

/**
 * Grid cell value container.
 * 
 * @author kim
 * @author MindBox
 * @since PowerEditor 4.0.0
 */
public interface GridValueContainable {

	void clearValues();

	void copyCellValue(GridValueContainable source);

	Object getCellValue(int row, String column);

	/**
	 * 
	 * @param i row
	 * @param j column
	 * @param defaultValue defaultValue
	 * @return the cell value object
	 * @throws InvalidDataException on error
	 * @since PowerEditor 4.1.1 if i or j is invalid
	 */
	Object getCellValueObject(int i, int j, Object defaultValue) throws InvalidDataException;

	String[] getColumnNames();

	String getColumnTitle(String columnName);

	Object[][] getDataObjects();

	/**
	 * Gets the number of rows.
	 * @return the row count
	 */
	int getNumRows();

	List<String> getRuleIDColumnNames();

	boolean hasSameCellValues(GridValueContainable valueContainer);

	boolean hasSameRow(int row, String[] columnNames, int targetRow, GridValueContainable valueContainable);

	boolean isEmpty();

	boolean isEmptyRow(int row);

	void setNumRows(int i);

	void setValue(int rowID, int col, Object value);

	void setValue(int rowID, String columnName, Object value);
}
