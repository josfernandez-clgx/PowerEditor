package com.mindbox.pe.server.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.common.ContextUtil;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.AbstractGrid;
import com.mindbox.pe.model.ContextContainer;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.server.ServerContextUtil;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.cache.TypeEnumValueManager;


/**
 * Provides support for filtering reports various criteria.
 * 
 * TODO: Most the these methods have been pulled from JSP pages and have not been
 * unit tested. 
 *
 */
public final class ReportFilterDataHolder {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

	private final String templateName;
	private final String templateID;
	private final List<TemplateUsageType> usageTypeList;
	private final List<GridTemplate> templateList;
	private final String[] columnNames;
	private final GuidelineContext[] contexts;
	private final boolean includeChildren;
	private final boolean includeParents;
	private final boolean filterColumnData;
	private final boolean includeEmptyContexts;
	private final String status;
	private final Date date;
	private final ErrorMessageStorage errorMessageStorage;

	public ReportFilterDataHolder(ErrorMessageStorage errorMessageStorage, boolean addAllTemplatesIfNoneSpecified, String templateName,
			String templateID, String usageType, String columns, String contextElements, String includeChildren, String includeParents,
			String filterColumnData, String includeEmptyContexts, String status, String dateStr) {
		// Note: create errorMessages first
		this.errorMessageStorage = errorMessageStorage;
		this.templateName = templateName;
		this.templateID = templateID;
		this.usageTypeList = new ArrayList<TemplateUsageType>();
		if (!UtilBase.isEmpty(usageType)) {
			String[] usageNames = usageType.split(",");
			for (int i = 0; i < usageNames.length; i++) {
				if (!UtilBase.isEmpty(usageNames[i])) {
					try {
						usageTypeList.add(TemplateUsageType.valueOf(usageNames[i].trim()));
					}
					catch (IllegalArgumentException ex) {
						addErrorMessage("Invalid usage: checking spelling and case: " + usageType);
					}
				}
			}
		}
		this.contexts = createContexts(contextElements, errorMessageStorage);
		this.includeChildren = Boolean.valueOf(includeChildren).booleanValue();
		this.includeParents = Boolean.valueOf(includeParents).booleanValue();
		this.filterColumnData = Boolean.valueOf(filterColumnData).booleanValue();
		this.includeEmptyContexts = Boolean.valueOf(includeEmptyContexts).booleanValue();
		this.status = (UtilBase.isEmpty(status) ? null : getStatus(status));
		this.date = (UtilBase.isEmpty(dateStr) ? null : parseDate(dateStr));
		this.templateList = new ArrayList<GridTemplate>();
		// Note: buildTemplateList() must be called after templateID and templateName are set AND
		//       after usageTypeList is built!
		buildTemplateList(addAllTemplatesIfNoneSpecified);
		// Note: asValidColumnNames() must be called after buildTemplateList()
		this.columnNames = asValidColumnNames(columns);
	}

	public boolean isUsageTypeSpecified() {
		return !usageTypeList.isEmpty();
	}

	public boolean isColumnNameSpecified() {
		return columnNames != null && columnNames.length > 0;
	}

	public boolean isContextSpecified() {
		return contexts != null && contexts.length > 0;
	}

	public boolean isStatusSpecified() {
		return status != null;
	}

	public boolean isDateSpecified() {
		return date != null;
	}

	public boolean isTemplateSpecified() {
		return templateList != null && !templateList.isEmpty();
	}

	public void removeTemplatesWithoutGrid() {
		for (Iterator<GridTemplate> iter = templateList.iterator(); iter.hasNext();) {
			GridTemplate gridtemplate = iter.next();
			if (!GridManager.getInstance().hasGrids(gridtemplate.getID())) {
				iter.remove();
			}
		}
	}

