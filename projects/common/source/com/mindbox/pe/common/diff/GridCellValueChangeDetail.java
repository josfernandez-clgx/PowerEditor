package com.mindbox.pe.common.diff;

public class GridCellValueChangeDetail extends AbstractValueChangeDetail implements Comparable<GridCellValueChangeDetail> {

	private final int row;
	private final int column;
	private int hashCode = 0;

	public GridCellValueChangeDetail(int row, int column, Object oldValue, Object newValue) {
		super(oldValue, newValue);
		this.row = row;
		this.column = column;
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}

	public int hashCode() {
		if (hashCode == 0) {
			hashCode = (row + "," + column).hashCode();
		}
		return hashCode;
	}

	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj instanceof GridCellValueChangeDetail) {
			return this.row == ((GridCellValueChangeDetail) obj).row && this.column == ((GridCellValueChangeDetail) obj).column;
		}
		else {
			return false;
		}
	}

	public int compareTo(GridCellValueChangeDetail detail) {
		if (this == detail) return 0;
		if (this.row == detail.row) {
			if (this.column == detail.column) return 0;
			return this.column < detail.column ? -1 : 1;
		}
		else {
			return this.row < detail.row ? -1 : 1;
		}
	}
}
