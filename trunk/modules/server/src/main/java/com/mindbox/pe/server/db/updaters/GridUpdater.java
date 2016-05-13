package com.mindbox.pe.server.db.updaters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.grid.GridValueContainable;
import com.mindbox.pe.server.Util;
import com.mindbox.pe.server.db.DBUtil;
import com.mindbox.pe.server.db.DateSynonymReferenceUpdater;
import com.mindbox.pe.server.model.GenericCategoryIdentity;
import com.mindbox.pe.server.model.GenericEntityIdentity;
import com.mindbox.pe.server.model.GridCellDetail;

/**
 * Guideline grid updater. No methods in this class performs connection management functions.
 * 
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 1.0
 */
public class GridUpdater extends DateSynonymReferenceUpdater {

	private static final String Q_GRID_INSERT = "insert into MB_GRID(grid_id,template_id,"
			+ " deploy_status,last_status_change_on,clone_of,creation_date," + "num_rows,comments)" + " values (?,?,?,?,?,?,?,?)";

	private static final String Q_GRID_UPDATE = "update MB_GRID set" + " deploy_status=?,last_status_change_on=?,"
			+ " num_rows=?,comments=?,clone_of=? where grid_id=?";

	private static final String Q_GRID_SET_TEMPLATE = "update MB_GRID set template_id=? where grid_id=?";

	private static final String Q_GRID_SET_EXPIRATION_DATE = "update MB_GRID_DATE_SYNONYM set expiration_synonym_id=? where grid_id=?";

	private static final String Q_DELETE_MB_GRID = "delete from MB_GRID where grid_id=?";

	private static final String Q_INSERT_ENTITY_CONTEXT = "INSERT INTO MB_ENTITY_GRID_CONTEXT (grid_id,entity_id,entity_type) values (?,?,?)";

	private static final String Q_DELETE_ENTITY_CONTEXT = "DELETE FROM MB_ENTITY_GRID_CONTEXT WHERE grid_id=?";

	private static final String Q_INSERT_CATEGORY_CONTEXT = "INSERT INTO MB_ENTITY_GRID_CONTEXT (grid_id,category_id,category_type) values (?,?,?)";

	private static final String Q_INSERT_GRID_DATE_SYNONYM = "insert into MB_GRID_DATE_SYNONYM (grid_id,effective_synonym_id,expiration_synonym_id) values (?,?,?)";

	private static final String Q_UPDATE_GRID_DATE_SYNONYM = "update MB_GRID_DATE_SYNONYM set effective_synonym_id=?,expiration_synonym_id=? where grid_id=?";

	private static final String Q_DELETE_GRID_DATE_SYNONYM = "delete from MB_GRID_DATE_SYNONYM where grid_id=?";

	private static final String Q_REPLACE_ALL_GRID_EFFECTIVE_DATE_SYNONYM = "update mb_grid_date_synonym set effective_synonym_id=? where effective_synonym_id in ("
			+ IN_CLAUSE_LIST_HOLDER + ")";

	private static final String Q_REPLACE_ALL_GRID_EXPIRATION_DATE_SYNONYM = "update mb_grid_date_synonym set expiration_synonym_id=? where expiration_synonym_id in ("
			+ IN_CLAUSE_LIST_HOLDER + ")";

	private static final String Q_INSERT_GRID_CELL_VALUE = "insert into MB_GRID_CELL_VALUE (grid_id,row_id,column_name,cell_value) values (?,?,?,?)";

	private static final String Q_UPDATE_GRID_CELL_VALUE = "update MB_GRID_CELL_VALUE set cell_value=? where grid_id=? and row_id=? and column_name=?";


	private static final String Q_DELETE_GRID_CELL_VALUES = "delete from MB_GRID_CELL_VALUE where grid_id=?";

	private static final String Q_DELETE_4_TEMPLATE_WHERE = " where grid_id in (select grid_id from MB_GRID where template_id=?)";

	private static final String Q_DELETE_GRID_CELL_VALUES_TEMPLATE = "delete from MB_GRID_CELL_VALUE" + Q_DELETE_4_TEMPLATE_WHERE;

