package com.mindbox.pe.model;

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
	
	/**
	 * Gets the number of rows.
	 * @return the row count
	 */
	int getNumRows();
	
	void setNumRows(int i);
	
	boolean hasSameCellValues(GridValueContainable valueContainer);
	
	boolean hasSameRow(int row, String[] columnNames, int targetRow, GridValueContainable valueContainable);
	
	boolean isEmpty();
	
	boolean isEmptyRow(int row);
	
	String[] getColumnNames();
	
	List<String> getRuleIDColumnNames();
	
	Object getCellValue(int row, String column);
	
	void clearValues();
	
	/**
	 * 
	 * @param i row
	 * @param j column
	 * @param defaultValue
	 * @return the cell value object
	 * @throws InvalidDataException
	 * @since PowerEditor 4.1.1 if i or j is invalid
	 */
	Object getCellValueObject(int i, int j, Object defaultValue) throws InvalidDataException;
	
	void setValue(int rowID, String columnName, Object value);
	
	void setValue(int rowID, int col, Object value);
	
	Object[][] getDataObjects();
	
	void copyCellValue(GridValueContainable source);	
}