	public List<TemplateUsageType> getUsageTypes() {
		return Collections.unmodifiableList(usageTypeList);
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public GuidelineContext[] getContexts() {
		return contexts;
	}

	public Date getDate() {
		return date;
	}

	public boolean isFilterColumnData() {
		return filterColumnData;
	}

	public boolean isIncludeChildren() {
		return includeChildren;
	}

	public boolean isIncludeEmptyContexts() {
		return includeEmptyContexts;
	}

	public boolean isIncludeParents() {
		return includeParents;
	}

	public String getStatus() {
		return status;
	}

	public List<GridTemplate> getTemplates() {
		return Collections.unmodifiableList(templateList);
	}

	public boolean isSpecifiedTemplateID(int templateID) {
		for (Iterator<GridTemplate> iter = templateList.iterator(); iter.hasNext();) {
			GridTemplate template = iter.next();
			if (template.getID() == templateID) return true;
		}
		return false;
	}

	public String[] getGridColumnNames() {
		int colCount = getMaxColumnCount();
		List<String> list = new LinkedList<String>();
		if (columnNames != null) {
			for (int i = 0; i < columnNames.length; i++) {
				list.add(ReportGenerator.toElementName(columnNames[i]) + "_column");
			}
		}
		for (int i = 1; i <= colCount; i++) {
			list.add("column" + ReportGenerator.getColumnNamePostfix(i));
		}
		return list.toArray(new String[0]);
	}

	private boolean containsContext(GuidelineContext[] contextToCheck) {
		boolean containsContext = true;
		GuidelineContext[] contexts = getContexts();
		if ((contexts != null) && (contexts.length > 0)) {
			if (!isIncludeChildren() && !isIncludeParents()) {
				containsContext = ContextUtil.containsContext(contextToCheck, contexts, isIncludeEmptyContexts());
			}
			else {
				if (isIncludeChildren() && isIncludeParents()) {
					containsContext = ServerContextUtil.isParentContext(contexts, contextToCheck, isIncludeEmptyContexts())
							|| ServerContextUtil.isParentContext(contextToCheck, contexts, isIncludeEmptyContexts());
				}
				else if (isIncludeChildren()) {
					containsContext = ServerContextUtil.isParentContext(contexts, contextToCheck, isIncludeEmptyContexts());
				}
				else if (isIncludeParents()) {
					containsContext = ServerContextUtil.isParentContext(contextToCheck, contexts, isIncludeEmptyContexts());
				}
			}
		}
		return containsContext;
	}

	/**
	 * @param contextContainer
	 * @return true if the contextContainer contains all of the contexts requested for the report.
	 */
	public boolean containsContext(ContextContainer contextContainer) {
		return containsContext(contextContainer.extractGuidelineContext());
	}

	public boolean matchesStatus(AbstractGrid<?> grid) {
		return matchesStatus(grid.getStatus());
	}

	public boolean matchesStatus(String statusToCheck) {
		if ((status != null) && !statusToCheck.equalsIgnoreCase(status)) {
			return false;
		}
		return true;

	}

	public Date parseDate(String dateStr) {
		Date date = null;
		try {
			date = dateFormat.parse(dateStr);
		}
		catch (ParseException e) {
			addErrorMessage("Invalid date format entered: " + dateStr + ". Date format pattern must be " + dateFormat.toPattern());
		}
		return date;
	}

	private String getStatus(String status) {
		List<TypeEnumValue> statusList = TypeEnumValueManager.getInstance().getAllEnumValues(TypeEnumValue.TYPE_STATUS);
		if (statusList != null) {
			for (Iterator<TypeEnumValue> i = statusList.iterator(); i.hasNext();) {
				TypeEnumValue value = i.next();
				if (value.getDisplayLabel() != null && value.getDisplayLabel().equals(status)) {
					return status;
				}
			}
		}
		addErrorMessage("Invalid status entered: " + status + ".");
		return null;
	}

	public static GuidelineContext[] createContexts(String contextElements, ErrorMessageStorage errorMessageStorage) {
		GuidelineContext[] contexts = null;

		if (!UtilBase.isEmpty(contextElements)) {
			Map<GenericEntityType, List<GenericEntity>> entityContextMap = new HashMap<GenericEntityType, List<GenericEntity>>();
			Map<GenericEntityType, List<GenericCategory>> catContextMap = new HashMap<GenericEntityType, List<GenericCategory>>();
			String[] contextString = contextElements.split(",");

			for (int i = 0; i < contextString.length; i++) {
				String[] contextData = contextString[i].split(":");

				if ((contextData == null)
						|| (contextData.length != 3)
						|| !(String.valueOf(contextData[1]).equalsIgnoreCase("category") || String.valueOf(contextData[1]).equalsIgnoreCase(
								"entity"))) {
					errorMessageStorage.addErrorMessage("Invalid structure for context element: " + contextString[i]);
					continue;
				}
				else {
					String entityType = contextData[0];
					String categoryOrEntity = contextData[1];
					String name = contextData[2];
					GenericEntityType type = GenericEntityType.forName(entityType);

					if (type == null) {
						errorMessageStorage.addErrorMessage("Invalid entity type (" + entityType + ") type for: " + contextString[i]);
						continue;
					}

					if (entityContextMap.get(type) != null) {
						if (categoryOrEntity.equalsIgnoreCase("category")) {
							errorMessageStorage.addErrorMessage("For each entity type either categories or entities can be specified. " + "For entity type "
									+ entityType + " an entity has already been specified as well as the following category: "
									+ contextElements);
							continue;
						}

						GenericEntity entity = EntityManager.getInstance().getEntity(type, name);
						if (entity != null) {
							entityContextMap.get(entityType).add(entity);
						}
						else {
							errorMessageStorage.addErrorMessage("Unable to find entity for " + contextElements);
							continue;
						}
					}
					else if (catContextMap.get(type) != null) {
						if (categoryOrEntity.equalsIgnoreCase("entity")) {
							errorMessageStorage.addErrorMessage("For each entity type either categories or entities can be specified. " + "For entity type "
									+ entityType + " a category has already been specified as well as the following entity: "
									+ contextElements);
							continue;
						}
						else {
							GenericCategory[] categories = EntityManager.getInstance().findGenericCategoryByName(
									type.getCategoryType(),
									name);
							if (categories != null && categories.length > 0) {
								for (int j = 0; j < categories.length; j++)
									catContextMap.get(entityType).add(categories[j]);
							}
							else {
								errorMessageStorage.addErrorMessage("Unable to find category for " + contextElements);
								continue;
							}
						}
					}
					else if (categoryOrEntity.equalsIgnoreCase("category")) {
						GenericCategory[] categories = EntityManager.getInstance().findGenericCategoryByName(type.getCategoryType(), name);
						if (categories != null && categories.length > 0) {
							List<GenericCategory> list = new ArrayList<GenericCategory>();
							for (int j = 0; j < categories.length; j++) {
								list.add(categories[j]);
							}
							catContextMap.put(type, list);
						}
						else {
							errorMessageStorage.addErrorMessage("Unable to find category for " + contextElements);
							continue;
						}
					}
					else if (categoryOrEntity.equalsIgnoreCase("entity")) {
						GenericEntity entity = EntityManager.getInstance().getEntity(type, name);
						if (entity != null) {
							List<GenericEntity> list = new ArrayList<GenericEntity>();
							list.add(entity);
							entityContextMap.put(type, list);
						}
						else {
							errorMessageStorage.addErrorMessage("Unable to find entity for " + contextElements);
							continue;
						}
					}
				}
			} // end for loop

			List<GuidelineContext> contextList = new ArrayList<GuidelineContext>();

			for (Map.Entry<GenericEntityType, List<GenericEntity>> entry : entityContextMap.entrySet()) {
				GenericEntityType type = entry.getKey();
				List<GenericEntity> entities = entry.getValue();
				int[] ids = new int[entities.size()];
				GuidelineContext context = new GuidelineContext(type);

				for (int j = 0; j < entities.size(); j++) {
					ids[j] = entities.get(j).getID();
				}

				context.setIDs(ids);
				contextList.add(context);
			}

			for (Map.Entry<GenericEntityType,List<GenericCategory>> entry : catContextMap.entrySet()) {
				GenericEntityType type = entry.getKey();
				List<GenericCategory> categories = entry.getValue();
				int[] ids = new int[categories.size()];
				GuidelineContext context = new GuidelineContext(type.getCategoryType());

				for (int j = 0; j < categories.size(); j++) {
					ids[j] = categories.get(j).getID();
				}

				context.setIDs(ids);
				contextList.add(context);
			}

			contexts = contextList.toArray(new GuidelineContext[0]);
		}

		return contexts;
	}


	private void buildTemplateList(boolean addAllTemplatesIfNoneSpecified) {
		if (usageTypeList.isEmpty() && UtilBase.isEmpty(templateName) && UtilBase.isEmpty(templateID)) {
			if (addAllTemplatesIfNoneSpecified) {
				templateList.addAll((GuidelineTemplateManager.getInstance().getAllTemplates()));
			}
		}
		else if (!usageTypeList.isEmpty()) {
			for (Iterator<TemplateUsageType> iter = usageTypeList.iterator(); iter.hasNext();) {
				TemplateUsageType usageType = iter.next();
				templateList.addAll(GuidelineTemplateManager.getInstance().getTemplates(usageType));
			}
		}
		else if (!UtilBase.isEmpty(templateName)) {
			String[] templateNames = templateName.split(",");
			for (int i = 0; i < templateNames.length; i++) {
				List<GridTemplate> tmpList = GuidelineTemplateManager.getInstance().getTemplatesByName(templateNames[i].trim());

				if ((tmpList == null) || tmpList.isEmpty()) {
					addErrorMessage("Invalid template name: no templates found with name " + templateNames[i]);
				}
				templateList.addAll(tmpList);
			}
		}
		else {
			try {
				templateList.addAll(GuidelineTemplateManager.getInstance().getTemplates(UtilBase.toIntArray(templateID.trim())));
			}
			catch (NumberFormatException ex) {
				addErrorMessage("Invalid template ids: " + templateID + ". templateid must be integer delimited by comma.");
			}
		}
	}

	private void addErrorMessage(String message) {
		errorMessageStorage.addErrorMessage(message);
	}

	private String[] asValidColumnNames(String colNames) {
		if ((colNames == null) || (colNames.trim().length() == 0)) {
			return null;
		}

		String[] sa = colNames.trim().split(",");
		List<String> list = new ArrayList<String>();

		for (int i = 0; i < sa.length; i++) {
			if (hasColumnWithTitle(templateList, sa[i])) {
				list.add(sa[i]);
			}
			else {
				addErrorMessage(sa[i] + " is not a valid column name");
			}
		}

		return list.toArray(new String[0]);
	}

	private boolean hasColumnWithTitle(List<GridTemplate> templateList, String title) {
		for (Iterator<GridTemplate> iter = templateList.iterator(); iter.hasNext();) {
			GridTemplate gridtemplate = iter.next();
			if (gridtemplate.findColumnWithTitle(title, true) != null) {
				return true;
			}
		}

		return false;
	}

	private int getMaxColumnCount() {
		int max = 0;
		for (Iterator<GridTemplate> iter = templateList.iterator(); iter.hasNext();) {
			GridTemplate gridtemplate = iter.next();
			max = Math.max(max, gridtemplate.getNumColumns());
		}

		if (columnNames == null || columnNames.length == 0) return max;

		for (Iterator<GridTemplate> iter = templateList.iterator(); iter.hasNext();) {
			GridTemplate gridtemplate = iter.next();
			max = Math.max(max, countNonMatchingColumns(gridtemplate));
		}
		return max;
	}

	private int countNonMatchingColumns(GridTemplate template) {
		int count = 0;
		for (int col = 1; col <= template.getNumColumns(); col++) {
			if (!UtilBase.isMember(template.getColumn(col).getTitle(), columnNames, true)) {
				++count;
			}
		}
		return count;
	}

}
