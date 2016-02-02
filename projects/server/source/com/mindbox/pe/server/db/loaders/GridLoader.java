package com.mindbox.pe.server.db.loaders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.table.EnumValuesDataHelper;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.KnowledgeBaseFilterConfig;
import com.mindbox.pe.server.db.DBConnectionManager;
import com.mindbox.pe.server.db.DBUtil;

/**
 * Grid loader.
 * 
 * @author kim
 * @since PowerEditor 4.0.0
 */
public class GridLoader extends AbstractLoader {

	private static final String Q_LOAD_GRID = "select g.grid_id, g.template_id, g.deploy_status,"
			+ " g.last_status_change_on, g.clone_of, g.creation_date, g.num_rows, g.comments,"
			+ " d.effective_synonym_id,d.expiration_synonym_id" + " from MB_GRID g, MB_GRID_DATE_SYNONYM d"
			+ " where g.grid_id = d.grid_id and g.deploy_status <> 'Retired'";

	private static final String Q_SELECT_ENTITY_CONTEXT = "SELECT grid_id,entity_id,entity_type,category_id,category_type FROM MB_ENTITY_GRID_CONTEXT ORDER BY grid_id";

	private static final String Q_LOAD_GRID_CELL_VALUES2 = "select grid_id,row_id,column_name,cell_value from MB_GRID_CELL_VALUE order by grid_id";

	public GridLoader() {
	}

	public static final class MigrateGridData {

		private final int gridID;

		private final Date actDate, expDate;

		private final String cellValues;

		MigrateGridData(int gridID, Date actDate, Date expDate, String cellValues) {
			this.gridID = gridID;
			this.actDate = actDate;
			this.expDate = expDate;
			this.cellValues = cellValues;
		}

		public String toString() {
			return "MigGrid[" + gridID + "," + actDate + "," + expDate + "]";
		}

		public Date getActDate() {
			return actDate;
		}

		public String getCellValues() {
			return cellValues;
		}

		public Date getExpDate() {
			return expDate;
		}

		public int getGridID() {
			return gridID;
		}
	}

	public void load(KnowledgeBaseFilterConfig knowledgeBaseFilterConfig) throws SQLException {
		// TODO GKim: Implement filtering
		long startTime = System.currentTimeMillis();

		logger.info("=== Grid Data ===");
		GridManager.getInstance().startLoading();
		try {
			dbLoadProductGrids(knowledgeBaseFilterConfig);
			logger.info("GridLoader: grid row loading time = " + (System.currentTimeMillis() - startTime));
			startTime = System.currentTimeMillis();

			loadGridCellValues2();
			logger.info("GridLoader: grid cell value loading time = " + (System.currentTimeMillis() - startTime));
			startTime = System.currentTimeMillis();

			loadEntityContext();
			logger.info("GridLoader: grid context loading time = " + (System.currentTimeMillis() - startTime));

		}
		finally {
			GridManager.getInstance().finishLoading();
		}
	}

