package com.mindbox.pe.server.imexport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.PasswordOneWayHashUtil;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.CategoryTypeDefinition;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.common.config.EntityPropertyDefinition;
import com.mindbox.pe.common.config.EntityTypeDefinition;
import com.mindbox.pe.model.AbstractGrid;
import com.mindbox.pe.model.AbstractGuidelineGrid;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.ParameterGrid;
import com.mindbox.pe.model.ParameterTemplate;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.admin.UserPassword;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.filter.PersistentFilterSpec;
import com.mindbox.pe.model.rule.RuleSet;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.server.ServerContextUtil;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.server.bizlogic.GridActionCoordinator;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.cache.ParameterTemplateManager;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.imexport.digest.ActivationDates;
import com.mindbox.pe.server.imexport.digest.Association;
import com.mindbox.pe.server.imexport.digest.CategoryDigest;
import com.mindbox.pe.server.imexport.digest.Entity;
import com.mindbox.pe.server.imexport.digest.EntityIdentity;
import com.mindbox.pe.server.imexport.digest.Grid;
import com.mindbox.pe.server.imexport.digest.GridActivation;
import com.mindbox.pe.server.imexport.digest.GridRow;
import com.mindbox.pe.server.imexport.digest.Parent;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletActionException;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public final class ObjectConverter {

	/**
	 * Generates a unique key for id map for the specified category type and id.
	 * 
	 * @param type
	 *            category type
	 * @param id
	 *            category id
	 * @return unique key
	 */
	public static String asCategoryIDMapKey(String type, int id) {
		return "C|" + type + ":" + id;
	}

	/**
	 * Generates a unique key for id map for the specified entity type and id.
	 * 
	 * @param type
	 *            entity type
	 * @param id
	 *            category id
	 * @return unique key
	 */
	public static String asEntityIDMapKey(String type, int id) {
		return type + ":" + id;
	}

	public static int getRequiredMappedID(String key, Map<String, Integer> idMap) throws ImportException {
		if (idMap.containsKey(key)) {
			return idMap.get(key).intValue();
		}
		else {
			throw new ImportException("Key " + key + " not found in the id map");
		}
	}

	private static int getMappedID(String key, Map<String, Integer> idMap, int defValue) {
		if (idMap.containsKey(key)) {
			return idMap.get(key).intValue();
		}
		else {
			return defValue;
		}

	}

	private static final Logger getLogger() {
		return Logger.getLogger(ObjectConverter.class);
	}

	private static String getColumnName(ParameterTemplate template, int templateColumn) {
		if (template.getColumn(templateColumn) == null) {
			return null;
		}
		else {
			return template.getColumn(templateColumn).getTitle(); // getName();
		}
	}

	private static String getColumnName(GridTemplate template, int templateColumn) {
		if (template.getColumn(templateColumn) == null) {
			return null;
		}
		else {
			return template.getColumn(templateColumn).getTitle();
		}
	}

	/**
	 * @param templateColumn
	 * @return digest column number for the specified template column
	 */
	private static int translateColumnNumber(ParameterTemplate template, int templateColumn, Grid gridDigest) {
		return gridDigest.findColumnNumberFor(getColumnName(template, templateColumn));
	}

	private static int translateColumnNumber(GridTemplate template, int templateColumn, Grid gridDigest) {
		return gridDigest.findColumnNumberFor(getColumnName(template, templateColumn));
	}

	private static String[][] extractCellValues(ParameterTemplate template, GridActivation activation, Grid gridDigest)
			throws ImportException {
		if (template == null) {
			throw new ImportException("template not found");
		}

		GridRow[] rows = activation.getRows();
		String[][] values = new String[rows.length][template.getColumnCount()];
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[i].length; j++) {
				int translatedColumn = translateColumnNumber(template, j + 1, gridDigest);
				if (translatedColumn == -1) translatedColumn = j + 1;
				values[i][j] = (rows[i] == null ? null : rows[i].getCellValueAt(translatedColumn));
				if (values[i][j] == null) {
					values[i][j] = "";
				}
			}
		}

		return values;
	}

	private static String[][] extractCellValues(GridTemplate template, GridActivation activation, Grid gridDigest) throws ImportException {
		if (template == null) {
			throw new ImportException("template not found");
		}

		if (getLogger().isDebugEnabled()) {
			getLogger().debug(">>> extractCellValues: " + template + ", " + gridDigest);
			getLogger().debug(
					"... extractCellValues: template usage type value = " + template.getUsageType().toString()
							+ ", template usage display name = " + template.getUsageType().getDisplayName());
		}
		GridRow[] rows = activation.getRows();
		getLogger().debug("... extractCellValues: rows = " + rows.length);

		String[][] values = new String[rows.length][template.getNumColumns()];
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[i].length; j++) {
				int translatedColumn = translateColumnNumber(template, j + 1, gridDigest);
				if (translatedColumn < 0) {
					throw new ImportException("Failed to find cell value for (" + (i + 1) + "," + (j + 1) + ") - column not found");
				}

				String valToTranslate = (rows[i] == null ? null : rows[i].getCellValueAt(translatedColumn));
				if (valToTranslate != null) {
					values[i][j] = valToTranslate;
				}
				if (values[i][j] == null) {
					values[i][j] = "";
				}
				getLogger().debug("... extractCellValues: set[" + i + "][" + j + "] = " + values[i][j]);
			}
		}
		getLogger().debug("<<< extractCellValues:");
		return values;
	}

	private static DateSynonym getEffectiveDateSynonym(ActivationDates activationDates, Map<Integer, Integer> dateSynonymIDMap, User user)
			throws ImportException, DataValidationFailedException {
		if (activationDates.hasEffectiveDateID()) {
			return DateSynonymManager.getInstance().getDateSynonym(
					findMappedDateSynonymID(activationDates.getEffectiveDateID(), dateSynonymIDMap));
		}
		else if (activationDates.effectiveDate() != null) {
			DateSynonym ds = DateSynonymManager.getInstance().getDateSynonym(activationDates.effectiveDate());
			if (ds == null) {
				try {
					ds = BizActionCoordinator.createNewDateSynonym(activationDates.effectiveDate(), user);
				}
				catch (ServletActionException e) {
					e.printStackTrace();
					throw new ImportException("Failed to create new date synonym for effectivate date for "
							+ activationDates.effectiveDate());
				}
			}
			return ds;
		}
		else {
			return null;
		}
	}

	private static DateSynonym getExpirationDateSynonym(ActivationDates activationDates, Map<Integer, Integer> dateSynonymIDMap, User user)
			throws ImportException, DataValidationFailedException {
		if (activationDates.hasExpirationDateID()) {
			return DateSynonymManager.getInstance().getDateSynonym(
					findMappedDateSynonymID(activationDates.getExpirationDateID(), dateSynonymIDMap));
		}
		else if (activationDates.expirationDate() != null) {
			DateSynonym ds = DateSynonymManager.getInstance().getDateSynonym(activationDates.expirationDate());
			if (ds == null) {
				try {
					ds = BizActionCoordinator.createNewDateSynonym(activationDates.expirationDate(), user);
				}
				catch (ServletActionException e) {
					e.printStackTrace();
					throw new ImportException("Failed to create new date synonym for expiration date for "
							+ activationDates.expirationDate());
				}
			}
			return ds;
		}
		else {
			return null;
		}
	}

	public static List<ParameterGrid> asParameterGridList(Grid gridDigest, GuidelineContext[] context, User user,
			Map<Integer, Integer> dateSynonymIDMap, ReplacementDateSynonymProvider replacementDateSynonymProvider) throws ImportException,
			DataValidationFailedException {
		List<ParameterGrid> gridList = new ArrayList<ParameterGrid>();
		List<GridActivation> actList = gridDigest.getActivations();
		for (GridActivation element : actList) {
			ActivationDates actDates = element.getActivationDates();

			if (dateSynonymIDMap != null && actDates != null) {
				Integer newID = dateSynonymIDMap.get(new Integer(actDates.getEffectiveDateID()));
				if (newID != null) {
					actDates.setEffectiveDateID(newID.intValue());
				}
				newID = dateSynonymIDMap.get(new Integer(actDates.getExpirationDateID()));
				if (newID != null) {
					actDates.setExpirationDateID(newID.intValue());
				}
			}

			DateSynonym effectiveDateSynonym = (actDates == null ? null : getEffectiveDateSynonym(actDates, dateSynonymIDMap, user));
			// If no activation date, create a new one with a date one day earlier than the earliest date synonym
			if (effectiveDateSynonym == null) {
				effectiveDateSynonym = replacementDateSynonymProvider.getReplacementDateSynonymForImport();
			}
			ParameterTemplate template = ParameterTemplateManager.getInstance().getTemplate(gridDigest.getTemplateID());
			ParameterGrid grid = new ParameterGrid(element.getId(), gridDigest.getTemplateID(), effectiveDateSynonym, (actDates == null
					? null
					: getExpirationDateSynonym(actDates, dateSynonymIDMap, user)));
			setInvariants(grid, element, context, extractCellValues(template, element, gridDigest));
			grid.setTemplate(template);
			gridList.add(grid);
		}
		return gridList;
	}

	public static List<ProductGrid> asGuidelineGridList(Grid gridDigest, GuidelineContext[] context, boolean merge, User user,
			Map<Integer, Integer> dateSynonymIDMap, Map<String, Integer> entityIDMap,
			ReplacementDateSynonymProvider replacementDateSynonymProvider) throws ImportException, DataValidationFailedException {
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();
		List<GridActivation> actList = gridDigest.getActivations();
		for (Iterator<GridActivation> iter = actList.iterator(); iter.hasNext();) {
			GridActivation element = iter.next();
			ActivationDates actDates = element.getActivationDates();

			GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(gridDigest.getTemplateID());
			if (template == null) {
				throw new ImportException("Invalid template id: " + gridDigest.getTemplateID());
			}
			if (dateSynonymIDMap != null && actDates != null) {
				Integer newID = dateSynonymIDMap.get(new Integer(actDates.getEffectiveDateID()));
				if (newID != null) {
					actDates.setEffectiveDateID(newID.intValue());
				}
				newID = dateSynonymIDMap.get(new Integer(actDates.getExpirationDateID()));
				if (newID != null) {
					actDates.setExpirationDateID(newID.intValue());
				}
			}
			boolean hasGrid = GridManager.getInstance().hasGrid(element.getId(), gridDigest.getTemplateID());
			int gridID = (merge ? -1 : hasGrid ? element.getId() : -1);
			DateSynonym effectiveDateSynonym = (actDates == null ? null : getEffectiveDateSynonym(actDates, dateSynonymIDMap, user));
			// If no activation date, create a new one with a date one day earlier than the earliest date synonym
			if (effectiveDateSynonym == null) {
				effectiveDateSynonym = replacementDateSynonymProvider.getReplacementDateSynonymForImport();
			}
			ProductGrid grid = new ProductGrid(gridID, template, effectiveDateSynonym, (actDates == null ? null : getExpirationDateSynonym(
					actDates,
					dateSynonymIDMap,
					user)));
			setInvariants(grid, element, context, extractCellValues(GuidelineTemplateManager.getInstance().getTemplate(
					gridDigest.getTemplateID()), element, gridDigest));
			fixEntityListColumnValues(grid, entityIDMap, merge);
			// if not-merge and grid exists, overwrite rule ids from existing grid
			if (hasGrid && !merge) {
				replaceRuleIDs(grid, GridManager.getInstance().getProductGrid(grid.getID()));
			}
			gridList.add(grid);
		}
		return gridList;
	}

	private static void replaceRuleIDs(ProductGrid targetGrid, ProductGrid sourceGrid) {
		assert targetGrid != null;
		if (sourceGrid != null) {
			targetGrid.copyColumns(targetGrid.getRuleIDColumnNames(), sourceGrid);
		}
	}

	private static void fixEntityListColumnValues(ProductGrid grid, Map<String, Integer> entityIDMap, boolean throwExceptionIfNotFound)
			throws ImportException {
		String[] columnNames = grid.getColumnNames();
		for (int i = 0; i < grid.getNumRows(); i++) {
			for (int c = 0; c < columnNames.length; c++) {
				Object value = grid.getCellValue(i + 1, columnNames[c]);
				if (value instanceof CategoryOrEntityValue) {
					fixGridCellValue((CategoryOrEntityValue) value, entityIDMap, throwExceptionIfNotFound);
				}
				else if (value instanceof CategoryOrEntityValues) {
					fixGridCellValue((CategoryOrEntityValues) value, entityIDMap, throwExceptionIfNotFound);
				}
			}
		}
	}

	/**
	 * This modifies <code>value</code>.
	 * @param value
	 * @param entityIDMap
	 * @param throwExceptionIfNotFound if set to <code>true</code> and the value is not found int he entityIDMap, an ImportException is thrown
	 * @throws ImportException if <code>throwExceptionIfNotFound</code> is set to <code>true</code> and the value is not found in <code>entityIDMap</code>
	 */
	private static void fixGridCellValue(CategoryOrEntityValue value, Map<String, Integer> entityIDMap, boolean throwExceptionIfNotFound)
			throws ImportException {
		if (value != null) {
			String key;
			if (value.isForEntity()) {
				key = asEntityIDMapKey(value.getEntityType().getName(), value.getId());
			}
			else {
				key = asCategoryIDMapKey(value.getEntityType().getName(), value.getId());
			}
			if (entityIDMap.containsKey(key)) {
				value.setId(entityIDMap.get(key).intValue());
			}
			else if (throwExceptionIfNotFound) {
				throw new ImportException("No id found for " + value);
			}
		}
	}

	/**
	 * This modifies <code>value</code>.
	 * @param value
	 * @param entityIDMap
	 */
	private static void fixGridCellValue(CategoryOrEntityValues value, Map<String, Integer> entityIDMap, boolean throwExceptionIfNotFound)
			throws ImportException {
		if (value != null) {
			for (int i = 0; i < value.size(); i++) {
				Object obj = value.get(i);
				if (obj instanceof CategoryOrEntityValue) {
					fixGridCellValue((CategoryOrEntityValue) obj, entityIDMap, throwExceptionIfNotFound);
				}
			}
		}
	}

	/**
	 * If there is a list of grids that have the same data as the specified grid list, merge context.
	 * Each grid in the grid list must match for merge.
	 * @param gridListList
	 * @param gridList
	 * @throws NullPointerException if <code>gridListList</code> or <code>gridList</code> is <code>null</code>
	 */
	public static <T extends AbstractGrid<?>> void addAndMergeGridContextWithSameDataIfFound(List<List<T>> gridListList, List<T> gridList) {
		if (gridListList == null) throw new NullPointerException("gridListList cannot be null");
		if (gridList.isEmpty()) return;
		boolean isMerged = false;
		if (!gridListList.isEmpty()) {
			for (Iterator<List<T>> iter = gridListList.iterator(); iter.hasNext();) {
				List<T> gridListToCheck = iter.next();
				if (hasSameData(gridListToCheck, gridList)) {
					// Assumption: all Grids in each list have identical context.  To check merge-ability, just grab the context off the first element in each list
					GuidelineContext[] mergeFromContext = gridList.get(0).extractGuidelineContext();
					GuidelineContext[] mergeToContext = gridListToCheck.get(0).extractGuidelineContext();
					if (contextsAreMergeable(mergeToContext, mergeFromContext)) {
						// add context
						for (Iterator<T> iterator = gridListToCheck.iterator(); iterator.hasNext();) {
							AbstractGrid<?> targetGrid = iterator.next();
							ServerContextUtil.addContext(targetGrid, mergeFromContext);
						}
						isMerged = true;
					}

				}
			}
		}
		if (!isMerged) {
			gridListList.add(gridList);
		}
	}

	// contexts can be merged if both have only Category elements, or if both have only Entity elements
	/**
	 * @return <code>false</code> if the specified context do not have conflicting context elements. That is,
	 *                           one has a category context and the other has entity context of the same entity type.
	 *                           Otherwise, this returns <code>true</code>
	 */
	private static boolean contextsAreMergeable(GuidelineContext[] mergeToContext, GuidelineContext[] mergeFromContext) {
		if (mergeToContext == null || mergeToContext.length == 0 || mergeFromContext == null || mergeFromContext.length == 0) {
			return true; // An empty context can be merged with any other
		}
		for (int i = 0; i < mergeFromContext.length; i++) {
			if (mergeFromContext[i].hasCategoryContext()) {
				for (int j = 0; j < mergeToContext.length; j++) {
					// if mergeToContext has a generic entity context of the same type, cannot merge
					if (!mergeToContext[j].hasCategoryContext()
							&& mergeToContext[j].getGenericEntityType().getCategoryType() == mergeFromContext[i].getGenericCategoryType()) {
						return false;
					}
				}
			}
			else {
				for (int j = 0; j < mergeToContext.length; j++) {
					// if mergeToContext has a generic category context of the same type, cannot merge
					if (mergeToContext[j].hasCategoryContext()
							&& mergeToContext[j].getGenericCategoryType() == mergeFromContext[i].getGenericEntityType().getCategoryType()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private static <T extends AbstractGrid<?>> boolean hasSameData(List<T> gridList1, List<T> gridList2) {
		if (gridList1.isEmpty() && gridList2.isEmpty()) return true;
		if (gridList1.size() != gridList2.size()) return false;
		for (Iterator<T> iter = gridList2.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (element instanceof ParameterGrid) {
				if (!hasIdenticalGrid(gridList1, (ParameterGrid) element)) {
					return false;
				}
			}
			else if (element instanceof AbstractGuidelineGrid) {
				if (!hasIdenticalGrid(gridList1, (AbstractGuidelineGrid) element)) {
					return false;
				}
			}
			else {
				return false;
			}
		}
		return true;
	}

	private static <T extends AbstractGrid<?>> boolean hasIdenticalGrid(List<T> gridList, AbstractGuidelineGrid grid) {
		for (Iterator<T> iter = gridList.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (element instanceof AbstractGuidelineGrid && ((AbstractGuidelineGrid) element).identical(grid)) {
				return true;
			}
		}
		return false;
	}

	private static <T extends AbstractGrid<?>> boolean hasIdenticalGrid(List<T> gridList, ParameterGrid grid) {
		for (Iterator<T> iter = gridList.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (element instanceof ParameterGrid && ((ParameterGrid) element).equals(grid)) {
				return true;
			}
		}
		return false;
	}

	private static void setInvariants(AbstractGrid<?> gridObject, GridActivation gridActivation, GuidelineContext[] context,
			String[][] cellValues) throws ImportException {
		gridObject.setCloneOf(gridActivation.getParentID());
		gridObject.setComments(gridActivation.getComment());
		gridObject.setCreationDate(ConfigUtil.toDate(gridActivation.getCreatedOn()));
		try {
			if (gridObject instanceof ParameterGrid) {
				GridActionCoordinator.setParameterGridCellValues((ParameterGrid) gridObject, cellValues);
			}
			else {
				GridActionCoordinator.setGridCellValues((AbstractGuidelineGrid) gridObject, cellValues);
			}
		}
		catch (InvalidDataException e) {
			throw new ImportException("Invalid cell values for " + gridObject + ": " + e.getMessage());
		}
		gridObject.setNumRows(gridActivation.countRows());
		gridObject.setStatus(gridActivation.getStatus());
		gridObject.setStatusChangeDate(ConfigUtil.toDate(gridActivation.getStatusChangedOn()));
		if (gridObject.getNumRows() == 0) {
			gridObject.setNumRows(1);
		}
		ServerContextUtil.setContext(gridObject, context);
	}

	private static GenericEntityType fetchGenericEntityType(EntityIdentity element) throws ImportException {
		return GenericEntityType.forName(element.getType());
	}

	private static GuidelineContext fetchGuidelineContextForEntity(List<EntityIdentity> contextList, GenericEntityType type, boolean merge,
			Map<String, Integer> entityIDMap) throws ImportException {
		List<Integer> intList = new ArrayList<Integer>();
		for (Iterator<EntityIdentity> iter = contextList.iterator(); iter.hasNext();) {
			EntityIdentity element = iter.next();
			if (type == fetchGenericEntityType(element)) {
				intList.add(new Integer((merge ? getMappedID(
						asEntityIDMapKey(type.toString(), element.getId()),
						entityIDMap,
						element.getId()) : element.getId())));
			}
		}
		if (intList.isEmpty()) {
			return null;
		}
		else {
			GuidelineContext context = new GuidelineContext(type);
			context.setIDs(UtilBase.toIntArray(intList));
			return context;
		}
	}

	private static GuidelineContext fetchGuidelineContextForCategory(List<EntityIdentity> contextList, GenericEntityType type,
			boolean merge, Map<String, Integer> entityIDMap) throws ImportException {
		List<Integer> intList = new ArrayList<Integer>();
		for (Iterator<EntityIdentity> iter = contextList.iterator(); iter.hasNext();) {
			EntityIdentity element = iter.next();
			String typeStr = element.getType();
			if (typeStr.startsWith("generic-category:")) {
				if (type == GenericEntityType.forName(typeStr.substring(17))) {
					intList.add(new Integer((merge ? getMappedID(
							asCategoryIDMapKey(type.toString(), element.getId()),
							entityIDMap,
							element.getId()) : element.getId())));
				}
			}
			else if (type.toString().equals("product") && typeStr.equals("category")) {
				intList.add(new Integer((merge ? getMappedID(
						asCategoryIDMapKey(type.toString(), element.getId()),
						entityIDMap,
						element.getId()) : element.getId())));
			}
		}
		if (intList.isEmpty()) {
			return null;
		}
		else {
			GuidelineContext context = new GuidelineContext(
					ConfigurationManager.getInstance().getEntityConfiguration().getCategoryDefinition(type).getTypeID());
			context.setIDs(UtilBase.toIntArray(intList));
			return context;
		}
	}

	private static int extractContextID(List<EntityIdentity> contextList, GenericEntityType type) throws ImportException {
		for (Iterator<EntityIdentity> iter = contextList.iterator(); iter.hasNext();) {
			EntityIdentity element = iter.next();
			if (type == fetchGenericEntityType(element)) {
				return element.getId();
			}
		}
		return -1;
	}

	/**
	 * Extract context from the specified grid digest. If <code>merge</code> is <code>true</code>,
	 * entity ids in the specified map will be used.
	 * 
	 * @param gridDigest
	 *            grid digest
	 * @param merge
	 *            merge flag
	 * @param entityIDMap
	 *            entity id map; ignored if <code>merge</code> is <code>false</code>
	 * @return guideline contxt array
	 * @throws ImportException
	 *             on error
	 */
	public static GuidelineContext[] fetchContext(Grid gridDigest, boolean merge, Map<String, Integer> entityIDMap) throws ImportException {
		List<GuidelineContext> resultList = new ArrayList<GuidelineContext>();
		List<EntityIdentity> contextList = gridDigest.getGridContext();
		GuidelineContext context = null;
		GenericEntityType[] types = GenericEntityType.getAllGenericEntityTypes();
		for (int i = 0; i < types.length; i++) {
			// check generic entity
			context = fetchGuidelineContextForEntity(contextList, types[i], merge, entityIDMap);
			if (context != null && context.getIDs().length > 0) {
				resultList.add(context);
			}
			// check generic category
			context = fetchGuidelineContextForCategory(contextList, types[i], merge, entityIDMap);
			if (context != null && context.getIDs().length > 0) {
				resultList.add(context);
			}
		}
		return resultList.toArray(new GuidelineContext[0]);
	}

	public static RuleSet asRuleset(com.mindbox.pe.server.imexport.digest.RuleSet rulesetDigest, boolean merge,
			Map<String, Integer> entityIDMap) throws ImportException {
		List<EntityIdentity> contextList = rulesetDigest.getContextElements();
		RuleSet ruleSet = new RuleSet(
				TemplateUsageType.valueOf(rulesetDigest.getUsage()),
				rulesetDigest.getId(),
				rulesetDigest.getName(),
				rulesetDigest.getDescription());
		ruleSet.setActivationDate(rulesetDigest.getActivationDates().effectiveDate());
		ruleSet.setExpirationDate(rulesetDigest.getActivationDates().expirationDate());
		// process hard-coded entity types
		GenericEntityType entityTypeToConsider = GenericEntityType.forName("product");
		if (entityTypeToConsider != null && entityTypeToConsider.hasCategory()) {
			GuidelineContext catContext = fetchGuidelineContextForCategory(contextList, entityTypeToConsider, merge, entityIDMap);
			if (catContext != null) {
				ruleSet.setCategoryIDs(catContext.getIDs());
			}
		}
		entityTypeToConsider = GenericEntityType.forName("channel");
		if (entityTypeToConsider != null) {
			ruleSet.setChannelID(extractContextID(contextList, entityTypeToConsider));
		}
		entityTypeToConsider = GenericEntityType.forName("investor");
		if (entityTypeToConsider != null) {
			ruleSet.setInvestorID(extractContextID(contextList, entityTypeToConsider));
		}
		entityTypeToConsider = GenericEntityType.forName("product");
		if (entityTypeToConsider != null) {
			ruleSet.setProductID(extractContextID(contextList, entityTypeToConsider));
		}
		GenericEntityType[] types = GenericEntityType.getAllGenericEntityTypes();
		for (int i = 0; i < types.length; i++) {
			GuidelineContext context = fetchGuidelineContextForEntity(contextList, types[i], merge, entityIDMap);
			if (context != null && context.getIDs().length > 0) {
				ruleSet.setGenericEntityID(types[i], context.getIDs()[0]);
			}
		}

		ruleSet.setStatus(rulesetDigest.getStatus());
		return ruleSet;
	}

	public static Role asRole(com.mindbox.pe.server.imexport.digest.Role roleDigest,
			List<com.mindbox.pe.server.imexport.digest.Privilege> digestPrivList) throws ImportException {
		// build list of model.Privilege objects for digested Role...
		List<Privilege> privList = new ArrayList<Privilege>();
		int[] ids = roleDigest.privilegeIDs();
		for (int i = 0; i < ids.length; i++) {
			// Since 5.0 EntityType and UsageType privilege ids are dynamically generated when the PE config file is parsed at server start up.
			// Therefore, we can no longer rely on the finding privs by the digested priv id.  Rather, we must search for existing privs by name.
			String digestPrivName = null;
			for (Iterator<com.mindbox.pe.server.imexport.digest.Privilege> digestPrivIter = digestPrivList.iterator(); digestPrivIter.hasNext()
					&& digestPrivName == null;) {
				com.mindbox.pe.server.imexport.digest.Privilege digestPriv = digestPrivIter.next();
				if (ids[i] == digestPriv.getId()) {
					digestPrivName = digestPriv.getName();
				}
			}

			if (UtilBase.isEmpty(digestPrivName)) {
				getLogger().warn(
						"Ignored privilege " + ids[i] + " for " + roleDigest.getName() + " role, privilege not found in import file.");
				continue;
			}

			Privilege priv = SecurityCacheManager.getInstance().findPrivilegeByName(digestPrivName);
			if (priv == null) {
				getLogger().warn(
						"Ignored privilege " + ids[i] + "=" + digestPrivName + " for " + roleDigest.getName()
								+ " role: privilege not found");
				continue;
			}
			if (!privList.contains(priv)) {
				privList.add(priv);
			}
		}

		// ...then, with the priv list, create the Role
		return new Role(roleDigest.getId(), roleDigest.getName(), privList);
	}

	/**
	 * returns privilegeId's which did not match any privilege from the DB but are in the file to be imported
	 * @since PowerEditor 5.0
	 * @param roleDigest
	 * @return List of unknown Privileges
	 * @throws ImportException
	 */
	public static List<String> unknownPrivsForRole(com.mindbox.pe.server.imexport.digest.Role roleDigest,
			List<com.mindbox.pe.server.imexport.digest.Privilege> digestPrivList) throws ImportException {
		List<String> unknownPrivList = new ArrayList<String>();
		int[] ids = roleDigest.privilegeIDs();
		for (int i = 0; i < ids.length; i++) {
			// Since 5.0 EntityType and UsageType privilege ids are dynamically generated when the PE config file is parsed at server start up.
			// Therefore, we can no longer rely on the finding privs by the digested priv id.  Rather, we must search for existing privs by name.
			String digestPrivName = null;
			for (Iterator<com.mindbox.pe.server.imexport.digest.Privilege> digestPrivIter = digestPrivList.iterator(); digestPrivIter.hasNext();) {
				com.mindbox.pe.server.imexport.digest.Privilege digestPriv = digestPrivIter.next();
				if (ids[i] == digestPriv.getId()) {
					digestPrivName = digestPriv.getName();
				}
			}

			Privilege priv = SecurityCacheManager.getInstance().findPrivilegeByName(digestPrivName);
			if (priv == null) {
				unknownPrivList.add(digestPrivName);
			}
		}
		return unknownPrivList;
	}

	public static User asUser(com.mindbox.pe.server.imexport.digest.User userDigest) throws ImportException {
		User user;
		if (!userDigest.getUserPassword().isEmpty()) {// coming from encrypted 5.1
			user = new User(
					userDigest.getId(),
					userDigest.getName(),
					userDigest.getStatus(),
					userDigest.getPasswordChangeRequired(),
					userDigest.getFailedLoginCounter(),
					convertUserPassword(userDigest.getUserPassword()));
		}
		else {// coming from pre 5.1 when userPassword was an attribute of user class and not a class of its own
			String encryptedPassword = PasswordOneWayHashUtil.convertToOneWayHash(
					userDigest.getPassword(),
					PasswordOneWayHashUtil.HASH_ALGORITHM_MD5);
			userDigest.setPassword(encryptedPassword);
			user = new User(
					userDigest.getId(),
					userDigest.getName(),
					userDigest.getStatus(),
					userDigest.getPassword(),
					userDigest.getPasswordChangeRequired(),
					userDigest.getFailedLoginCounter());
		}

		int[] roleIDs = userDigest.roleIDs();
		for (int i = 0; i < roleIDs.length; i++) {
			if (SecurityCacheManager.getInstance().getRole(roleIDs[i]) == null) {
				throw new ImportException("Invalid role id " + roleIDs[i]);
			}
			user.add(SecurityCacheManager.getInstance().getRole(roleIDs[i]));
		}
		// validate user objects
		validateUserObject(user);
		return user;
	}

	// TODO Kim, 2007-04-26: replace with validation framework, including info/warning/errors
	private static void validateUserObject(User user) throws ImportException {
		if (user == null) return;
		if (UtilBase.isEmpty(user.getUserID())) throw new ImportException("User not imported, since there is no user ID.");
		if (UtilBase.isEmpty(user.getCurrentPassword())) throw new ImportException("User not imported, since there is no password.");
		if (UtilBase.isEmpty(user.getStatus())) throw new ImportException("User not imported, since there is no status.");
	}

	/**
	 * converts {@link com.mindbox.pe.server.imexport.digest.UserPassword} to {@link com.mindbox.pe.model.admin.UserPassword}
	 * during the import process.
	 * @author vineet khosla
	 * @since PowerEditor 5.1
	 * @param userPasswordList
	 * @return list of {@link com.mindbox.pe.model.admin.UserPassword}
	 */
	private static List<UserPassword> convertUserPassword(List<com.mindbox.pe.server.imexport.digest.UserPassword> userPasswordList) {
		LinkedList<UserPassword> ls = new LinkedList<UserPassword>();
		for (int i = 0; i < userPasswordList.size(); i++) {
			com.mindbox.pe.server.imexport.digest.UserPassword a = userPasswordList.get(i);
			UserPassword up = new UserPassword(a.getEncryptedPassword(), a.getPasswordChangeDate());
			ls.add(up);
		}
		return ls;
	}

	public static String toFilterTypeString(PersistentFilterSpec filter) {
		return (filter.isForGenericEntity() ? GenericEntityType.forID(filter.getEntityTypeID()).toString() : EntityType.forID(
				filter.getEntityTypeID()).toString());
	}

	/*	public static PersistentFilterSpec asFilterSpec(Filter filter) throws ImportException {
			PersistentFilterSpec filterSpec = null;
			GenericEntityType genericEntityType = GenericEntityType.forName(filter.getType());
			if (genericEntityType != null) {
				filterSpec = new GenericEntityFilterSpec(genericEntityType, filter.getId(), filter.getName());
			}
			else {
				if (filter.getType().equals("CBR Case Base")) {
					filterSpec = new NameDescriptionFilterSpec<CBRCaseBase>(EntityType.CBR_CASE_BASE, null, filter.getId(), filter.getName());
				}
				else if (filter.getType().equals("CBR Case")) {
					filterSpec = new NameDescriptionFilterSpec<CBRCase>(EntityType.CBR_CASE, null, filter.getId(), filter.getName());
				}
				else if (filter.getType().equals("CBR Attribute")) {
					filterSpec = new NameDescriptionFilterSpec<CBRAttribute>(EntityType.CBR_ATTRIBUTE, null, filter.getId(), filter.getName());
				}
				else if (filter.getType().equals("GuidelineAction")) {
					filterSpec = new NameDescriptionFilterSpec<ActionTypeDefinition>(EntityType.GUIDELINE_ACTION, null, filter.getId(), filter.getName());
				}
				else if (filter.getType().equals("GuidelineTestCondition")) {
					filterSpec = new NameDescriptionFilterSpec<TestTypeDefinition>(EntityType.GUIDELINE_TEST_CONDITION, null, filter.getId(), filter.getName());
				}
			}
			if (filterSpec != null) {
				if (filter.getCriteria() != null) {
					if (filterSpec.isForGenericEntity()) {
						filterSpec.setInvariants(
								FilterManager.parseFilterParams(filter.getCriteria()),
								ConfigurationManager.getInstance().getEntityConfiguration().findEntityTypeDefinition(genericEntityType));
					}
					else {
						filterSpec.setInvariants(FilterManager.parseFilterParams(filter.getCriteria()));
					}
				}
				return filterSpec;
			}
			else {
				throw new ImportException("Invalid filter type " + filter.getType());
			}
		}
	*/
	/**
	 * Extracts generic entity compatibility data for the specified generic entity. If
	 * <code>merge</code> is <code>true</code>, target ids will be mapped using the specified
	 * id map.
	 * 
	 * @param type
	 *            type of the specified generic entity
	 * @param entity
	 *            generic entity
	 * @param merge
	 *            merge flag
	 * @param idMap
	 *            id map; ignored if <code>merge</code> is <code>false</code>
	 * @return list of {@link GenericEntityCompatibilityData} objects
	 * @throws ImportException
	 *             on error
	 * @throws DataValidationFailedException 
	 * @since 4.5.0
	 */
	public static List<GenericEntityCompatibilityData> extractCompabilityLinks(GenericEntityType type, Entity entity, boolean merge,
			Map<String, Integer> idMap, Map<Integer, Integer> dateSynonymIDMap,
			ReplacementDateSynonymProvider replacementDateSynonymProvider, User user) throws ImportException, DataValidationFailedException {
		List<GenericEntityCompatibilityData> resultList = new ArrayList<GenericEntityCompatibilityData>();
		List<Association> entityLinkList = entity.getAssociations();
		for (int i = 0; i < entityLinkList.size(); i++) {
			Association assc = entityLinkList.get(i);
			GenericEntityType targetType = GenericEntityType.forName(assc.getEntityLink().getType());
			if (type != null && targetType != null) {
				int sourceEntityID = entity.getId();
				if (merge) {
					sourceEntityID = getMappedID(asEntityIDMapKey(type.toString(), sourceEntityID), idMap, assc.getEntityLink().getId());
					assc.getEntityLink().setId(
							getMappedID(
									asEntityIDMapKey(targetType.toString(), assc.getEntityLink().getId()),
									idMap,
									assc.getEntityLink().getId()));
				}
				DateSynonym effDate = (assc.getActivationDates() == null ? null : getEffectiveDateSynonym(
						assc.getActivationDates(),
						dateSynonymIDMap,
						user));
				// If no activation date, create a new one with a date one day earlier than the earliest date synonym
				if (effDate == null) {
					effDate = replacementDateSynonymProvider.getReplacementDateSynonymForImport();
				}
				DateSynonym expDate = (assc.getActivationDates() == null ? null : getExpirationDateSynonym(
						assc.getActivationDates(),
						dateSynonymIDMap,
						user));
				GenericEntityCompatibilityData compData = new GenericEntityCompatibilityData(
						type,
						sourceEntityID,
						targetType,
						assc.getEntityLink().getId(),
						effDate,
						expDate);
				resultList.add(compData);
			}
		}
		return resultList;
	}

	/**
	 * To support importing product categories as generic categories.
	 * 
	 * @param entityType
	 * @param entity
	 * @param merge
	 * @param idMap
	 * @return GenericCategory
	 * @throws ImportException
	 *             on import error
	 * @throws NullPointerException
	 *             if <code>entityType</code>, <code>entity</code> or <code>idMap</code> is
	 *             <code>null</code>
	 * @throws IllegalArgumentException
	 *             if <code>entity.getType()</code> is not <code>"category"</code>.
	 */
	public static GenericCategory asGenericCategory(GenericEntityType entityType, Entity entity, boolean merge, Map<String, Integer> idMap,
			ReplacementDateSynonymProvider replacementDateSynonymProvider) throws ImportException {
		CategoryTypeDefinition ctd = ConfigurationManager.getInstance().getEntityConfiguration().getCategoryDefinition(entityType);
		if (ctd == null) throw new ImportException("No category defined for entity type " + entityType);
		if (!entity.getType().equals("category"))
			throw new IllegalArgumentException("Entity type must be set to category, but was " + entity.getType());
		GenericCategory cat = new GenericCategory((merge ? getRequiredMappedID(
				asCategoryIDMapKey(entityType.toString(), entity.getId()),
				idMap) : entity.getId()), entity.getProperty("name"), ctd.getTypeID());
		int parentID = entity.getParentID();
		if (parentID > 0) {
			parentID = getMappedID(asCategoryIDMapKey(entityType.toString(), parentID), idMap, parentID);
			cat.addParentKey(new DefaultMutableTimedAssociationKey(
					parentID,
					replacementDateSynonymProvider.getReplacementDateSynonymForImport(),
					null));
			cat.setRootIndicator(false);
		}
		else {
			cat.setRootIndicator(true);
		}
		return cat;
	}

	public static int findMappedDateSynonymID(int dateSynonymID, Map<Integer, Integer> dateSynonymIDMap) {
		if (dateSynonymID < 0 || dateSynonymIDMap == null || dateSynonymIDMap.isEmpty()) return dateSynonymID;
		Integer key = new Integer(dateSynonymID);
		if (dateSynonymIDMap.containsKey(key)) {
			return dateSynonymIDMap.get(key).intValue();
		}
		else {
			return dateSynonymID;
		}
	}

	public static GenericCategory asGenericCategory(CategoryDigest categoryDigest, boolean merge, Map<String, Integer> idMap,
			Map<Integer, Integer> dateSynonymIDMap, ReplacementDateSynonymProvider replacementDateSynonymProvider) throws ImportException {
		CategoryTypeDefinition ctd = ConfigurationManager.getInstance().getEntityConfiguration().getCategoryDefinition(
				GenericEntityType.forName(categoryDigest.getType()));
		if (ctd == null) {
			throw new ImportException("No category defined for entity type " + categoryDigest.getType());
		}

		GenericCategory cat = new GenericCategory((merge ? getRequiredMappedID(asCategoryIDMapKey(
				categoryDigest.getType(),
				categoryDigest.getId()), idMap) : categoryDigest.getId()), categoryDigest.getProperty("name"), ctd.getTypeID());

		// add parent associations
		// post 5.0, multiple parents -- check this first
		for (Iterator<Parent> parentIter = categoryDigest.getParents().iterator(); parentIter.hasNext();) {
			Parent parent = parentIter.next();
			int parentId = getMappedID(asCategoryIDMapKey(categoryDigest.getType(), parent.getId()), idMap, parent.getId());

			if (parentId > -1) {
				int effectiveDateId = parent.getActivationDates() == null ? -1 : parent.getActivationDates().getEffectiveDateID();
				DateSynonym effectiveDateSynonym = DateSynonymManager.getInstance().getDateSynonym(
						findMappedDateSynonymID(effectiveDateId, dateSynonymIDMap));
				// If no activation date, create a new one with a date one day earlier than the earliest date synonym
				if (effectiveDateSynonym == null) {
					effectiveDateSynonym = replacementDateSynonymProvider.getReplacementDateSynonymForImport();
				}

				int expirationDateId = parent.getActivationDates() == null ? -1 : parent.getActivationDates().getExpirationDateID();
				DateSynonym expirationDateSynonym = DateSynonymManager.getInstance().getDateSynonym(
						findMappedDateSynonymID(expirationDateId, dateSynonymIDMap));

				cat.addParentKey(new DefaultMutableTimedAssociationKey(parentId, effectiveDateSynonym, expirationDateSynonym));
			}
		}

		// pre 5.0, single parent -- do this only if parent list is empty
		if (categoryDigest.getParents().isEmpty() && categoryDigest.getParentID() > -1) {
			int parentId = getMappedID(
					asCategoryIDMapKey(categoryDigest.getType(), categoryDigest.getParentID()),
					idMap,
					categoryDigest.getParentID());
			cat.addParentKey(new DefaultMutableTimedAssociationKey(
					parentId,
					replacementDateSynonymProvider.getReplacementDateSynonymForImport(),
					null));
		}
		cat.setRootIndicator(cat.isRoot());
		return cat;
	}

	public static GenericEntity asGenericEntity(GenericEntityType type, Entity entity, boolean merge, Map<String, Integer> idMap,
			Map<Integer, Integer> dateSynonymIDMap, ReplacementDateSynonymProvider replacementDateSynonymProvider) throws ImportException {
		GenericEntity genericEntity = new GenericEntity((merge ? getRequiredMappedID(
				asEntityIDMapKey(type.toString(), entity.getId()),
				idMap) : entity.getId()), type, entity.getProperty("name"));
		Map<String, Object> propertyMap = new HashMap<String, Object>();

		EntityTypeDefinition entityTypeDef = ConfigurationManager.getInstance().getEntityConfiguration().findEntityTypeDefinition(type);
		if (entityTypeDef != null) {
			EntityPropertyDefinition[] propDefs = entityTypeDef.getEntityPropertyDefinitions();
			for (int i = 0; i < propDefs.length; i++) {
				String propName = propDefs[i].getName();
				String value = entity.getProperty(propName);
				propertyMap.put(propName, value);
			}
		}
		EntityManager.setGenericEntityProperties(genericEntity, propertyMap);

		// Load generic category links
		CategoryTypeDefinition catTypeDef = ConfigurationManager.getInstance().getEntityConfiguration().getCategoryDefinition(type);
		List<Association> entityLinkList = entity.getAssociations();
		for (int i = 0; i < entityLinkList.size(); i++) {
			Association assc = entityLinkList.get(i);
			if (assc.getEntityLink().getType().equals("category")) {
				if (catTypeDef == null) throw new ImportException("Entity of type " + type + " does not support categories.");
				if (merge) {
					assc.getEntityLink().setId(
							getMappedID(
									asCategoryIDMapKey(type.toString(), assc.getEntityLink().getId()),
									idMap,
									assc.getEntityLink().getId()));
				}
				// this check requires that generic categories are imported before generic entities
				if (EntityManager.getInstance().hasGenericCategory(catTypeDef.getTypeID(), assc.getEntityLink().getId())) {
					int categoryId = getMappedID(
							asCategoryIDMapKey(catTypeDef.getName(), assc.getEntityLink().getId()),
							idMap,
							assc.getEntityLink().getId());

					int effectiveDateId = assc.getActivationDates() == null ? -1 : assc.getActivationDates().getEffectiveDateID();
					DateSynonym effectiveDateSynonym = DateSynonymManager.getInstance().getDateSynonym(
							findMappedDateSynonymID(effectiveDateId, dateSynonymIDMap));
					// If no activation date, create a new one with a date one day earlier than the earliest date synonym
					if (effectiveDateSynonym == null) {
						effectiveDateSynonym = replacementDateSynonymProvider.getReplacementDateSynonymForImport();
					}

					int expirationDateId = assc.getActivationDates() == null ? -1 : assc.getActivationDates().getExpirationDateID();
					DateSynonym expirationDateSynonym = DateSynonymManager.getInstance().getDateSynonym(
							findMappedDateSynonymID(expirationDateId, dateSynonymIDMap));

					MutableTimedAssociationKey key = new DefaultMutableTimedAssociationKey(
							categoryId,
							effectiveDateSynonym,
							expirationDateSynonym);
					if (!entityTypeDef.canBelongToMultipleCategories() && genericEntity.hasOverlappingCategoryAssociation(key)) {
						throw new ImportException("Entities of type '" + entityTypeDef.getName()
								+ "' can belong to only one category at a time. The entity " + genericEntity.getName()
								+ " has an existing category association that overlaps with the category ID " + categoryId);
					}
					else {
						genericEntity.addCategoryAssociation(key);
					}
				}
				else {
					throw new ImportException("entity-link id " + assc.getEntityLink().getId() + " of type "
							+ assc.getEntityLink().getType() + " not found");
				}
			}
		}
		return genericEntity;
	}

	private ObjectConverter() {
	}
}