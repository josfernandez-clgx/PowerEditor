package com.mindbox.pe.common.diff;

import java.util.List;

import com.mindbox.pe.xsd.audit.ChangeDetail;


/**
 * Encapsulates results of grid diff.
 * 
 * @author kim
 *
 */
public interface GridDiffResult {

	GridCellValueChangeDetailSet getGridCellValueChangeDetailSet();

	boolean hasDeletedRow();

	boolean hasInsertedRow();

	List<ChangeDetail> getDeletedRowChangeDetails();

	List<ChangeDetail> getInsertedRowChangeDetails();
}
