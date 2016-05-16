package com.mindbox.pe.server.bizlogic;

import static com.mindbox.pe.common.LogUtil.logDebug;
import static com.mindbox.pe.common.LogUtil.logError;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.AbstractIDObject;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridSummary;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.IntegerPair;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.exceptions.SapphireException;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.model.grid.AbstractGrid;
import com.mindbox.pe.model.grid.AbstractGuidelineGrid;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.rule.RuleSet;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.server.ServerContextUtil;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.Util;
import com.mindbox.pe.server.audit.AuditLogger;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.cache.LockManager;
import com.mindbox.pe.server.cache.ParameterManager;
import com.mindbox.pe.server.cache.ParameterTemplateManager;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.db.DBConnectionManager;
import com.mindbox.pe.server.db.DBIdGenerator;
import com.mindbox.pe.server.db.DBUtil;
import com.mindbox.pe.server.db.updaters.GridUpdater;
import com.mindbox.pe.server.db.updaters.ParameterUpdater;
import com.mindbox.pe.server.imexport.ImportException;
import com.mindbox.pe.server.model.GridCellDetail;
import com.mindbox.pe.server.model.LockException;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletActionException;

/**
 * Performs grid services.
 * 
 * @author Gene Kim
 * @author MindBox
 * @since PowerEditor 1.10.0
 */
public final class GridActionCoordinator {

	private static class GridIDKey {

		private final int gridID;
		private final int hashCode;
		private final AbstractGrid<?> activation;

		public GridIDKey(int gridID, AbstractGrid<?> activation) {
			if (activation == null) throw new NullPointerException("grid cannot be null");
			this.gridID = gridID;
			this.activation = activation;
			this.hashCode = (gridID + ":" + activation.getTemplateID() + ":" + activation.getEffectiveDate() + "-" + activation.getExpirationDate()).hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj instanceof GridIDKey) {
				return this.gridID == ((GridIDKey) obj).gridID;
			}
			else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return hashCode;
		}

