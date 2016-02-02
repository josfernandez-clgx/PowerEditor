package com.mindbox.pe.server.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.AbstractTemplateColumn;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.exceptions.SapphireException;
import com.mindbox.pe.model.filter.TemplateFilter;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.server.Util;
import com.mindbox.pe.server.config.ConfigurationManager;

public final class GuidelineTemplateManager extends AbstractCacheManager {

	private static GuidelineTemplateManager mSingleton = null;

	public static synchronized GuidelineTemplateManager getInstance() {
		if (mSingleton == null) mSingleton = new GuidelineTemplateManager();
		return mSingleton;
	}

	private Map<Integer, GridTemplate> templateMap;
	private final Map<String, AttributeReferenceFinder> attrFinderMap;

	private GuidelineTemplateManager() {
		templateMap = new HashMap<Integer, GridTemplate>();
		attrFinderMap = new HashMap<String, AttributeReferenceFinder>();
	}

	/**
	 * Gets the privilege string for the specified template.
	 * 
	 * @param templateID
	 * @param flag
	 * @return the privilege string for <code>templateID</code>, if found; <code>null</code>, otherwise
	 */
	public String getTemplatePermission(int templateID, boolean viewOnly) {
		String s = null;
		GridTemplate gridtemplate = getTemplate(templateID);
		if (gridtemplate != null) {
			s = UtilBase.getRequiredPermission(gridtemplate.getUsageType(), viewOnly);
		}
		return s;
	}

	private boolean hasColumnFor(GridTemplate template, String className, String attrName) {
		return getColumnNumFor(template, className, attrName) > 0;
	}

	public AttributeReferenceFinder getAttributeReferenceFinder(int templateID, RuleDefinition ruleDef) {//DeploymentRule deploymentRule) {
		String key = String.valueOf(templateID);
		if (attrFinderMap.containsKey(key)) {
			return attrFinderMap.get(key);
		}
		else {
			AttributeReferenceFinder finder = new AttributeReferenceFinder(ruleDef);
			attrFinderMap.put(key, finder);
			return finder;
		}
	}

	public AttributeReferenceFinder getAttributeReferenceFinder(int templateID, int columnID, RuleDefinition ruleDef) { //DeploymentRule deploymentRule) {
		String key = String.valueOf(templateID) + "." + String.valueOf(columnID);
		if (attrFinderMap.containsKey(key)) {
			return attrFinderMap.get(key);
		}
		else {
			AttributeReferenceFinder finder = new AttributeReferenceFinder(ruleDef);
			attrFinderMap.put(key, finder);
			return finder;
		}
	}

	private int getColumnNumFor(GridTemplate template, String className, String attrName) {
		logger.debug(">>> getColumnNumFor: " + template + "," + className + "," + attrName);

		// 1. check attributeMap first
		for (int i = 1; i <= template.getNumColumns(); i++) {
			AbstractTemplateColumn column = template.getColumn(i);
			if (column.getMAClassName() != null && column.getMAClassName().equalsIgnoreCase(className)
					&& column.getMAAttributeName() != null && column.getMAAttributeName().equalsIgnoreCase(attrName)) {
				logger.debug("<<< getColumnNumFor(on-attrMap): " + i);
				return i;
			}
		}

		// use RuleDefinition, not deploy info
		if (template.getRuleDefinition() != null) {
			AttributeReferenceFinder finder = getAttributeReferenceFinder(template.getID(), template.getRuleDefinition());
			int col = finder.getColumnNoForAttributeReference(className, attrName);
			logger.debug("<<< getColumnNumFor(temp-deploy-info): " + col);
			return col;
		}
		else {
			for (GridTemplateColumn element : template.getColumns()) {
				if (element.getRuleDefinition() != null) {
					AttributeReferenceFinder finder = getAttributeReferenceFinder(
							template.getID(),
							element.getColumnNumber(),
							element.getRuleDefinition());
					int col = finder.getColumnNoForAttributeReference(className, attrName);
					if (col == 0) {
						logger.debug("<<< getColumnNumFor(col-deploy-info-cellValue): " + element.getColumnNumber());
						return element.getColumnNumber();
					}
					else if (col > 0) {
						logger.debug("<<< getColumnNumFor(col-deploy-info): " + col);
						return col;
					}
				}
			}
		}

		logger.debug("<<< getColumnNumFor: -1");
		return -1;
	}

	public List<GridTemplate> getAllTemplates() {
		LinkedList<GridTemplate> linkedlist = new LinkedList<GridTemplate>();
		GridTemplate gridtemplate;
		for (Iterator<GridTemplate> iter = templateMap.values().iterator(); iter.hasNext();) {
			gridtemplate = iter.next();
			linkedlist.add(gridtemplate);
		}
		return linkedlist;
	}