	private void loadGridCellValues2() throws SQLException {
		Connection connection = DBConnectionManager.getInstance().getConnection();
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ProductGrid grid = null;

			// load all grid cell value rows and process them
			ps = connection.prepareStatement(Q_LOAD_GRID_CELL_VALUES2);
			rs = ps.executeQuery();
			while (rs.next()) {
				// process a grid cell value row
				int id = rs.getInt(1);
				int rowID = rs.getInt(2);
				String columnName = UtilBase.trim(rs.getString(3));
				String value = UtilBase.trim(rs.getString(4));
				if (grid == null || grid.getID() != id) {
					grid = GridManager.getInstance().getProductGrid(id);
				}
				// Only load if grid exists
				if (grid == null) {
					logger.warn("Grid-Values: ignored - no grid " + id + " exists: rowID=" + rowID + ",col=" + columnName + ",val=" + value);
				}
				else if (rowID < 1) {
					logger.warn("Grid-Values: ignored - invalid row id: rowID=" + rowID + ",col=" + columnName + ",val=" + value);
				}
				else {
					GridTemplateColumn templateColumn = (GridTemplateColumn) grid.getTemplate().getColumn(columnName);
					if (templateColumn == null) {
						logger.warn("Grid-Values: ignored - invalid column name: rowID=" + rowID + ",col=" + columnName + ",val=" + value);
					}
					else {
						try {
							setGridCellValue(grid, rowID, templateColumn, value);
						}
						catch (InvalidDataException e) {
							logger.warn("Grid-Values: ignored - invalid value: rowID=" + rowID + ",col=" + columnName + ",val=" + value, e);
						}
					}
				}
			}
			rs.close();
			rs = null;
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
			DBConnectionManager.getInstance().freeConnection(connection);
		}
	}

	private void setGridCellValue(ProductGrid grid, int rowID, GridTemplateColumn column, String value) throws InvalidDataException {
		String valueToSet = value;
		if (!UtilBase.isEmpty(value) && column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENUM_LIST)) {
			List<EnumValue> enumValueList = EnumValuesDataHelper.getAllEnumValues(
					column.getColumnDataSpecDigest(),
					DomainManager.getInstance(),
					ConfigurationManager.getInstance().getEnumerationSourceConfigSet());
			valueToSet = EnumValues.replaceExclusionPrefixIfOld(value, enumValueList);
		}
		grid.setValue(rowID, column.getName(), column.convertToCellValue(
				valueToSet,
				DomainManager.getInstance(),
				ConfigurationManager.getInstance().getEnumerationSourceConfigSet()));
	}

	private synchronized void dbLoadProductGrids(KnowledgeBaseFilterConfig knowledgeBaseFilterConfig) throws SQLException {
		Connection connection = DBConnectionManager.getInstance().getConnection();
		GridManager gridmanager = GridManager.getInstance();
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(Q_LOAD_GRID);
			rs = ps.executeQuery();

			processResultSet(gridmanager, rs, knowledgeBaseFilterConfig);
		}
		catch (ParseException ex) {
			logger.fatal("Failed to load product grids", ex);
			throw new SQLException(ex.getMessage());
		}
		finally {
			DBUtil.closeLocallyManagedResources(rs, ps);
			DBConnectionManager.getInstance().freeConnection(connection);
		}
	}

	private void processResultSet(GridManager gridmanager, ResultSet rs, KnowledgeBaseFilterConfig knowledgeBaseFilterConfig)
			throws SQLException, ParseException {
		int gridID;
		int templateID;
		String status;
		java.util.Date statusChangedDate;
		int cloneOf;
		java.util.Date creationDate;
		int numRows;
		String comments;
		int effID, expID;

		while (rs.next()) {
			gridID = rs.getInt(1);
			templateID = rs.getInt(2);
			status = UtilBase.trim(rs.getString(3));
			statusChangedDate = DBUtil.getDateValue(rs, 4);
			cloneOf = rs.getInt(5);
			creationDate = DBUtil.getDateValue(rs, 6);
			numRows = rs.getInt(7);
			comments = UtilBase.trim(rs.getString(8));

			effID = rs.getInt(9);
			expID = rs.getInt(10);

			// check data elements for validity
			if (gridID <= 0) {
				logger.warn("Guideline Grid Ignored - invalid grid ID (" + gridID + ";template=" + templateID + "; status=" + status
						+ "; NumRows=" + numRows + ")");
			}
			else if (templateID < 0) {
				logger.warn("Guideline Grid Ignored - invalid template ID (" + gridID + ";template=" + templateID + "; status=" + status
						+ "; NumRows=" + numRows + ")");
			}
			else if (GuidelineTemplateManager.getInstance().getTemplate(templateID) == null) {
				logger.warn("Guideline Grid Ignored - template not found (" + gridID + ";template=" + templateID + "; status=" + status
						+ "; NumRows=" + numRows + ")");
			}
			else {
				if (dateSysnonymsPassFilter(effID, expID, knowledgeBaseFilterConfig.getDateFilterConfig())) {
					logger.info(gridID + ";template=" + templateID + "; status=" + status + "; NumRows=" + numRows + "; effID=" + effID
							+ ",expID=" + expID);

					gridmanager.addProductGrid(
							gridID,
							templateID,
							comments,
							null,
							status,
							statusChangedDate,
							DateSynonymManager.getInstance().getDateSynonym(effID),
							DateSynonymManager.getInstance().getDateSynonym(expID),
							numRows,
							cloneOf,
							creationDate);
				}
				else {
					logger.info(String.format(
							"*SKIPPED* Guideline grid filtered out by KBDateFilter: gridID=%d,template=%d,status=%s,numRows=%d,effID=%d,expID=%d",
							gridID,
							templateID,
							status,
							numRows,
							effID,
							expID));
				}
			}
		}
	}

	/**
	 * @throws SQLException
	 * @since 3.0.0
	 */
	private void loadEntityContext() throws SQLException {
		logger.info("=== Grid Entity Context ===");
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection conn = dbconnectionmanager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_SELECT_ENTITY_CONTEXT);
			rs = ps.executeQuery();

			while (rs.next()) {
				int gridID = rs.getInt(1);
				int entityID = rs.getInt(2);
				int entityType = rs.getInt(3);
				int categoryID = rs.getInt(4);
				int categoryType = rs.getInt(5);

				if (entityID > 0) {
					GenericEntityType genericEntityType = GenericEntityType.forID(entityType);
					if (genericEntityType == null) {
						logger.warn("Grid entity context ignored - invalid entity type: " + gridID + "," + entityID + ',' + entityType);
					}
					else if (!ConfigurationManager.getInstance().getEntityConfiguration().findEntityTypeDefinition(genericEntityType).useInContext()) {
						logger.warn("Grid entity context ignored - entity type not configured for context: " + gridID + "," + entityID
								+ ',' + entityType);
					}
					else if (EntityManager.getInstance().getEntity(genericEntityType, entityID) == null) {
						logger.warn("Grid entity context ignored - generic entity not found: " + gridID + "," + entityID + ',' + entityType);
					}
					else {
						boolean added = GridManager.getInstance().addGridContext(gridID, GenericEntityType.forID(entityType), entityID);

						if (added) {
							logger.info("Grid EntityContext: " + gridID + ",type=" + entityType + ",id=" + entityID);
						}
						else {
							logger.warn("Ignoring Grid EntityContext: grid=" + gridID + ",type=" + entityType + ",entityID=" + entityID
									+ "; no grid " + gridID + " exist");
						}
					}
				}
				else if (categoryID > 0) {
					if (EntityManager.getInstance().getGenericCategory(categoryType, categoryID) == null) {
						logger.warn("Grid entity context ignored - generic category not found: " + gridID + "," + categoryType + ','
								+ categoryID);
					}
					else {
						boolean added = GridManager.getInstance().addGridContext(gridID, categoryType, categoryID);

						if (added) {
							logger.info("Grid CategoryContext: " + gridID + ",type=" + categoryType + ",id=" + categoryID);
						}
						else {
							logger.warn("Ignoring Grid CategoryContext: grid=" + gridID + ",categoryType=" + categoryType + ",categoryID="
									+ categoryID + "; no grid " + gridID + " exist");

						}
					}
				}
			}
		}
		finally {
			DBUtil.closeLocallyManagedResources(rs, ps);
			dbconnectionmanager.freeConnection(conn);
		}
	}

}