		@Override
		public String toString() {
			return "GridIDKey[" + gridID + ":" + activation + ",hash=" + hashCode + "]";
		}
	}

	private static final GridActionCoordinator INSTANCE = new GridActionCoordinator();

	private static Map<IntegerPair, Integer> asDateSynonymPairGridIdMap(final Map<GridIDKey, Integer> gridIDMap) {
		assert gridIDMap != null;
		final Map<IntegerPair, Integer> dateSynonymPairGridIdMap = new HashMap<IntegerPair, Integer>();
		for (Map.Entry<GridIDKey, Integer> entry : gridIDMap.entrySet()) {
			dateSynonymPairGridIdMap.put(entry.getKey().activation.getEffDateExpDateAsIntegerPair(), entry.getValue());
		}
		return dateSynonymPairGridIdMap;
	}

	private static void fillCutoverGuidelinesForTemplate(int templateID, DateSynonym cutoverDate, String username, List<GuidelineReportData> cutoverList,
			List<GuidelineReportData> nonCutoverList) throws ServerException {
		GuidelineReportFilter filter = new GuidelineReportFilter();
		filter.addGuidelineTemplateID(new Integer(templateID));
		List<AbstractIDObject> guidelineList = SearchCooridinator.getInstance().process(filter, username);
		for (Iterator<AbstractIDObject> iter = guidelineList.iterator(); iter.hasNext();) {
			GuidelineReportData element = (GuidelineReportData) iter.next();
			if (isToBeCutover(element, cutoverDate)) {
				cutoverList.add(element);
			}
			else {
				nonCutoverList.add(element);
			}
		}
	}

	public static GridActionCoordinator getInstance() {
		return INSTANCE;
	}

	private static boolean isToBeCutover(GuidelineReportData data, DateSynonym cutoverDate) {
		if (data.getExpirationDate() == null || data.getExpirationDate().getDate() == null) return true;
		if (data.getExpirationDate().equals(cutoverDate)) return false;
		return data.getExpirationDate().after(cutoverDate);
	}

	/**
	 * Repairs the specified cell values for the specified column changes.
	 * 
	 * @param templateID templateID
	 * @param addedColsUnsorted addedColsUnsorted
	 * @param deletedCols deletedCols
	 * @param cellValueStr cellValueStr
	 * @return the repaied cell value string
	 * @throws InvalidDataException on error
	 */
	public static String repairCellValues(int templateID, int[] addedColsUnsorted, int[] deletedCols, String cellValueStr) throws InvalidDataException {
		Logger logger = Logger.getLogger(GridActionCoordinator.class);
		logger.debug(">>> repairCellValues: added=" + UtilBase.toString(addedColsUnsorted) + ",deleted=" + UtilBase.toString(deletedCols));
		logger.debug("                      " + cellValueStr);

		int[] addedCols = addedColsUnsorted;
		Arrays.sort(addedCols);

		StringBuilder buff = new StringBuilder();

		String[] rowValues = cellValueStr.split("~");
		if (logger.isDebugEnabled()) {
			logger.debug("repairCellValues: row values count = " + rowValues.length);
		}
		for (int i = 0; i < rowValues.length; i++) {
			if (logger.isDebugEnabled()) {
				logger.debug("repairCellValues: processing row " + i);
			}

			if (i != 0) {
				buff.append("~");
			}

			String[] columnValues = rowValues[i].split("\\|", -1);
			if (logger.isDebugEnabled()) {
				logger.debug("repairCellValues: col values count = " + columnValues.length);
			}

			List<String> colValueList = new LinkedList<String>(); // to preserve order
			for (int j = 0; j < columnValues.length; ++j) {
				colValueList.add(columnValues[j]);
			}

			// process added columns
			for (int c = addedCols.length - 1; c >= 0; c--) {
				if (addedCols[c] > colValueList.size()) {
					throw new InvalidDataException("Template " + templateID, "Column " + addedCols[c], "column does not exist");
				}
				colValueList.add(addedCols[c], "");
			}
			if (logger.isDebugEnabled()) {
				logger.debug("repairCellValues: col values count after inserts: " + colValueList.size());
			}

			// process deleted columns
			for (int c = deletedCols.length - 1; c >= 0; c--) {
				int index = deletedCols[c] - 1;
				if (logger.isDebugEnabled()) {
					logger.debug("repairCellValues: deleting " + index);
				}
				colValueList.remove(index);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("repairCellValues: col values count after deletes: " + colValueList.size());
			}

			for (Iterator<String> iter = colValueList.iterator(); iter.hasNext();) {
				buff.append(iter.next());
				if (iter.hasNext()) {
					buff.append("|");
				}
			}
		}

		logger.debug("<<< repairCellValues: " + buff.toString());
		return buff.toString();
	}

	public static void setGridCellValues(AbstractGuidelineGrid grid, String[][] cellValues) throws InvalidDataException {
		if (cellValues == null) {
			grid.clearValues();
		}
		else {
			if (grid.getTemplate() == null) {
				throw new IllegalArgumentException("grid has no template");
			}
			int rowCount = cellValues.length;
			for (int r = 0; r < rowCount; r++) {
				for (int c = 0; c < grid.getTemplate().getNumColumns(); c++) {
					final Object cellValue = grid.getTemplate().getColumn(c + 1).convertToCellValue(
							cellValues[r][c],
							DomainManager.getInstance(),
							ConfigurationManager.getInstance().getEnumerationSourceConfigHelper());
					grid.setValue(r + 1, c + 1, cellValue);
				}
			}
		}
	}

	public static void setParameterGridCellValues(ParameterGrid parameterGrid, String[][] as) throws InvalidDataException {
		validateParameterGridCellValues(parameterGrid, as);
		StringBuilder buff = new StringBuilder();
		for (int i = 0; i < as.length; i++) {
			for (int j = 0; j < as[i].length; j++) {
				boolean flag1 = false;
				if (as[i].length - 1 == j) flag1 = true;
				buff.append(as[i][j]);
				if (!flag1) buff.append("|");
			}

			boolean flag = false;
			if (as.length - 1 == i) {
				flag = true;
			}
			if (!flag) {
				buff.append("~");
			}
		}
		parameterGrid.setCellValues(buff.toString());
		parameterGrid.setNumRows(as.length);
	}

	private static String toDebugString(Map<GridIDKey, Integer> map) {
		StringBuilder buff = new StringBuilder(System.getProperty("line.separator"));
		for (Iterator<GridIDKey> iter = map.keySet().iterator(); iter.hasNext();) {
			GridIDKey idKey = iter.next();
			buff.append(idKey);
			buff.append(" == ");
			buff.append(map.get(idKey));
			buff.append(System.getProperty("line.separator"));
		}
		return buff.toString();
	}

	private static void validateParameterGridCellValues(ParameterGrid parameterGrid, String[][] as) throws InvalidDataException {
		if (as != null && parameterGrid.getTemplate() != null) {
			for (int i = 0; i < as.length; i++) {
				for (int j = 0; j < as[i].length; j++) {
					parameterGrid.getTemplate().getColumn(j + 1).convertToCellValue(
							as[i][j],
							DomainManager.getInstance(),
							ConfigurationManager.getInstance().getEnumerationSourceConfigHelper());
				}
			}
		}
	}

	private final Logger logger = Logger.getLogger(GridActionCoordinator.class);

	private GridActionCoordinator() {
	}

	private List<GridCellDetail> buildEntityListGridCellDetailsToUpdate(GenericEntityType type, int id, boolean forEntity) {
		// find templates that has EntityList column for the specified type
		List<GridTemplate> templateList = GuidelineTemplateManager.getInstance().getTemplatesWithEntityListColumn(type, forEntity);

		// find grids that has references to the deleted entity
		List<GridCellDetail> gridCellDetailList = new ArrayList<GridCellDetail>();
		for (GridTemplate element : templateList) {
			List<ProductGrid> gridList = GridManager.getInstance().getAllGridsForTemplate(element.getID());
			for (Iterator<ProductGrid> iter2 = gridList.iterator(); iter2.hasNext();) {
				ProductGrid grid = iter2.next();
				List<GridCellDetail> updateList = ServerContextUtil.findReferencedGridCellsForEntities(grid, type, id, forEntity);
				for (GridCellDetail detail : updateList) {
					// if it's a single value, clear it
					if (detail.getCellValue() instanceof CategoryOrEntityValue) {
						detail.setCellValue(null);
					}
					// if it's multi-valued, remove reference
					else if (detail.getCellValue() instanceof CategoryOrEntityValues) {
						if (forEntity) {
							((CategoryOrEntityValues) detail.getCellValue()).removeEntityID(id);
						}
						else {
							((CategoryOrEntityValues) detail.getCellValue()).removeCategoryID(id);
						}
					}
					gridCellDetailList.add(detail);
				}
			}
		}
		return gridCellDetailList;
	}

	private void checkGridLocked(int templateID, GuidelineContext[] contexts, User user) throws LockException {
		LockManager.getInstance().getExistingProductGridLock(templateID, contexts, user);
	}

	// NOTE: Assume all elements in grids have the same context
	// TBD: reimplement when each grid can have its own context
	private void checkGridLocked(int templateID, List<ProductGrid> gridList, User user) throws LockException {
		if (gridList != null && !gridList.isEmpty()) {
			checkGridLocked(templateID, gridList.get(0).extractGuidelineContext(), user);
		}
	}

	/**
	 * @param entityType entityType
	 * @param sourceEntityID sourceEntityID
	 * @param newEntityID newEntityID
	 * @param gridUpdater gridUpdater
	 * @return
	 * @throws SQLException on error
	 * @throws SapphireException if failed to generate new id
	 */
	List<ProductGrid> cloneGuidelines(GenericEntityType entityType, int sourceEntityID, int newEntityID, GridUpdater gridUpdater) throws SQLException {
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();
		List<ProductGrid> list = GridManager.getInstance().getProductGrids(entityType, sourceEntityID);
		for (Iterator<ProductGrid> iterator = list.iterator(); iterator.hasNext();) {
			ProductGrid productGrid = iterator.next();
			ProductGrid newGrid = createProductGridWithoutCellValues(
					productGrid.getID(),
					productGrid.getTemplateID(),
					productGrid.getComments(),
					productGrid.getStatus(),
					new Date(),
					productGrid.getEffectiveDate(),
					productGrid.getExpirationDate(),
					productGrid.getNumRows(),
					productGrid.getID(),
					new Date(),
					productGrid.extractGuidelineContext());
			newGrid.addGenericEntityID(entityType, newEntityID);
			gridUpdater.updateGridContext(
					productGrid.getID(),
					ServerContextUtil.extractGenericEntityIdentities(newGrid),
					ServerContextUtil.extractGenericCategoryIdentities(newGrid));

			gridList.add(newGrid);
		}
		return gridList;
	}

	public void cloneGuidelines(int oldTemplateID, int newTemplateID, User user) throws ServletActionException {
		GridTemplate newTemplate = GuidelineTemplateManager.getInstance().getTemplate(newTemplateID);
		if (newTemplate == null) {
			throw new ServletActionException("msg.error", "No template of " + newTemplateID + " exists");
		}

		if (logger.isDebugEnabled()) {
			logger.debug(">>> cloneGuidelines: " + oldTemplateID + "->" + newTemplateID);
		}
		Connection conn = null;
		try {
			conn = DBConnectionManager.getInstance().getConnection();
			conn.setAutoCommit(false);

			List<ProductGrid> gridList = GridManager.getInstance().getAllGridsForTemplate(oldTemplateID);
			if (logger.isDebugEnabled()) {
				logger.debug("Cloning " + gridList.size() + " grids");
			}

			for (Iterator<ProductGrid> iter = gridList.iterator(); iter.hasNext();) {
				ProductGrid element = iter.next();
				// clone each grid
				ProductGrid clonedGrid = ProductGrid.copyOf(element, newTemplate, element.getEffectiveDate(), element.getExpirationDate());

				int gridID = DBIdGenerator.getInstance().nextGridID();
				if (logger.isDebugEnabled()) {
					logger.debug("    inserting: new grid-id = " + gridID);
				}

				// insert new activation into DB

				GridUpdater updater = new GridUpdater(conn);
				updater.insertProductGrid(
						gridID,
						newTemplateID,
						clonedGrid.getComments(),
						clonedGrid,
						clonedGrid.getStatus(),
						clonedGrid.getStatusChangeDate(),
						clonedGrid.getEffectiveDate(),
						clonedGrid.getExpirationDate(),
						clonedGrid.getNumRows(),
						clonedGrid.getCloneOf(),
						clonedGrid.getCreationDate(),
						ServerContextUtil.extractGenericEntityIdentities(clonedGrid),
						ServerContextUtil.extractGenericCategoryIdentities(clonedGrid));

				clonedGrid.setID(gridID);

				// insert new activation into cache
				GridManager.getInstance().addProductGrid(
						gridID,
						newTemplateID,
						clonedGrid.getComments(),
						clonedGrid,
						clonedGrid.getStatus(),
						clonedGrid.getStatusChangeDate(),
						clonedGrid.getEffectiveDate(),
						clonedGrid.getExpirationDate(),
						clonedGrid.getNumRows(),
						clonedGrid.getCloneOf(),
						clonedGrid.getCreationDate());
				GridManager.getInstance().setContext(gridID, clonedGrid.extractGuidelineContext());
			}
			conn.commit();

			AuditLogger.getInstance().logCloneTemplate(newTemplate, oldTemplateID, user.getUserID());
			logger.debug("<<< cloneGuidelines");
		}
		catch (Exception ex) {
			try {
				conn.rollback();
			}
			catch (SQLException ex2) {
				logger.warn("Failed to roll back", ex2);
			}
			logger.error("Failed to clone guidelines for " + oldTemplateID + "->" + newTemplateID, ex);
			throw new ServletActionException("msg.error", ex.getMessage());
		}
		finally {
			DBConnectionManager.getInstance().freeConnection(conn);
		}
	}

	List<ParameterGrid> cloneParameters(GenericEntityType entityType, int sourceEntityID, int newEntityID, ParameterUpdater parameterUpdater) throws SQLException {
		List<ParameterGrid> gridList = new ArrayList<ParameterGrid>();
		List<ParameterGrid> list = ParameterManager.getInstance().getGrids(entityType, sourceEntityID);
		for (Iterator<ParameterGrid> iterator = list.iterator(); iterator.hasNext();) {
			ParameterGrid parameterGrid = iterator.next();
			ParameterGrid newGrid = createParameterGridWithoutCellValues(
					parameterGrid.getID(),
					parameterGrid.getTemplateID(),
					parameterGrid.getComments(),
					parameterGrid.getStatus(),
					new Date(),
					parameterGrid.getEffectiveDate(),
					parameterGrid.getExpirationDate(),
					parameterGrid.getNumRows(),
					parameterGrid.getID(),
					new Date(),
					parameterGrid.extractGuidelineContext());
			newGrid.addGenericEntityID(entityType, newEntityID);
			parameterUpdater.updateGridContext(
					parameterGrid.getID(),
					ServerContextUtil.extractGenericEntityIdentities(newGrid),
					ServerContextUtil.extractGenericCategoryIdentities(newGrid));

			gridList.add(newGrid);
		}
		return gridList;
	}

	private ParameterGrid createParameterGridWithoutCellValues(int gridID, int templateID, String comments, String status, Date statusChangeDate, DateSynonym effDate,
			DateSynonym expDate, int numRows, int cloneOf, Date creationDate, GuidelineContext[] context) {
		ParameterGrid parameterGrid = new ParameterGrid(gridID, templateID, effDate, expDate);
		parameterGrid.setComments(comments);
		parameterGrid.setNumRows(numRows);
		parameterGrid.setStatus(status);
		parameterGrid.setStatusChangeDate(statusChangeDate);
		parameterGrid.setCloneOf(cloneOf);
		parameterGrid.setCreationDate(creationDate);
		ServerContextUtil.setContext(parameterGrid, context);
		parameterGrid.setTemplate(ParameterTemplateManager.getInstance().getTemplate(templateID));
		return parameterGrid;
	}

	private ProductGrid createProductGridWithoutCellValues(int gridID, int templateID, String comments, String status, Date statusChangeDate, DateSynonym effDate,
			DateSynonym expDate, int numRows, int cloneOf, Date creationDate, GuidelineContext[] context) {
		ProductGrid productgrid = new ProductGrid(gridID, GuidelineTemplateManager.getInstance().getTemplate(templateID), effDate, expDate);
		productgrid.setComments(comments);
		productgrid.setNumRows(numRows);
		productgrid.setStatus(status);
		productgrid.setStatusChangeDate(statusChangeDate);
		productgrid.setCloneOf(cloneOf);
		productgrid.setCreationDate(creationDate);
		ServerContextUtil.setContext(productgrid, context);
		return productgrid;
	}

	/**
	 * Saves the specified template and cutover guidelines for the source template to the specified
	 * template for the specified date. Core of this method is synchronized on <code>this</code>.
	 * 
	 * @param sourceTemplateID id of the template of which guidelines to cutover
	 * @param templateToSave template to save and to cutover guidelines to
	 * @param cutoverDate the cutover date
	 * @param guidelinesToCutOver guidelines to cut over
	 * @param user the user requesting this service
	 * @return the new id of the saved template
	 * @throws ServletActionException on error
	 */
	public int cutoverForAndStoreTemplate(final int sourceTemplateID, final GridTemplate templateToSave, final DateSynonym cutoverDate,
			final List<GuidelineReportData> guidelinesToCutOver, final User user) throws ServerException {
		if (sourceTemplateID < 1) throw new IllegalArgumentException("Invalid sourceTemplateID: " + sourceTemplateID);
		if (templateToSave == null) throw new NullPointerException("templateToSave cannot be null");
		if (cutoverDate == null || cutoverDate.getDate() == null) throw new IllegalArgumentException("Invalid cutover date: " + cutoverDate);

		logDebug(
				logger,
				"---> cutOverForAndStoreTemplate: %s, %s, %s, cutOverSize=%d for %s",
				sourceTemplateID,
				templateToSave,
				cutoverDate,
				guidelinesToCutOver.size(),
				user.getUserID());

		synchronized (this) { // to synchronize db update and cache update
			if (logger.isDebugEnabled()) {
				logger.debug("    cutoverForAndStoreTemplate: to-cutovers = " + guidelinesToCutOver.size());
			}

			final int id = BizActionCoordinator.getInstance().save(templateToSave, user);
			templateToSave.setID(id);

			final Map<GuidelineReportData, DateSynonym> guidelineToSaveMap = new HashMap<GuidelineReportData, DateSynonym>();
			final List<GuidelineReportData> expirationSetGuidelineList = new ArrayList<GuidelineReportData>();
			final List<GuidelineReportData> templateChangedGuidelineList = new ArrayList<GuidelineReportData>();

			// process guidelines to cut over
			Connection conn = null;
			try {
				conn = DBConnectionManager.getInstance().getConnection();
				conn.setAutoCommit(false);

				final GridUpdater updater = new GridUpdater(conn);

				for (final GuidelineReportData guidelineReportData : guidelinesToCutOver) {
					// [A] if effective date is not set pr before the cutover date
					if (guidelineReportData.getActivationDate() == null || guidelineReportData.getActivationDate().getDate() == null
							|| guidelineReportData.getActivationDate().before(cutoverDate)) {

						// 1 modify current one (set expiration date to the cutover date
						updateExpirationDatesOfGridsFor(guidelineReportData, cutoverDate, updater);
						expirationSetGuidelineList.add(guidelineReportData);

						// 2 add a new guideline with effectivate date set to cutover date for new
						// template
						guidelineToSaveMap.put(guidelineReportData, guidelineReportData.getExpirationDate());
					}
					// [B] effective date is after the cutover date
					else {
						// just change the template
						updateTemplateOfGridsFor(guidelineReportData, id, updater);
						templateChangedGuidelineList.add(guidelineReportData);
					}
				}

				// [C] insert all grids for data in guidelineToSaveList
				final Map<GridIDKey, Integer> gridIDMap = new HashMap<GridIDKey, Integer>();
				for (Map.Entry<GuidelineReportData, DateSynonym> entry : guidelineToSaveMap.entrySet()) {
					GuidelineReportData oldData = entry.getKey();
					DateSynonym expDate = entry.getValue();
					insertGridsFor(oldData, cutoverDate, expDate, templateToSave, user);
				}

				if (logger.isDebugEnabled()) {
					logDebug(logger, "... cutoverForAndStoreTemplate: idMap is %s", toDebugString(gridIDMap));
				}

				logDebug(logger, "... cutoverForAndStoreTemplate: commiting...");
				conn.commit();

				logDebug(logger, "... cutoverForAndStoreTemplate: updating cache...");

				// update cache with changes
				// ORDER IS IMPORTANT: must insert new ones before updating existing grids!!!
				for (Map.Entry<GuidelineReportData, DateSynonym> entry : guidelineToSaveMap.entrySet()) {
					GuidelineReportData oldData = entry.getKey();
					DateSynonym expDate = entry.getValue();
					updateCacheForInsertedGrids(oldData, cutoverDate, expDate, templateToSave, user, gridIDMap);
				}
				for (int i = 0; i < expirationSetGuidelineList.size(); i++) {
					GuidelineReportData element = expirationSetGuidelineList.get(i);
					updateCacheForExpirationDateOfGrids(element, cutoverDate);
				}
				for (int i = 0; i < templateChangedGuidelineList.size(); i++) {
					GuidelineReportData element = templateChangedGuidelineList.get(i);
					updateCacheForTemplateOfGrids(element, templateToSave);
				}

				AuditLogger.getInstance().logNewTemplateWithCutover(templateToSave, sourceTemplateID, cutoverDate, user.getUserID());
				logDebug(logger, "<-- cutoverForAndStoreTemplate: %s", id);
				return id;
			}
			catch (Exception ex) {
				logError(logger, ex, "Failed to cutover guidelines for new template version: %s to %s", sourceTemplateID, templateToSave.getVersion());
				try {
					conn.rollback();
				}
				catch (SQLException ex2) {
					logger.warn("Failed to roll back", ex2);
				}
				BizActionCoordinator.getInstance().deleteEntity(PeDataType.TEMPLATE, id, user);
				throw new ServletActionException("msg.error", ex.getMessage());
			}
			finally {
				DBConnectionManager.getInstance().freeConnection(conn);
			}
		}
	}

	/**
	 * Deletes a list of grids and updates the cache.  
	 * 
	 * @param gridManager gridManager
	 * @param updater updater
	 * @param gridList List of grids to delete
	 * @throws SQLException on error
	 * @throws SapphireException on error
	 */
	private void deleteGridList(GridUpdater updater, List<ProductGrid> gridList) throws SQLException, SapphireException {
		logger.debug(">>> deleteGridList with " + gridList.size());
		assert (gridList != null);
		for (Iterator<ProductGrid> i = gridList.iterator(); i.hasNext();) {
			ProductGrid grid = i.next();
			GridManager.getInstance().removeFromCache(grid);
			updater.deleteProductGrid(grid.getID());
		}

		logger.debug("<<< deleteGridList");
	}

	public List<GuidelineContext> fetchFullContext(int templateID, GuidelineContext[] subContexts) throws ServletActionException {
		return GridManager.getInstance().getFullContext(templateID, subContexts);
	}

	public List<ProductGrid> fetchGridData(int templateID, GuidelineContext[] contexts) throws ServletActionException {
		List<ProductGrid> list = new LinkedList<ProductGrid>();
		fetchGridData_internal(list, templateID, contexts);
		return list;
	}

	private void fetchGridData_internal(List<ProductGrid> list, int templateID, GuidelineContext[] contexts) throws ServletActionException {
		list.addAll(GridManager.getInstance().getProductGrids(templateID, contexts));
	}

	/**
	 * Note this only handles contexts of size one of two.
	 * 
	 * @param usageType usageType
	 *            the template usage type
	 * @param contexts contexts
	 *            guideline context; must be of size one or two
	 * @param user user
	 *            the user for which to retrieve grid summaries
	 * @return a list of {@link GridSummary}objects
	 * @throws ServletActionException on error
	 *             on error
	 */
	public List<GridSummary> fetchGridSummaries(TemplateUsageType usageType, GuidelineContext contexts[], User user) throws ServerException {
		logger.debug(">>> fetchGridSummaries for " + usageType + ": context-depth=" + contexts.length);

		List<GridSummary> summaryList = new java.util.ArrayList<GridSummary>();

		// retrieve all templates of the given usage type
		GuidelineTemplateManager templateManager = GuidelineTemplateManager.getInstance();
		List<GridTemplate> templates = templateManager.getTemplates(usageType);
		logger.info("# of templates retrieved: " + templates.size());

		GridManager gridmanager = GridManager.getInstance();
		SecurityCacheManager securitycontroller = SecurityCacheManager.getInstance();

		for (Iterator<GridTemplate> iterator = templates.iterator(); iterator.hasNext();) {
			GridTemplate gridtemplate = iterator.next();
			int templateID = gridtemplate.getID();
			if (securitycontroller.allowView(templateID, user.getUserID())) {
				// build a summary instance
				GridSummary gridsummary = new GridSummary(gridtemplate);
				gridsummary.setEditAllowed(securitycontroller.allowEdit(templateID, user.getUserID()));
				gridsummary.setCommon(gridmanager.isSame(templateID, contexts));
				gridsummary.setGridInstantiations(gridmanager.hasApplicableGrids(templateID, contexts));
				// don't check for subcontext, if no grids exists or not common
				if (gridsummary.hasGridInstantiations() && gridsummary.isCommon()) {
					gridsummary.setSubContext(gridmanager.isSubContext(templateID, contexts));
				}

				// check lock status
				try {
					checkGridLocked(templateID, contexts, user);
					gridsummary.setLocked(false);
				}
				catch (LockException ex) {
					gridsummary.setLocked(true);
				}
				summaryList.add(gridsummary);
			}
		}

		logger.debug("<<< fetchGridSummaries");
		return summaryList;
	}

	public void generateMissingRuleIDsForTemplate(int templateID) throws SapphireException, InvalidDataException {
		List<String> ruleIDColList = GuidelineTemplateManager.getInstance().getTemplate(templateID).getRuleIDColumnNames();
		List<GridCellDetail> gridCellDetailList = new ArrayList<GridCellDetail>();
		List<ProductGrid> gridList = GridManager.getInstance().getAllGridsForTemplate(templateID);
		for (ProductGrid grid : gridList) {
			for (String columnName : ruleIDColList) {
				for (int row = 1; row <= grid.getNumRows(); row++) {
					if (grid.getCellValue(row, columnName) == null) {
						Long ruleID = new Long(DBIdGenerator.getInstance().nextRuleID());
						grid.setValue(row, columnName, ruleID);
						GridCellDetail gridCellDetail = new GridCellDetail();
						gridCellDetail.setGridID(grid.getID());
						gridCellDetail.setRowID(row);
						gridCellDetail.setColumnName(columnName);
						gridCellDetail.setCellValue(ruleID);
						gridCellDetailList.add(gridCellDetail);
					}
				}
			}
		}
		Connection conn = null;
		try {
			conn = DBConnectionManager.getInstance().getConnection();
			conn.setAutoCommit(false);

			GridUpdater updater = new GridUpdater(conn);
			updater.setCellValues(gridCellDetailList.toArray(new GridCellDetail[0]));
			conn.commit();
		}
		catch (Exception ex) {
			logger.error("Failed to generate rule ids for template " + templateID, ex);
			try {
				conn.rollback();
			}
			catch (SQLException ex2) {
				logger.warn("Failed to roll back", ex2);
			}
			// roll back changes made in the cache
			for (GridCellDetail gridCellDetail : gridCellDetailList) {
				ProductGrid grid = GridManager.getInstance().getProductGrid(gridCellDetail.getGridID());
				grid.setValue(gridCellDetail.getRowID(), gridCellDetail.getColumnName(), null);
			}
		}
		finally {
			DBConnectionManager.getInstance().freeConnection(conn);
		}
	}

	private List<AbstractGuidelineGrid> getGuidelineGrids(GuidelineReportData data) throws ServletActionException {
		List<ProductGrid> list = new ArrayList<ProductGrid>();
		fetchGridData_internal(list, data.getID(), data.getContext());

		List<AbstractGuidelineGrid> resultList = new ArrayList<AbstractGuidelineGrid>();
		for (int i = 0; i < list.size(); i++) {
			AbstractGuidelineGrid grid = list.get(i);
			logger.debug("... getGuidelineGrids: checking " + grid);
			if (UtilBase.isSame(data.getActivationDate(), grid.getEffectiveDate()) && UtilBase.isSame(data.getExpirationDate(), grid.getExpirationDate())) {
				logger.debug("... getGuidelineGrids: adding " + grid);
				resultList.add(grid);
			}
		}
		return resultList;
	}

	public ProductGrid getProductGridFor(GuidelineReportData guideline) throws ServletActionException {
		List<ProductGrid> resultList = fetchGridData(guideline.getID(), guideline.getContext());
		for (Iterator<ProductGrid> iter = resultList.iterator(); iter.hasNext();) {
			ProductGrid element = iter.next();
			if (element.hasIdenticalEffExtDates(guideline.getActivationDate(), guideline.getExpirationDate())) {
				return element;
			}
		}
		return null;
	}

	public void importGridData(int templateID, List<ProductGrid> grids, User user) throws ImportException {
		logger.debug(">>> importGridData: " + templateID + ", grids=" + grids.size());
		synchronized (this) {
			try {
				checkGridLocked(templateID, grids, user);
				syncGridData_internal(templateID, grids, null, user, true, false); // don't update rule ids
			}
			catch (LockException e) {
				throw new ImportException("Failed to lock - locked by " + e.getLockedBy());
			}
			catch (ServletActionException e) {
				throw new ImportException("Failed to update DB - " + e.getLocalizedMessage());
			}
		}
		logger.debug("<<< importGridData");
	}

	private void insertGridsFor(GuidelineReportData oldData, DateSynonym effDate, DateSynonym expDate, GridTemplate newTemplate, User user)
			throws ServletActionException, SapphireException, SQLException {
		List<AbstractGuidelineGrid> oldList = getGuidelineGrids(oldData);
		List<ProductGrid> saveList = new ArrayList<ProductGrid>();
		if (!oldList.isEmpty()) {
			for (Iterator<AbstractGuidelineGrid> iter = oldList.iterator(); iter.hasNext();) {
				ProductGrid oldGrid = (ProductGrid) iter.next();
				saveList.add(ProductGrid.copyOf(oldGrid, newTemplate, effDate, expDate));
			}
			syncGridData_internal(newTemplate.getID(), saveList, null, user, true, false);
		}
	}

	public void lockGrid(int templateID, GuidelineContext[] contexts, User user) throws LockException {
		synchronized (this) {
			lockGrid_internal(templateID, contexts, user);
		}
	}

	private void lockGrid_internal(int templateID, GuidelineContext[] contexts, User user) throws LockException {
		LockManager.getInstance().lockProductGrid(templateID, contexts, user);
	}

	// This is only used for importing ad-hoc rules
	// TBD: Remove this when ad-hoc rule is no longer supported
	public ProductGrid produceGrid(GridTemplate template, List<String> valueStrList, RuleSet ruleSet, GuidelineContext[] context, User user)
			throws ServletActionException, ImportException, DataValidationFailedException {
		ProductGrid grid = new ProductGrid(
				-1,
				template,
				BizActionCoordinator.getDateSynonymAndCreateIfNecessary(ruleSet.getActivationDate(), user),
				BizActionCoordinator.getDateSynonymAndCreateIfNecessary(ruleSet.getExpirationDate(), user));

		grid.setNumRows(1);
		grid.setCreationDate(new Date());
		grid.setStatus(ruleSet.getStatus());
		grid.setStatusChangeDate(new Date());
		String[][] cellValues = new String[1][valueStrList.size()];
		for (int i = 0; i < cellValues[0].length; i++) {
			cellValues[0][i] = valueStrList.get(i);
		}
		try {
			setGridCellValues(grid, cellValues);
		}
		catch (InvalidDataException e) {
			throw new ImportException("Invalid cell values");
		}
		ServerContextUtil.setContext(grid, context);

		return grid;
	}

	public void removeGenericCategoryFromContextAndEntityListColumns(int categoryType, int categoryID) throws ServletActionException {
		List<GridCellDetail> gridCellDetailList = buildEntityListGridCellDetailsToUpdate(GenericEntityType.forCategoryType(categoryType), categoryID, false);

		// remove references from DB
		Connection connection = null;
		try {
			connection = DBConnectionManager.getInstance().getConnection();
			connection.setAutoCommit(false);

			GridUpdater gridUpdater = new GridUpdater(connection);
			gridUpdater.deleteCategoryFromContext(categoryType, categoryID);

			ParameterUpdater paramUpdater = new ParameterUpdater(connection);
			paramUpdater.deleteCategoryFromContext(categoryType, categoryID);

			GridUpdater updater = new GridUpdater(connection);
			updater.updateCellValues(gridCellDetailList.toArray(new GridCellDetail[0]));
			connection.commit();

			// remove references from cache
			GridManager.getInstance().replaceCellValues(gridCellDetailList.toArray(new GridCellDetail[0]));
			GridManager.getInstance().removeCategoryFromAllContext(categoryType, categoryID);
			ParameterManager.getInstance().removeCategoryFromAllContext(categoryType, categoryID);
		}
		catch (SQLException ex) {
			DBUtil.rollBackLocallyManagedConnection(connection);
			logger.error("Failed to remove category from context and EntityList columns for " + categoryType + "," + categoryID, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
		catch (Exception ex) {
			DBUtil.rollBackLocallyManagedConnection(connection);
			logger.error("Failed remove category from context and EntityList columns for " + categoryType + "," + categoryID, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
		finally {
			if (connection != null) {
				DBConnectionManager.getInstance().freeConnection(connection);
			}
		}
	}

	public void removeGenericEntityFromContextAndEntityListColumns(GenericEntityType type, int entityID) throws ServletActionException {
		logger.debug(">>> removeGenericEntityFromContextAndEntityListColumns: " + type + ", " + entityID);
		List<GridCellDetail> gridCellDetailList = buildEntityListGridCellDetailsToUpdate(type, entityID, true);

		// remove references from DB
		Connection connection = null;
		try {
			connection = DBConnectionManager.getInstance().getConnection();
			connection.setAutoCommit(false);

			GridUpdater gridUpdater = new GridUpdater(connection);
			gridUpdater.deleteEntityFromContext(type.getID(), entityID);

			ParameterUpdater paramUpdater = new ParameterUpdater(connection);
			paramUpdater.deleteEntityFromContext(type.getID(), entityID);

			GridUpdater updater = new GridUpdater(connection);
			updater.updateCellValues(gridCellDetailList.toArray(new GridCellDetail[0]));
			connection.commit();

			// remove references from cache
			GridManager.getInstance().replaceCellValues(gridCellDetailList.toArray(new GridCellDetail[0]));
			GridManager.getInstance().removeGenericEntityFromAllContext(type, entityID);
			ParameterManager.getInstance().removeEntityFromAllContext(type, entityID);
		}
		catch (SQLException ex) {
			DBUtil.rollBackLocallyManagedConnection(connection);
			logger.error("Failed to remove entity from context and EntityList columns for " + type + "," + entityID, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
		catch (Exception ex) {
			DBUtil.rollBackLocallyManagedConnection(connection);
			logger.error("Failed remove entity from context and EntityList columns for " + type + "," + entityID, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
		finally {
			if (connection != null) {
				DBConnectionManager.getInstance().freeConnection(connection);
			}
		}
	}

	/**
	 * Gets a list of two lists of instances of {@link com.mindbox.pe.model.GuidelineReportData} for
	 * the specified template that will be cutover.
	 * 
	 * @param templateID templateID
	 *            tmplate id
	 * @param cutoverDate cutoverDate
	 *            the cutover date
	 * @param username username
	 *            user id
	 * @return list of list of {@link com.mindbox.pe.model.GuidelineReportData} instances; first one
	 *         with cutover guidelines, the other non-cutover guidelines
	 * @throws ServletActionException on error
	 */
	public List<List<GuidelineReportData>> retrieveCutoverGuidelinesForTemplate(int templateID, DateSynonym cutoverDate, String username) throws ServerException {
		if (templateID < 1) throw new IllegalArgumentException("Invalid template ID: " + templateID);
		if (cutoverDate == null || cutoverDate.getDate() == null) throw new IllegalArgumentException("Invalid cutover date: " + cutoverDate);

		List<GuidelineReportData> cutoverList = new ArrayList<GuidelineReportData>();
		List<GuidelineReportData> nonCutoverList = new ArrayList<GuidelineReportData>();

		fillCutoverGuidelinesForTemplate(templateID, cutoverDate, username, cutoverList, nonCutoverList);

		List<List<GuidelineReportData>> resultList = new ArrayList<List<GuidelineReportData>>();
		resultList.add(cutoverList);
		resultList.add(nonCutoverList);

		return resultList;
	}

	public void saveGridData(int templateID, List<ProductGrid> grids, boolean updateEvenIfIdentical, User user) throws ServletActionException, LockException {
		syncGridData(templateID, grids, null, updateEvenIfIdentical, user);
	}

	public Map<IntegerPair, Integer> syncGridData(int templateID, List<ProductGrid> grids, List<ProductGrid> removedGrids, boolean updateEvenIfIdentical, User user)
			throws ServletActionException, LockException {
		if (grids == null) return null;

		synchronized (this) {
			// check locks --
			checkGridLocked(templateID, grids, user);
			// save grid data
			return syncGridData_internal(templateID, grids, removedGrids, user, true, updateEvenIfIdentical);
		}
	}

	/**
	 * Synchronizes the list of grids to save and grids to delete with the 
	 * persisted grids and cache. 
	 * Make sure contexts has at least one element.
	 * 
	 * @param templateID templateID
	 * @param contexts contexts
	 * @param grids grids
	 * @param user user
	 * @throws ServletActionException on error
	 */
	private Map<IntegerPair, Integer> syncGridData_internal(int templateID, List<ProductGrid> grids, List<ProductGrid> removedGrids, User user, boolean generateGridID,
			boolean updateEvenIfIdentical) throws ServletActionException {
		if (logger.isDebugEnabled()) {
			logger.debug(">>> saveGridData_internal with " + templateID + ", " + grids.size() + " grids");
		}
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection connection = null;
		try {
			connection = dbconnectionmanager.getConnection();
			connection.setAutoCommit(false);

			GridUpdater updater = new GridUpdater(connection);

			// for storing new grid ID's
			Map<GridIDKey, Integer> gridIDMap = new HashMap<GridIDKey, Integer>();

			syncGridList(GridManager.getInstance(), updater, templateID, grids, removedGrids, gridIDMap, generateGridID, updateEvenIfIdentical);

			if (logger.isDebugEnabled()) {
				logger.debug("saveGridData: idMap is " + toDebugString(gridIDMap));
			}

			connection.commit();

			logger.info("Transaction Committed. Updating cache...");

			updateCache(templateID, grids, user, gridIDMap);

			// audit removed activations
			AuditLogger.getInstance().logDeleteGrids(removedGrids, user.getUserID());
			return asDateSynonymPairGridIdMap(gridIDMap);
		}
		catch (SQLException ex) {
			DBUtil.rollBackLocallyManagedConnection(connection);
			logger.error("Failed to save grid data", ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
		catch (Exception ex) {
			DBUtil.rollBackLocallyManagedConnection(connection);
			logger.error("Failed to save grid data", ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
		finally {
			if (connection != null) {
				dbconnectionmanager.freeConnection(connection);
			}
		}
	}

	/**
	 * Synchronizes the list of grids to save and grids to delete with the 
	 * persisted grids and cache. 
	 *  
	 * Do not update context here.
	 * 
	 * @param gridManager gridManager
	 * @param updater updater
	 * @param templateID templateID
	 * @param gridList gridList
	 * @param gridIDMap gridIDMap
	 * @param generateGridID generateGridID
	 * @throws SQLException on error
	 * @throws SapphireException on error
	 */
	private void syncGridList(GridManager gridManager, GridUpdater updater, int templateID, List<ProductGrid> gridList, List<ProductGrid> removedGrids,
			Map<GridIDKey, Integer> gridIDMap, boolean generateGridID, boolean updateEvenIfIdentical) throws SQLException, SapphireException {
		if (logger.isDebugEnabled()) {
			logger.debug(">>> saveGridList with " + templateID + ",grids=" + gridList.size() + ",generateGridID=?" + generateGridID);
		}
		assert (gridList != null);
		// update if exists and insert if new
		for (Iterator<ProductGrid> iter = gridList.iterator(); iter.hasNext();) {
			ProductGrid grid = iter.next();
			ProductGrid cachedGrid = gridManager.getProductGrid(grid.getID());
			if (logger.isDebugEnabled()) {
				logger.debug("    gridToSave: " + grid);
				logger.debug("    cachedGrid: " + cachedGrid);
			}
			if (cachedGrid == null) {
				int newGridID = ((generateGridID) ? DBIdGenerator.getInstance().nextGridID() : grid.getID());
				if (logger.isDebugEnabled()) {
					logger.debug("    inserting: new grid-id = " + newGridID);
				}

				// insert new activation
				updater.insertProductGrid(
						newGridID,
						templateID,
						grid.getComments(),
						grid,
						grid.getStatus(),
						grid.getStatusChangeDate(),
						grid.getEffectiveDate(),
						grid.getExpirationDate(),
						grid.getNumRows(),
						grid.getCloneOf(),
						grid.getCreationDate(),
						ServerContextUtil.extractGenericEntityIdentities(grid),
						ServerContextUtil.extractGenericCategoryIdentities(grid));

				gridIDMap.put(new GridIDKey(grid.getID(), grid), newGridID);
			}
			else {
				if (updateEvenIfIdentical || !cachedGrid.identical(grid)) {
					if (logger.isDebugEnabled()) {
						logger.debug("    updating " + cachedGrid);
					}
					updater.updateGrid(
							cachedGrid.getID(),
							grid.getComments(),
							grid,
							grid.getStatus(),
							grid.getStatusChangeDate(),
							grid.getEffectiveDate(),
							grid.getExpirationDate(),
							grid.getCloneOf());
				}
			}
		}

		if (removedGrids != null) {
			deleteGridList(updater, removedGrids);
		}

		logger.debug("<<< saveGridList");
	}

	public void unlockGrid(int templateID, GuidelineContext contexts[], User user) throws LockException {
		synchronized (this) {
			unlockGrid_internal(templateID, contexts, user);
		}
	}

	private void unlockGrid_internal(int templateID, GuidelineContext[] contexts, User user) throws LockException {
		LockManager.getInstance().unlockProductGrid(templateID, contexts, user);
	}

	private void updateCache(int templateID, List<ProductGrid> gridList, User user, Map<GridIDKey, Integer> gridIDMap) throws SapphireException, SQLException {
		if (logger.isDebugEnabled()) {
			logger.debug(">>> updateCache with " + templateID + ",grid.size=" + gridList.size() + ",user=" + user);
		}
		// update if exists and insert if new
		for (Iterator<ProductGrid> iter = gridList.iterator(); iter.hasNext();) {
			ProductGrid grid = iter.next();
			updateCache(templateID, grid, user, gridIDMap);
		} // for
		logger.debug("<<< updateCache");
	}

	private void updateCache(int templateID, ProductGrid grid, User user, Map<GridIDKey, Integer> gridIDMap) throws SapphireException, SQLException {
		if (logger.isDebugEnabled()) {
			logger.debug("updateCache: processing " + grid);
		}
		ProductGrid cachedGrid = GridManager.getInstance().getProductGrid(grid.getID());
		if (cachedGrid != null) {
			if (!cachedGrid.identical(grid)) {
				AuditLogger.getInstance().logUpdateGrid(grid, cachedGrid, user.getUserID());
				GridManager.getInstance().updateCache(cachedGrid, grid);
			}
			else {
				logger.debug("updateCache: no update made - contains no changed data");
			}
		}
		else {
			logger.info("updateCache: inserting new activation");
			Integer gridIDObject = gridIDMap.get(new GridIDKey(grid.getID(), grid));

			DateSynonym cachedEffDate = (grid.getEffectiveDate() == null ? null : DateSynonymManager.getInstance().getDateSynonym(grid.getEffectiveDate().getID()));
			DateSynonym cachedExpDate = (grid.getExpirationDate() == null ? null : DateSynonymManager.getInstance().getDateSynonym(grid.getExpirationDate().getID()));

			if (gridIDObject != null) {
				// insert new activation
				GridManager.getInstance().addProductGrid(
						gridIDObject.intValue(),
						templateID,
						grid.getComments(),
						grid,
						grid.getStatus(),
						grid.getStatusChangeDate(),
						cachedEffDate,
						cachedExpDate,
						grid.getNumRows(),
						grid.getCloneOf(),
						grid.getCreationDate());
				GridManager.getInstance().setContext(gridIDObject.intValue(), grid);
				AuditLogger.getInstance().logNewGrid(GridManager.getInstance().getProductGrid(gridIDObject.intValue()), user.getUserID());
			}
			else {
				logger.error("updateCache: *** grid ID not found in the ID-map: " + grid);
				logger.error("updateCache: *** ignoring insertion into cache ***");
			}
		}
	}

	private void updateCacheForExpirationDateOfGrids(GuidelineReportData data, DateSynonym cutoverDate) throws ServletActionException {
		DateSynonym cachedCutoverDate = (cutoverDate == null ? null : DateSynonymManager.getInstance().getDateSynonym(cutoverDate.getID()));
		if (logger.isDebugEnabled()) {
			logger.debug(">>> updateCacheForExpirationDateOfGrids: " + data + " at " + cachedCutoverDate);
		}
		List<AbstractGuidelineGrid> list = getGuidelineGrids(data);
		for (Iterator<AbstractGuidelineGrid> iter = list.iterator(); iter.hasNext();) {
			AbstractGuidelineGrid grid = iter.next();
			if (logger.isDebugEnabled()) {
				logger.debug("    updateCacheForExpirationDateOfGrids: updating cache expiration date of " + grid);
			}
			GridManager.getInstance().updateExpirationDate(grid, cachedCutoverDate);
		}
		logger.debug("<<< updateCacheForExpirationDateOfGrids");
	}

	private void updateCacheForInsertedGrids(GuidelineReportData oldData, DateSynonym effDate, DateSynonym expDate, GridTemplate newTemplate, User user,
			Map<GridIDKey, Integer> gridIDMap) throws ServletActionException, SapphireException, SQLException {
		List<AbstractGuidelineGrid> oldList = getGuidelineGrids(oldData);
		if (!oldList.isEmpty()) {
			for (Iterator<AbstractGuidelineGrid> iter = oldList.iterator(); iter.hasNext();) {
				ProductGrid oldGrid = (ProductGrid) iter.next();
				ProductGrid newGrid = ProductGrid.copyOf(oldGrid, newTemplate, effDate, expDate);
				updateCache(newTemplate.getID(), newGrid, user, gridIDMap);
			}
		}
	}

	private void updateCacheForTemplateOfGrids(GuidelineReportData data, GridTemplate newTemplate) throws ServletActionException {
		if (logger.isDebugEnabled()) {
			logger.debug(">>> updateCacheForTemplateOfGrids: " + data + " to " + newTemplate);
		}
		List<AbstractGuidelineGrid> list = getGuidelineGrids(data);
		for (Iterator<AbstractGuidelineGrid> iter = list.iterator(); iter.hasNext();) {
			AbstractGuidelineGrid grid = iter.next();
			GridManager.getInstance().updateTemplate(grid, newTemplate);
		}
		logger.debug("<<< updateCacheForTemplateOfGrids");
	}

	// TT-19
	public void updateCellValuesForRearrangedColumns(final int templateID, final Map<Integer, Integer> rearrangedColumnMap, final User user)
			throws ServletActionException, LockException {
		logDebug(logger, "---> updateCellValuesForRearrangedColumns: %s, %s, %s", templateID, rearrangedColumnMap, user);
		final List<ProductGrid> gridsToUpdate = new ArrayList<ProductGrid>();
		for (ProductGrid grid : GridManager.getInstance().getAllGridsForTemplate(templateID)) {
			if (grid.updateValuesForRearrangedColumns(rearrangedColumnMap)) {
				gridsToUpdate.add(grid);
			}
		}

		if (!gridsToUpdate.isEmpty()) {
			syncGridData(templateID, gridsToUpdate, null, true, user);
		}

		logDebug(logger, "<--- updateCellValuesForRearrangedColumns: %s", templateID);
	}

	private void updateExpirationDatesOfGridsFor(GuidelineReportData data, DateSynonym cutoverDate, GridUpdater updater) throws SQLException, ServletActionException {
		if (logger.isDebugEnabled()) {
			logger.debug(">>> updateExpirationDatesOfGridsFor: " + data + " at " + cutoverDate);
		}
		List<AbstractGuidelineGrid> list = getGuidelineGrids(data);
		for (Iterator<AbstractGuidelineGrid> iter = list.iterator(); iter.hasNext();) {
			AbstractGuidelineGrid grid = iter.next();
			if (logger.isDebugEnabled()) {
				logger.debug("    updateExpirationDatesOfGridsFor: updating expiration date of " + grid);
			}
			updater.setExpirationDateOfGrid(grid.getID(), cutoverDate.getID());
		}
		logger.debug("<<< updateExpirationDatesOfGridsFor");
	}

	/**
	 * Updates contexts for the specified grids.
	 * 
	 * @param templateID template for the grids
	 * @param grids grids of which context to update
	 * @param newContexts new context
	 * @param user user
	 * @throws ServletActionException on error
	 * @throws LockException on lock error
	 * @since PowerEditor 4.2.0
	 */
	public void updateGridContext(int templateID, List<ProductGrid> grids, GuidelineContext[] newContexts, User user) throws ServletActionException, LockException {
		if (logger.isDebugEnabled()) {
			logger.debug("--> updateGridContext: " + templateID);
			logger.debug("    updateGridContext: grid.size=" + (grids == null ? 0 : grids.size()));
			logger.debug("    updateGridContext: newContext = " + Util.toString(newContexts));
		}

		synchronized (this) {
			// 1. check grid locks
			checkGridLocked(templateID, newContexts, user);

			// 2. modify DB & update cache
			updateGridContext_internal2(templateID, grids, newContexts, user);
		}
		logger.debug("<-- updateGridContext");
	}

	private void updateGridContext_internal2(int templateID, List<ProductGrid> grids, GuidelineContext[] newContexts, User user) throws ServletActionException {
		if (logger.isDebugEnabled()) {
			logger.debug(">>> updateGridContext_internal2 with " + templateID + ", " + ", " + newContexts.length + " new-contexts");
		}
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection connection = null;

		try {
			connection = dbconnectionmanager.getConnection();
			connection.setAutoCommit(false);

			GridUpdater updater = new GridUpdater(connection);

			// store old context just for audit
			Map<ProductGrid, GuidelineContext[]> oldContextMap = new HashMap<ProductGrid, GuidelineContext[]>();
			for (Iterator<ProductGrid> iter = grids.iterator(); iter.hasNext();) {
				ProductGrid element = iter.next();
				updater.updateGridContext(
						element.getID(),
						ServerContextUtil.extractGenericEntityIdentities(newContexts),
						ServerContextUtil.extractGenericCategoryIdentities(newContexts));

				GuidelineContext[] oldContexts = GridManager.getInstance().getProductGrid(element.getID()).extractGuidelineContext();
				oldContextMap.put(element, oldContexts);
				GridManager.getInstance().setContext(element.getID(), newContexts);
			}
			connection.commit();

			AuditLogger.getInstance().logUpdateContext(oldContextMap, newContexts, user.getUserID());
			logger.info("<<< updateGridContext_internal2: Transaction Committed");
		}
		catch (Exception ex) {
			DBUtil.rollBackLocallyManagedConnection(connection);
			logger.error("Failed to update grid context", ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
		finally {
			if (connection != null) {
				dbconnectionmanager.freeConnection(connection);
			}
		}
	}

	private void updateTemplateOfGridsFor(GuidelineReportData data, int newTemplateID, GridUpdater updater) throws SQLException, ServletActionException {
		if (logger.isDebugEnabled()) {
			logger.debug(">>> updateTemplateOfGridsFor: " + data + " to " + newTemplateID);
		}
		List<AbstractGuidelineGrid> list = getGuidelineGrids(data);
		for (Iterator<AbstractGuidelineGrid> iter = list.iterator(); iter.hasNext();) {
			AbstractGuidelineGrid grid = iter.next();
			if (logger.isDebugEnabled()) {
				logger.debug("    updateExpirationDatesOfGridsFor: updating template of " + grid);
			}
			updater.setTemplateOfGrid(grid.getID(), newTemplateID);
		}
		logger.debug("<<< updateTemplateOfGridsFor");
	}

}
