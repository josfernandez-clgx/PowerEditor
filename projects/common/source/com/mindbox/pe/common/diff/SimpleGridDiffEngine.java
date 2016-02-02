package com.mindbox.pe.common.diff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.AbstractGrid;
import com.mindbox.pe.model.GridValueContainable;

public final class SimpleGridDiffEngine implements GridDiffEngine {

	private static void findCellValueChanges(String[] columnNames, GridValueContainable grid1, int grid1Row, GridValueContainable grid2,
			int grid2Row, DefaultGridDiffResult gridDiffResult) {
		for (int i = 0; i < columnNames.length; i++) {
			if (!UtilBase.isSame(grid1.getCellValue(grid1Row, columnNames[i]), grid2.getCellValue(grid2Row, columnNames[i]))) {
				GridCellValueChangeDetail changeDetail = new GridCellValueChangeDetail(grid1Row, (i + 1), grid1.getCellValue(
						grid1Row,
						columnNames[i]), grid2.getCellValue(grid2Row, columnNames[i]));
				gridDiffResult.getGridCellValueChangeDetailSet().add(changeDetail);
			}
		}
	}

	private static boolean hasSameCellValues(String[] columnNames, GridValueContainable grid1, int grid1Row, GridValueContainable grid2,
			int grid2Row) {
		for (int i = 0; i < columnNames.length; i++) {
			if (!UtilBase.isSame(grid1.getCellValue(grid1Row, columnNames[i]), grid2.getCellValue(grid2Row, columnNames[i]))) {
				return false;
			}
		}
		return true;
	}

	private static int getMatchingRowForDeletion(String[] columnNames, GridValueContainable grid1, int grid1Row,
			GridValueContainable grid2, int scanLimit) {
		for (int row2 = grid1Row; row2 > 0 && (grid1Row - row2) <= scanLimit; row2--) {
			if (hasSameCellValues(columnNames, grid1, grid1Row, grid2, row2)) {
				return row2;
			}
		}
		return -1;
	}

	private static Map<Integer,Integer> getRowMappingForDeletion(String[] columnNames, GridValueContainable grid1, GridValueContainable grid2) {
		int delta = grid1.getNumRows() - grid2.getNumRows();
		assert (delta > 0);
		Map<Integer,Integer> map = new HashMap<Integer,Integer>();
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

	private static Map<Integer,Integer> getRowMappingForInsertion(String[] columnNames, GridValueContainable grid1, GridValueContainable grid2) {
		int delta = grid2.getNumRows() - grid1.getNumRows();
		assert (delta > 0);
		Map<Integer,Integer> reverseDeletedMap = getRowMappingForDeletion(columnNames, grid2, grid1);
		return UtilBase.reverseMapping(reverseDeletedMap);
	}

	private static int[] extractInsertedRows(int grid2RowCount, Map<Integer,Integer> rowMapping) {
		Map<Integer,Integer> reverseDeletedMap = UtilBase.reverseMapping(rowMapping);
		return extractDeletedRows(grid2RowCount, reverseDeletedMap);
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

	public <G extends AbstractGrid<?>> GridDiffResult diff(G grid1, G grid2) {
		DefaultGridDiffResult gridDiffResult = new DefaultGridDiffResult();
		int grid1RowCount = grid1.getNumRows();
		int grid2RowCount = grid2.getNumRows();
		if (grid1RowCount == 0) {
			processForInsertions(gridDiffResult, grid2);
		}
		else if (grid2RowCount == 0) {
			processForDeletions(gridDiffResult, grid1);
		}
		else {
			String[] columnNames = grid1.getColumnNames();
			if (grid1RowCount < grid2RowCount) {
				Map<Integer,Integer> rowMapping = getRowMappingForInsertion(columnNames, grid1, grid2);
				// find inserted rows
				int[] insertedRows = extractInsertedRows(grid2RowCount, rowMapping);
				for (int i = 0; i < insertedRows.length; i++) {
					gridDiffResult.addInsertedRow(insertedRows[i]);
				}
				// find cell value changes
				for (int row = 1; row <= grid1RowCount; row++) {
					findCellValueChanges(
							columnNames,
							grid1,
							row,
							grid2,
							rowMapping.get(new Integer(row)).intValue(),
							gridDiffResult);
				}
			}
			else if (grid1RowCount > grid2RowCount) {
				// find deleted rows
				Map<Integer, Integer> rowMapping = getRowMappingForDeletion(columnNames, grid1, grid2);
				int[] deletedRows = extractDeletedRows(grid1RowCount, rowMapping);
				for (int i = 0; i < deletedRows.length; i++) {
					gridDiffResult.addDeletedRow(deletedRows[i]);
				}
				// find cell value changes
				for (int row = 1; row <= grid1RowCount; row++) {
					if (rowMapping.containsKey(new Integer(row))) {
						findCellValueChanges(
								columnNames,
								grid1,
								row,
								grid2,
								rowMapping.get(new Integer(row)).intValue(),
								gridDiffResult);
					}
				}
			}
			else {
				// just find cell value changes
				for (int row = 1; row <= grid1RowCount; row++) {
					findCellValueChanges(columnNames, grid1, row, grid2, row, gridDiffResult);
				}
			}
		}
		return gridDiffResult;
	}

	private static void processForInsertions(DefaultGridDiffResult gridDiffResult, AbstractGrid<?> grid2) {
		for (int row = 1; row <= grid2.getNumRows(); row++) {
			gridDiffResult.addInsertedRow(row);
		}
	}

	private static void processForDeletions(DefaultGridDiffResult gridDiffResult, AbstractGrid<?> grid1) {
		for (int row = 1; row <= grid1.getNumRows(); row++) {
			gridDiffResult.addDeletedRow(row);
		}
	}

}
