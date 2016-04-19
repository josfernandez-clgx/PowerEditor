package com.mindbox.pe.common.diff;

import static com.mindbox.pe.common.AuditConstants.KB_MOD_TYPE_ADD_GRID_ROW;
import static com.mindbox.pe.common.AuditConstants.KB_MOD_TYPE_MODIFY_GRID_CELL;
import static com.mindbox.pe.common.AuditConstants.KB_MOD_TYPE_REMOVE_GRID_ROW;
import static com.mindbox.pe.common.AuditConstants.getModTypeDescription;
import static com.mindbox.pe.common.diff.DiffHelper.convertToAuditStringValue;
import static com.mindbox.pe.common.diff.DiffHelper.convertToNativeValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.grid.AbstractGrid;
import com.mindbox.pe.model.grid.GridValueContainable;
import com.mindbox.pe.xsd.audit.CellValueSet;
import com.mindbox.pe.xsd.audit.ChangeDetail;

public final class SimpleGridDiffEngine implements GridDiffEngine {

	private static void addDeletedRowChangeDetail(final GridCellValueChangeDetailSet changeDetailSet, final GridValueContainable grid, int row) {
		final ChangeDetail changeDetail = new ChangeDetail();
		changeDetail.setChangeType(getModTypeDescription(KB_MOD_TYPE_REMOVE_GRID_ROW));
		changeDetail.setRowNumber(row);
		for (final String columnName : grid.getColumnNames()) {
			changeDetail.getRemovedValues().add(asCellValueSet(grid, row, columnName));
		}
		changeDetailSet.add(changeDetail);
	}

	private static void addInsertedRowChangeDetail(final GridCellValueChangeDetailSet changeDetailSet, final GridValueContainable grid, int row) {
		final ChangeDetail changeDetail = new ChangeDetail();
		changeDetail.setChangeType(getModTypeDescription(KB_MOD_TYPE_ADD_GRID_ROW));
		changeDetail.setRowNumber(row);
		for (final String columnName : grid.getColumnNames()) {
			changeDetail.getInsertedValues().add(asCellValueSet(grid, row, columnName));
		}
		changeDetailSet.add(changeDetail);
	}

	private static CellValueSet asCellValueSet(final GridValueContainable grid, int row, String columnName) {
		final CellValueSet cellValueSet = new CellValueSet();
		cellValueSet.setColumnName(grid.getColumnTitle(columnName));
		cellValueSet.setColumnId(columnName);
		cellValueSet.setCellValue(convertToAuditStringValue(grid.getCellValue(row, columnName)));
		cellValueSet.setNativeValue(convertToNativeValue(grid.getCellValue(row, columnName)));
		return cellValueSet;
	}

	private static int[] extractDeletedRows(int grid1RowCount, Map<Integer, Integer> rowMapping) {
		List<Integer> intList = new ArrayList<Integer>();
		for (int row = 1; row <= grid1RowCount; row++) {
			if (!rowMapping.containsKey(row)) {
				intList.add(row);
			}
		}
		Collections.sort(intList);
		return UtilBase.toIntArray(intList);
	}

	private static int[] extractInsertedRows(int grid2RowCount, Map<Integer, Integer> rowMapping) {
		Map<Integer, Integer> reverseDeletedMap = UtilBase.reverseMapping(rowMapping);
		return extractDeletedRows(grid2RowCount, reverseDeletedMap);
	}

	private static void findCellValueChanges(String[] columnNames, GridValueContainable grid1, int grid1Row, GridValueContainable grid2, int grid2Row,
			final GridCellValueChangeDetailSet changeDetailSet) {
		for (final String columnName : columnNames) {
			if (!UtilBase.isSame(grid1.getCellValue(grid1Row, columnName), grid2.getCellValue(grid2Row, columnName))) {
				final Object oldValue = grid1.getCellValue(grid1Row, columnName);
				final Object newValue = grid2.getCellValue(grid2Row, columnName);
				final ChangeDetail changeDetail = new ChangeDetail();
				changeDetail.setChangeType(getModTypeDescription(KB_MOD_TYPE_MODIFY_GRID_CELL));
				changeDetail.setRowNumber(grid1Row);
				changeDetail.setColumnName(grid1.getColumnTitle(columnName));
				changeDetail.setColumnId(columnName);
				changeDetail.setNewNativeValue(convertToNativeValue(newValue));
				changeDetail.setNewValue(convertToAuditStringValue(newValue));
				changeDetail.setPreviousNativeValue(convertToNativeValue(oldValue));
				changeDetail.setPreviousValue(convertToAuditStringValue(oldValue));
				changeDetailSet.add(changeDetail);
			}
		}
	}

	private static int getMatchingRowForDeletion(String[] columnNames, GridValueContainable grid1, int grid1Row, GridValueContainable grid2, int scanLimit) {
		for (int row2 = grid1Row; row2 > 0 && (grid1Row - row2) <= scanLimit; row2--) {
			if (hasSameCellValues(columnNames, grid1, grid1Row, grid2, row2)) {
				return row2;
			}
		}
		return -1;
	}