	private static final String Q_DELETE_GRID_ENTITY_CONTEXT_TEMPLATE = "delete from MB_ENTITY_GRID_CONTEXT" + Q_DELETE_4_TEMPLATE_WHERE;

	private static final String Q_DELETE_GRID_DATE_SYN_TEMPLATE = "delete from MB_GRID_DATE_SYNONYM" + Q_DELETE_4_TEMPLATE_WHERE;

	private static final String Q_DELETE_GRID_4_TEMPLATE = "delete from MB_GRID where template_id=?";

	private static final String Q_DELETE_GRID_CONTEXT_GENERIC_CATEGORY = "delete from MB_ENTITY_GRID_CONTEXT where category_type=? and category_id=?";

	private static final String Q_DELETE_GRID_CONTEXT_GENERIC_ENTITY = "delete from MB_ENTITY_GRID_CONTEXT where entity_type=? and entity_id=?";

	public GridUpdater(Connection connection) {
		logger.info("GridUpdate.<init>: " + connection);
		setConnection(connection);
	}

	/**
	 * Deletes the specified generic category from all grid context. Note: this does not perform
	 * connection management functions.
	 * 
	 * @param categoryType
	 *            category type
	 * @param categoryID
	 *            category id
	 * @throws SQLException
	 *             on error
	 * @since 4.4.1
	 */
	public void deleteCategoryFromContext(int categoryType, int categoryID) throws SQLException {
		logger.debug(">>> deleteCategoryFromContext: type=" + categoryType + ", id=" + categoryID);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			int count = 0;
			ps = conn.prepareStatement(Q_DELETE_GRID_CONTEXT_GENERIC_CATEGORY);
			ps.setInt(1, categoryType);
			ps.setInt(2, categoryID);
			count = ps.executeUpdate();
			logger.debug("    deleteCategoryFromContext: no of context rows deleted = " + count);

			logger.debug("<<< deleteCategoryFromContext");
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	/**
	 * Deletes the specified generic category from all grid context. Note: this does not perform
	 * connection management functions.
	 * 
	 * @param categoryType
	 *            category type
	 * @param entityID
	 *            category id
	 * @throws SQLException
	 *             on error
	 * @since 4.4.1
	 */
	public void deleteEntityFromContext(int entityType, int entityID) throws SQLException {
		logger.debug(">>> deleteEntityFromContext: type=" + entityType + ", id=" + entityID);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			int count = 0;
			ps = conn.prepareStatement(Q_DELETE_GRID_CONTEXT_GENERIC_ENTITY);
			ps.setInt(1, entityType);
			ps.setInt(2, entityID);
			count = ps.executeUpdate();
			logger.debug("    deleteEntityFromContext: no of context rows deleted = " + count);

			logger.debug("<<< deleteEntityFromContext");
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	/**
	 * Removes all guidelines for the specified template.
	 * 
	 * @param templateID
	 * @throws SQLException
	 * @since PowerEditor 4.3.7
	 */
	public void deleteGridsForTemplate(int templateID) throws SQLException {
		logger.debug(">>> deleteGridsForTemplate: " + templateID);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			int count = 0;
			ps = conn.prepareStatement(Q_DELETE_GRID_CELL_VALUES_TEMPLATE);
			ps.setInt(1, templateID);
			count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.debug("    deleteGridsForTemplate: no of cell value rows deleted = " + count);

			ps = conn.prepareStatement(Q_DELETE_GRID_ENTITY_CONTEXT_TEMPLATE);
			ps.setInt(1, templateID);
			count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.debug("    deleteGridsForTemplate: no of entity grid context rows deleted = " + count);

			ps = conn.prepareStatement(Q_DELETE_GRID_DATE_SYN_TEMPLATE);
			ps.setInt(1, templateID);
			count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.debug("    deleteGridsForTemplate: no of date synonym rows deleted = " + count);

			ps = conn.prepareStatement(Q_DELETE_GRID_4_TEMPLATE);
			ps.setInt(1, templateID);
			count = ps.executeUpdate();
			logger.debug("    deleteGridsForTemplate: no of grid rows deleted = " + count);

			logger.debug("<<< deleteGridsForTemplate: " + templateID);
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	/**
	 * Inserts a new guideline template row into the database.
	 * 
	 * @param gridID
	 * @param templateID
	 * @param comment
	 * @param valueContainer
	 * @param status
	 * @param statusChanged
	 * @param effDate
	 * @param expDate
	 * @param numRows
	 * @param cloneOf
	 * @param created
	 * @param entityIdentities entity identities; can be <code>null</code>
	 * @param categoryIdentities category identities; can be <code>null</code>
	 * @throws SQLException
	 *             on db error
	 */
	public void insertProductGrid(int gridID, int templateID, String comment, GridValueContainable valueContainer, String status,
			Date statusChanged, DateSynonym effDate, DateSynonym expDate, int numRows, int cloneOf, Date created,
			GenericEntityIdentity[] entityIdentities, GenericCategoryIdentity[] categoryIdentities) throws SQLException {

		logger.debug(">>> insertProductGrid: " + gridID + "," + templateID + "," + effDate + "," + expDate);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			insertGrid(conn, gridID, templateID, comment, status, statusChanged, numRows, cloneOf, created);
			int count = 0;
			if (entityIdentities != null && entityIdentities.length > 0) {
				ps = conn.prepareStatement(Q_INSERT_ENTITY_CONTEXT);
				for (int i = 0; i < entityIdentities.length; i++) {
					ps.setInt(1, gridID);
					ps.setInt(2, entityIdentities[i].getEntityID());
					ps.setInt(3, entityIdentities[i].getEntityType());
					count = ps.executeUpdate();
					if (count < 1) {
						throw new SQLException("Grid Entity Context Insert Failed (count=" + count + ")");
					}
				}
				ps.close();
				ps = null;
			}
			if (categoryIdentities != null && categoryIdentities.length > 0) {
				ps = conn.prepareStatement(Q_INSERT_CATEGORY_CONTEXT);
				for (int i = 0; i < categoryIdentities.length; i++) {
					ps.setInt(1, gridID);
					ps.setInt(2, categoryIdentities[i].getCategoryID());
					ps.setInt(3, categoryIdentities[i].getCategoryType());
					count = ps.executeUpdate();
					if (count < 1) {
						throw new SQLException("Grid Category Context Insert Failed (count=" + count + ")");
					}
				}
				ps.close();
				ps = null;
			}

			// insert date synonym
			ps = conn.prepareStatement(Q_INSERT_GRID_DATE_SYNONYM);
			ps.setInt(1, gridID);
			ps.setInt(2, (effDate == null ? -1 : effDate.getID()));
			ps.setInt(3, (expDate == null ? -1 : expDate.getID()));
			count = ps.executeUpdate();
			if (count < 0) {
				throw new SQLException("Failed to insert date synonym for " + gridID);
			}
			ps.close();
			ps = null;

			// insert cell values
			setGridCellValues(conn, gridID, valueContainer, false);
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	/**
	 * Update template id of the specified grid.
	 * 
	 * @param gridID
	 *            the grid id
	 * @param templateID
	 *            new template id
	 * @throws SQLException
	 *             on database error
	 * @since PowerEditor 4.2.0
	 */
	public void setTemplateOfGrid(int gridID, int templateID) throws SQLException {
		if (templateID < 1) throw new IllegalArgumentException("Invalid templateID: " + templateID);
		logger.debug(">>> setTemplateOfGrid: " + gridID + "," + templateID);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_GRID_SET_TEMPLATE);
			ps.setInt(1, templateID);
			ps.setInt(2, gridID);

			int count = ps.executeUpdate();
			logger.info("Updated " + count + " row(s)!");
			if (count < 1) {
				throw new SQLException("Failed to update row (count=" + count + ")");
			}
			ps.close();
			ps = null;
			logger.debug("<<< setTemplateOfGrid");
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	/**
	 * Update expiration date of the specified grid.
	 * 
	 * @param gridID
	 *            the grid id
	 * @param expDateSynonymID
	 *            new exp. date synonym id
	 * @throws SQLException
	 *             on database error
	 * @since PowerEditor 4.2.0
	 */
	public void setExpirationDateOfGrid(int gridID, int expDateSynonymID) throws SQLException {
		if (expDateSynonymID < 1) throw new IllegalArgumentException("Invalid expDateSynonymID: " + expDateSynonymID);
		logger.debug(">>> setExpirationDateOfGrid: " + gridID + "," + expDateSynonymID);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_GRID_SET_EXPIRATION_DATE);
			ps.setInt(1, expDateSynonymID);
			ps.setInt(2, gridID);

			int count = ps.executeUpdate();
			logger.info("Updated " + count + " row(s)!");
			if (count < 1) {
				throw new SQLException("Failed to update row (count=" + count + ")");
			}
			ps.close();
			ps = null;
			logger.debug("<<< setExpirationDateOfGrid");
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	/**
	 * Update the context row of the specified grid.
	 * 
	 * @param gridID
	 * @param entityIdentities entity identities; can be <code>null</code>
	 * @param categoryIdentities category identities; can be <code>null</code>
	 * @throws SQLException
	 *             on db error
	 * @since PowerEditor 4.2.0
	 */
	public void updateGridContext(int gridID, GenericEntityIdentity[] entityIdentities, GenericCategoryIdentity[] categoryIdentities)
			throws SQLException {
		logger.debug(">>> updateGridContext: " + gridID + "," + entityIdentities + "," + categoryIdentities);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			if (gridID == -1) throw new SQLException("Invalid grid ID: " + gridID + ". Context insert failed.");
			ps = conn.prepareStatement(Q_DELETE_ENTITY_CONTEXT);
			ps.setInt(1, gridID);
			int count = ps.executeUpdate();
			logger.debug(count + " entity context rows deleted");
			ps.close();
			ps = null;

			if (entityIdentities != null && entityIdentities.length > 0) {
				ps = conn.prepareStatement(Q_INSERT_ENTITY_CONTEXT);
				for (int i = 0; i < entityIdentities.length; i++) {
					ps.setInt(1, gridID);
					ps.setInt(2, entityIdentities[i].getEntityID());
					ps.setInt(3, entityIdentities[i].getEntityType());
					count = ps.executeUpdate();
					if (count < 1) {
						throw new SQLException("Grid Entity Context Insert Failed (count=" + count + ")");
					}
				}
				ps.close();
				ps = null;
			}
			if (categoryIdentities != null && categoryIdentities.length > 0) {
				ps = conn.prepareStatement(Q_INSERT_CATEGORY_CONTEXT);
				for (int i = 0; i < categoryIdentities.length; i++) {
					ps.setInt(1, gridID);
					ps.setInt(2, categoryIdentities[i].getCategoryID());
					ps.setInt(3, categoryIdentities[i].getCategoryType());
					count = ps.executeUpdate();
					if (count < 1) {
						throw new SQLException("Grid Category Context Insert Failed (count=" + count + ")");
					}
				}
				ps.close();
				ps = null;
			}
		}
		catch (SQLException exp) {
			if (exp.getMessage().equalsIgnoreCase("No Data Found") == false) {
				throw exp;
			}
			else {
				logger.info("No Data Found");
			}
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	/**
	 * Updates details of the specified grid row in db. <b>Note</b>: this does NOT update the
	 * context.
	 * 
	 * @param gridID
	 * @param comments
	 * @param valueContainer
	 * @param status
	 * @param statusChangeDate
	 * @param effDate
	 * @param expDate
	 * @param cloneOf
	 * @throws SQLException
	 *             on db error
	 */
	public void updateGrid(int gridID, String comments, GridValueContainable valueContainer, String status, Date statusChangeDate,
			DateSynonym effDate, DateSynonym expDate, int cloneOf) throws SQLException {
		logger.debug(">>> updateGrid: " + gridID + "," + valueContainer + "," + effDate + "," + expDate);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_GRID_UPDATE);
			ps.setString(1, status);
			DBUtil.setDateValue(ps, 2, statusChangeDate);
			ps.setInt(3, valueContainer.getNumRows());
			ps.setString(4, comments);
			ps.setInt(5, cloneOf);
			ps.setInt(6, gridID);

			int count = ps.executeUpdate();
			logger.info("Updated " + count + " row(s)!");
			if (count < 1) {
				throw new SQLException("Failed to update row (count=" + count + ")");
			}
			ps.close();
			ps = null;

			// update date synonym
			ps = conn.prepareStatement(Q_UPDATE_GRID_DATE_SYNONYM);
			ps.setInt(1, (effDate == null ? -1 : effDate.getID()));
			ps.setInt(2, (expDate == null ? -1 : expDate.getID()));
			ps.setInt(3, gridID);
			count = ps.executeUpdate();
			if (count < 0) {
				throw new SQLException("Failed to update date synonym for " + gridID);
			}
			ps.close();
			ps = null;

			// update cell values
			setGridCellValues(conn, gridID, valueContainer, true);
		}
		catch (SQLException exp) {
			if (exp.getMessage().equalsIgnoreCase("No Data Found") == false) {
				throw exp;
			}
			else {
				logger.info("No Data Found");
			}
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	private void insertGrid(Connection conn, int gridID, int templateID, String comment, String status, Date statusChanged, int numRows,
			int cloneOf, Date created) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_GRID_INSERT);
			ps.setInt(1, gridID);
			ps.setInt(2, templateID);
			ps.setString(3, status);
			DBUtil.setDateValue(ps, 4, statusChanged);

			ps.setInt(5, cloneOf);
			DBUtil.setDateValue(ps, 6, (created == null ? new Date() : created));
			ps.setInt(7, numRows);
			ps.setString(8, comment);
			int count = ps.executeUpdate();
			if (count < 0) {
				throw new SQLException("Failed to insert a new row for " + gridID);
			}
			logger.info("Inserted " + count + " grid row(s)!");
			ps.close();
			ps = null;
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	private void setGridCellValues(Connection conn, int gridID, GridValueContainable valueContainer, boolean deleteFirst)
			throws SQLException {
		logger.debug(">>> setGridCellValues: " + gridID + "," + valueContainer + "," + deleteFirst);
		PreparedStatement ps = null;
		try {
			int count = 0;
			if (deleteFirst) {
				ps = conn.prepareStatement(Q_DELETE_GRID_CELL_VALUES);
				ps.setInt(1, gridID);
				count = ps.executeUpdate();
				logger.debug("    setGridCellValues: removed " + count + " grid cell values");
				ps.close();
				ps = null;
			}

			if (!valueContainer.isEmpty()) {
				String[] columnNames = valueContainer.getColumnNames();
				ps = conn.prepareStatement(Q_INSERT_GRID_CELL_VALUE);
				for (int r = 0; r < valueContainer.getNumRows(); r++) {
					for (String columnName : columnNames) {
						ps.setInt(1, gridID);
						ps.setInt(2, r + 1);
						ps.setString(3, columnName);
						ps.setString(4, Util.convertCellValueToString(valueContainer.getCellValue(r + 1, columnName)));

						count = ps.executeUpdate();
						if (count < 1) {
							throw new SQLException("Failed to insert grid cell value for " + gridID + "," + (r + 1) + "," + columnName);
						}
					}
				}
			}
			logger.debug("<<< setGridCellValues");
		}
		finally {
			if (ps != null) ps.close();
		}

	}

	/**
	 * Removes the specified guideline grid from the database.
	 * 
	 * @param gridID
	 * @throws SQLException
	 *             on db error
	 */
	public void deleteProductGrid(int gridID) throws SQLException {
		logger.debug(">>> deleteProductGrid: " + gridID);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_DELETE_ENTITY_CONTEXT);
			ps.setInt(1, gridID);
			int count = ps.executeUpdate();
			logger.info("deleted " + count + " entity context");
			ps.close();
			ps = null;

			// delete label
			ps = conn.prepareStatement(Q_DELETE_GRID_DATE_SYNONYM);
			ps.setInt(1, gridID);
			count = ps.executeUpdate();
			logger.info("deleted " + count + " grid date synonyms");
			ps.close();
			ps = null;

			// delete cell values
			ps = conn.prepareStatement(Q_DELETE_GRID_CELL_VALUES);
			ps.setInt(1, gridID);
			count = ps.executeUpdate();
			logger.debug("    setGridCellValues: removed " + count + " grid cell values");
			ps.close();
			ps = null;
		}
		catch (SQLException ex) {
			if (ex.getMessage().equalsIgnoreCase("No Data Found") == false) {
				throw ex;
			}
			else {
				logger.info("No Data Found");
			}
		}
		finally {
			if (ps != null) ps.close();
		}

		deleteGrid(conn, gridID);
		logger.debug("<<< deleteProductGrid: " + gridID);
	}

	private void deleteGrid(Connection conn, int gridID) throws SQLException {
		logger.debug(">>> deleteGrid: " + gridID);

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_DELETE_MB_GRID);
			ps.setInt(1, gridID);
			int j = ps.executeUpdate();

			logger.info("Deleted " + j + " grid row(s)!");
		}
		catch (SQLException exp) {
			if (exp.getMessage().equalsIgnoreCase("No Data Found") == false) {
				throw exp;
			}
			else {
				logger.info("No Data Found");
			}
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	public void replaceDateSynonymReferences(DateSynonym[] toBeReplaced, DateSynonym replacement) throws SQLException {
		replaceDateSynonymReferencesInIntersectionTable(
				toBeReplaced,
				replacement,
				Q_REPLACE_ALL_GRID_EFFECTIVE_DATE_SYNONYM,
				Q_REPLACE_ALL_GRID_EXPIRATION_DATE_SYNONYM);
	}

	public void setCellValues(GridCellDetail[] gridCellDetails) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement psUpdate = null;
		PreparedStatement psInsert = null;
		try {
			int count = 0;
			psUpdate = conn.prepareStatement(Q_UPDATE_GRID_CELL_VALUE);
			psInsert = conn.prepareStatement(Q_INSERT_GRID_CELL_VALUE);
			for (int i = 0; i < gridCellDetails.length; i++) {
				String cellValueStr = Util.convertCellValueToString(gridCellDetails[i].getCellValue());
				psUpdate.setString(1, cellValueStr);
				psUpdate.setInt(2, gridCellDetails[i].getGridID());
				psUpdate.setInt(3, gridCellDetails[i].getRowID());
				psUpdate.setString(4, gridCellDetails[i].getColumnName());
				count = psUpdate.executeUpdate();
				if (count < 1) {
					psInsert.setInt(1, gridCellDetails[i].getGridID());
					psInsert.setInt(2, gridCellDetails[i].getRowID());
					psInsert.setString(3, gridCellDetails[i].getColumnName());
					psInsert.setString(4, cellValueStr);
					count = psInsert.executeUpdate();
					if (count < 1) {
						throw new SQLException("No row updated for " + gridCellDetails[i]);
					}
				}
			}
		}
		finally {
			if (psUpdate != null) psUpdate.close();
			if (psInsert != null) psInsert.close();
		}
	}

	public void updateCellValues(GridCellDetail[] gridCellDetails) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			int count = 0;
			ps = conn.prepareStatement(Q_UPDATE_GRID_CELL_VALUE);
			for (int i = 0; i < gridCellDetails.length; i++) {
				ps.setString(1, Util.convertCellValueToString(gridCellDetails[i].getCellValue()));
				ps.setInt(2, gridCellDetails[i].getGridID());
				ps.setInt(3, gridCellDetails[i].getRowID());
				ps.setString(4, gridCellDetails[i].getColumnName());
				count = ps.executeUpdate();
				if (count < 1) throw new SQLException("No row updated for " + gridCellDetails[i]);
			}
		}
		finally {
			if (ps != null) ps.close();
		}
	}
}