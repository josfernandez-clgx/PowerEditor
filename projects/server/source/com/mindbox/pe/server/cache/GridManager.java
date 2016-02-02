package com.mindbox.pe.server.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.AbstractGuidelineGrid;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GridValueContainable;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.server.ServerContextUtil;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.Util;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.model.GenericCategoryIdentity;
import com.mindbox.pe.server.model.GenericEntityIdentity;
import com.mindbox.pe.server.model.GridCellDetail;
import com.mindbox.pe.server.servlet.ServletActionException;

public class GridManager extends AbstractCacheManager {

	private static final String ALL_ACCESS_USERID = "com.mindbox.allAccessUser";

	private static GridManager mSingleton = null;

	public static synchronized GridManager getInstance() {
		if (mSingleton == null) mSingleton = new GridManager();
		return mSingleton;
	}

	/**
	 * Tests if the context of the specified grid contains the specified context array.
	 * If both contexts are identical, this returns <code>false</code>.
	 * @param grid the grid
	 * @param contexts the context array
	 * @return <code>true</code> if <code>grid</code>'s context contains <code>contexts</code>;
	 *         <code>false</code>, otherwise
	 */
	private static boolean isContainedIn(GuidelineContext[] contexts, ProductGrid grid) {
		if (contexts == null || contexts.length == 0) return false;
		if (ServerContextUtil.hasSameContext(grid, contexts)) return false;
		for (int i = 0; i < contexts.length; i++) {
			boolean contextHasIds = contexts[i].getIDs() != null && contexts[i].getIDs().length > 0;
			GenericEntityType genericEntityType = contexts[i].getGenericEntityType();
			if (genericEntityType != null) {
				// check entity first
				int[] idsFromGrid = grid.getGenericEntityIDs(genericEntityType);
				if (idsFromGrid != null && idsFromGrid.length > 0) {
					if (!(contextHasIds && Util.isContainedIn(contexts[i].getIDs(), idsFromGrid))) return false;
				}
				if (grid.hasGenericCategoryContext(genericEntityType)) return false;
			}
			else if (contexts[i].getGenericCategoryType() > 0) {
				genericEntityType = ConfigurationManager.getInstance().getEntityConfiguration().findEntityTypeForCategoryType(
						contexts[i].getGenericCategoryType());
				int[] idsFromGrid = grid.getGenericCategoryIDs(genericEntityType);
				if (idsFromGrid != null && idsFromGrid.length > 0) {
					if (!(contextHasIds && Util.equals(contexts[i].getIDs(), idsFromGrid))) return false;
				}
				if (grid.hasGenericEntityContext(genericEntityType)) return false;
			}
		}
		return true;
	}

	/**
	 * @param prodGrids
	 *            grid array; length must be >= 1
	 * @return the guideline report data
	 * @throws ServerException 
	 */
	private static GuidelineReportData toGuidelineReportData(ProductGrid prodGrid, String userId) throws ServerException {
		int templateID = prodGrid.getTemplateID();
		GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(templateID);
		if (template == null) return null;
		boolean allowEdit = (userId.equals(ALL_ACCESS_USERID) ? true : SecurityCacheManager.getInstance().allowEdit(templateID, userId));
		GuidelineReportData data = new GuidelineReportData(templateID, prodGrid.getID(), template.getName(), template.getUsageType(), template.getVersion(),
				prodGrid.extractGuidelineContext(), prodGrid.getCreationDate(), prodGrid.getEffectiveDate(), prodGrid.getExpirationDate(), allowEdit);
		data.setStatus(prodGrid.getStatus());
		return data;
	}

	// /////////////////// Instance Fields and Methods ////////////////

	private Hashtable<Integer, ProductGrid> productGridMap;

	private Hashtable<Integer, List<ProductGrid>> gridTemplateMap;

	private GridManager() {
		productGridMap = new Hashtable<Integer, ProductGrid>();
		gridTemplateMap = new Hashtable<Integer, List<ProductGrid>>();
	}

	public ProductGrid getProductGrid(int gridID) {
		ProductGrid productgrid = null;
		productgrid = productGridMap.get(new Integer(gridID));
		return productgrid;
	}