	public int getTemplateCount() {
		return templateMap.size();
	}

	public boolean removeFromCache(int i) {
		return templateMap.remove(new Integer(i)) != null;
	}

	public List<GridTemplate> searchTemplates(TemplateFilter templateFilter) {
		return getTemplates(templateFilter.getClassName(), templateFilter.getAttributeName());
	}

	public List<GridTemplate> getTemplates(String className, String attributeName) {
		logger.debug(">>> getTemplates: " + className + "," + attributeName);
		if (className != null && className.length() > 0 && attributeName != null && attributeName.length() > 0) {
			List<GridTemplate> list = new ArrayList<GridTemplate>();
			for (Iterator<GridTemplate> iter = templateMap.values().iterator(); iter.hasNext();) {
				GridTemplate element = iter.next();
				if (hasColumnFor(element, className, attributeName)) {
					list.add(element);
				}
			}
			return list;
		}
		else {
			return getAllTemplates();
		}
	}

	public int findColumnForAttribReference(GridTemplate template, String className, String attributeName) {
		if (template == null) {
			return -1;
		}
		else {
			return getColumnNumFor(template, className, attributeName);
		}
	}

	/**
	 * 
	 * @param usages
	 * @return list of templates for <code>usages</code>
	 * @since PowerEditor 4.3.6
	 */
	public List<GridTemplate> getTemplates(TemplateUsageType[] usages) {
		List<GridTemplate> list = new ArrayList<GridTemplate>();
		for (int i = 0; i < usages.length; i++) {
			list.addAll(getTemplates(usages[i]));
		}
		return list;
	}

	/**
	 * 
	 * @param usages
	 * @return list of templates for <code>usages</code>
	 * @since PowerEditor 4.3.6
	 */
	public List<GridTemplate> getTemplates(List<TemplateUsageType> usages) {
		List<GridTemplate> list = new ArrayList<GridTemplate>();
		for (TemplateUsageType usage : usages) {
			list.addAll(getTemplates(usage));
		}
		return list;
	}

	/**
	 * 
	 * @param templateIDs
	 * @return list of templates
	 * @since PowerEditor 4.3.6
	 */
	public List<GridTemplate> getTemplates(int[] templateIDs) {
		if (Util.isEmpty(templateIDs)) return getAllTemplates();
		List<GridTemplate> list = new ArrayList<GridTemplate>();
		for (int i = 0; i < templateIDs.length; i++) {
			GridTemplate template = getTemplate(templateIDs[i]);
			if (template != null) {
				list.add(template);
			}
		}
		return list;
	}

	public List<GridTemplate> getTemplates(TemplateUsageType templateType) {
		LinkedList<GridTemplate> linkedlist = new LinkedList<GridTemplate>();
		for (Iterator<GridTemplate> iter = templateMap.values().iterator(); iter.hasNext();) {
			GridTemplate gridtemplate = iter.next();
			if (gridtemplate.getUsageType().equals(templateType)) linkedlist.add(gridtemplate);

		}
		return linkedlist;
	}

	public List<GridTemplate> getTemplatesWithEntityListColumn(GenericEntityType type, boolean forEntity) {
		if (type == null) throw new NullPointerException("type cannot be null");
		List<GridTemplate> list = new ArrayList<GridTemplate>();
		for (Iterator<GridTemplate> iter = templateMap.values().iterator(); iter.hasNext();) {
			GridTemplate gridtemplate = iter.next();
			if (gridtemplate.hasEntityTypeColumnFor(type, forEntity)) {
				list.add(gridtemplate);
			}
		}
		return list;
	}

	public void startLoading() {
		templateMap.clear();
		attrFinderMap.clear();
	}

	private void resetOldParserObjectTrees(GridTemplate template) {
		if (template.getRuleDefinition() != null) template.getRuleDefinition().setOldParserObjectForAction(null);
		for (Iterator<GridTemplateColumn> iter = template.getColumns().iterator(); iter.hasNext();) {
			GridTemplateColumn element = iter.next();
			if (element.getRuleDefinition() != null) element.getRuleDefinition().setOldParserObjectForAction(null);
		}
	}

	public synchronized void addTemplate(GridTemplate template) {
		logger.info(">>> addTemplate: " + template);
		Integer key = new Integer(template.getID());
		if (templateMap.containsKey(key)) {
			logger.warn("*** WARNING - addTemplate: template already cached: " + template);
		}
		else {
			templateMap.put(key, template);
		}
	}

	public synchronized void updateTemplate(GridTemplate template) throws InvalidDataException {
		logger.info(">>> updateTemplate: " + template);
		GridTemplate cachedTemplate = getTemplate(template.getID());
		logger.info("... updateTemplate: cached = " + cachedTemplate);
		if (cachedTemplate != null) {
			cachedTemplate.copyFrom(template);
			resetOldParserObjectTrees(cachedTemplate);

			convertGridEnumTypesForCurrentSelectionModel(cachedTemplate);
		}
		logger.info("<<< updateTemplate: done - " + cachedTemplate);
	}

