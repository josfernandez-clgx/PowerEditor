package com.mindbox.pe.common.diff;

import java.util.ArrayList;
import java.util.List;

import com.mindbox.pe.common.UtilBase;

/**
 * This is not thread safe. Do not reuse instances.
 * @author kim
 *
 */
public class DefaultGridDiffResult implements GridDiffResult {

	private List<Integer> insertedRowList;
	private List<Integer> deletedRowList;
	private final GridCellValueChangeDetailSet detailSet;

	public DefaultGridDiffResult() {
		insertedRowList = new ArrayList<Integer>();
		this.deletedRowList = new ArrayList<Integer>();
		detailSet = new GridCellValueChangeDetailSet();
	}

	public void addInsertedRow(int row) {
		if (insertedRowList.contains(row)) throw new IllegalArgumentException("Already added: " + row);
		if (deletedRowList.contains(row)) throw new IllegalArgumentException("The specified row was added as a deleted row: " + row);
		insertedRowList.add(row);
	}

	public void addDeletedRow(int row) {
		if (deletedRowList.contains(row)) throw new IllegalArgumentException("Already added: " + row);
		if (insertedRowList.contains(row)) throw new IllegalArgumentException("The specified row was added as an inserted row: " + row);
		deletedRowList.add(row);
	}

	public GridCellValueChangeDetailSet getGridCellValueChangeDetailSet() {
		return detailSet;
	}

	public int[] getInsertedRows() {
		return UtilBase.toIntArray(insertedRowList);
	}

	public int[] getDeletedRows() {
		return UtilBase.toIntArray(deletedRowList);
	}

	public boolean hasDeletedRow() {
		return !deletedRowList.isEmpty();
	}

	public boolean hasInsertedRow() {
		return !insertedRowList.isEmpty();
	}
}