	private static Map<Integer, Integer> getRowMappingForDeletion(String[] columnNames, GridValueContainable grid1, GridValueContainable grid2) {
		int delta = grid1.getNumRows() - grid2.getNumRows();
		assert (delta > 0);
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		// find matching rows
		for (int row1 = 1; row1 <= grid1.getNumRows(); row1++) {
			int matchingRow = getMatchingRowForDeletion(columnNames, grid1, row1, grid2, delta);
			if (matchingRow > 0) {
				map.put(row1, matchingRow);
			}
		}
		// guess delete rows if the number of non matching rows is not delta
		if (grid1.getNumRows() - map.size() > delta) {
			int noRowsToAdd = grid1.getNumRows() - map.size() - delta;
			for (int row1 = 1; noRowsToAdd > 0 && row1 <= grid1.getNumRows(); row1++) {
				Integer row1Key = new Integer(row1);
				if (!map.containsKey(row1Key)) {
					map.put(row1Key, row1Key);
					--noRowsToAdd;
				}
			}
		}
		return map;
	}

	private static Map<Integer, Integer> getRowMappingForInsertion(String[] columnNames, GridValueContainable grid1, GridValueContainable grid2) {
		int delta = grid2.getNumRows() - grid1.getNumRows();
		assert (delta > 0);
		Map<Integer, Integer> reverseDeletedMap = getRowMappingForDeletion(columnNames, grid2, grid1);
		return UtilBase.reverseMapping(reverseDeletedMap);
	}

	private static boolean hasSameCellValues(String[] columnNames, GridValueContainable grid1, int grid1Row, GridValueContainable grid2, int grid2Row) {
		for (int i = 0; i < columnNames.length; i++) {
			if (!UtilBase.isSame(grid1.getCellValue(grid1Row, columnNames[i]), grid2.getCellValue(grid2Row, columnNames[i]))) {
				return false;
			}
		}
		return true;
	}

	private static void processForDeletions(final GridCellValueChangeDetailSet changeDetailSet, final GridValueContainable grid1) {
		for (int row = 1; row <= grid1.getNumRows(); row++) {
			addDeletedRowChangeDetail(changeDetailSet, grid1, row);
		}
	}

	private static void processForInsertions(final GridCellValueChangeDetailSet changeDetailSet, final GridValueContainable grid2) {
		for (int row = 1; row <= grid2.getNumRows(); row++) {
			addInsertedRowChangeDetail(changeDetailSet, grid2, row);
		}
	}

	public <G extends AbstractGrid<?>> GridDiffResult diff(G grid1, G grid2) {
		final DefaultGridDiffResult gridDiffResult = new DefaultGridDiffResult();
		final GridCellValueChangeDetailSet changeDetailSet = gridDiffResult.getGridCellValueChangeDetailSet();

		int grid1RowCount = grid1.getNumRows();
		int grid2RowCount = grid2.getNumRows();
		if (grid1RowCount == 0) {
			processForInsertions(changeDetailSet, grid2);
		}
		else if (grid2RowCount == 0) {
			processForDeletions(changeDetailSet, grid1);
		}
		else {
			String[] columnNames = grid1.getColumnNames();
			if (grid1RowCount < grid2RowCount) {
				Map<Integer, Integer> rowMapping = getRowMappingForInsertion(columnNames, grid1, grid2);
				// find inserted rows
				final int[] insertedRows = extractInsertedRows(grid2RowCount, rowMapping);
				for (final int insertedRow : insertedRows) {
					addInsertedRowChangeDetail(changeDetailSet, grid2, insertedRow);
				}
				// find cell value changes
				for (int row = 1; row <= grid1RowCount; row++) {
					findCellValueChanges(columnNames, grid1, row, grid2, rowMapping.get(new Integer(row)).intValue(), gridDiffResult.getGridCellValueChangeDetailSet());
				}
			}
			else if (grid1RowCount > grid2RowCount) {
				// find deleted rows
				Map<Integer, Integer> rowMapping = getRowMappingForDeletion(columnNames, grid1, grid2);
				final int[] deletedRows = extractDeletedRows(grid1RowCount, rowMapping);
				for (final int deletedRow : deletedRows) {
					addDeletedRowChangeDetail(changeDetailSet, grid1, deletedRow);
				}
				// find cell value changes
				for (int row = 1; row <= grid1RowCount; row++) {
					if (rowMapping.containsKey(new Integer(row))) {
						findCellValueChanges(columnNames, grid1, row, grid2, rowMapping.get(new Integer(row)).intValue(), gridDiffResult.getGridCellValueChangeDetailSet());
					}
				}
			}
			else {
				// just find cell value changes
				for (int row = 1; row <= grid1RowCount; row++) {
					findCellValueChanges(columnNames, grid1, row, grid2, row, gridDiffResult.getGridCellValueChangeDetailSet());
				}
			}
		}
		return gridDiffResult;
	}

}
