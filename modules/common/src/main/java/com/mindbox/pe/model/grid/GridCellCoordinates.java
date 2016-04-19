package com.mindbox.pe.model.grid;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Immuatable coordinates to one cell of a grid.  Row and column values are zero indexed. 
 * 
 * There is no "natural ordering" over this class.  Rather, two comparators 
 * RowFirstComparator and ColumnFirstComparator are available for sorting.
 * 
 * Includes a general purpose payload property for associating data with a cell, without 
 * modifying the cell value itself.
 */
public final class GridCellCoordinates implements Serializable {

	private static final long serialVersionUID = -1398944467004394970L;
	
	private final int row;
	private final int column;
	private Serializable payload; 

	public GridCellCoordinates(int row, int column) {
		if (row < 0) {
			throw new IllegalArgumentException("Row must be non-negative; found: " + row);
		}
		if (column < 0) {
			throw new IllegalArgumentException("Column must be non-negative; found: " + column);
		}
		this.row = row;
		this.column = column;
	}

	public GridCellCoordinates(int row, int column, Serializable payload) {
		this(row, column);
		setPayload(payload);
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}

	/**
	 * A general purpose container for associating data with a cell, without 
	 * modifying the cell value itself.
	 */
	public Serializable getPayload() {
		return payload;
	}

	public void setPayload(Serializable payload) {
		this.payload = payload;
	}

	/** 
	 * Does not consider payload.  Two instances with the same row and column but different payload are equal.
	 * This is consistent with RowFirstComparator and ColumnFirstComparator.
	 */
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (o.getClass().getName().equals(GridCellCoordinates.class.getName())) {
			GridCellCoordinates that = (GridCellCoordinates) o;
			return this.row == that.row && this.column == that.column;
		}
		return false;
	}
	
	public int hashCode() {
		return row + column;
	}
	
	public String toString() {
		return "Grid cell (row*col): (" + row + "*" + column + (payload == null ? ")" : "), " + payload);
	}
	
	public static interface GridCellCoordinatesComparator extends Comparator<GridCellCoordinates> {}
	
	public static class RowFirstComparator implements GridCellCoordinatesComparator, Serializable {

		private static final long serialVersionUID = 9064506730836133551L;

		public int compare(GridCellCoordinates gcc1, GridCellCoordinates gcc2) {
			return gcc1.row - gcc2.row == 0 ? gcc1.column - gcc2.column : gcc1.row - gcc2.row;
		}
	}

	public static class ColumnFirstComparator implements GridCellCoordinatesComparator, Serializable {

		private static final long serialVersionUID = -2577068782778672347L;

		public int compare(GridCellCoordinates gcc1, GridCellCoordinates gcc2) {
			if (gcc1 == null || gcc2 == null) {
				throw new ClassCastException("Null arg: " + gcc1 + ", " + gcc2);
			}
			return gcc1.column - gcc2.column == 0 ? gcc1.row - gcc2.row : gcc1.column - gcc2.column;
		}
	}

}
