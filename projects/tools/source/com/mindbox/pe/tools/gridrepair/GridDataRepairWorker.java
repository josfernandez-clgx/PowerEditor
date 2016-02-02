/*
 * Created on 2004. 1. 30.
 *
 */
package com.mindbox.pe.tools.gridrepair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.server.bizlogic.GridActionCoordinator;
import com.mindbox.pe.tools.InvalidSpecException;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class GridDataRepairWorker {

	private static class GridData {
		private int gridID = -1;
		private int rowCount = 0;
		private String cellValues = null;
		GridData(int gridID, int rowCount, String cellValues) {
			this.gridID = gridID;
			this.rowCount = rowCount;
			this.cellValues = cellValues;
		}
		public String toString() {
			return "GridData[" + gridID + "," + cellValues + "," + rowCount + "]";
		}
	}

	private static final String Q_GET_GRIDS = "SELECT grid_id,cell_values FROM MB_GRID WHERE template_id=?";

	private static final String Q_UPDATE_GRID = "UPDATE MB_GRID SET cell_values=? WHERE grid_id=?";

	private static GridDataRepairWorker instance = null;

	public static GridDataRepairWorker getInstance() {
		if (instance == null) {
			instance = new GridDataRepairWorker();
		}
		return instance;
	}

	private final Logger logger = Logger.getLogger("GridDataRepairWorker");

	private GridDataRepairWorker() {}

	private int countAddedColumnsAt(int[] addedCols, int col) throws InvalidSpecException {
		logger.config(">>> countAddedColumnsAt: " + UtilBase.toString(addedCols) + ",col=" + col);
		int count = 0;
		for (int i = 0; i < addedCols.length; i++) {
			if (addedCols[i] < col) {
				++count;
			}
		}
		logger.config("<<< countAddedColumnsAt: " + count);
		return count;
	}

	private int[] getModifiedDeleteColumns(TemplateColumnChangeSpec spec, int colCount) throws InvalidSpecException {
		logger.config(">>> getModifiedDeleteColumns: " + spec + ",colCount=" + colCount);

		if (spec.addedColumnPositions() == null || spec.addedColumnPositions().length == 0) {
			return spec.removedColumns();
		}

		int[] deletedCols = spec.removedColumns();
		Arrays.sort(deletedCols);
		for (int i = 0; i < deletedCols.length; i++) {
			logger.config("  checking (" + i + ") = " + deletedCols[i]);
			if (deletedCols[i] <= 0) {
				throw new InvalidSpecException(
					"Column number must be greater than or equal to 1 (in spec for Template "
						+ spec.getTemplateID()
						+ ")");
			}
			else if (deletedCols[i] > colCount) {
				throw new InvalidSpecException(
					"Column " + deletedCols[i] + " does not exist for Template " + spec.getTemplateID());
			}
			deletedCols[i] += countAddedColumnsAt(spec.addedColumnPositions(), deletedCols[i]);
		}
		logger.config("<<< getModifiedDeleteColumns: " + UtilBase.toString(deletedCols));
		return deletedCols;
	}

	private String repairCellValues(
		int templateID,
		int[] addedColsUnsorted,
		int[] deletedCols,
		String cellValueStr)
		throws InvalidSpecException {
		/*
		logger.config(
			">>> repairCellValues: added="
				+ UtilBase.toString(addedColsUnsorted)
				+ ",deleted="
				+ UtilBase.toString(deletedCols));
		logger.config("                      " + cellValueStr);

		int[] addedCols = addedColsUnsorted;
		Arrays.sort(addedCols);

		StringBuffer buff = new StringBuffer();

		String[] rowValues = cellValueStr.split("~");
		logger.config("repairCellValues: row values count = " + rowValues.length);
		for (int i = 0; i < rowValues.length; i++) {
			logger.config("repairCellValues: processing row " + i);

			if (i != 0) {
				buff.append("~");
			}

			String[] columnValues = rowValues[i].split("\\|", -1);
			logger.config("repairCellValues: col values count = " + columnValues.length);

			List colValueList = new LinkedList(); // to preserve order
			for (int j = 0; j < columnValues.length; ++j) {
				colValueList.add(columnValues[j]);
			}

			// process added columns
			for (int c = addedCols.length - 1; c >= 0; c--) {
				if (addedCols[c] > colValueList.size()) {
					throw new InvalidSpecException(
						"Column " + addedCols[c] + " does not exist for Template " + templateID);
				}
				colValueList.add(addedCols[c], "");
			}
			logger.config("repairCellValues: col values count after inserts: " + colValueList.size());

			// process deleted columns
			for (int c = deletedCols.length - 1; c >= 0; c--) {
				int index = deletedCols[c] - 1;
				logger.fine("repairCellValues: deleting " + index);
				colValueList.remove(index);
			}
			logger.config("repairCellValues: col values count after deletes: " + colValueList.size());

			for (Iterator iter = colValueList.iterator(); iter.hasNext();) {
				buff.append(iter.next());
				if (iter.hasNext()) {
					buff.append("|");
				}
			}
		}

		logger.config("<<< repairCellValues: " + buff.toString());
		return buff.toString();
		*/
		try {
			return GridActionCoordinator.repairCellValues(templateID,addedColsUnsorted,deletedCols,cellValueStr);
		}
		catch (InvalidDataException ex) {
			throw new InvalidSpecException(ex.getMessage());
		}
	}

	private int extractColumnCount(String cellValues) {
		logger.fine(">>> extractColumnCount: " + cellValues);
		String[] rowValues = cellValues.split("~");
		int count = 99;
		if (rowValues.length > 0) {
			count = rowValues[0].split("\\|", -1).length;
		}
		logger.fine("<<< extractColumnCount: " + count);
		return count;
	}

	private int extractRowCount(String cellValues) {
		logger.fine(">>> extractRowCount: " + cellValues);
		if (cellValues == null || cellValues.length() == 0) {
			return 0;
		}
		else {
			int count = cellValues.split("~").length;
			logger.fine("<<< extractRowCount: " + count);
			return count;
		}
	}

	/**
	 * 
	 * @param conn
	 * @param changeSpec
	 * @return the total number of rows updated
	 * @throws SQLException
	 * @throws InvalidSpecException
	 */
	public int repairData(Connection conn, TemplateChangeSpec changeSpec) throws SQLException, InvalidSpecException {
		logger.fine(">>> repairData: " + changeSpec);

		int updatedRowCount = 0;

		int limit = changeSpec.numberOfColumnChanges();

		conn.setAutoCommit(false);

		PreparedStatement psGet = null;
		psGet = conn.prepareStatement(Q_GET_GRIDS);
		PreparedStatement psUpdate = null;
		psUpdate = conn.prepareStatement(Q_UPDATE_GRID);

		ResultSet rs = null;
		try {
			logger.fine("repairData: db resource prepared. applying changes...");

			List<GridData> gridDataList = new LinkedList<GridData>();

			for (int i = 0; i < limit; i++) {
				gridDataList.clear();

				TemplateColumnChangeSpec spec = changeSpec.getColumnChangeSpecAt(i);
				logger.info("Repairing data for " + spec);

				psGet.setInt(1, spec.getTemplateID());
				rs = psGet.executeQuery();
				GridData data = null;
				while (rs.next()) {
					int gridID = rs.getInt(1);
					String cellValues = rs.getString(2);
					if (cellValues != null) {
						cellValues = cellValues.trim();
					}
					int rowCount = extractRowCount(cellValues);

					data = new GridData(gridID, rowCount, cellValues);
					gridDataList.add(data);
					logger.config("repairData: fetched " + data);
				}
				rs.close();
				rs = null;

				logger.config("repairData: retrieved " + gridDataList.size() + " grids");

				int[] modifiedDeletedCols =
					getModifiedDeleteColumns(spec, extractColumnCount((data == null ? "" : data.cellValues)));

				for (Iterator<GridData> iter = gridDataList.iterator(); iter.hasNext();) {
					GridData element = iter.next();
					logger.info("- repairing grid " + element.gridID + " (rowCount=" + element.rowCount + ")");

					if (element.rowCount < 1) {
						logger.info("- grid " + element.gridID + " skipped for it contains no rows");
					}
					else if (element.cellValues == null || element.cellValues.length() == 0) {
						logger.info("- grid " + element.gridID + " skipped for its cell value is empty");
					}
					else {
						String repairedValues =
							repairCellValues(
								spec.getTemplateID(),
								spec.addedColumnPositions(),
								modifiedDeletedCols,
								element.cellValues);

						logger.info("- updating grid " + element.gridID + " with " + repairedValues);

						/**/
						psUpdate.setString(1, repairedValues);
						psUpdate.setInt(2, element.gridID);
						int count = psUpdate.executeUpdate();

						if (count < 1) {
							throw new SQLException(
								"Failed to update the row for grid " + element.gridID + ": count = " + count);
						}
						else {
							logger.info("- grid " + element.gridID + " updated successful");
							++updatedRowCount;
						}
						/**/
					}
				}
			}

			logger.fine("repairData: committing changes made...");
			conn.commit();

			logger.info("All changes are committed with success: # of rows updated = " + updatedRowCount);
			return updatedRowCount;
		}
		catch (InvalidSpecException ex) {
			conn.rollback();
			logger.log(Level.SEVERE, "Invalid specification", ex);
			throw ex;
		}
		catch (SQLException ex) {
			conn.rollback();
			logger.log(Level.SEVERE, "DB Error while repairing data", ex);
			throw ex;
		}
		catch (Exception ex) {
			conn.rollback();
			logger.log(Level.SEVERE, "Failed to repair data", ex);
			throw new SQLException(ex.getMessage());
		}
		finally {
			if (rs != null)
				rs.close();
			if (psGet != null)
				psGet.close();
			if (psUpdate != null)
				psUpdate.close();
		}
	}

}
