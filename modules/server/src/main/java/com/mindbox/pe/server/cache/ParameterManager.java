package com.mindbox.pe.server.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.template.ParameterTemplate;
import com.mindbox.pe.server.ServerContextUtil;
import com.mindbox.pe.server.config.ConfigurationManager;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.2.0
 */
public final class ParameterManager extends AbstractCacheManager {

	private static ParameterManager instance = null;

	public static synchronized ParameterManager getInstance() {
		if (instance == null) {
			instance = new ParameterManager();
		}
		return instance;
	}

	/**
	 * @param template template
	 * @param gridList parameter grid list; length must be >= 1
	 * @param String username. Not sure if it's used.
	 * @return the list of guideline report data objects
	 */
	private static List<GuidelineReportData> toGuidelineReportData(ParameterTemplate template, List<ParameterGrid> gridList) {
		List<GuidelineReportData> retList = new ArrayList<GuidelineReportData>();
		int templateID = template.getID();
		for (Iterator<ParameterGrid> iter = gridList.iterator(); iter.hasNext();) {
			ParameterGrid grid = iter.next();
			GuidelineReportData data = new GuidelineReportData(templateID, template.getName(), grid, true);
			data.setStatus(grid.getStatus());
			retList.add(data);
		}
		return retList;
	}

	private final Map<Integer, ParameterGrid> gridMap;

	private ParameterManager() {
		super();
		this.gridMap = Collections.synchronizedMap(new HashMap<Integer, ParameterGrid>());
	}

	public boolean addGridContext(int gridID, GenericEntityType type, int entityID) {
		Integer key = new Integer(gridID);
		if (gridMap.containsKey(key)) {
			ParameterGrid grid = gridMap.get(key);
			grid.addGenericEntityID(type, entityID);

			return true;
		}
		else {
			return false;
		}
	}

	public boolean addGridContext(int gridID, int categoryType, int categoryID) {
		Integer key = new Integer(gridID);
		if (gridMap.containsKey(key)) {
			ParameterGrid grid = gridMap.get(key);
			grid.addGenericCategoryID(ConfigurationManager.getInstance().getEntityConfigHelper().findEntityTypeForCategoryType(categoryType), categoryID);
			return true;
		}
		else {
			return false;
		}
	}

	public void addParameterGrid(int gridID, int templateID, String cellValue, int numRows, DateSynonym effDate, DateSynonym expDate, String status) {

		ParameterGrid grid = new ParameterGrid(gridID, templateID, effDate, expDate);
		grid.setCellValues(cellValue);
		grid.setNumRows(numRows);
		grid.setStatus(status);
		grid.setTemplate(ParameterTemplateManager.getInstance().getTemplate(templateID));

		gridMap.put(new Integer(gridID), grid);
	}

	public void finishLoading() {
	}

	public ParameterGrid getGrid(int gridID) {
		return gridMap.get(new Integer(gridID));
	}

	public List<ParameterGrid> getGrids(GenericEntityType entityType, int entityID) {
		LinkedList<ParameterGrid> linkedlist = new LinkedList<ParameterGrid>();
		for (Iterator<ParameterGrid> iter = gridMap.values().iterator(); iter.hasNext();) {
			ParameterGrid element = iter.next();
			if (UtilBase.isMember(entityID, element.getGenericEntityIDs(entityType))) {
				linkedlist.add(element);
			}
		}
		return linkedlist;
	}

	public List<ParameterGrid> getGrids(int templateID) {
		List<ParameterGrid> list = new ArrayList<ParameterGrid>();
		for (Iterator<ParameterGrid> iter = gridMap.values().iterator(); iter.hasNext();) {
			ParameterGrid element = iter.next();
			if (element.getTemplateID() == templateID) {
				list.add(element);
			}
		}
		return list;
	}

