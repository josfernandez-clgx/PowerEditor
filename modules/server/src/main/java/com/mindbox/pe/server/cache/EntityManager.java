package com.mindbox.pe.server.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.ContextContainer;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.ChildAssociationKeySet;
import com.mindbox.pe.model.assckey.DefaultChildAssociationKeySet;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.GenericEntityAssociationKey;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.TimedAssociationKey;
import com.mindbox.pe.model.comparator.DateSynonymComparatorByDate;
import com.mindbox.pe.model.comparator.TimedAssociationKeyComparator;
import com.mindbox.pe.server.SetOperations;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.model.GenericEntityIdentity;
import com.mindbox.pe.server.servlet.ServletActionException;
import com.mindbox.pe.server.spi.db.EntityDataHolder;
import com.mindbox.pe.xsd.config.CategoryType;
import com.mindbox.pe.xsd.config.EntityPropertyType;
import com.mindbox.pe.xsd.config.EntityType;

/**
 * Entity cache manager. This manages channels, investors, and their relationships. Added generic
 * entity supported from PowerEditor 3.0.0.
 * 
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 1.0
 */
public class EntityManager extends AbstractCacheManager implements EntityDataHolder {

	/**
	 * Compatibility matrix data.
	 * 
	 * @since PowerEditor 3.0.0
	 */
	private static final class CompatibilityData {

		private DateSynonym effDate, expDate = null;

		CompatibilityData(DateSynonym effDate, DateSynonym expDate) {
			this.effDate = effDate;
			this.expDate = expDate;
		}

		@Override
		public String toString() {
			return "[" + effDate + "," + expDate + "]";
		}
	}

	/**
	 * Compatibility matrix key.
	 * 
	 * @since PowerEditor 3.0.0
	 */
	private static final class CompatibilityKey {

		private final GenericEntityType type1, type2;

		private final int id1, id2;

		private CompatibilityKey(GenericEntityType type1, int id1, GenericEntityType type2, int id2) {
			this.type1 = type1;
			this.id1 = id1;
			this.type2 = type2;
			this.id2 = id2;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof CompatibilityKey) {
				CompatibilityKey data = (CompatibilityKey) obj;
				return type1 == data.type1 && id1 == data.id1 && type2 == data.type2 && id2 == data.id2;
			}
			else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return (type1.getID() + "." + id1 + ":" + type2.getID() + "." + id2).hashCode();
		}

