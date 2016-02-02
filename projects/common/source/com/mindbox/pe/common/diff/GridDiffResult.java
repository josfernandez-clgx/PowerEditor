package com.mindbox.pe.common.diff;

/**
 * Encapsulates results of grid diff.
 * 
 * @author kim
 *
 */
public interface GridDiffResult {

	int[] getDeletedRows();

	int[] getInsertedRows();

	GridCellValueChangeDetailSet getGridCellValueChangeDetailSet();

	boolean hasDeletedRow();

	boolean hasInsertedRow();

}
