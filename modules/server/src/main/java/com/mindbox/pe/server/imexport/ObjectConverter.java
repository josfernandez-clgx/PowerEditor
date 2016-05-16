package com.mindbox.pe.server.imexport;

import static com.mindbox.pe.common.LogUtil.logDebug;
import static com.mindbox.pe.common.LogUtil.logInfo;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.MutableBoolean;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.admin.UserPassword;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;
import com.mindbox.pe.model.cbr.CBRAttribute;
import com.mindbox.pe.model.cbr.CBRAttributeValue;
import com.mindbox.pe.model.cbr.CBRCase;
import com.mindbox.pe.model.cbr.CBRCaseBase;
import com.mindbox.pe.model.cbr.CBREnumeratedValue;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.exceptions.SapphireException;
import com.mindbox.pe.model.filter.PersistentFilterSpec;
import com.mindbox.pe.model.grid.AbstractGrid;
import com.mindbox.pe.model.grid.AbstractGuidelineGrid;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.FunctionParameterDefinition;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.TestTypeDefinition;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.ParameterTemplate;
import com.mindbox.pe.server.ServerContextUtil;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.server.bizlogic.GridActionCoordinator;
import com.mindbox.pe.server.cache.CBRManager;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.cache.ParameterTemplateManager;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletActionException;
import com.mindbox.pe.xsd.config.CategoryType;
import com.mindbox.pe.xsd.config.EntityProperty;
import com.mindbox.pe.xsd.config.EntityType;
import com.mindbox.pe.xsd.data.ActionParameters.Parameter;
import com.mindbox.pe.xsd.data.CBRAttributeElement;
import com.mindbox.pe.xsd.data.CBRAttributeElement.EnumValues.EnumValue;
import com.mindbox.pe.xsd.data.CBRAttributeValueElement;
import com.mindbox.pe.xsd.data.CBRCaseBaseElement;
import com.mindbox.pe.xsd.data.CBRCaseElement;
import com.mindbox.pe.xsd.data.DateDataElement.DateElement;
import com.mindbox.pe.xsd.data.EntityBase;
import com.mindbox.pe.xsd.data.EntityDataElement.Category;
import com.mindbox.pe.xsd.data.EntityLink;
import com.mindbox.pe.xsd.data.GridActivationElement;
import com.mindbox.pe.xsd.data.GridActivationElement.GridValues.Row;
import com.mindbox.pe.xsd.data.GuidelineActionDataElement.GuidelineAction;
import com.mindbox.pe.xsd.data.PropertyElement;
import com.mindbox.pe.xsd.data.TestConditionDataElement.TestCondition;
import com.mindbox.server.parser.jtb.rule.ParseException;
import com.mindbox.server.parser.jtb.rule.RuleParser;
import com.mindbox.server.parser.jtb.rule.syntaxtree.DeploymentRule;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public final class ObjectConverter {

	private static final Logger LOG = Logger.getLogger(ObjectConverter.class);

	/**
	 * If there is a list of grids that have the same data as the specified grid list, merge context.
	 * Each grid in the grid list must match for merge.
	 * @param <T> grid type
	 * @param gridListList gridListList
	 * @param gridList gridList
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

	public static ActionTypeDefinition asActionTypeDefinition(final GuidelineAction guidelineAction) {
		final ActionTypeDefinition actionTypeDefinition = new ActionTypeDefinition();
		actionTypeDefinition.setDeploymentRule(guidelineAction.getDeploymentRule());
		actionTypeDefinition.setDescription(guidelineAction.getDescription());
		actionTypeDefinition.setID(guidelineAction.getId());
		actionTypeDefinition.setName(guidelineAction.getName());
		// set parameters
		if (guidelineAction.getParameters() != null) {
			for (final Parameter parameter : guidelineAction.getParameters().getParameter()) {
				final FunctionParameterDefinition functionParameterDefinition = new FunctionParameterDefinition();
				functionParameterDefinition.setDeployType(DeployType.valueOf(parameter.getDeployType()));
				functionParameterDefinition.setID(parameter.getId());
				functionParameterDefinition.setName(parameter.getName());
				functionParameterDefinition.setParamDataString(parameter.getDataString());
				actionTypeDefinition.addParameterDefinition(functionParameterDefinition);
			}
		}

		// TT-82 set usage types
		for (final String usageTypeName : guidelineAction.getUsage()) {
			actionTypeDefinition.addUsageTypeString(usageTypeName);
		}
		return actionTypeDefinition;
	}

	/**
	 * Generates a unique key for id map for the specified category type and id.
	 * 
	 * @param type type
	 *            category type
	 * @param id id
	 *            category id
	 * @return unique key
	 */
	public static String asCategoryIDMapKey(String type, int id) {
		return "C|" + type + ":" + id;
	}

	public static CBRAttribute asCbrAttribute(final CBRAttributeElement cbrAttributeElement) {
		final CBRAttribute cbrAttribute = new CBRAttribute();
		cbrAttribute.setAbsencePenalty(cbrAttributeElement.getAbsencePenalty());
		cbrAttribute.setAttributeType(CBRManager.getInstance().getCBRAttributeType(cbrAttributeElement.getAttributeType().getId()));
		cbrAttribute.setCaseBase(CBRManager.getInstance().getCBRCaseBase(cbrAttributeElement.getCaseBase().getId()));
		cbrAttribute.setDescription(cbrAttributeElement.getDescription());
		cbrAttribute.setHighestValue(cbrAttributeElement.getHighestValue());
		cbrAttribute.setID(cbrAttributeElement.getId());
		cbrAttribute.setLowestValue(cbrAttributeElement.getLowestValue());
		cbrAttribute.setMatchContribution(cbrAttributeElement.getMatchContribution());
		cbrAttribute.setMatchInterval(cbrAttributeElement.getMatchInterval());
		cbrAttribute.setMismatchPenalty(cbrAttributeElement.getMismatchPenalty());
		cbrAttribute.setName(cbrAttributeElement.getName());
		if (cbrAttributeElement.getValueRange() != null && cbrAttributeElement.getValueRange().getId() != null) {
			cbrAttribute.setValueRange(CBRManager.getInstance().getCBRValueRange(cbrAttributeElement.getValueRange().getId()));
		}
		final List<CBREnumeratedValue> cbrEnumeratedValues = new ArrayList<CBREnumeratedValue>();
		for (final EnumValue enumValue : cbrAttributeElement.getEnumValues().getEnumValue()) {
			final CBREnumeratedValue cbrEnumeratedValue = new CBREnumeratedValue();
			cbrEnumeratedValue.setID(enumValue.getId());
			cbrEnumeratedValue.setName(enumValue.getName());
			cbrEnumeratedValues.add(cbrEnumeratedValue);
		}
		cbrAttribute.setEnumeratedValues(cbrEnumeratedValues);
		return cbrAttribute;
	}

	private static CBRAttributeValue asCBRAttributeValue(final CBRAttributeValueElement attributeValueElement) {
		final CBRAttributeValue cbrAttributeValue = new CBRAttributeValue();
		cbrAttributeValue.setAttribute(CBRManager.getInstance().getCBRAttribute(attributeValueElement.getAttribute().getId()));
		cbrAttributeValue.setDescription(attributeValueElement.getDescription());
		cbrAttributeValue.setID(attributeValueElement.getId());
		cbrAttributeValue.setMatchContribution(attributeValueElement.getMatchContribution());
		cbrAttributeValue.setMismatchPenalty(attributeValueElement.getMismatchPenalty());
		cbrAttributeValue.setName(attributeValueElement.getValue());
		return cbrAttributeValue;
	}

	public static CBRCase asCbrCase(final CBRCaseElement cbrCaseElement) {
		final CBRCase cbrCase = new CBRCase();
		cbrCase.setCaseBase(CBRManager.getInstance().getCBRCaseBase(cbrCaseElement.getCaseBase().getId()));
		cbrCase.setDescription(cbrCaseElement.getDescription());
		if (cbrCaseElement.getActivationDates() != null) {
			if (cbrCaseElement.getActivationDates().getEffectiveDateID() != null) {
				cbrCase.setEffectiveDate(DateSynonymManager.getInstance().getDateSynonym(cbrCaseElement.getActivationDates().getEffectiveDateID()));
			}
			if (cbrCaseElement.getActivationDates().getExpirationDateID() != null) {
				cbrCase.setExpirationDate(DateSynonymManager.getInstance().getDateSynonym(cbrCaseElement.getActivationDates().getExpirationDateID()));
			}
		}
		cbrCase.setID(cbrCaseElement.getId());
		cbrCase.setName(cbrCaseElement.getName());
		final List<CBRAttributeValue> cbrAttributeValues = new ArrayList<CBRAttributeValue>();
		for (final CBRAttributeValueElement attributeValueElement : cbrCaseElement.getAttributeValues().getAttributeValue()) {
			cbrAttributeValues.add(asCBRAttributeValue(attributeValueElement));
		}
		cbrCase.setAttributeValues(cbrAttributeValues);
		return cbrCase;
	}

	public static CBRCaseBase asCbrCaseBase(final CBRCaseBaseElement cbrCaseBaseElement) {
		final CBRCaseBase cbrCaseBase = new CBRCaseBase();
		cbrCaseBase.setCaseClass(CBRManager.getInstance().getCBRCaseClass(cbrCaseBaseElement.getCaseClass().getId()));
		cbrCaseBase.setDescription(cbrCaseBaseElement.getDescription());
		if (cbrCaseBaseElement.getActivationDates() != null) {
			if (cbrCaseBaseElement.getActivationDates().getEffectiveDateID() != null) {
				cbrCaseBase.setEffectiveDate(DateSynonymManager.getInstance().getDateSynonym(cbrCaseBaseElement.getActivationDates().getEffectiveDateID()));
			}
			if (cbrCaseBaseElement.getActivationDates().getExpirationDateID() != null) {
				cbrCaseBase.setExpirationDate(DateSynonymManager.getInstance().getDateSynonym(cbrCaseBaseElement.getActivationDates().getExpirationDateID()));
			}
		}
		cbrCaseBase.setID(cbrCaseBaseElement.getId());
		cbrCaseBase.setIndexFile(cbrCaseBaseElement.getIndexFile());
		cbrCaseBase.setMatchThreshold(cbrCaseBaseElement.getMatchThreshold());
		cbrCaseBase.setMaximumMatches(cbrCaseBaseElement.getMaximumMatches());
		cbrCaseBase.setName(cbrCaseBaseElement.getName());
		cbrCaseBase.setNamingAttribute(cbrCaseBaseElement.getNamingAttribute());
		if (cbrCaseBaseElement.getScoringFunction() != null && cbrCaseBaseElement.getScoringFunction().getId() != null) {
			cbrCaseBase.setScoringFunction(CBRManager.getInstance().getCBRScoringFunction(cbrCaseBaseElement.getScoringFunction().getId()));
		}
		return cbrCaseBase;
	}

	public static DateSynonym asDateSynonym(final DateElement dateElement) {
		final DateSynonym dateSynonym = new DateSynonym();
		dateSynonym.setDate(dateElement.getDate());
		dateSynonym.setDescription(dateElement.getDescription());
		dateSynonym.setID(dateElement.getId());
		dateSynonym.setName(dateElement.getName());
		return dateSynonym;
	}

	/**
	 * Generates a unique key for id map for the specified entity type and id.
	 * 
	 * @param type type
	 *            entity type
	 * @param id id
	 *            category id
	 * @return unique key
	 */
	public static String asEntityIDMapKey(String type, int id) {
		return type + ":" + id;
	}

	public static GenericCategory asGenericCategory(final Category category, final boolean merge, final Map<String, Integer> idMap, final Map<Integer, Integer> dateSynonymIDMap,
			final ReplacementDateSynonymProvider replacementDateSynonymProvider) throws ImportException {
		CategoryType ctd = ConfigurationManager.getInstance().getEntityConfigHelper().getCategoryDefinition(GenericEntityType.forName(category.getType()));
		if (ctd == null) {
			throw new ImportException("No category defined for entity type " + category.getType());
		}

		final GenericCategory genericCategory = new GenericCategory(
				(merge ? getRequiredMappedID(asCategoryIDMapKey(category.getType(), category.getId()), idMap) : category.getId()),
				getProperty(category, "name"),
				ctd.getTypeID().intValue());

		// add parent associations
		// post 5.0, multiple parents -- check this first
		for (final com.mindbox.pe.xsd.data.EntityDataElement.Category.Parent parent : category.getParent()) {
			int parentId = getMappedID(asCategoryIDMapKey(category.getType(), parent.getParentID()), idMap, parent.getParentID());

			if (parentId > -1) {
				int effectiveDateId = parent.getActivationDates() == null || parent.getActivationDates().getEffectiveDateID() == null
						? -1
						: parent.getActivationDates().getEffectiveDateID();
				DateSynonym effectiveDateSynonym = DateSynonymManager.getInstance().getDateSynonym(findMappedDateSynonymID(effectiveDateId, dateSynonymIDMap));
				// If no activation date, create a new one with a date one day earlier than the earliest date synonym
				if (effectiveDateSynonym == null) {
					effectiveDateSynonym = replacementDateSynonymProvider.getReplacementDateSynonymForImport();
				}

				int expirationDateId = parent.getActivationDates() == null || parent.getActivationDates().getExpirationDateID() == null
						? -1
						: parent.getActivationDates().getExpirationDateID();
				DateSynonym expirationDateSynonym = DateSynonymManager.getInstance().getDateSynonym(findMappedDateSynonymID(expirationDateId, dateSynonymIDMap));

				genericCategory.addParentKey(new DefaultMutableTimedAssociationKey(parentId, effectiveDateSynonym, expirationDateSynonym));
			}
		}

		genericCategory.setRootIndicator(genericCategory.isRoot());
		return genericCategory;
	}

	public static GenericCategory asGenericCategory(final GenericEntityType entityType, final com.mindbox.pe.xsd.data.EntityDataElement.Entity entity, final boolean merge,
			final Map<String, Integer> idMap, final ReplacementDateSynonymProvider replacementDateSynonymProvider) throws ImportException {
		final CategoryType ctd = ConfigurationManager.getInstance().getEntityConfigHelper().getCategoryDefinition(entityType);
		if (ctd == null) {
			throw new ImportException("No category defined for entity type " + entityType);
		}
		if (!entity.getType().equals("category")) {
			throw new IllegalArgumentException("Entity type must be set to category, but was " + entity.getType());
		}
		final GenericCategory cat = new GenericCategory(
				(merge ? getRequiredMappedID(asCategoryIDMapKey(entityType.toString(), entity.getId()), idMap) : entity.getId()),
				getProperty(entity, "name"),
				ctd.getTypeID().intValue());
		int parentID = entity.getParentID();
		if (parentID > 0) {
			parentID = getMappedID(asCategoryIDMapKey(entityType.toString(), parentID), idMap, parentID);
			cat.addParentKey(new DefaultMutableTimedAssociationKey(parentID, replacementDateSynonymProvider.getReplacementDateSynonymForImport(), null));
			cat.setRootIndicator(false);
		}
		else {
			cat.setRootIndicator(true);
		}
		return cat;
	}

	public static GenericEntity asGenericEntity(final GenericEntityType type, final com.mindbox.pe.xsd.data.EntityDataElement.Entity entity, final boolean merge,
			final Map<String, Integer> idMap, final Map<Integer, Integer> dateSynonymIDMap, final ReplacementDateSynonymProvider replacementDateSynonymProvider)
			throws ImportException {
		final GenericEntity genericEntity = new GenericEntity(
				(merge ? getRequiredMappedID(asEntityIDMapKey(type.toString(), entity.getId()), idMap) : entity.getId()),
				type,
				getProperty(entity, "name"));
		final Map<String, Object> propertyMap = new HashMap<String, Object>();

		final EntityType entityTypeDef = ConfigurationManager.getInstance().getEntityConfigHelper().findEntityTypeDefinition(type);
		if (entityTypeDef != null) {
			for (final EntityProperty property : entityTypeDef.getEntityProperty()) {
				String propName = property.getName();
				String value = getProperty(entity, propName);
				propertyMap.put(propName, value);
			}
		}
		EntityManager.setGenericEntityProperties(genericEntity, propertyMap);

		// Load generic category links
		final CategoryType catTypeDef = ConfigurationManager.getInstance().getEntityConfigHelper().getCategoryDefinition(type);
		for (final com.mindbox.pe.xsd.data.EntityDataElement.Entity.Association assc : entity.getAssociation()) {
			if (assc.getEntityLink().getType().equals("category")) {
				if (catTypeDef == null) {
					throw new ImportException("Entity of type " + type + " does not support categories.");
				}
				if (merge) {
					assc.getEntityLink().setId(getMappedID(asCategoryIDMapKey(type.toString(), assc.getEntityLink().getId()), idMap, assc.getEntityLink().getId()));
				}
				// this check requires that generic categories are imported before generic entities
				if (EntityManager.getInstance().hasGenericCategory(catTypeDef.getTypeID().intValue(), assc.getEntityLink().getId())) {
					int categoryId = getMappedID(asCategoryIDMapKey(catTypeDef.getName(), assc.getEntityLink().getId()), idMap, assc.getEntityLink().getId());

					int effectiveDateId = assc.getActivationDates() == null || assc.getActivationDates().getEffectiveDateID() == null
							? -1
							: assc.getActivationDates().getEffectiveDateID();
					DateSynonym effectiveDateSynonym = DateSynonymManager.getInstance().getDateSynonym(findMappedDateSynonymID(effectiveDateId, dateSynonymIDMap));
					// If no activation date, create a new one with a date one day earlier than the earliest date synonym
					if (effectiveDateSynonym == null) {
						effectiveDateSynonym = replacementDateSynonymProvider.getReplacementDateSynonymForImport();
					}

					int expirationDateId = assc.getActivationDates() == null || assc.getActivationDates().getExpirationDateID() == null
							? -1
							: assc.getActivationDates().getExpirationDateID();
					DateSynonym expirationDateSynonym = DateSynonymManager.getInstance().getDateSynonym(findMappedDateSynonymID(expirationDateId, dateSynonymIDMap));

					MutableTimedAssociationKey key = new DefaultMutableTimedAssociationKey(categoryId, effectiveDateSynonym, expirationDateSynonym);
					if (!ConfigUtil.isCanBelongToMultipleCategories(entityTypeDef) && genericEntity.hasOverlappingCategoryAssociation(key)) {
						throw new ImportException(
								"Entities of type '" + entityTypeDef.getName() + "' can belong to only one category at a time. The entity " + genericEntity.getName()
										+ " has an existing category association that overlaps with the category ID " + categoryId);
					}
					else {
						genericEntity.addCategoryAssociation(key);
					}
				}
				else {
					throw new ImportException("entity-link id " + assc.getEntityLink().getId() + " of type " + assc.getEntityLink().getType() + " not found");
				}
			}
		}
		return genericEntity;
	}

	public static List<ProductGrid> asGuidelineGridList(com.mindbox.pe.xsd.data.GridDataElement.Grid gridDigest, GuidelineContext[] context, boolean merge, User user,
			Map<Integer, Integer> dateSynonymIDMap, Map<String, Integer> entityIDMap, ReplacementDateSynonymProvider replacementDateSynonymProvider)
			throws ImportException, DataValidationFailedException {
		final List<ProductGrid> gridList = new ArrayList<ProductGrid>();
		for (final GridActivationElement element : gridDigest.getActivation()) {
			logDebug(LOG, "Processing GridActivationElement %s (%s)", element.getId(), element);
			com.mindbox.pe.xsd.data.ActivationDates actDates = element.getActivationDates();

			final GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(gridDigest.getTemplateID());
			if (template == null) {
				throw new ImportException("Invalid template id: " + gridDigest.getTemplateID());
			}
			if (dateSynonymIDMap != null && actDates != null) {
				Integer newID = actDates.getEffectiveDateID() == null ? null : dateSynonymIDMap.get(actDates.getEffectiveDateID());
				if (newID != null) {
					actDates.setEffectiveDateID(newID.intValue());
				}
				newID = actDates.getExpirationDateID() == null ? null : dateSynonymIDMap.get(actDates.getExpirationDateID());
				if (newID != null) {
					actDates.setExpirationDateID(newID.intValue());
				}
			}
			final boolean hasGrid = GridManager.getInstance().hasGrid(element.getId(), gridDigest.getTemplateID());
			int gridID = (merge ? -1 : hasGrid ? element.getId() : -1);
			DateSynonym effectiveDateSynonym = (actDates == null ? null : getEffectiveDateSynonym(actDates, dateSynonymIDMap, user));
			// If no activation date, create a new one with a date one day earlier than the earliest date synonym
			if (effectiveDateSynonym == null) {
				effectiveDateSynonym = replacementDateSynonymProvider.getReplacementDateSynonymForImport();
			}

			final ProductGrid grid = new ProductGrid(
					gridID,
					template,
					effectiveDateSynonym,
					(actDates == null ? null : getExpirationDateSynonym(actDates, dateSynonymIDMap, user)));
			setInvariants(grid, element, context, extractCellValues(GuidelineTemplateManager.getInstance().getTemplate(gridDigest.getTemplateID()), element, gridDigest));
			fixEntityListColumnValues(grid, entityIDMap, merge);
			// if not-merge and grid exists, overwrite rule ids from existing grid
			if (hasGrid && !merge) {
				replaceRuleIDs(grid, GridManager.getInstance().getProductGrid(grid.getID()));
			}
			gridList.add(grid);
		}
		return gridList;
	}


	public static List<ParameterGrid> asParameterGridList(com.mindbox.pe.xsd.data.GridDataElement.Grid gridDigest, GuidelineContext[] context, User user,
			Map<Integer, Integer> dateSynonymIDMap, ReplacementDateSynonymProvider replacementDateSynonymProvider) throws ImportException, DataValidationFailedException {
		final List<ParameterGrid> gridList = new ArrayList<ParameterGrid>();
		for (final GridActivationElement element : gridDigest.getActivation()) {
			final com.mindbox.pe.xsd.data.ActivationDates actDates = element.getActivationDates();

			if (dateSynonymIDMap != null && actDates != null) {
				Integer newID = actDates.getEffectiveDateID() == null ? null : dateSynonymIDMap.get(actDates.getEffectiveDateID());
				if (newID != null) {
					actDates.setEffectiveDateID(newID.intValue());
				}
				newID = actDates.getExpirationDateID() == null ? null : dateSynonymIDMap.get(actDates.getExpirationDateID());
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
			ParameterGrid grid = new ParameterGrid(
					element.getId(),
					gridDigest.getTemplateID(),
					effectiveDateSynonym,
					(actDates == null ? null : getExpirationDateSynonym(actDates, dateSynonymIDMap, user)));
			setInvariants(grid, element, context, extractCellValues(template, element, gridDigest));
			grid.setTemplate(template);
			gridList.add(grid);
		}
		return gridList;
	}

	public static Role asRole(final com.mindbox.pe.xsd.data.RolesElement.Role roleDigest, final List<com.mindbox.pe.xsd.data.PrivilegesElement.Privilege> digestPrivList)
			throws ImportException {
		// build list of model.Privilege objects for digested Role...
		final List<Privilege> privList = new ArrayList<Privilege>();
		for (final int privilegeID : roleDigest.getPrivilegeLink()) {
			// Since 5.0 EntityType and UsageType privilege ids are dynamically generated when the PE config file is parsed at server start up.
			// Therefore, we can no longer rely on the finding privs by the digested priv id.  Rather, we must search for existing privs by name.
			final String digestPrivName = getPrivilegeName(privilegeID, digestPrivList);

			if (UtilBase.isEmpty(digestPrivName)) {
				LOG.warn("Ignored privilege " + privilegeID + " for " + roleDigest.getName() + " role, privilege not found in import file.");
				continue;
			}

			Privilege priv = SecurityCacheManager.getInstance().findPrivilegeByName(digestPrivName);
			if (priv == null) {
				LOG.warn("Ignored privilege " + privilegeID + "=" + digestPrivName + " for " + roleDigest.getName() + " role: privilege not found");
				continue;
			}
			if (!privList.contains(priv)) {
				privList.add(priv);
			}
		}

		// ...then, with the priv list, create the Role
		return new Role(roleDigest.getId(), roleDigest.getName(), privList);
	}

	public static TestTypeDefinition asTestTypeDefinition(TestCondition testCondition) {
		final TestTypeDefinition testTypeDefinition = new TestTypeDefinition();
		testTypeDefinition.setDeploymentRule(testCondition.getDeploymentRule());
		testTypeDefinition.setDescription(testCondition.getDescription());
		testTypeDefinition.setID(testCondition.getId());
		testTypeDefinition.setName(testCondition.getName());
		// set parameters
		if (testCondition.getParameters() != null) {
			for (final Parameter parameter : testCondition.getParameters().getParameter()) {
				final FunctionParameterDefinition functionParameterDefinition = new FunctionParameterDefinition();
				functionParameterDefinition.setDeployType(DeployType.valueOf(parameter.getDeployType()));
				functionParameterDefinition.setID(parameter.getId());
				functionParameterDefinition.setName(parameter.getName());
				functionParameterDefinition.setParamDataString(parameter.getDataString());
				testTypeDefinition.addParameterDefinition(functionParameterDefinition);
			}
		}
		return testTypeDefinition;
	}

	public static User asUser(com.mindbox.pe.xsd.data.UsersElement.User userDigest) throws ImportException {
		final User user = new User(
				userDigest.getId(),
				userDigest.getName(),
				userDigest.getStatus().value(),
				userDigest.isPasswordChangeRequired(),
				userDigest.getFailedLoginCounter(),
				null,
				convertUserPassword(userDigest.getUserPassword()));

		for (final int roleID : userDigest.getRoleLink()) {
			if (SecurityCacheManager.getInstance().getRole(roleID) == null) {
				throw new ImportException("Invalid role id " + roleID);
			}
			user.add(SecurityCacheManager.getInstance().getRole(roleID));
		}
		// validate user objects
		validateUserObject(user);
		return user;
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
					if (!mergeToContext[j].hasCategoryContext() && mergeToContext[j].getGenericEntityType().getCategoryType() == mergeFromContext[i].getGenericCategoryType()) {
						return false;
					}
				}
			}
			else {
				for (int j = 0; j < mergeToContext.length; j++) {
					// if mergeToContext has a generic category context of the same type, cannot merge
					if (mergeToContext[j].hasCategoryContext() && mergeToContext[j].getGenericCategoryType() == mergeFromContext[i].getGenericEntityType().getCategoryType()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public static RuleDefinition convertToRuleDefinition(GridTemplate template, int colNo, String ruleName, String ruleString, MutableBoolean wasActionCreated, User user)
			throws ParseException, SapphireException {
		RuleParser.getInstance(new StringReader(ruleString));
		DeploymentRule deploymentRule = RuleParser.parseDeploymentRule();

		RuleDefinition ruleDef = RuleGeneratorHelper.toRuleDefinition(template, colNo, ruleName, deploymentRule, wasActionCreated, user);
		return ruleDef;
	}

	/**
	 * converts {@link com.mindbox.pe.server.imexport.digest.UserPassword} to {@link com.mindbox.pe.model.admin.UserPassword}
	 * during the import process.
	 * @author vineet khosla
	 * @since PowerEditor 5.1
	 * @param userPasswordList userPasswordList
	 * @return list of {@link com.mindbox.pe.model.admin.UserPassword}
	 */
	private static List<UserPassword> convertUserPassword(List<com.mindbox.pe.xsd.data.UsersElement.User.UserPassword> userPasswordList) {
		final LinkedList<UserPassword> list = new LinkedList<UserPassword>();
		for (final com.mindbox.pe.xsd.data.UsersElement.User.UserPassword userPassword : userPasswordList) {
			UserPassword up = new UserPassword(userPassword.getEncryptedPassword(), userPassword.getPasswordChangeDate() == null ? null : userPassword.getPasswordChangeDate());
			list.add(up);
		}
		return list;
	}

	private static String[][] extractCellValues(GridTemplate template, GridActivationElement activation, com.mindbox.pe.xsd.data.GridDataElement.Grid gridDigest)
			throws ImportException {
		if (template == null) {
			throw new ImportException("template not found");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug(">>> extractCellValues: " + template + ", " + gridDigest);
			LOG.debug(
					"... extractCellValues: template usage type value = " + template.getUsageType().toString() + ", template usage display name = "
							+ template.getUsageType().getDisplayName());
		}

		if (activation.getGridValues() == null || activation.getGridValues().getRow() == null) {
			logInfo(LOG, "no rows found; importing empty grid...");
			return null;
		}
		else {
			final List<Row> rows = activation.getGridValues().getRow();
			logDebug(LOG, "... extractCellValues: rows = %s", rows.size());

			String[][] values = new String[rows.size()][template.getNumColumns()];
			for (int i = 0; i < values.length; i++) {
				for (int j = 0; j < values[i].length; j++) {
					final int translatedColumn = translateColumnNumber(template, j + 1, gridDigest);
					if (translatedColumn < 0) {
						throw new ImportException("Failed to find cell value for (" + (i + 1) + "," + (j + 1) + ") - column not found");
					}

					logDebug(LOG, "... extractCellValues: translatedColumn=%d,column=%s", translatedColumn, template.getColumn(translatedColumn));

					String valToTranslate = (rows.get(i) == null ? null : getCellValueAt(rows.get(i), translatedColumn));
					if (valToTranslate != null) {
						values[i][j] = valToTranslate;
					}
					if (values[i][j] == null) {
						values[i][j] = "";
					}

					logDebug(LOG, "... extractCellValues: set[%d][%d]=%s", i, j, values[i][j]);
				}
			}
			LOG.debug("<<< extractCellValues:");
			return values;
		}
	}

	private static String[][] extractCellValues(ParameterTemplate template, GridActivationElement activation, com.mindbox.pe.xsd.data.GridDataElement.Grid gridDigest)
			throws ImportException {
		if (template == null) {
			throw new ImportException("template not found");
		}

		final List<Row> rows = activation.getGridValues().getRow();
		LOG.debug("... extractCellValues: rows = " + rows.size());

		String[][] values = new String[rows.size()][template.getNumColumns()];
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[i].length; j++) {
				int translatedColumn = translateColumnNumber(template, j + 1, gridDigest);
				if (translatedColumn == -1) translatedColumn = j + 1;
				values[i][j] = (rows.get(i) == null ? null : getCellValueAt(rows.get(i), translatedColumn));
				if (values[i][j] == null) {
					values[i][j] = "";
				}
			}
		}

		return values;
	}

	/**
	 * Extracts generic entity compatibility data for the specified generic entity. If
	 * <code>merge</code> is <code>true</code>, target ids will be mapped using the specified
	 * id map.
	 * 
	 * @param type type of the specified generic entity
	 * @param entity entity
	 * @param merge  merge flag
	 * @param idMap id map; ignored if <code>merge</code> is <code>false</code>
	 * @param dateSynonymIDMap dateSynonymIDMap
	 * @param replacementDateSynonymProvider replacementDateSynonymProvider
	 * @param user user
	 * @return list of {@link GenericEntityCompatibilityData} objects
	 * @throws ImportException on error
	 * @throws DataValidationFailedException on error
	 * @since 4.5.0
	 */
	public static List<GenericEntityCompatibilityData> extractCompabilityLinks(final GenericEntityType type, final com.mindbox.pe.xsd.data.EntityDataElement.Entity entity,
			final boolean merge, Map<String, Integer> idMap, final Map<Integer, Integer> dateSynonymIDMap, final ReplacementDateSynonymProvider replacementDateSynonymProvider,
			final User user) throws ImportException, DataValidationFailedException {
		final List<GenericEntityCompatibilityData> resultList = new ArrayList<GenericEntityCompatibilityData>();
		for (final com.mindbox.pe.xsd.data.EntityDataElement.Entity.Association assc : entity.getAssociation()) {
			GenericEntityType targetType = GenericEntityType.forName(assc.getEntityLink().getType());
			if (type != null && targetType != null) {
				int sourceEntityID = entity.getId();
				if (merge) {
					sourceEntityID = getMappedID(asEntityIDMapKey(type.toString(), sourceEntityID), idMap, assc.getEntityLink().getId());
					assc.getEntityLink().setId(getMappedID(asEntityIDMapKey(targetType.toString(), assc.getEntityLink().getId()), idMap, assc.getEntityLink().getId()));
				}
				DateSynonym effDate = (assc.getActivationDates() == null ? null : getEffectiveDateSynonym(assc.getActivationDates(), dateSynonymIDMap, user));
				// If no activation date, create a new one with a date one day earlier than the earliest date synonym
				if (effDate == null) {
					effDate = replacementDateSynonymProvider.getReplacementDateSynonymForImport();
				}
				DateSynonym expDate = (assc.getActivationDates() == null ? null : getExpirationDateSynonym(assc.getActivationDates(), dateSynonymIDMap, user));
				GenericEntityCompatibilityData compData = new GenericEntityCompatibilityData(type, sourceEntityID, targetType, assc.getEntityLink().getId(), effDate, expDate);
				resultList.add(compData);
			}
		}
		return resultList;
	}

	public static GuidelineContext[] fetchContext(com.mindbox.pe.xsd.data.GridDataElement.Grid gridDigest, boolean merge, Map<String, Integer> entityIDMap) throws ImportException {
		final List<GuidelineContext> resultList = new ArrayList<GuidelineContext>();
		final List<EntityLink> contextList = gridDigest.getContext().getEntityLink();
		GuidelineContext context = null;
		GenericEntityType[] types = GenericEntityType.getAllGenericEntityTypes();
		for (int i = 0; i < types.length; i++) {
			// check generic entity
			context = fetchGuidelineContextForEntityLink(contextList, types[i], merge, entityIDMap);
			if (context != null && context.getIDs().length > 0) {
				resultList.add(context);
			}
			// check generic category
			context = fetchGuidelineContextForCategoryLink(contextList, types[i], merge, entityIDMap);
			if (context != null && context.getIDs().length > 0) {
				resultList.add(context);
			}
		}
		return resultList.toArray(new GuidelineContext[0]);
	}

	private static GenericEntityType fetchGenericEntityType(EntityLink element) throws ImportException {
		return GenericEntityType.forName(element.getType());
	}

	private static GuidelineContext fetchGuidelineContextForCategoryLink(List<EntityLink> contextList, GenericEntityType type, boolean merge, Map<String, Integer> entityIDMap)
			throws ImportException {
		List<Integer> intList = new ArrayList<Integer>();
		for (final EntityLink element : contextList) {
			final String typeStr = element.getType();
			if (typeStr.startsWith("generic-category:")) {
				if (type == GenericEntityType.forName(typeStr.substring(17))) {
					intList.add(new Integer((merge ? getMappedID(asCategoryIDMapKey(type.toString(), element.getId()), entityIDMap, element.getId()) : element.getId())));
				}
			}
			else if (type.toString().equals("product") && typeStr.equals("category")) {
				intList.add(new Integer((merge ? getMappedID(asCategoryIDMapKey(type.toString(), element.getId()), entityIDMap, element.getId()) : element.getId())));
			}
		}
		if (intList.isEmpty()) {
			return null;
		}
		else {
			GuidelineContext context = new GuidelineContext(ConfigurationManager.getInstance().getEntityConfigHelper().getCategoryDefinition(type).getTypeID().intValue());
			context.setIDs(UtilBase.toIntArray(intList));
			return context;
		}
	}

	private static GuidelineContext fetchGuidelineContextForEntityLink(List<EntityLink> contextList, GenericEntityType type, boolean merge, Map<String, Integer> entityIDMap)
			throws ImportException {
		List<Integer> intList = new ArrayList<Integer>();
		for (final EntityLink element : contextList) {
			if (type == fetchGenericEntityType(element)) {
				intList.add(new Integer((merge ? getMappedID(asEntityIDMapKey(type.toString(), element.getId()), entityIDMap, element.getId()) : element.getId())));
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

	private static int findColumnNumberFor(final String columnName, final com.mindbox.pe.xsd.data.GridDataElement.Grid grid) {
		if (columnName == null) {
			return -1;
		}
		for (int i = 0; i < grid.getColumnNames().getColumn().size(); i++) {
			if (columnName.equals(grid.getColumnNames().getColumn().get(i))) {
				return i + 1;
			}
		}
		return -1;
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

	private static void fixEntityListColumnValues(ProductGrid grid, Map<String, Integer> entityIDMap, boolean throwExceptionIfNotFound) throws ImportException {
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
	 * @param value value
	 * @param entityIDMap entityIDMap
	 * @param throwExceptionIfNotFound if set to <code>true</code> and the value is not found int he entityIDMap, an ImportException is thrown
	 * @throws ImportException if <code>throwExceptionIfNotFound</code> is set to <code>true</code> and the value is not found in <code>entityIDMap</code>
	 */
	private static void fixGridCellValue(CategoryOrEntityValue value, Map<String, Integer> entityIDMap, boolean throwExceptionIfNotFound) throws ImportException {
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
	 * @param value value
	 * @param entityIDMap entityIDMap
	 */
	private static void fixGridCellValue(CategoryOrEntityValues value, Map<String, Integer> entityIDMap, boolean throwExceptionIfNotFound) throws ImportException {
		if (value != null) {
			for (int i = 0; i < value.size(); i++) {
				Object obj = value.get(i);
				if (obj instanceof CategoryOrEntityValue) {
					fixGridCellValue((CategoryOrEntityValue) obj, entityIDMap, throwExceptionIfNotFound);
				}
			}
		}
	}

	private static String getCellValueAt(final Row row, final int columnNo) {
		if (columnNo > 0 && columnNo <= row.getCellValue().size()) {
			return row.getCellValue().get(columnNo - 1);
		}
		else {
			return null;
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

	private static String getColumnName(ParameterTemplate template, int templateColumn) {
		if (template.getColumn(templateColumn) == null) {
			return null;
		}
		else {
			return template.getColumn(templateColumn).getTitle(); // getName();
		}
	}

	private static DateSynonym getEffectiveDateSynonym(com.mindbox.pe.xsd.data.ActivationDates activationDates, Map<Integer, Integer> dateSynonymIDMap, User user)
			throws ImportException, DataValidationFailedException {
		if (activationDates.getEffectiveDateID() != null) {
			return DateSynonymManager.getInstance().getDateSynonym(findMappedDateSynonymID(activationDates.getEffectiveDateID(), dateSynonymIDMap));
		}
		else if (activationDates.getActivationDate() != null) {
			DateSynonym ds = DateSynonymManager.getInstance().getDateSynonym(activationDates.getActivationDate());
			if (ds == null) {
				try {
					ds = BizActionCoordinator.createNewDateSynonym(activationDates.getActivationDate(), user);
				}
				catch (ServletActionException e) {
					e.printStackTrace();
					throw new ImportException("Failed to create new date synonym for effectivate date for " + activationDates.getActivationDate());
				}
			}
			return ds;
		}
		else {
			return null;
		}
	}

	private static DateSynonym getExpirationDateSynonym(com.mindbox.pe.xsd.data.ActivationDates activationDates, Map<Integer, Integer> dateSynonymIDMap, User user)
			throws ImportException, DataValidationFailedException {
		if (activationDates.getExpirationDateID() != null) {
			return DateSynonymManager.getInstance().getDateSynonym(findMappedDateSynonymID(activationDates.getExpirationDateID(), dateSynonymIDMap));
		}
		else if (activationDates.getExpirationDate() != null) {
			DateSynonym ds = DateSynonymManager.getInstance().getDateSynonym(activationDates.getExpirationDate());
			if (ds == null) {
				try {
					ds = BizActionCoordinator.createNewDateSynonym(activationDates.getExpirationDate(), user);
				}
				catch (ServletActionException e) {
					e.printStackTrace();
					throw new ImportException("Failed to create new date synonym for expiration date for " + activationDates.getExpirationDate());
				}
			}
			return ds;
		}
		else {
			return null;
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

	private static String getPrivilegeName(final int id, final List<com.mindbox.pe.xsd.data.PrivilegesElement.Privilege> privilegeList) {
		for (final com.mindbox.pe.xsd.data.PrivilegesElement.Privilege privilege : privilegeList) {
			if (privilege.getId() == id) {
				return privilege.getName();
			}
		}
		return null;
	}

	public static String getProperty(final EntityBase entityBase, final String name) {
		for (final PropertyElement propertyElement : entityBase.getProperty()) {
			if (propertyElement.getName().equals(name)) {
				return propertyElement.getValue();
			}
		}
		return null;
	}

	public static int getRequiredMappedID(String key, Map<String, Integer> idMap) throws ImportException {
		if (idMap.containsKey(key)) {
			return idMap.get(key).intValue();
		}
		else {
			throw new ImportException("Key " + key + " not found in the id map");
		}
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

	public static boolean isRoot(final Category category) {
		final List<com.mindbox.pe.xsd.data.EntityDataElement.Category.Parent> parentList = category.getParent();
		return (parentList.isEmpty() || (parentList.size() == 1 && (parentList.get(0).getParentID() == null || parentList.get(0).getParentID() == -1)));
	}

	private static void replaceRuleIDs(ProductGrid targetGrid, ProductGrid sourceGrid) {
		assert targetGrid != null;
		if (sourceGrid != null) {
			targetGrid.copyColumns(targetGrid.getRuleIDColumnNames(), sourceGrid);
		}
	}

	private static void setInvariants(AbstractGrid<?> gridObject, GridActivationElement gridActivation, GuidelineContext[] context, String[][] cellValues) throws ImportException {
		gridObject.setCloneOf(gridActivation.getParentID());
		gridObject.setComments(gridActivation.getComment());
		if (gridActivation.getCreatedOn() != null) {
			gridObject.setCreationDate(gridActivation.getCreatedOn());
		}
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
		gridObject.setNumRows(gridActivation.getGridValues() == null || gridActivation.getGridValues().getRow() == null ? 0 : gridActivation.getGridValues().getRow().size());
		gridObject.setStatus(gridActivation.getStatus());
		if (gridActivation.getStatusChangedOn() != null) {
			gridObject.setStatusChangeDate(gridActivation.getStatusChangedOn());
		}
		if (gridObject.getNumRows() == 0) {
			gridObject.setNumRows(1);
		}
		ServerContextUtil.setContext(gridObject, context);
	}

	public static String toFilterTypeString(PersistentFilterSpec filter) {
		return (filter.isForGenericEntity() ? GenericEntityType.forID(filter.getEntityTypeID()).toString() : PeDataType.forID(filter.getEntityTypeID()).toString());
	}

	private static int translateColumnNumber(GridTemplate template, int templateColumn, com.mindbox.pe.xsd.data.GridDataElement.Grid grid) {
		return findColumnNumberFor(getColumnName(template, templateColumn), grid);
	}

	private static int translateColumnNumber(ParameterTemplate template, int templateColumn, com.mindbox.pe.xsd.data.GridDataElement.Grid grid) {
		return findColumnNumberFor(getColumnName(template, templateColumn), grid);
	}

	/**
	 * returns privilegeId's which did not match any privilege from the DB but are in the file to be imported
	 * @since PowerEditor 5.0
	 * @param roleDigest roleDigest
	 * @param digestPrivList digestPrivListReportGenerato
	 * @return List of unknown Privileges
	 * @throws ImportException on error
	 */
	public static List<String> unknownPrivsForRole(com.mindbox.pe.xsd.data.RolesElement.Role roleDigest, List<com.mindbox.pe.xsd.data.PrivilegesElement.Privilege> digestPrivList)
			throws ImportException {
		final List<String> unknownPrivList = new ArrayList<String>();
		for (final int privilegeID : roleDigest.getPrivilegeLink()) {
			// Since 5.0 EntityType and UsageType privilege ids are dynamically generated when the PE config file is parsed at server start up.
			// Therefore, we can no longer rely on the finding privs by the digested priv id.  Rather, we must search for existing privs by name.
			final String digestPrivName = getPrivilegeName(privilegeID, digestPrivList);

			Privilege priv = SecurityCacheManager.getInstance().findPrivilegeByName(digestPrivName);
			if (priv == null) {
				unknownPrivList.add(digestPrivName);
			}
		}
		return unknownPrivList;
	}

	// TODO Kim, 2007-04-26: replace with validation framework, including info/warning/errors
	private static void validateUserObject(User user) throws ImportException {
		if (user == null) return;
		if (UtilBase.isEmpty(user.getUserID())) throw new ImportException("User not imported, since there is no user ID.");
		if (UtilBase.isEmpty(user.getCurrentPassword())) throw new ImportException("User not imported, since there is no password.");
		if (UtilBase.isEmpty(user.getStatus())) throw new ImportException("User not imported, since there is no status.");
	}

	private ObjectConverter() {
	}
}