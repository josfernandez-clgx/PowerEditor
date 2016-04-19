/*
 * Created on 2004. 8. 9.
 */
package com.mindbox.pe.model.rule;

import java.io.Serializable;

/**
 * Implementation of ColumnReference.
 * @author kim
 * @author MindBox
 * @since PowerEditor 
 */
class ColumnRefImpl implements ColumnReference, Serializable {

	private static final long serialVersionUID = 20040809200000L;

	private int columnNo;

	ColumnRefImpl(int columnNo) {
		assert (columnNo > 0);
		this.columnNo = columnNo;
	}

	public int getColumnNo() {
		return columnNo;
	}

	/**
	 * @param columnNo The columnNo to set.
	 */
	public void setColumnNo(int columnNo) {
		this.columnNo = columnNo;
	}

	public String toString() {
		return "Column-" + columnNo;
	}

}