	// If a enum template column changed its selection model (from multi select to single, or vice versa) update cached enum value type
	private void convertGridEnumTypesForCurrentSelectionModel(GridTemplate template) throws InvalidDataException {
		for (Iterator<GridTemplateColumn> colIter = template.getColumns().iterator(); colIter.hasNext();) {
			GridTemplateColumn col = colIter.next();
			if (ColumnDataSpecDigest.TYPE_ENUM_LIST.equals(col.getColumnDataSpecDigest().getType())) {
				Class<?> expectedCellValueType = col.getColumnDataSpecDigest().isMultiSelectAllowed() ? EnumValues.class : EnumValue.class;
				for (Iterator<ProductGrid> gridIter = GridManager.getInstance().getAllGridsForTemplate(template.getID()).iterator(); gridIter.hasNext();) {
					ProductGrid grid = gridIter.next();
					for (int r = 1; r <= grid.getNumRows(); r++) {
						Object currCellValue = grid.getCellValue(r, col.getName());
						if (currCellValue != null && !expectedCellValueType.equals(currCellValue.getClass())) {
							grid.setValue(r, col.getName(), col.convertToCellValue(
									currCellValue.toString(),
									DomainManager.getInstance(),
									ConfigurationManager.getInstance().getEnumerationSourceConfigSet()));
						}
					}
				}
			}
		}
	}

	public synchronized void resetOldParserObjectTressForAction(int actionTypeID) {
		GridTemplate template;
		GridTemplateColumn column;
		for (Iterator<GridTemplate> iter = templateMap.values().iterator(); iter.hasNext();) {
			template = iter.next();
			resetOldParserObjectTressForActionIfMatch(template.getRuleDefinition(), actionTypeID);
			for (Iterator<GridTemplateColumn> iter2 = template.getColumns().iterator(); iter2.hasNext();) {
				column = iter2.next();
				resetOldParserObjectTressForActionIfMatch(column.getRuleDefinition(), actionTypeID);
			}
		}
	}

	private void resetOldParserObjectTressForActionIfMatch(RuleDefinition ruleDef, int actionTypeID) {
		if (ruleDef != null && !ruleDef.isEmpty()) {
			ActionTypeDefinition actionType = ruleDef.getRuleAction().getActionType();
			if (actionType != null && actionType.getID() == actionTypeID) {
				ruleDef.setOldParserObjectForAction(null);
			}
		}
	}

	public String toString() {
		String s = "";
		s += "TemplateManager with " + templateMap.size() + " Templates!";
		s += templateMap.toString();
		return s;
	}

	public boolean validateTemplate(GridTemplate gridtemplate) {
		int i = gridtemplate.getID();
		TemplateUsageType s = gridtemplate.getUsageType();
		if (s == null) {
			logger.error("Invalid Usage type for template id " + i + ": null");
			return false;
		}
		else {
			return true;
		}
	}

	public boolean validateAllTemplates() {
		return true;
	}

	public void finishLoading() throws SapphireException {
		for (Iterator<GridTemplate> iter = templateMap.values().iterator(); iter.hasNext();) {
			// process template
			GridTemplate template = iter.next();
			if (UtilBase.isEmpty(template.getVersion())) {
				template.setVersion(GridTemplate.DEFAULT_VERSION);
			}

			// process column
			for (Iterator<GridTemplateColumn> iterator = template.getColumns().iterator(); iterator.hasNext();) {
				GridTemplateColumn column = iterator.next();

				// use attribute's title when column is mapped to one
				if (column.getMappedAttribute() != null && column.getTitle() == null) {
					String className = column.getMAClassName();
					String attrName = column.getMAAttributeName();
					DomainAttribute domainattribute = DomainManager.getInstance().getDomainAttribute(className, attrName);
					if (domainattribute != null) {
						column.setTitle(domainattribute.getDisplayLabel());
					}
					else {
						column.setTitle(column.getMappedAttribute());
					}
				}
			}
		}
	}

	public GridTemplate getTemplate(int i) {
		GridTemplate gridtemplate = templateMap.get(new Integer(i));
		return gridtemplate;
	}

	public List<GridTemplate> getTemplatesByName(String name) {
		List<GridTemplate> list = new ArrayList<GridTemplate>();
		for (Map.Entry<Integer, GridTemplate> entry : templateMap.entrySet()) {
			if (entry.getValue().getName().equalsIgnoreCase(name)) {
				list.add(entry.getValue());
			}
		}
		return list;
	}

}