	/**
	 * Tests if the specified template has any guidelines.
	 * 
	 * @param templateID
	 * @return <code>true</code> if template with the specified id has at least one guideline;
	 *         <code>false</code>, otherwise
	 * @since PowerEditor 4.3.7
	 */
	public boolean hasGrids(int templateID) {
		if (templateID <= 0) return false;
		Integer key = new Integer(templateID);
		if (gridTemplateMap.containsKey(key)) {
			return !gridTemplateMap.get(key).isEmpty();
		}
		else {
			return false;
		}
	}

	/**
	 * Tests if the specified grid id exists and it's associated with the specified template id.
	 * @param gridID
	 * @param templateID
	 * @return
	 */
	public boolean hasGrid(int gridID, int templateID) {
		ProductGrid grid = getProductGrid(gridID);
		return (grid != null && grid.getTemplateID() == templateID);
	}

	/**
	 * Tests if the specifie date synonym is used by at least one guideline grid.
	 * @param dateSynonym
	 * @return <code>true</code> if <code>dateSynonym</code> is used by at least one guideline grid; 
	 *         <code>false</code>, otherwise
	 * @throws NullPointerException if <code>dateSynonym</code> is <code>null</code>
	 */
	public boolean isInUse(DateSynonym dateSynonym) {
		if (dateSynonym == null) throw new NullPointerException("dateSynonym cannot be null");
		for (Iterator<ProductGrid> iter = productGridMap.values().iterator(); iter.hasNext();) {
			ProductGrid productgrid = iter.next();
			if (dateSynonym.equals(productgrid.getEffectiveDate()) || dateSynonym.equals(productgrid.getExpirationDate())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isSubContext(int templateID, GuidelineContext[] contexts) throws ServletActionException {
		logger.debug(">>> isSubContext: " + templateID + "," + UtilBase.toString(contexts));
		List<ProductGrid> allGridList = getAllGridsForTemplate(templateID);
		if (allGridList == null || allGridList.isEmpty()) return false;

		for (Iterator<ProductGrid> iter = allGridList.iterator(); iter.hasNext();) {
			ProductGrid element = iter.next();
			if (isContainedIn(contexts, element)) {
				return true;
			}
		}
		return false;
	}

	public List<GuidelineContext> getFullContext(int templateID, GuidelineContext[] subContexts) throws ServletActionException {
		logger.debug(">>> getFullContext: " + templateID + "," + UtilBase.toString(subContexts));
		List<ProductGrid> allGridList = getAllGridsForTemplate(templateID);
		if (allGridList != null) {
			for (Iterator<ProductGrid> iter = allGridList.iterator(); iter.hasNext();) {
				ProductGrid element = iter.next();
				if (isContainedIn(subContexts, element)) {
					GuidelineContext[] fullContext = element.extractGuidelineContext();
					return Arrays.asList(fullContext);
				}
			}
		}
		return new ArrayList<GuidelineContext>();
	}

	public boolean hasApplicableGrids(int templateID, GuidelineContext[] contexts) {
		logger.debug(">>> hasGrids: " + templateID + ", " + contexts);
		List<ProductGrid> list = getApplicableGuidelineGrids(templateID, contexts); //getGuidelineGrids_internal(templateID, contexts);
		return (list != null && list.size() > 0);
	}

	private List<ProductGrid> getApplicableGuidelineGrids(int templateID, GuidelineContext[] context) {
		logger.debug(">>> getGuidelineGrids_internal for " + templateID + "," + context);
		LinkedList<ProductGrid> linkedlist = new LinkedList<ProductGrid>();
		List<ProductGrid> list = getAllGridsForTemplate(templateID);
		if (list == null || list.isEmpty()) {
			return linkedlist;
		}

		for (Iterator<ProductGrid> iterator = list.iterator(); iterator.hasNext();) {
			ProductGrid abstractgrid = iterator.next();
			if (abstractgrid instanceof ProductGrid) {
				logger.debug("    getGuidelineGrids_internal: checking " + abstractgrid);
				ProductGrid productgrid = (ProductGrid) abstractgrid;
				if (ServerContextUtil.hasApplicableContext(productgrid, context)) {
					logger.debug("    adding " + productgrid);
					linkedlist.add(productgrid);
				}
			}
		}
		logger.debug("<<< getGuidelineGrids_internal with " + linkedlist.size() + " grids (activations)");
		return linkedlist;
	}

	public boolean isSame(int templateID, GuidelineContext[] context) {
		logger.debug(">>> isSame: " + templateID + "," + UtilBase.toString(context));
		return true;
	}

	public List<ProductGrid> getProductGrids(GenericEntityType type, int entityID) {
		LinkedList<ProductGrid> linkedlist = new LinkedList<ProductGrid>();
		for (Iterator<ProductGrid> iter = productGridMap.values().iterator(); iter.hasNext();) {
			ProductGrid productgrid = iter.next();
			if (UtilBase.isMember(entityID, productgrid.getGenericEntityIDs(type))) {
				linkedlist.add(productgrid);
			}
		}
		return linkedlist;
	}

	/**
	 * Get list of guidelines that apply to the specified context.
	 */
	public List<ProductGrid> getProductGrids(int templateID, GuidelineContext[] context) {
		return getApplicableGuidelineGrids(templateID, context);
	}

	public List<ProductGrid> getProductGrids(int templateID, GuidelineContext context) {
		return getProductGrids(templateID, new GuidelineContext[] { context });
	}

	/**
	 * Removes all guidelines for the specified template from the cache.
	 * 
	 * @param templateID
	 * @since PowerEditor 4.3.7
	 */
	public synchronized void removeGuidelinesForTemplate(int templateID) {
		ProductGrid[] gridsToRemove = getAllGridsForTemplate(templateID).toArray(new ProductGrid[0]);
		for (int i = 0; i < gridsToRemove.length; i++) {
			removeFromProductGridMap(gridsToRemove[i]);
		}
		gridTemplateMap.remove(new Integer(templateID));
	}

	/**
	 * Removes the specified generic category from all context.
	 * 
	 * @param categoryType
	 *            category type
	 * @param categoryID
	 *            category id
	 */
	public void removeCategoryFromAllContext(int categoryType, int categoryID) {
		GenericEntityType type = ConfigurationManager.getInstance().getEntityConfiguration().findEntityTypeForCategoryType(categoryType);
		if (type != null) {
			for (Iterator<ProductGrid> iter = this.productGridMap.values().iterator(); iter.hasNext();) {
				ProductGrid grid = iter.next();
				grid.removeGenericCategoryID(type, categoryID);
			}
		}
	}

	/**
	 * Gets grid to category ids map for deleting the specified product category.
	 * 
	 * @param type
	 *            entity type
	 * @param categoryID
	 *            category id for delete
	 * @return map: Integer to int array; value will be <code>null</code> if the grid no longer
	 *         has category ids
	 */
	public Map<Integer, int[]> getGridCatgoryMapForGenericCategoryDelete(GenericEntityType type, int categoryID) {
		Map<Integer, int[]> map = new HashMap<Integer, int[]>();
		for (Iterator<ProductGrid> iter = this.productGridMap.values().iterator(); iter.hasNext();) {
			ProductGrid grid = iter.next();
			if (grid.hasGenericCategoryContext(type) && UtilBase.isMember(categoryID, grid.getGenericCategoryIDs(type))) {
				List<Integer> list = UtilBase.toIntegerList(grid.getGenericCategoryIDs(type));
				list.remove(new Integer(categoryID));
				map.put(grid.getID(), (list.isEmpty() ? null : UtilBase.toIntArray(list)));
			}
		}
		return map;
	}

	/**
	 * Removes the specified product category from all context.
	 * 
	 * @param categoryID
	 *            category id
	 */
	public void removeGenericCategoryFromAllContext(GenericEntityType type, int categoryID) {
		for (Iterator<ProductGrid> iter = this.productGridMap.values().iterator(); iter.hasNext();) {
			ProductGrid grid = iter.next();
			if (grid.hasGenericCategoryContext(type) && UtilBase.isMember(categoryID, grid.getGenericCategoryIDs(type))) {
				grid.removeGenericCategoryID(type, categoryID);
			}
		}
	}

	/**
	 * Removes the specified generic entity from all context.
	 * 
	 * @param categoryID
	 *            category id
	 */
	public void removeGenericEntityFromAllContext(GenericEntityType type, int entityID) {
		for (Iterator<ProductGrid> iter = this.productGridMap.values().iterator(); iter.hasNext();) {
			ProductGrid grid = iter.next();
			if (grid.hasGenericEntityContext(type) && UtilBase.isMember(entityID, grid.getGenericEntityIDs(type))) {
				grid.removeGenericEntityID(type, entityID);
			}
		}
	}

	public synchronized boolean removeFromCache(ProductGrid grid) {
		return removeFromProductGridMap(grid);
	}

	public synchronized void startLoading() {
		productGridMap.clear();
		gridTemplateMap.clear();
	}

	/**
	 * @return the list of grids for the template
	 */
	public List<ProductGrid> getAllGridsForTemplate(int templateID) {
		Integer key = new Integer(templateID);
		if (gridTemplateMap.containsKey(key)) {
			return gridTemplateMap.get(key);
		}
		else {
			List<ProductGrid> list = new ArrayList<ProductGrid>();
			gridTemplateMap.put(key, list);
			return list;
		}
	}

	/**
	 * Returns a list of {@link GuidelineReportData} for the specified template. For generating
	 * reports.
	 * 
	 * @param templateID
	 * @param username
	 * @return a list of GuidelineReportData objects
	 * @throws ServerException 
	 */
	public List<GuidelineReportData> getAllGuidelineReportDataForTemplate(int templateID, String username) throws ServerException {
		List<ProductGrid> gridList = getAllGridsForTemplate(templateID);
		return asGuidelineReportDataList(gridList, username);
	}

	private List<GuidelineReportData> asGuidelineReportDataList(List<ProductGrid> gridList, String username) throws ServerException {
		logger.debug(">>> asGuidelineReportDataList: " + gridList);
		List<GuidelineReportData> resultList = new ArrayList<GuidelineReportData>();
		if (gridList == null || gridList.size() == 0) return resultList;

		ProductGrid[] grids = gridList.toArray(new ProductGrid[0]);
		logger.debug("... asGuidelineReportDataList: grids count = " + grids.length);
		for (int i = 0; i < grids.length; i++) {
			GuidelineReportData gridReportData = toGuidelineReportData(grids[i], username);
			resultList.add(gridReportData);
		}
		logger.debug("<<< asGuidelineReportDataList: size = " + resultList.size());
		return resultList;
	}

	public List<GuidelineReportData> getAllGuidelineReportData(String username) throws ServerException {
		List<GuidelineReportData> resultList = new ArrayList<GuidelineReportData>();
		for (Iterator<Integer> iter = gridTemplateMap.keySet().iterator(); iter.hasNext();) {
			Integer key = iter.next();
			// consolidate grids for the same template
			List<ProductGrid> gridList = gridTemplateMap.get(key);
			resultList.addAll(asGuidelineReportDataList(gridList, username));
		}
		return resultList;
	}

	public List<GuidelineReportData> searchGuidelinesWithCategoryCheck(GuidelineReportFilter filter, String username) throws ServerException {
		logger.debug(">>> searchGuidelinesWithCategoryCheck: " + filter + ", user = " + username);

		// get guidelines for specified class and attribute names
		List<GuidelineReportData> result = getGuidelineReportData(filter.getClassName(), filter.getAttributeName(), filter.getValue(), username);

		// remove from result filtered out guidelines for category selection
		GenericEntityType[] types = GenericEntityType.getAllGenericEntityTypes();
		for (int i = 0; i < types.length; i++) {
			int[] entityIDs = filter.getIDsForEntityType(types[i]);
			if (entityIDs != null && entityIDs.length > 0) {
				for (Iterator<GuidelineReportData> iter = result.iterator(); iter.hasNext();) {
					GuidelineReportData grid = iter.next();
					int[] catIDs = grid.getIDsForCategoryType(types[i].getCategoryType());
					if (catIDs != null && catIDs.length > 0) {
						GenericEntityIdentity[] entities = EntityManager.getInstance().getGenericEntitiesInCategorySetAtAnyTime(
								types[i].getCategoryType(),
								catIDs,
								true); // true to indicate that we don't care about dates on entity-category associations
						if (!Util.isContainedIn(entityIDs, GenericEntityIdentity.toIDArray(entities))) {
							iter.remove();
						}
					}
				}
			}
		}
		return result;
	}

	private List<GuidelineReportData> getGuidelineReportData(String className, String attrName, String value, String username) throws ServerException {
		if (className == null || attrName == null) {
			return getAllGuidelineReportData(username);
		}

		List<GuidelineReportData> resultList = new ArrayList<GuidelineReportData>();
		List<GridTemplate> templateList = GuidelineTemplateManager.getInstance().getTemplates(className, attrName);
		for (Iterator<GridTemplate> iter = templateList.iterator(); iter.hasNext();) {
			GridTemplate element = iter.next();
			int col = GuidelineTemplateManager.getInstance().findColumnForAttribReference(element, className, attrName);

			if (col > 0) {
				List<ProductGrid> gridList = gridTemplateMap.get(new Integer(element.getID()));
				if (gridList != null) {
					List<ProductGrid> tempList = new ArrayList<ProductGrid>();
					for (Iterator<ProductGrid> iterator = gridList.iterator(); iterator.hasNext();) {
						ProductGrid grid = iterator.next();
						// add if grid contains a value in the right column
						boolean gridAdded = false;
						for (int row = 1; row <= grid.getNumRows() && !gridAdded; row++) {
							try {
								Object cellValue = grid.getCellValueObject(row, col, null);
								logger.debug("getConsolidatedGuidelineReportData: cell-value=" + cellValue);
								if ((value == null || value.trim().length() == 0) && cellValue != null) {
									tempList.add(grid);
									gridAdded = true;
								}
								else if (value != null && value.trim().length() > 0 && cellValue != null) {
									tempList.add(grid);
									gridAdded = true;
								}
							}
							catch (InvalidDataException ex) {
								logger.warn("getConsolidatedGuidelineReportData: ignored " + grid, ex);
							}
						}
					}
					resultList.addAll(asGuidelineReportDataList(tempList, username));
				}
			}
		}
		return resultList;
	}

	private boolean hasGridWithSameContext(int gridID, int templateID, ProductGrid target) {
		List<ProductGrid> list = gridTemplateMap.get(templateID);
		if (list != null && !list.isEmpty()) {
			for (Iterator<ProductGrid> iter = list.iterator(); iter.hasNext();) {
				ProductGrid grid = iter.next();
				if (grid.getID() != gridID && Util.isSame(grid.getEffectiveDate(), target.getEffectiveDate())
						&& Util.isSame(grid.getExpirationDate(), target.getExpirationDate())) {
					return grid.hasSameContext(target);
				}
			}
		}
		return false;
	}

	public void finishLoading() {
		// check duplicate context grids - Check for complete context, including all generic entity
		// context elements
		boolean checkDuplicate = false;
		if (checkDuplicate) {
			long startTime = System.currentTimeMillis();
			logger.info("Checking for duplicate grids. This may take a while...");
			for (Iterator<Integer> iter = gridTemplateMap.keySet().iterator(); iter.hasNext();) {
				Integer templateIDKey = iter.next();
				List<ProductGrid> gridList = gridTemplateMap.get(templateIDKey);
				if (gridList != null && !gridList.isEmpty()) {
					for (Iterator<ProductGrid> gi = gridList.iterator(); gi.hasNext();) {
						ProductGrid grid = gi.next();
						if (hasGridWithSameContext(grid.getID(), templateIDKey.intValue(), grid)) {
							logger.warn("GridManager: *** WARNING: guideline grid with duplicate (possible) context elements found: " + grid);
						}
					}
				}
			}
			logger.info("Duplicate check complete: elasped tiem = " + (System.currentTimeMillis() - startTime) + " (ms)");
		}
		else {
			logger.info("Duplicate check skipped");
		}
	}

	public synchronized boolean updateCache(AbstractGuidelineGrid cachedGrid, AbstractGuidelineGrid abstractgrid1) {
		logger.debug(">>> updateCache: " + cachedGrid + "," + abstractgrid1);
		logger.debug("    updateCache: cached grid's template = " + cachedGrid.getTemplate());
		logger.debug("    updateCache: source grid's template = " + abstractgrid1.getTemplate());

		if (cachedGrid.getTemplateID() != abstractgrid1.getTemplateID() && abstractgrid1.getTemplateID() > 0) {
			cachedGrid.setTemplate(GuidelineTemplateManager.getInstance().getTemplate(abstractgrid1.getTemplateID()));
		}

		DateSynonym cachedEffDate = (abstractgrid1.getEffectiveDate() == null ? null : DateSynonymManager.getInstance().getDateSynonym(
				abstractgrid1.getEffectiveDate().getID()));
		DateSynonym cachedExpDate = (abstractgrid1.getExpirationDate() == null ? null : DateSynonymManager.getInstance().getDateSynonym(
				abstractgrid1.getExpirationDate().getID()));

		cachedGrid.copyCellValue(abstractgrid1);
		cachedGrid.setComments(abstractgrid1.getComments());
		cachedGrid.setNumRows(abstractgrid1.getNumRows());
		cachedGrid.setStatus(abstractgrid1.getStatus());
		cachedGrid.setStatusChangeDate(abstractgrid1.getStatusChangeDate());
		cachedGrid.setEffectiveDate(cachedEffDate);
		cachedGrid.setExpirationDate(cachedExpDate);
		cachedGrid.setCloneOf(abstractgrid1.getCloneOf());
		return true;
	}

	public synchronized boolean updateExpirationDate(AbstractGuidelineGrid grid, DateSynonym expDate) {
		ProductGrid cachedGrid = getProductGrid(grid.getID());
		if (cachedGrid != null) {
			cachedGrid.setExpirationDate(expDate);
		}
		return true;
	}

	public synchronized boolean updateTemplate(AbstractGuidelineGrid grid, GridTemplate template) {
		logger.debug("--> updateTemplate: " + grid + " to " + template);
		ProductGrid cachedGrid = getProductGrid(grid.getID());
		if (cachedGrid != null) {
			// remove grid from old template cache
			removeFromGridTemplateMap(cachedGrid);

			cachedGrid.setTemplate(template);
			addToGridTemplateMap(cachedGrid);

			logger.debug("<-- updateTemplate");
		}
		return true;
	}

	private synchronized boolean removeFromGridTemplateMap(ProductGrid abstractgrid) {
		int templateID = abstractgrid.getTemplateID();
		List<ProductGrid> list = gridTemplateMap.get(new Integer(templateID));
		boolean flag = true;
		if (list == null) {
			flag = false;
		}
		else {
			int size = list.size();
			int indexToRemove = -1;
			for (int i = 0; i < size; i++) {
				ProductGrid grid = list.get(i);
				if (grid.getID() == abstractgrid.getID() && grid.equals(abstractgrid)) {
					indexToRemove = i;
					break;
				}
			}

			if (indexToRemove > -1) {
				flag = list.remove(indexToRemove) != null;
			}
			else {
				flag = false;
			}
		}
		if (!flag) {
			logger.warn("No grid of id " + abstractgrid.getID() + " found for template " + templateID + "; nothing was removed.");
		}
		return flag;
	}

	private boolean removeFromProductGridMap(ProductGrid abstractgrid) {
		logger.debug(">>> removeFromProductGridMap with " + abstractgrid);
		Object object = productGridMap.remove(new Integer(abstractgrid.getID()));
		if (object == null) {
			logger.warn("No product-grid found for " + abstractgrid.getID() + "; nothing was removed");
		}
		boolean result = removeFromGridTemplateMap(abstractgrid);
		return object != null && result;
	}

	private void addToGridTemplateMap(ProductGrid abstractgrid) {
		Integer templateKey = new Integer(abstractgrid.getTemplateID());
		List<ProductGrid> list = gridTemplateMap.get(templateKey);
		if (list == null) {
			list = new LinkedList<ProductGrid>();
			list.add(abstractgrid);
			gridTemplateMap.put(templateKey, list);
		}
		else {
			list.add(abstractgrid);
		}
	}

	private void addToProductGridMap(ProductGrid abstractgrid) {
		logger.debug(">>> addToProductGridMap: " + abstractgrid);
		productGridMap.put(new Integer(abstractgrid.getID()), abstractgrid);
		addToGridTemplateMap(abstractgrid);
	}

	/**
	 * Gets the id of the grid that matches the specified criteria exactly.
	 */
	public int getGridID(int templateID, GuidelineContext[] context, DateSynonym effDate, DateSynonym expDate) {
		List<ProductGrid> list = getAllGridsForTemplate(templateID);
		if (list == null || list.isEmpty()) {
			return -1;
		}
		for (Iterator<ProductGrid> iter = list.iterator(); iter.hasNext();) {
			ProductGrid element = iter.next();
			if (ServerContextUtil.hasSameContext(element, context) && Util.isSame(element.getEffectiveDate(), effDate)
					&& Util.isSame(element.getExpirationDate(), expDate)) {
				return element.getID();
			}
		}
		return -1;
	}

	/**
	 * Adds the specified product grid into the cache. If templateID is not valid (e.g., not found
	 * in the loaded template cache), this does not add the grid into the cache (no-op).
	 */
	public synchronized void addProductGrid(int gridID, int templateID, String comments, GridValueContainable gridValues, String status,
			Date statusChanged, DateSynonym effDate, DateSynonym expDate, int numRows, int cloneOf, Date created) {
		GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(templateID);
		if (template == null) {
			logger.warn("* template ID " + templateID + " not loaded; grid " + gridID + " not loaded!!!");
		}
		else {
			ProductGrid productgrid = new ProductGrid(gridID, template, effDate, expDate);
			productgrid.setComments(comments);
			if (gridValues != null) {
				productgrid.copyCellValue(gridValues);
			}
			productgrid.setStatus(status);
			productgrid.setStatusChangeDate(statusChanged);
			productgrid.setCloneOf(cloneOf);
			productgrid.setCreationDate(created);
			productgrid.setNumRows(numRows);
			addToProductGridMap(productgrid);
		}
	}

	public synchronized void addProductGrid(ProductGrid productgrid) {
		addToProductGridMap(productgrid);
	}

	/**
	 * Update context of the specified grid.
	 * 
	 * @since PowerEditor 4.2.0
	 */
	public void updateGridContext(int gridID, GuidelineContext[] context) {
		Integer key = new Integer(gridID);
		if (productGridMap.containsKey(key)) {
			ProductGrid grid = productGridMap.get(key);
			ServerContextUtil.setContext(grid, context);
		}
	}

	public boolean addGridContext(int gridID, int categoryType, int categoryID) {
		Integer key = new Integer(gridID);
		if (productGridMap.containsKey(key)) {
			ProductGrid grid = productGridMap.get(key);
			grid.addGenericCategoryID(
					ConfigurationManager.getInstance().getEntityConfiguration().findEntityTypeForCategoryType(categoryType),
					categoryID);
			
			return true;
		}
		else {
			return false;
		}
	}

	public void setContext(int gridID, GuidelineContext[] context) {
		ServerContextUtil.setContext(getProductGrid(gridID), context);
	}

	public void setContext(int gridID, ProductGrid grid) {
		getProductGrid(gridID).copyEntireContext(grid);
	}

	public boolean addGridContext(int gridID, GenericEntityType type, int entityID) {
		Integer key = new Integer(gridID);
		if (productGridMap.containsKey(key)) {
			ProductGrid grid = productGridMap.get(key);
			grid.addGenericEntityID(type, entityID);
			
			return true;
		}
		else {
			return false;
		}
	}

	public void addGridContext(int gridID, GenericEntityIdentity geIdentity) {
		if (geIdentity != null) {
			addGridContext(gridID, GenericEntityType.forID(geIdentity.getEntityType()), geIdentity.getEntityID());
		}
	}

	public void addGridContext(int gridID, GenericCategoryIdentity identity) {
		if (identity != null) {
			addGridContext(gridID, identity.getCategoryType(), identity.getCategoryID());
		}
	}
	
	/**
	 * If <code>gridCellDetails</code> is <code>null</code>, this is a noop.
	 * @param gridCellDetails grid cell details
	 * @throws NullPointerException if <code>gridCellDetails</code> contains a <code>null</code>
	 */
	public synchronized void replaceCellValues(GridCellDetail[] gridCellDetails) {
		if (gridCellDetails != null) {
			for (int i = 0; i < gridCellDetails.length; i++) {
				replaceCellValue(gridCellDetails[i]);
			}
		}
	}

	private void replaceCellValue(GridCellDetail detail) {
		ProductGrid grid = getProductGrid(detail.getGridID());
		if (grid == null) throw new IllegalArgumentException("Invalid grid id in " + detail + "; no grid of id " + detail.getGridID() + " exists");
		grid.setValue(detail.getRowID(), detail.getColumnName(), detail.getCellValue());
	}
}