	public void insertIntoCache(int gridID, int templateID, String cellValue, int numRows, DateSynonym effDate, DateSynonym expDate, String status, GuidelineContext[] context) {
		ParameterGrid grid = new ParameterGrid(gridID, templateID, effDate, expDate);
		grid.setCellValues(cellValue);
		grid.setNumRows(numRows);
		grid.setStatus(status);
		grid.setTemplate(ParameterTemplateManager.getInstance().getTemplate(templateID));
		ServerContextUtil.setContext(grid, context);
		gridMap.put(new Integer(gridID), grid);
	}

	/**
	 * Tests if the specifie date synonym is used by at least one parameter grid.
	 * @param dateSynonym dateSynonym
	 * @return <code>true</code> if <code>dateSynonym</code> is used by at least one parameter grid; 
	 *         <code>false</code>, otherwise
	 * @throws NullPointerException if <code>dateSynonym</code> is <code>null</code>
	 */
	public boolean isInUse(DateSynonym dateSynonym) {
		if (dateSynonym == null) throw new NullPointerException("dateSynonym cannot be null");
		for (Iterator<ParameterGrid> iter = gridMap.values().iterator(); iter.hasNext();) {
			ParameterGrid grid = iter.next();
			if (dateSynonym.equals(grid.getEffectiveDate()) || dateSynonym.equals(grid.getExpirationDate())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes the specified generic category from all context.
	 * 
	 * @param categoryType categoryType
	 * @param categoryID categoryID
	 */
	public void removeCategoryFromAllContext(int categoryType, int categoryID) {
		GenericEntityType type = ConfigurationManager.getInstance().getEntityConfigHelper().findEntityTypeForCategoryType(categoryType);
		if (type != null) {
			for (Iterator<ParameterGrid> iter = gridMap.values().iterator(); iter.hasNext();) {
				ParameterGrid grid = iter.next();
				grid.removeGenericCategoryID(type, categoryID);
			}
		}
	}

	public void removeEntityFromAllContext(GenericEntityType type, int entityID) {
		for (Iterator<ParameterGrid> iter = gridMap.values().iterator(); iter.hasNext();) {
			ParameterGrid grid = iter.next();
			if (grid.hasGenericEntityContext(type) && UtilBase.isMember(entityID, grid.getGenericEntityIDs(type))) {
				grid.removeGenericEntityID(type, entityID);
			}
		}
	}

	public void removeGridFromCache(int gridID) {
		gridMap.remove(new Integer(gridID));
	}

	public List<GuidelineReportData> searchParameters(List<ParameterTemplate> templateList) {
		List<GuidelineReportData> retList = new ArrayList<GuidelineReportData>();
		for (Iterator<ParameterTemplate> iter = templateList.iterator(); iter.hasNext();) {
			ParameterTemplate template = iter.next();
			retList.addAll(toGuidelineReportData(template, getGrids(template.getID())));
		}

		return retList;
	}

	public void startLoading() {
		this.gridMap.clear();
	}

	public void updateCache(ParameterGrid grid) {
		Integer key = new Integer(grid.getID());
		if (gridMap.containsKey(key)) {
			ParameterGrid cachedGrid = gridMap.get(key);
			cachedGrid.setCellValues(grid.getCellValues());
			cachedGrid.setNumRows(grid.getNumRows());
			cachedGrid.setStatus(grid.getStatus());
			cachedGrid.setEffectiveDate(grid.getEffectiveDate());
			cachedGrid.setExpirationDate(grid.getExpirationDate());
			cachedGrid.copyEntireContext(grid);
		}
		else {
			gridMap.put(key, grid);
		}
	}

	/**
	 * Update context of the specified grid.
	 * @param gridID gridID
	 * @param context context
	 * @since PowerEditor 4.2.0
	 */
	public void updateGridContext(int gridID, GuidelineContext[] context) {
		Integer key = new Integer(gridID);
		if (gridMap.containsKey(key)) {
			ParameterGrid grid = gridMap.get(key);
			ServerContextUtil.setContext(grid, context);
		}
	}
}