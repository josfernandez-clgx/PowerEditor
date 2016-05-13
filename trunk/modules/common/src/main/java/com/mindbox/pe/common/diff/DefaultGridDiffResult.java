package com.mindbox.pe.common.diff;

import java.util.ArrayList;
import java.util.List;

import com.mindbox.pe.xsd.audit.ChangeDetail;

/**
 * This is not thread safe. Do not reuse instances.
 * @author kim
 *
 */
public class DefaultGridDiffResult implements GridDiffResult {

	private final GridCellValueChangeDetailSet detailSet;

	public DefaultGridDiffResult() {
		detailSet = new GridCellValueChangeDetailSet();
	}

	public GridCellValueChangeDetailSet getGridCellValueChangeDetailSet() {
		return detailSet;
	}

	@Override
	public boolean hasDeletedRow() {
		for (final ChangeDetail changeDetail : detailSet.getDetailList()) {
			if (!changeDetail.getRemovedValues().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasInsertedRow() {
		for (final ChangeDetail changeDetail : detailSet.getDetailList()) {
			if (!changeDetail.getInsertedValues().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<ChangeDetail> getDeletedRowChangeDetails() {
		List<ChangeDetail> list = new ArrayList<ChangeDetail>();
		for (final ChangeDetail changeDetail : detailSet.getDetailList()) {
			if (!changeDetail.getRemovedValues().isEmpty()) {
				list.add(changeDetail);
			}
		}
		return list;
	}

	@Override
	public List<ChangeDetail> getInsertedRowChangeDetails() {
		List<ChangeDetail> list = new ArrayList<ChangeDetail>();
		for (final ChangeDetail changeDetail : detailSet.getDetailList()) {
			if (!changeDetail.getInsertedValues().isEmpty()) {
				list.add(changeDetail);
			}
		}
		return list;
	}
}