		@Override
		public String toString() {
			return "[" + type1 + "." + id1 + ":" + type2 + "." + id2 + "]";
		}
	}

	private static class DefaultGenericEntityPropertySetter implements GenericEntityPropertySetter {
		@Override
		public void setGenericEntityProperty(GenericEntity entity, String propertyName, EntityPropertyType propertyType, Object value) {
			if (value == null) return;
			if (value instanceof Boolean) {
				entity.setProperty(propertyName, ((Boolean) value).booleanValue());
			}
			else if (value instanceof Date) {
				entity.setProperty(propertyName, (Date) value);
			}
			else if (value instanceof Double) {
				entity.setProperty(propertyName, ((Double) value).doubleValue());
			}
			else if (value instanceof Float) {
				entity.setProperty(propertyName, ((Float) value).floatValue());
			}
			else if (value instanceof Integer) {
				entity.setProperty(propertyName, ((Integer) value).intValue());
			}
			else if (value instanceof Long) {
				entity.setProperty(propertyName, ((Long) value).longValue());
			}
			else if (value instanceof String) {
				String valueStr = (String) value;
				switch (propertyType) {
				case BOOLEAN:
					entity.setProperty(propertyName, ConfigUtil.asBoolean(valueStr));
					break;
				case DATE:
					Date date = toDate(valueStr);
					if (date == null) date = ConfigUtil.toDate(valueStr);
					entity.setProperty(propertyName, date);
					break;
				case CURRENCY:
				case DOUBLE:
					entity.setProperty(propertyName, Double.valueOf(valueStr));
					break;
				case FLOAT:
				case PERCENT:
					entity.setProperty(propertyName, Float.valueOf(valueStr));
					break;
				case INTEGER:
					entity.setProperty(propertyName, Integer.valueOf(valueStr));
					break;
				case LONG:
					entity.setProperty(propertyName, Long.valueOf(valueStr));
					break;
				default:
					entity.setProperty(propertyName, valueStr);
				}
			}
		}
	}

	public static interface GenericEntityPropertySetter {
		void setGenericEntityProperty(GenericEntity entity, String propertyName, EntityPropertyType propertyType, Object value);
	}

	private static EntityManager instance = null;

	public static synchronized EntityManager getInstance() {
		if (instance == null) instance = new EntityManager();
		return instance;
	}

	public static void setGenericEntityProperties(GenericEntity entity, Map<String, Object> propertyMap) {
		setGenericEntityProperties(entity, propertyMap, getInstance().propertySetter);
	}

	public static void setGenericEntityProperties(GenericEntity entity, Map<String, Object> propertyMap, GenericEntityPropertySetter propertySetter) {
		if (propertyMap != null && !propertyMap.isEmpty()) {
			EntityType entityTypeDef = ConfigurationManager.getInstance().getEntityConfigHelper().findEntityTypeDefinition(entity.getType());
			if (entityTypeDef != null) {
				for (Map.Entry<String, Object> element : propertyMap.entrySet()) {
					String key = element.getKey();
					final EntityPropertyType propType = ConfigUtil.findPropertyType(entityTypeDef, key);
					if (propType != null) {
						propertySetter.setGenericEntityProperty(entity, key, propType, element.getValue());
					}
				}
			}
		}
	}

	private static Date toDate(String dateStr) {
		if (dateStr == null || dateStr.length() == 0) {
			return null;
		}
		try {
			return Constants.THREADLOCAL_FORMAT_DATE_TIME_SEC.get().parse(dateStr);
		}
		catch (Exception ex) {
			return null;
		}
	}

	private final Map<GenericEntityType, Map<Integer, GenericEntity>> entityMap; // maintains entity type to entity objects mappings
	private final Map<CompatibilityKey, CompatibilityData> compatibilityMap; // maintains CompatibilityKey to CompatibilityData mappings
	private final Map<Integer, List<GenericCategory>> categoryMap; // maintains generic category type to category objects mappings
	private final Map<Integer, Map<Integer, ChildAssociationKeySet>> categoryToEntityMap; // maintains category object to
	private final GenericEntityPropertySetter propertySetter = new DefaultGenericEntityPropertySetter();

	private EntityManager() {
		entityMap = new HashMap<GenericEntityType, Map<Integer, GenericEntity>>();
		compatibilityMap = new HashMap<CompatibilityKey, CompatibilityData>();
		categoryMap = new HashMap<Integer, List<GenericCategory>>();
		categoryToEntityMap = new HashMap<Integer, Map<Integer, ChildAssociationKeySet>>();
	}

	public void addCategoryAssociations(int entityID, int entityType, int categoryType, MutableTimedAssociationKey[] associationKeys) {
		for (int i = 0; i < associationKeys.length; i++) {
			addGenericEntityToCategory_internal(entityID, entityType, categoryType, associationKeys[i], true);
		}
	}

	private void addChildCategoryLinkToParent(GenericCategory category, TimedAssociationKey parentAssociationKey) {
		GenericCategory parentCategory = getGenericCategory(category.getType(), parentAssociationKey.getAssociableID());
		if (parentCategory != null) {
			parentCategory.addChildAssociation(
					new DefaultMutableTimedAssociationKey(category.getId(), parentAssociationKey.getEffectiveDate(), parentAssociationKey.getExpirationDate()));
		}
		else {
			throw new IllegalArgumentException("No category " + parentAssociationKey.getAssociableID() + " of type " + category.getType() + " exists");
		}
	}

	private void addDateSynonymForChangesInEntityToCategoryRelationships(Collection<DateSynonym> collection, GenericEntityType type, int[] categoryIDs, DateSynonym startDate,
			DateSynonym endDate) {
		GenericEntityIdentity[] identies = getGenericEntitiesInCategorySetAtAnyTime(type.getCategoryType(), categoryIDs, true);
		for (int i = 0; i < identies.length; i++) {
			GenericEntity entity = getEntity(type, identies[i].getEntityID());
			if (entity != null) {
				Set<DateSynonym> tempSet = entity.getDateSynonymsUsedInCategoryAssociations();
				for (Iterator<DateSynonym> iter = tempSet.iterator(); iter.hasNext();) {
					DateSynonym element = iter.next();
					if (element != null && (startDate == null || element.after(startDate)) && (endDate == null || element.before(endDate))) {
						if (!collection.contains(element)) collection.add(element);
					}
				}
			}
		}
	}

	@Override
	public void addEntityCompatibility(int entityType1, int entityID1, int entityType2, int entityID2, DateSynonym effectiveDate, DateSynonym expirationDate) {
		insertEntityCompatibility(GenericEntityType.forID(entityType1), entityID1, GenericEntityType.forID(entityType2), entityID2, effectiveDate, expirationDate);
	}

	@Override
	public void addGenericEntity(int entityID, int entityType, String name, int parentID, Map<String, Object> propertyMap) {
		GenericEntityType type = GenericEntityType.forID(entityType);
		GenericEntity entity = new GenericEntity(entityID, type, name);
		setGenericEntityProperties(entity, propertyMap, propertySetter);
		entity.setParentID(parentID);
		insertEntity(type, entity);
	}

	/**
	 * Call this to insert generic categories into cache.
	 * 
	 * @param categoryType categoryType
	 * @param categoryID categoryID
	 * @param categoryName categoryName
	 */
	@Override
	public void addGenericEntityCategory(int categoryType, int categoryID, String categoryName) {
		GenericCategory category = new GenericCategory(categoryID, categoryName, categoryType);
		List<GenericCategory> catList = getCategoryCacheList(categoryType);
		catList.add(category);
	}

	@Override
	public void addGenericEntityToCategories(int[] categoryIDs, int categoryType, int entityID, int entityType, int effDateID, int expDateID) {
		GenericEntity entity = getEntity(GenericEntityType.forID(entityType), entityID);
		if (entity != null) {
			if (categoryIDs != null && categoryIDs.length > 0) {
				for (int i = 0; i < categoryIDs.length; i++) {
					addGenericEntityToCategory_internal(categoryIDs[i], categoryType, entityID, entityType, effDateID, expDateID, false);
				}
				DateSynonym effDS = DateSynonymManager.getInstance().getDateSynonym(effDateID);
				DateSynonym expDS = DateSynonymManager.getInstance().getDateSynonym(expDateID);
				// add cateogry ids to the cached entity
				for (int i = 0; i < categoryIDs.length; i++) {
					entity.addCategoryAssociation(new DefaultMutableTimedAssociationKey(categoryIDs[i], effDS, expDS));
				}
			}
		}
	}

	@Override
	public void addGenericEntityToCategory(int categoryID, int categoryType, int entityID, int entityType, int effDateID, int expDateID) {
		addGenericEntityToCategory_internal(categoryID, categoryType, entityID, entityType, effDateID, expDateID, true);
	}

	private void addGenericEntityToCategory_internal(int categoryID, int categoryType, int entityID, int entityType, int effDateID, int expDateID, boolean updateEntity) {
		addGenericEntityToCategory_internal(
				entityID,
				entityType,
				categoryType,
				new DefaultMutableTimedAssociationKey(
						categoryID,
						DateSynonymManager.getInstance().getDateSynonym(effDateID),
						DateSynonymManager.getInstance().getDateSynonym(expDateID)),
				updateEntity);
	}

	private void addGenericEntityToCategory_internal(int entityID, int entityType, int categoryType, MutableTimedAssociationKey associationKey, boolean updateEntity) {
		ChildAssociationKeySet keySet = getCategoryToEntityCachKeySet(associationKey.getAssociableID(), categoryType);
		keySet.add(new DefaultMutableTimedAssociationKey(entityID, associationKey.getEffectiveDate(), associationKey.getExpirationDate()));
		if (updateEntity) {
			// add cateogry id to the cached entity
			GenericEntity entity = getEntity(GenericEntityType.forID(entityType), entityID);
			if (entity != null) {
				entity.addCategoryAssociation(associationKey);
			}
		}
	}

	/**
	 * Adds the specified parent relationship.
	 * <p>
	 * This updates children links of the parent category specified in <code>associationKey</code>.
	 */
	@Override
	public void addParentAssociation(int categoryType, int categoryID, int parentID, int effectiveDateID, int expirationDateID) {
		addParentAssociation(
				categoryType,
				categoryID,
				new DefaultMutableTimedAssociationKey(
						parentID,
						DateSynonymManager.getInstance().getDateSynonym(effectiveDateID),
						DateSynonymManager.getInstance().getDateSynonym(expirationDateID)));
	}

	public void addParentAssociation(int categoryType, int categoryID, MutableTimedAssociationKey associationKey) {
		GenericCategory category = getGenericCategory(categoryType, categoryID);
		if (category == null) throw new IllegalArgumentException("No category found for id " + categoryID + " of type " + categoryType);
		category.addParentKey(associationKey);
		addChildCategoryLinkToParent(category, associationKey);
	}

	public void addParentAssociations(GenericCategory category) {
		for (Iterator<MutableTimedAssociationKey> iter = category.getParentKeyIterator(); iter.hasNext();) {
			MutableTimedAssociationKey element = iter.next();
			addParentAssociation(category.getType(), category.getId(), element);
		}
	}

	/**
	 * @param entityIDs entityIDs
	 * @param categoryID categoryID
	 * @param categoryType categoryType
	 * @param date the date
	 * @return true if all entities in entityID are descendents of categoryID 
	 */
	public boolean areEntitiesDescendentOfCategoryAsOf(int entityIDs[], int categoryID, int categoryType, Date date) {
		Set<GenericEntityIdentity> identities = getGenericEntitiesInCategoryAsOf(categoryID, categoryType, date);
		int[] ids = new int[identities.size()];
		int n = 0;
		for (Iterator<GenericEntityIdentity> iter = identities.iterator(); iter.hasNext();) {
			GenericEntityIdentity identity = iter.next();
			ids[n] = identity.getEntityID();
			n++;
		}
		return UtilBase.contains(entityIDs, ids);
	}

	private GenericEntityAssociationKey asAssociationKey(CompatibilityKey key) {
		CompatibilityData compData = compatibilityMap.get(key);
		GenericEntityAssociationKey asscKey = new GenericEntityAssociationKey(
				key.type2,
				key.id2,
				(compData == null ? null : compData.effDate),
				(compData == null ? null : compData.expDate));
		return asscKey;
	}

	private GenericEntityCompatibilityData asCompatibilityData(CompatibilityKey key) {
		return asCompatibilityData(key, false);
	}

	private GenericEntityCompatibilityData asCompatibilityData(CompatibilityKey key, boolean reverseOrder) {
		CompatibilityData compData = compatibilityMap.get(key);
		GenericEntityCompatibilityData data = null;
		if (reverseOrder) {
			data = new GenericEntityCompatibilityData(
					key.type2,
					key.id2,
					key.type1,
					key.id1,
					(compData == null ? null : compData.effDate),
					(compData == null ? null : compData.expDate));
		}
		else {
			data = new GenericEntityCompatibilityData(
					key.type1,
					key.id1,
					key.type2,
					key.id2,
					(compData == null ? null : compData.effDate),
					(compData == null ? null : compData.expDate));
		}
		return data;
	}

	private GenericCategory findCategory(int categoryType, int categoryID) {
		List<GenericCategory> catList = getCategoryCacheList(categoryType);
		for (GenericCategory element : catList) {
			if (element.getID() == categoryID) {
				return element;
			}
		}
		return null;
	}

	/**
	 * Note this considers all dates.
	 * @param parentCategory parentCategory
	 * @param name name
	 * @param date date
	 * @return
	 */
	private GenericCategory findChildCategoryByName(GenericCategory parentCategory, String name) {
		for (Iterator<MutableTimedAssociationKey> iter = parentCategory.getChildrenKeyIterator(); iter.hasNext();) {
			TimedAssociationKey element = iter.next();
			GenericCategory child = getGenericCategory(parentCategory.getType(), element.getAssociableID());
			if (child.getName().equals(name)) {
				return child;
			}
		}
		return null;
	}

	public GenericCategory findFirstCategory(int categoryID) {
		for (Iterator<List<GenericCategory>> iter = categoryMap.values().iterator(); iter.hasNext();) {
			List<GenericCategory> list = iter.next();
			for (Iterator<GenericCategory> iter2 = list.iterator(); iter2.hasNext();) {
				GenericCategory element = iter2.next();
				if (element.getID() == categoryID) {
					return element;
				}
			}
		}
		return null;
	}

	public GenericEntity findFirstEntity(int entityID) {
		Integer key = new Integer(entityID);
		for (Map<Integer, GenericEntity> map : entityMap.values()) {
			if (map.containsKey(key)) {
				return (GenericEntity) map.get(key);
			}
		}
		return null;
	}

	/**
	 * This considers all dates. 
	 * @param categoryType categoryType
	 * @param path path
	 * @param date date
	 * @return
	 */
	private GenericCategory findFullyQualifiedGenericCategoryByPath(int categoryType, String path) {
		GenericCategory category = null;
		String[] categoryNodes = path.split(Constants.CATEGORY_PATH_DELIMITER);
		if (categoryNodes != null && categoryNodes.length > 0) {
			category = getGenericCategoryRoot(categoryType);
			if (category != null && category.getName().equals(categoryNodes[0])) {
				for (int i = 1; i < categoryNodes.length; i++) {
					category = findChildCategoryByName(category, categoryNodes[i]);
					if (category == null) {
						break;
					}
				}
			}
			else {
				category = null;
			}
		}
		return category;
	}

	/**
	 * Note this considers all dates for checking path.
	 * @param categoryType Type of generic catgory
	 * @param name Name of generic entity. Could be fully qualified delimited with "/" or just the name. 
	 * @return an array of matching GenericCategory instances or <code>null</code> if it isnt found.
	 */
	public GenericCategory[] findGenericCategoryByName(int categoryType, String name) {
		List<GenericCategory> results = new ArrayList<GenericCategory>();
		if (!UtilBase.isEmpty(name)) {
			if (name.indexOf(Constants.CATEGORY_PATH_DELIMITER) > 0) {
				return new GenericCategory[] { findFullyQualifiedGenericCategoryByPath(categoryType, name) };
			}
			else {
				for (Iterator<GenericCategory> i = getCategoryCacheList(categoryType).iterator(); i.hasNext();) {
					GenericCategory element = i.next();
					if (element.getName().equals(name)) {
						results.add(element);
					}
				}
			}
		}
		return results.size() == 0 ? null : results.toArray(new GenericCategory[0]);
	}

	/**
	 * Performs post loading clean up/build-up.
	 * <p>
	 * As of 5.1.0, no longer needed as all categories were read first and then parent relationships.
	 * @see #addParentAssociation(int, int, MutableTimedAssociationKey)
	 */
	public synchronized void finishLoading() {
		logger.info(">>> finishLoading");

		// create dummy root generic categories, if not found
		// if found, add children to parents
		GenericEntityType[] types = GenericEntityType.getAllGenericEntityTypes();
		for (int i = 0; i < types.length; i++) {
			final CategoryType categoryDef = ConfigurationManager.getInstance().getEntityConfigHelper().getCategoryDefinition(types[i]);
			if (categoryDef != null) {
				List<GenericCategory> categoryList = getCategoryCacheList(categoryDef.getTypeID().intValue());
				if (categoryList.isEmpty()) {
					logger.info("Creating a root generic category for " + categoryDef);
					try {
						final GenericCategory rootCategory = new GenericCategory(
								-1,
								"Root " + categoryDef.getName().toUpperCase() + " Created by PowerEditor",
								categoryDef.getTypeID().intValue());
						rootCategory.setRootIndicator(true);
						BizActionCoordinator.getInstance().save(rootCategory, false, null);
					}
					catch (ServletActionException ex) {
						logger.error("Failed to create a root generic category", ex);
						throw new IllegalStateException("No " + categoryDef.getName() + " exists: failed to create a root category - " + ex.getMessage());
					}
					catch (DataValidationFailedException ex) {
						logger.error("Failed to validate a root generic category", ex);
						throw new IllegalStateException("No " + categoryDef.getName() + " exists: failed to create a root category - " + ex.getMessage());
					}
				}
			}
		}
	}

	public List<GenericCategory> getAllAssociatedCategoriesForEntityAsOf(int entityID, GenericEntityType entityType, Date date) {
		List<GenericCategory> resultList = new ArrayList<GenericCategory>();
		//GenericEntityType entityType = GenericEntityType.forID(entityID);

		GenericEntity entity = getEntity(entityType, entityID);
		List<Integer> list = entity.getCategoryIDList(date);
		for (Iterator<Integer> iter = list.iterator(); iter.hasNext();) {
			int id = iter.next().intValue();
			resultList.add(getGenericCategory(entityType.getCategoryType(), id));
		}
		return resultList;
	}

	public List<GenericCategory> getAllAssociatedCategoriesForEntityAtAnyTime(int entityID, GenericEntityType entityType) {
		List<GenericCategory> resultList = new ArrayList<GenericCategory>();
		//GenericEntityType entityType = GenericEntityType.forID(entityID);
		GenericEntity entity = getEntity(entityType, entityID);

		for (Iterator<MutableTimedAssociationKey> iter = entity.getCategoryIterator(); iter.hasNext();) {
			TimedAssociationKey key = iter.next();
			resultList.add(getGenericCategory(entityType.getCategoryType(), key.getAssociableID()));
		}
		return resultList;
	}

	/**
	 * Gets a list of all {@link GenericCategory} of the specified type as an unmodifiable list.
	 * @param categoryType categoryType
	 * @return unmodifiable list of {@link GenericCategory} instances of the specified type; never <code>null</code>
	 */
	public List<GenericCategory> getAllCategories(int categoryType) {
		return Collections.unmodifiableList(getCategoryCacheList(categoryType));
	}

	/**
	 * Gets list of all {@link GenericEntityCompatibilityData}between the specified two generic
	 * entity types.
	 * 
	 * @param type1 type1
	 *            source generic entity type
	 * @param type2 type2
	 *            target generic entity type
	 * @return List
	 * @since 3.0.1
	 */
	public List<GenericEntityCompatibilityData> getAllCrossCompatibilities(GenericEntityType type1, GenericEntityType type2) {
		// return type1->type2 and type2->type1
		List<GenericEntityCompatibilityData> list = new ArrayList<GenericEntityCompatibilityData>();
		for (Map.Entry<CompatibilityKey, CompatibilityData> entry : compatibilityMap.entrySet()) {
			CompatibilityKey key = (CompatibilityKey) entry.getKey();
			if (key.type1 == type1 && key.type2 == type2) {
				list.add(asCompatibilityData(key));
			}
			else if (key.type2 == type1 && key.type1 == type2) {
				list.add(asCompatibilityData(key, true));
			}
		}
		return list;
	}

	/**
	 * @param categoryType categoryType
	 * @param categoryID categoryID
	 * @param date date
	 * @return category ids
	 */
	public List<Integer> getAllDescendentCategoryIDsAsOf(int categoryType, int categoryID, Date date) {
		logger.debug(">>> getAllDescendentCategoryIDs: " + categoryID);
		List<Integer> list = new ArrayList<Integer>();
		GenericCategory cat = getGenericCategory(categoryType, categoryID);
		if (cat != null) {
			getAllDescendentCategoryIDsAsOf_aux(cat, list, date);
		}
		logger.debug("<<< getAllDescendentCategoryIDs: " + list.size());
		return list;
	}

	private void getAllDescendentCategoryIDsAsOf_aux(GenericCategory cat, List<Integer> list, Date date) {
		logger.debug(">>> getAllDescendentCategoryIDs_aux: " + cat);
		list.add(new Integer(cat.getID()));
		for (Iterator<Integer> iter = cat.getChildIDs(date).iterator(); iter.hasNext();) {
			int childID = iter.next().intValue();
			GenericCategory child = getGenericCategory(cat.getType(), childID);
			getAllDescendentCategoryIDsAsOf_aux(child, list, date);
		}
		logger.debug("<<< getAllDescendentCategoryIDs_aux: " + cat);
	}

	public Collection<GenericEntity> getAllEntities(GenericEntityType type) {
		return getEntityCacheMap(type).values();
	}

	public List<GenericCategory> getAllGenericCategoriesByName(int categoryType, String name) {
		if (name == null) throw new NullPointerException("name cannot be null");
		List<GenericCategory> categoryList = new ArrayList<GenericCategory>();
		for (GenericCategory category : getCategoryCacheList(categoryType)) {
			if (category.getName().equals(name)) {
				categoryList.add(category);
			}
		}
		return categoryList;
	}

	public List<GenericEntity> getAllGenericEntitiesInCategoryAsOf(int categoryID, int categoryType, Date date) {
		List<GenericEntity> resultList = new ArrayList<GenericEntity>();

		ChildAssociationKeySet keySet = getCategoryToEntityCachKeySet(categoryID, categoryType);
		List<Integer> list = keySet.getChildrendAsOf(date);
		for (Iterator<Integer> iter = list.iterator(); iter.hasNext();) {
			int id = iter.next().intValue();
			resultList.add(getEntity(GenericEntityType.forCategoryType(categoryType), id));
		}
		return resultList;
	}

	public List<GenericEntity> getAllGenericEntitiesInCategoryAtAnyTime(int categoryID, int categoryType) {
		List<GenericEntity> resultList = new ArrayList<GenericEntity>();

		ChildAssociationKeySet keySet = getCategoryToEntityCachKeySet(categoryID, categoryType);
		for (Iterator<MutableTimedAssociationKey> iter = keySet.iterator(); iter.hasNext();) {
			TimedAssociationKey key = iter.next();
			resultList.add(getEntity(GenericEntityType.forCategoryType(categoryType), key.getAssociableID()));
		}
		return resultList;
	}

	public List<GenericCategory> getAllRootCategories(int categoryType) {
		List<GenericCategory> resultList = new ArrayList<GenericCategory>();
		List<GenericCategory> list = getCategoryCacheList(categoryType);
		for (GenericCategory category : list) {
			if (category.isRoot()) {
				resultList.add(category);
			}
		}
		return resultList;
	}

	private int getAncestorCategoryIDAsOf(int categoryType, int categoryID, Date date) {
		for (Iterator<GenericCategory> iter = getAllRootCategories(categoryType).iterator(); iter.hasNext();) {
			GenericCategory rootCat = iter.next();
			if (rootCat.getID() == categoryID) {
				return categoryID;
			}
			for (Iterator<Integer> iter2 = rootCat.getChildIDs(date).iterator(); iter2.hasNext();) {
				int childID = iter2.next().intValue();
				GenericCategory child = getGenericCategory(categoryType, childID);
				if (child.getID() == categoryID) {
					return categoryID;
				}
				else if (isDescendentAsOf(categoryID, child, date)) {
					return child.getID();
				}
			}
		}
		return -1;
	}

	private List<GenericCategory> getCategoryCacheList(int categoryType) {
		if (!this.categoryMap.containsKey(categoryType)) {
			this.categoryMap.put(categoryType, Collections.synchronizedList(new ArrayList<GenericCategory>()));
		}
		return categoryMap.get(categoryType);
	}

	private Map<Integer, ChildAssociationKeySet> getCategoryToEntityCacheMap(int categoryType) {
		Integer key = new Integer(categoryType);
		if (!categoryToEntityMap.containsKey(key)) {
			this.categoryToEntityMap.put(key, Collections.synchronizedMap(new HashMap<Integer, ChildAssociationKeySet>()));
		}
		return this.categoryToEntityMap.get(key);
	}

	private ChildAssociationKeySet getCategoryToEntityCachKeySet(int categoryID, int categoryType) {
		Map<Integer, ChildAssociationKeySet> map = getCategoryToEntityCacheMap(categoryType);
		ChildAssociationKeySet keySet = null;
		Integer key = new Integer(categoryID);
		if (map.containsKey(key)) {
			keySet = map.get(key);
		}
		else {
			keySet = new DefaultChildAssociationKeySet();
			map.put(key, keySet);
		}
		return keySet;
	}

	/**
	 * Gets list of all {@link GenericEntityCompatibilityData}between the specified two generic
	 * entity types.
	 * 
	 * @param type1 type1
	 *            source generic entity type
	 * @param type2 type2
	 *            target generic entity type
	 * @return List
	 * @since 3.0.0
	 */
	public List<GenericEntityCompatibilityData> getCompatibilities(GenericEntityType type1, GenericEntityType type2) {
		List<GenericEntityCompatibilityData> list = new ArrayList<GenericEntityCompatibilityData>();
		for (Map.Entry<CompatibilityKey, CompatibilityData> entry : compatibilityMap.entrySet()) {
			CompatibilityKey key = entry.getKey();
			if (key.type1 == type1 && key.type2 == type2) {
				list.add(asCompatibilityData(key));
			}
		}
		return list;
	}

	/**
	 * Gets list of all {@link GenericEntityAssociationKey}for the specified generic entity.
	 * 
	 * @param entityID entityID
	 *            generic entity id
	 * @param type type
	 *            generic entity type
	 * @return List
	 * @since 3.0.0
	 */
	public List<GenericEntityAssociationKey> getCompatibilities(int entityID, GenericEntityType type) {
		List<GenericEntityAssociationKey> list = new ArrayList<GenericEntityAssociationKey>();
		for (Map.Entry<CompatibilityKey, CompatibilityData> entry : compatibilityMap.entrySet()) {
			CompatibilityKey key = (CompatibilityKey) entry.getKey();
			if (key.type1 == type && key.id1 == entityID) {
				list.add(asAssociationKey(key));
			}
		}
		return list;
	}

	/**
	 * Gets list of all {@link GenericEntityAssociationKey}for the specified generic entity to
	 * <code>type2</code>.
	 * 
	 * @param entityID entityID
	 *            generic entity id
	 * @param type1 type1
	 *            generic entity type
	 * @param type2 type2
	 *            the target type
	 * @return List
	 * @since 3.0.0
	 */
	public List<GenericEntityAssociationKey> getCompatibilities(int entityID, GenericEntityType type1, GenericEntityType type2) {
		List<GenericEntityAssociationKey> list = new ArrayList<GenericEntityAssociationKey>();
		for (Map.Entry<CompatibilityKey, CompatibilityData> entry : compatibilityMap.entrySet()) {
			CompatibilityKey key = (CompatibilityKey) entry.getKey();
			if (key.type1 == type1 && key.id1 == entityID && key.type2 == type2) {
				list.add(asAssociationKey(key));
			}
		}
		return list;
	}

	public List<GenericEntityAssociationKey> getCompatibilitiesAsOf(int entityID, GenericEntityType type, Date date) {
		List<GenericEntityAssociationKey> list = new ArrayList<GenericEntityAssociationKey>();
		for (Map.Entry<CompatibilityKey, CompatibilityData> entry : compatibilityMap.entrySet()) {
			CompatibilityKey key = (CompatibilityKey) entry.getKey();
			if (key.type1 == type && key.id1 == entityID) {
				GenericEntityAssociationKey entityAssoc = asAssociationKey(key);
				if (entityAssoc.isEffectiveAt(date)) {
					list.add(entityAssoc);
				}
			}
		}
		return list;
	}

	/**
	 * Gets a set of {@link DateSynonym} instances used in category-category relationship chanages for the specified category type.
	 * Note that this only looks at parent assocation keys since child associations are driven by parent associations.
	 * @param categoryType category type
	 * @return a set of {@link DateSynonym} instances
	 */
	@Override
	public Set<DateSynonym> getDateSynonymsForChangesInCategoryRelationships(int categoryType) {
		Set<DateSynonym> set = new HashSet<DateSynonym>();
		for (Iterator<GenericCategory> iter = getAllCategories(categoryType).iterator(); iter.hasNext();) {
			GenericCategory category = iter.next();
			if (category != null) {
				for (Iterator<MutableTimedAssociationKey> iter2 = category.getParentKeyIterator(); iter2.hasNext();) {
					MutableTimedAssociationKey element = iter2.next();
					if (element.hasEffectiveDate()) set.add(element.getEffectiveDate());
					if (element.hasExpirationDate()) set.add(element.getExpirationDate());
				}
			}
		}
		return set;
	}

	public DateSynonym[] getDateSynonymsForChangesInCategoryToEntityRelationships(ContextContainer contextContainer, DateSynonym startDate, DateSynonym endDate) {
		GenericEntityType[] types = contextContainer.getGenericCategoryEntityTypesInUse();
		List<DateSynonym> list = new LinkedList<DateSynonym>();
		for (int i = 0; i < types.length; i++) {
			if (!UtilBase.isEmpty(contextContainer.getGenericCategoryIDs(types[i]))) {
				Set<DateSynonym> tmpSet = getDateSynonymsForChangesInCategoryRelationships(types[i].getCategoryType());
				for (Iterator<DateSynonym> iter = tmpSet.iterator(); iter.hasNext();) {
					DateSynonym element = iter.next();
					if (element != null && (startDate == null || element.after(startDate)) && (endDate == null || element.before(endDate))) {
						if (!list.contains(element)) list.add(element);
					}
				}
				addDateSynonymForChangesInEntityToCategoryRelationships(list, types[i], contextContainer.getGenericCategoryIDs(types[i]), startDate, endDate);
			}
		}

		// add start and end date and sort by date
		Collections.sort(list, DateSynonymComparatorByDate.getInstance());
		list.add(0, startDate);
		list.add(endDate);
		return list.toArray(new DateSynonym[0]);
	}

	public List<GenericEntity> getDescendents(GenericEntityType type, int parentID, boolean allDescendents) {
		logger.debug(">>> getDescendents: " + parentID + ",allDesc?=" + allDescendents);
		List<GenericEntity> list = new ArrayList<GenericEntity>();
		for (Iterator<GenericEntity> iter = getEntityCacheMap(type).values().iterator(); iter.hasNext();) {
			GenericEntity entity = iter.next();
			if (entity.getParentID() == parentID || (parentID < 0 && entity.getParentID() < 0)) {
				logger.debug("    adding " + entity);
				list.add(entity);
				if (allDescendents) list.addAll(getDescendents(type, entity.getID(), allDescendents));
			}
		}
		logger.debug("<<< getDescendents with " + list.size() + " clones");
		return list;
	}

	public GenericEntity getEntity(GenericEntityType type, int entityID) {
		return getEntityCacheMap(type).get(new Integer(entityID));
	}

	public GenericEntity getEntity(GenericEntityType type, String name) {
		if (name == null) throw new NullPointerException("name cannot be null");

		for (Iterator<GenericEntity> iter = getEntityCacheMap(type).values().iterator(); iter.hasNext();) {
			Object object = iter.next();
			if (object instanceof GenericEntity) {
				GenericEntity nameObject = (GenericEntity) object;
				if (nameObject.getName().equals(name)) {
					return nameObject;
				}
			}
		}
		return null;
	}

	private Map<Integer, GenericEntity> getEntityCacheMap(GenericEntityType entityType) {
		if (!entityMap.containsKey(entityType)) {
			this.entityMap.put(entityType, Collections.synchronizedMap(new HashMap<Integer, GenericEntity>()));
		}
		return this.entityMap.get(entityType);
	}

	public GenericCategory getGenericCategory(int categoryType, int categoryID) {
		List<GenericCategory> list = getCategoryCacheList(categoryType);
		for (Iterator<GenericCategory> iter = list.iterator(); iter.hasNext();) {
			GenericCategory element = iter.next();
			if (element.getID() == categoryID) {
				return element;
			}
		}
		return null;
	}

	/**
	 * Returns the GenericCategory which is the root for given categoryType.
	 * @author vineet khosla
	 * @since PowerEditor 5.0.0
	 * @param categoryType categoryType
	 * @return GenericCategory
	 */
	public GenericCategory getGenericCategoryRoot(int categoryType) {
		List<GenericCategory> list = getCategoryCacheList(categoryType);
		for (Iterator<GenericCategory> iter = list.iterator(); iter.hasNext();) {
			GenericCategory element = iter.next();
			if (element.isRoot()) {
				return element;
			}
		}
		return null;
	}

	/**
	 * @param categoryID categoryID
	 * @param categoryType categoryType
	 * @param includeDescendents Indicating if you want to include the entities linked with all of the descendent categories.  
	 *             flag value of false would return only those entities directly linked to the category. 
	 * @param date date
	 * @return set containing {@link GenericEntityIdentity} instances
	 */
	private Set<GenericEntityIdentity> getGenericEntitiesInCategoryAsOf(int categoryID, int categoryType, boolean includeDescendents, Date date,
			boolean ignoreEntityToCategoryDates) {
		if (!includeDescendents) {
			if (ignoreEntityToCategoryDates) {
				return getGenericEntitiesInCategoryAtAnyTime(categoryID, categoryType);
			}
			else {
				return getGenericEntitiesInCategoryAsOf(categoryID, categoryType, date);
			}
		}
		else {
			Set<GenericEntityIdentity> set = new HashSet<GenericEntityIdentity>();
			List<Integer> list = getAllDescendentCategoryIDsAsOf(categoryType, categoryID, date);
			if (list != null) {
				int catID;
				for (Iterator<Integer> iterator = list.iterator(); iterator.hasNext();) {
					catID = iterator.next().intValue();
					Set<GenericEntityIdentity> tempSet = getGenericEntitiesInCategoryAsOf(catID, categoryType, false, date, ignoreEntityToCategoryDates);
					if (tempSet != null) {
						set.addAll(tempSet);
					}
				}
			}
			return set;
		}
	}

	/**
	 * @param categoryID categoryID
	 * @param categoryType categoryType
	 * @return a set of {@link GenericEntityIdentity} instances;
	 *         only those entities that are directly linked to the category
	 */
	private Set<GenericEntityIdentity> getGenericEntitiesInCategoryAsOf(int categoryID, int categoryType, Date date) {
		ChildAssociationKeySet keySet = getCategoryToEntityCachKeySet(categoryID, categoryType);
		List<Integer> list = keySet.getChildrendAsOf(date);
		Set<GenericEntityIdentity> set = new HashSet<GenericEntityIdentity>();
		for (Iterator<Integer> iter = list.iterator(); iter.hasNext();) {
			int id = iter.next().intValue();
			set.add(new GenericEntityIdentity(GenericEntityType.forCategoryType(categoryType).getID(), id));
		}
		return set;
	}

	private Set<GenericEntityIdentity> getGenericEntitiesInCategoryAtAnyTime(int categoryID, int categoryType) {
		ChildAssociationKeySet keySet = getCategoryToEntityCachKeySet(categoryID, categoryType);
		Set<GenericEntityIdentity> set = new HashSet<GenericEntityIdentity>();
		for (Iterator<MutableTimedAssociationKey> iter = keySet.iterator(); iter.hasNext();) {
			TimedAssociationKey key = iter.next();
			set.add(new GenericEntityIdentity(GenericEntityType.forCategoryType(categoryType).getID(), key.getAssociableID()));
		}
		return set;
	}

	public GenericEntityIdentity[] getGenericEntitiesInCategorySetAsOfWithDescendents(int categoryType, int[] categoryIDs, Date date, boolean ignoreEntityToCategoryDates) {
		// [1] build cat map for Union / Intersaction
		if (categoryIDs == null) return new GenericEntityIdentity[0];
		Map<Integer, List<Integer>> catMap = new HashMap<Integer, List<Integer>>();
		for (int i = 0; i < categoryIDs.length; i++) {
			int parentID = getAncestorCategoryIDAsOf(categoryType, categoryIDs[i], date);
			if (parentID != -1) {
				Integer key = new Integer(parentID);
				Integer idObject = new Integer(categoryIDs[i]);
				if (catMap.containsKey(key)) {
					List<Integer> tempList = catMap.get(key);
					if (!tempList.contains(idObject)) {
						tempList.add(idObject);
					}
				}
				else {
					List<Integer> newList = new ArrayList<Integer>();
					newList.add(idObject);
					catMap.put(key, newList);
				}
			}
		}

		// [2] build union with the same keys, and intersection on different keys
		Set<GenericEntityIdentity> catSet = null;
		for (Iterator<Integer> iter = catMap.keySet().iterator(); iter.hasNext();) {
			List<Integer> idList = catMap.get(iter.next());
			Set<GenericEntityIdentity> unionSet = null;
			for (Iterator<Integer> iter2 = idList.iterator(); iter2.hasNext();) {
				Integer idObject = iter2.next();
				if (unionSet == null) {
					unionSet = getGenericEntitiesInCategoryAsOf(idObject.intValue(), categoryType, true, date, ignoreEntityToCategoryDates);
				}
				else {
					unionSet = SetOperations.union(unionSet, getGenericEntitiesInCategoryAsOf(idObject.intValue(), categoryType, true, date, ignoreEntityToCategoryDates));
				}
			}

			if (catSet == null) {
				catSet = unionSet;
			}
			else {
				catSet = SetOperations.intersection(catSet, unionSet);
			}
		}
		return (catSet == null ? new GenericEntityIdentity[0] : catSet.toArray(new GenericEntityIdentity[0]));
	}

	/**
	 * @param categoryIDs categoryIDs
	 * @param categoryType categoryType
	 * @param date date
	 * @return a set of {@link GenericEntityIdentity} instances;
	 *         only those entities that are directly linked to the category
	 */
	public GenericEntityIdentity[] getGenericEntitiesInCategorySetAsOfWithOutDescendents(int categoryType, int[] categoryIDs, Date date) {
		if (categoryIDs == null) return new GenericEntityIdentity[0];
		Set<GenericEntityIdentity> set = new HashSet<GenericEntityIdentity>();
		for (int i = 0; i < categoryIDs.length; i++) {
			set.addAll(getGenericEntitiesInCategoryAsOf(categoryIDs[i], categoryType, date));
		}
		return (set == null ? new GenericEntityIdentity[0] : set.toArray(new GenericEntityIdentity[0]));
	}

	/**
	 * @param categoryIDs categoryIDs
	 * @param categoryType categoryType
	 * @param includeCategoryDescendents includeCategoryDescendents
	 * @return a set of {@link GenericEntityIdentity} instances;
	 * Only those entities that are directly linked to the category. 
	 */
	public GenericEntityIdentity[] getGenericEntitiesInCategorySetAtAnyTime(int categoryType, int[] categoryIDs, boolean includeCategoryDescendents) {
		if (categoryIDs == null) return new GenericEntityIdentity[0];
		if (includeCategoryDescendents) {
			return getGenericEntitiesInCategorySetAtAnyTimeWithDescendents(categoryType, categoryIDs);
		}
		else {
			return getGenericEntitiesInCategorySetAtAnyTimeWithoutDescendents(categoryType, categoryIDs);
		}
	}

	private GenericEntityIdentity[] getGenericEntitiesInCategorySetAtAnyTimeWithDescendents(int categoryType, int[] categoryIDs) {
		return getGenericEntitiesInCategorySetAtAnyTimeWithDescendentsAsSet(categoryType, categoryIDs).toArray(new GenericEntityIdentity[0]);
	}

	private Set<GenericEntityIdentity> getGenericEntitiesInCategorySetAtAnyTimeWithDescendentsAsSet(int categoryType, int[] categoryIDs) {
		// we need to use date synonyms used in the system and iterate them
		// otherwise, we cannot determine union/intersection relationships
		Set<DateSynonym> dsSet = getDateSynonymsForChangesInCategoryRelationships(categoryType);
		Set<GenericEntityIdentity> resultSet = new HashSet<GenericEntityIdentity>();
		if (dsSet.isEmpty()) {
			resultSet.addAll(Arrays.asList(getGenericEntitiesInCategorySetAsOfWithDescendents(categoryType, categoryIDs, new Date(), true)));
		}
		else {
			for (Iterator<DateSynonym> iter = dsSet.iterator(); iter.hasNext();) {
				DateSynonym dateSynonym = iter.next();
				resultSet.addAll(Arrays.asList(getGenericEntitiesInCategorySetAsOfWithDescendents(categoryType, categoryIDs, dateSynonym.getDate(), true)));
			}
		}
		return resultSet;
	}

	private GenericEntityIdentity[] getGenericEntitiesInCategorySetAtAnyTimeWithoutDescendents(int categoryType, int[] categoryIDs) {
		return getGenericEntitiesInCategorySetAtAnyTimeWithoutDescendentsAsSet(categoryType, categoryIDs).toArray(new GenericEntityIdentity[0]);
	}

	private Set<GenericEntityIdentity> getGenericEntitiesInCategorySetAtAnyTimeWithoutDescendentsAsSet(int categoryType, int[] categoryIDs) {
		Set<GenericEntityIdentity> set = new HashSet<GenericEntityIdentity>();
		for (int i = 0; i < categoryIDs.length; i++) {
			int categoryID = categoryIDs[i];
			ChildAssociationKeySet keySet = getCategoryToEntityCachKeySet(categoryID, categoryType);
			for (Iterator<MutableTimedAssociationKey> iter = keySet.iterator(); iter.hasNext();) {
				TimedAssociationKey key = iter.next();
				int id = key.getAssociableID();
				set.add(new GenericEntityIdentity(GenericEntityType.forCategoryType(categoryType).getID(), id));
			}
		}
		return set;
	}

	/**
	 * Return the most recent fully qualified category name. 
	 * @param category category
	 * @return fully qualified name.
	 * This is similar method in com.mindbox.pe.client.EntityModelCacheFactory 
	 */
	public String getMostRecentFullyQualifiedCategoryName(GenericCategory category) {
		String fullyQualifiedName = null;
		if (category.isRoot()) {
			fullyQualifiedName = category.getName();
		}
		else {
			List<GenericCategory> path = getMostRecentPathToRoot(category);
			for (Iterator<GenericCategory> i = path.iterator(); i.hasNext();) {
				GenericCategory pathCat = i.next();
				fullyQualifiedName = fullyQualifiedName == null ? pathCat.getName() : fullyQualifiedName + Constants.CATEGORY_PATH_DELIMITER_REPORT + pathCat.getName();
			}
		}
		return fullyQualifiedName;
	}

	/*
	 * This is a copy of method in com.mindbox.pe.client.EntityModelCacheFactory 
	 */
	private List<GenericCategory> getMostRecentPathToRoot(GenericCategory category) {
		List<GenericCategory> path = new ArrayList<GenericCategory>();
		if (category.isRoot()) {
			path.add(0, category);
		}
		else {
			path = getMostRecentPathToRoot(category, path);
		}
		return path;
	}

	/**
	 * This is a copy of method in com.mindbox.pe.client.EntityModelCacheFactory 
	 */
	private List<GenericCategory> getMostRecentPathToRoot(GenericCategory child, List<GenericCategory> path) {
		path.add(0, child);
		if (!child.isRoot()) {
			List<MutableTimedAssociationKey> parentKeys = child.getAllParentAssociations();
			Collections.sort(parentKeys, TimedAssociationKeyComparator.getInstance());
			TimedAssociationKey parentKey = parentKeys.get(0);
			GenericCategory parent = getGenericCategory(child.getType(), parentKey.getAssociableID());
			path = getMostRecentPathToRoot(parent, path);
		}
		return path;
	}

	/**
	 * Groups the specified category ids according to set INTERSECTION/UNION property.
	 * Used for generating object pattern for guideline rules when categories are in the context.
	 * @param categoryType categoryType
	 * @param categoryIDs categoryIDs
	 * @param date date
	 * @return list of collections (java.util.Collection); nevern null
	 * @throws IllegalArgumentException if <code>categoryType</code> is invalid or 
	 *                                  <code>categoryIDs</code> contain an invalid category id
	 */
	public List<Collection<Integer>> groupForContextMatchCategoryArgAsOf(int categoryType, int[] categoryIDs, Date date) {
		Map<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();
		for (int i = 0; i < categoryIDs.length; i++) {
			if (!hasGenericCategory(categoryType, categoryIDs[i])) {
				throw new IllegalArgumentException("No category of id " + categoryIDs[i] + " found for category type " + categoryType);
			}
			Integer ancestor = new Integer(getAncestorCategoryIDAsOf(categoryType, categoryIDs[i], date));
			// if it has no ancestor, skip it
			if (ancestor.intValue() > -1) {
				Set<Integer> set = null;
				if (map.containsKey(ancestor)) {
					set = map.get(ancestor);
				}
				else {
					set = new TreeSet<Integer>();
					map.put(ancestor, set);
				}
				set.add(categoryIDs[i]);
			}
		}
		List<Collection<Integer>> listList = new ArrayList<Collection<Integer>>();
		listList.addAll(map.values());
		return listList;
	}

	public boolean hasGenericCategory(int categoryType, int categoryID) {
		List<GenericCategory> list = getCategoryCacheList(categoryType);
		for (Iterator<GenericCategory> iter = list.iterator(); iter.hasNext();) {
			GenericCategory element = iter.next();
			if (element.getID() == categoryID) {
				return true;
			}
		}
		return false;
	}

	public boolean hasGenericEntity(GenericEntityType type, int entityID) {
		return getEntityCacheMap(type).containsKey(new Integer(entityID));
	}

	void insertEntity(GenericEntityType entityType, GenericEntity entity) {
		getEntityCacheMap(entityType).put(new Integer(entity.getID()), entity);
	}

	public void insertEntityCompatibility(GenericEntityCompatibilityData data) {
		insertEntityCompatibility(data.getSourceType(), data.getSourceID(), data.getGenericEntityType(), data.getAssociableID(), data.getEffectiveDate(), data.getExpirationDate());
	}

	public void insertEntityCompatibility(GenericEntityType entityType1, int entityID1, GenericEntityType entityType2, int entityID2, DateSynonym effectiveDate,
			DateSynonym expirationDate) {
		CompatibilityKey key = new CompatibilityKey(entityType1, entityID1, entityType2, entityID2);
		synchronized (compatibilityMap) {
			if (compatibilityMap.containsKey(key)) {
				compatibilityMap.remove(key);
			}
			compatibilityMap.put(key, new CompatibilityData(effectiveDate, expirationDate));
		}
	}

	/** 
	 * @param data data
	 * @return A copy of cached compatibility data, if it exists, otherwise null. 
	 */
	public GenericEntityCompatibilityData isCached(GenericEntityCompatibilityData data) {
		return isCached(data.getSourceType(), data.getSourceID(), data.getGenericEntityType(), data.getAssociableID());
	}

	/** 
	 * @param entityType1 entityType1
	 * @param entityID1 entityID1
	 * @param entityType2 entityType2
	 * @param entityID2 entityID2
	 * @return A copy of cached compatibility data, if it exists, otherwise null. 
	 */
	public GenericEntityCompatibilityData isCached(GenericEntityType entityType1, int entityID1, GenericEntityType entityType2, int entityID2) {
		CompatibilityKey key = new CompatibilityKey(entityType1, entityID1, entityType2, entityID2);
		if (compatibilityMap.containsKey(key)) {
			return (GenericEntityCompatibilityData) asCompatibilityData(key);
		}

		CompatibilityKey keyReversed = new CompatibilityKey(entityType2, entityID2, entityType1, entityID1);
		if (compatibilityMap.containsKey(keyReversed)) {
			return (GenericEntityCompatibilityData) asCompatibilityData(keyReversed);
		}

		return null;
	}

	/**
	 * Tests if the specified category id is a descendent of the specified category as of the specified date.
	 * @param categoryID id to check
	 * @param category the category to check
	 * @param date the date to check
	 * @return <code>true</code> if categoryID is a child category of category as of the specified date
	 */
	private boolean isDescendentAsOf(int categoryID, GenericCategory category, Date date) {
		for (Iterator<Integer> iter = category.getChildIDs(date).iterator(); iter.hasNext();) {
			int childID = iter.next().intValue();
			GenericCategory child = getGenericCategory(category.getType(), childID);
			if (child.getID() == categoryID) {
				return true;
			}
			else if (isDescendentAsOf(categoryID, child, date)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param childID childID
	 * @param parentID parentID
	 * @param categoryType categoryType
	 * @param date date
	 * @return true if categoryID is a child category of category
	 */
	public boolean isDescendentAsOf(int childID, int parentID, int categoryType, Date date) {
		GenericCategory category = findCategory(categoryType, parentID);
		return isDescendentAsOf(childID, category, date);
	}

	private boolean isDescendentAtAnyTime(int categoryID, GenericCategory category) {
		for (Iterator<MutableTimedAssociationKey> iter = category.getChildrenKeyIterator(); iter.hasNext();) {
			TimedAssociationKey key = iter.next();
			GenericCategory child = getGenericCategory(category.getType(), key.getAssociableID());
			if (child.getID() == categoryID) {
				return true;
			}
			else if (isDescendentAtAnyTime(categoryID, child)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tests if the specified childID is a chid of all the specified parent ids as of the specified date.
	 * @param childID the child id
	 * @param parentIDs parent ids
	 * @param categoryType category type
	 * @return <code>true</code> if childID is a child of all parentIDs
	 */
	public boolean isDescendentAtAnyTime(int childID, int parentIDs[], int categoryType) {
		for (int i = 0; i < parentIDs.length; i++) {
			int parentID = parentIDs[i];
			GenericCategory category = findCategory(categoryType, parentID);
			if (!isDescendentAtAnyTime(childID, category)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * This considers all dates.
	 * @param entityID entityID
	 * @param categoryIDs array
	 * @param categoryType categoryType
	 * @return true if entityID is a descenent of any of the categoryIDs in categoryIDs 
	 * @throws NullPointerException if <code>categoryIDs</code> is <code>null</code>
	 */
	public boolean isEntityDescendentOfCategorySetAtAnyTime(int entityID, int[] categoryIDs, int categoryType) {
		if (categoryIDs == null) throw new NullPointerException("categoryIDs cannot be null");
		GenericEntityIdentity[] identities = getGenericEntitiesInCategorySetAtAnyTimeWithDescendents(categoryType, categoryIDs);
		for (int i = 0; i < identities.length; i++) {
			if (entityID == identities[i].getEntityID()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tests if the specified date synonym is used by any data in EntityManager.
	 * @param dateSynonym dateSynonym
	 * @return <code>true</code> if <code>dateSynonym</code> is used by any data; 
	 *         <code>false</code>, otherwise
	 * @throws NullPointerException if <code>dateSynonym</code> is <code>null</code>
	 */
	public boolean isInUse(DateSynonym dateSynonym) {
		if (dateSynonym == null) throw new NullPointerException("dateSynonym cannot be null");
		// check compatibility
		synchronized (compatibilityMap) {
			for (CompatibilityData compatibilityData : compatibilityMap.values()) {
				if (dateSynonym.equals(compatibilityData.effDate) || dateSynonym.equals(compatibilityData.expDate)) {
					return true;
				}
			}
		}
		// check parent-child category assocations
		synchronized (categoryMap) {
			for (List<GenericCategory> categoryList : categoryMap.values()) {
				for (GenericCategory category : categoryList) {
					if (category.isInUseForChildAssociation(dateSynonym)) {
						return true;
					}
				}
			}
		}
		// check entity-category associations
		synchronized (categoryToEntityMap) {
			for (Map<Integer, ChildAssociationKeySet> keySetMap : categoryToEntityMap.values()) {
				for (ChildAssociationKeySet keySet : keySetMap.values()) {
					if (keySet.isInUse(dateSynonym)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * This considers all dates.
	 * @param parentIDs parentIDs
	 * @param childIDs childIDs
	 * @param categoryType categoryType
	 * @return true if all of the parentIDs are parents of all of the childIDs
	 */
	public boolean isParentCategoriesAtAnyTime(int[] parentIDs, int[] childIDs, int categoryType) {
		for (int i = 0; i < parentIDs.length; i++) {
			int parentCatID = parentIDs[i];
			for (int j = 0; j < childIDs.length; j++) {
				if (!isParentCategoryAtAnyTime(parentCatID, childIDs, categoryType)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @param parentID parentID
	 * @param childIDs childIDs
	 * @param categoryType categoryType
	 * @return true if the parentID is a parent of all of the childIDs or if childIDs is empty.
	 */
	public boolean isParentCategoryAtAnyTime(int parentID, int[] childIDs, int categoryType) {
		for (int j = 0; j < childIDs.length; j++) {
			int childCatID = childIDs[j];
			if (!isDescendentAtAnyTime(childCatID, new int[] { parentID }, categoryType)) {
				return false;
			}
		}
		return true;
	}

	private void removeAllCategoryToGenericEntity(int entityID, GenericEntityType entityType) {
		Map<Integer, ChildAssociationKeySet> map = getCategoryToEntityCacheMap(entityType.getCategoryType());
		for (Iterator<ChildAssociationKeySet> iter = map.values().iterator(); iter.hasNext();) {
			ChildAssociationKeySet keySet = iter.next();
			keySet.removeAll(entityID);
		}
	}

	private void removeAllChildCatgoryLinksFromParents(GenericCategory category) {
		for (Iterator<MutableTimedAssociationKey> iter = category.getParentKeyIterator(); iter.hasNext();) {
			MutableTimedAssociationKey key = iter.next();
			GenericCategory oldParent = getGenericCategory(category.getType(), key.getAssociableID());
			if (oldParent != null) {
				oldParent.removeAllChildAssociations(category.getID());
			}
		}
	}

	public void removeAllEntityCompatibility(GenericEntityType entityType, int entityID) {
		synchronized (compatibilityMap) {
			List<CompatibilityKey> removeList = new ArrayList<CompatibilityKey>();
			for (Iterator<CompatibilityKey> iter = compatibilityMap.keySet().iterator(); iter.hasNext();) {
				CompatibilityKey compatibilityKey = iter.next();
				if ((compatibilityKey.type1 == entityType && compatibilityKey.id1 == entityID) || (compatibilityKey.type2 == entityType && compatibilityKey.id2 == entityID)) {
					removeList.add(compatibilityKey);
				}
			}
			for (Iterator<CompatibilityKey> iter = removeList.iterator(); iter.hasNext();) {
				CompatibilityKey keyToRemove = iter.next();
				compatibilityMap.remove(keyToRemove);
			}
		}
	}

	private void removeAllGenericEntityToCategory(int categoryID, int categoryType) {
		// update entities, first
		GenericEntityType type = GenericEntityType.forCategoryType(categoryType);
		ChildAssociationKeySet keySet = getCategoryToEntityCachKeySet(categoryID, categoryType);
		for (Iterator<MutableTimedAssociationKey> iter = keySet.iterator(); iter.hasNext();) {
			MutableTimedAssociationKey element = iter.next();
			GenericEntity entity = getEntity(type, element.getAssociableID());
			if (entity != null) {
				entity.removeCategoryAssociation(new DefaultMutableTimedAssociationKey(categoryID, element.getEffectiveDate(), element.getExpirationDate()));
			}
		}
		// remove from cache map
		Map<Integer, ChildAssociationKeySet> map = getCategoryToEntityCacheMap(categoryType);
		Integer key = new Integer(categoryID);
		if (map.containsKey(key)) {
			keySet = map.get(key);
			keySet.clear();
			map.remove(key);
		}
	}

	public void removeCategory(int categoryType, int categoryID) {
		GenericCategory category = getGenericCategory(categoryType, categoryID);
		if (category != null) {
			removeAllChildCatgoryLinksFromParents(category);
			getCategoryCacheList(categoryType).remove(category);
		}
		removeAllGenericEntityToCategory(categoryID, categoryType);
	}

	public void removeEntity(GenericEntityType type, int entityID) {
		Integer key = new Integer(entityID);
		Map<Integer, GenericEntity> entityMapCache = getEntityCacheMap(type);
		if (entityMapCache.containsKey(key)) {
			entityMapCache.remove(key);
			removeAllCategoryToGenericEntity(entityID, type);
			removeAllEntityCompatibility(type, entityID);
		}
	}

	public void removeEntityCompatibility(GenericEntityType entityType1, int entityID1, GenericEntityType entityType2, int entityID2) {
		CompatibilityKey key = new CompatibilityKey(entityType1, entityID1, entityType2, entityID2);
		synchronized (compatibilityMap) {
			if (compatibilityMap.containsKey(key)) {
				compatibilityMap.remove(key);
			}
		}
	}

	public synchronized void startLoading() {
		entityMap.clear();
		compatibilityMap.clear();
		categoryMap.clear();
		categoryToEntityMap.clear();
	}

	@Override
	public String toString() {
		return "EntityManager[" + entityMap.size() + "," + compatibilityMap.size() + "]";
	}

	public void updateCache(GenericCategory cached, GenericCategory source) {
		logger.debug(">>> updateCached: cached=" + cached + ",source=" + source);
		cached.setName(source.getName());
		if (!cached.hasSameParentAssociations(source)) {
			// remove as child from all of old parents
			removeAllChildCatgoryLinksFromParents(cached);

			// add as child to all of new parents
			cached.setParentAssociations(source);
			for (Iterator<MutableTimedAssociationKey> iter = source.getParentKeyIterator(); iter.hasNext();) {
				MutableTimedAssociationKey parentKey = iter.next();
				addChildCategoryLinkToParent(cached, parentKey);
			}
		}
	}


	public void updateCache(GenericEntity entity) {
		GenericEntity cached = (GenericEntity) getEntity(entity.getType(), entity.getID());
		updateCache(cached, entity);
	}

	private void updateCache(GenericEntity cached, GenericEntity source) {
		cached.copyFrom(source);
		// Needs to update category associations source is identical to cache
		// or if category associations has changed
		removeAllCategoryToGenericEntity(source.getId(), source.getType());
		for (Iterator<MutableTimedAssociationKey> iter = cached.getCategoryIterator(); iter.hasNext();) {
			MutableTimedAssociationKey key = iter.next();
			addGenericEntityToCategory_internal(cached.getID(), cached.getType().getID(), cached.getType().getCategoryType(), key, false);
		}
	}

	public void updateCategoryAssociationDateSynonyms(DateSynonym ds) {
		for (List<GenericCategory> GenericCategoryList : categoryMap.values()) {
			for (int i = 0; i < GenericCategoryList.size(); i++) {
				GenericCategory category = GenericCategoryList.get(i);
				for (Iterator<MutableTimedAssociationKey> j = category.getParentKeyIterator(); j.hasNext();) {
					MutableTimedAssociationKey key = j.next();
					key.updateEffExpDates(ds);
				}
			}
		}
	}

	public void updateEntityAssociationDateSynonyms(DateSynonym ds) {
		GenericEntityType[] types = GenericEntityType.getAllGenericEntityTypes();
		for (int i = 0; i < types.length; i++) {
			for (Iterator<GenericEntity> iter = getEntityCacheMap(types[i]).values().iterator(); iter.hasNext();) {
				GenericEntity entity = iter.next();
				for (Iterator<MutableTimedAssociationKey> k = entity.getCategoryIterator(); k.hasNext();) {
					MutableTimedAssociationKey key = k.next();
					key.updateEffExpDates(ds);
				}
			}
		}
	}

	public void updateEntityCompatibility(GenericEntityCompatibilityData data) {
		updateEntityCompatibility(data.getSourceType(), data.getSourceID(), data.getGenericEntityType(), data.getAssociableID(), data.getEffectiveDate(), data.getExpirationDate());
	}

	public void updateEntityCompatibility(GenericEntityType entityType1, int entityID1, GenericEntityType entityType2, int entityID2, DateSynonym effectiveDate,
			DateSynonym expirationDate) {
		CompatibilityKey key = new CompatibilityKey(entityType1, entityID1, entityType2, entityID2);
		synchronized (compatibilityMap) {
			if (compatibilityMap.containsKey(key)) {
				CompatibilityData data = compatibilityMap.get(key);
				data.effDate = effectiveDate;
				data.expDate = expirationDate;
			}
			else {
				compatibilityMap.put(key, new CompatibilityData(effectiveDate, expirationDate));
			}
		}
	}

}