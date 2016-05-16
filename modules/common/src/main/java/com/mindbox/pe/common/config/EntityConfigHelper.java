package com.mindbox.pe.common.config;

import java.util.List;

import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.xsd.config.CategoryType;
import com.mindbox.pe.xsd.config.EntityConfig;
import com.mindbox.pe.xsd.config.EntityProperty;
import com.mindbox.pe.xsd.config.EntityPropertyType;
import com.mindbox.pe.xsd.config.EntityType;

public class EntityConfigHelper {

	private final EntityConfig entityConfig;

	public EntityConfigHelper(final EntityConfig entityConfig) {
		this.entityConfig = entityConfig;
	}

	/**
	 * 
	 * @param typeID typeID
	 * @return category type definition with id of <code>typeID</code>, if found; <code>null</code>, otherwise
	 * @since PowerEditor 3.1.0
	 */
	public CategoryType findCategoryTypeDefinition(int typeID) {
		for (CategoryType categoryType : entityConfig.getCategoryType()) {
			if (categoryType.getTypeID().intValue() == typeID) {
				return categoryType;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param type type
	 * @return null if not found
	 */
	public EntityType findEntityTypeDefinition(GenericEntityType type) {
		return (type == null ? null : findEntityTypeDefinition(type.getID()));
	}

	/**
	 * 
	 * @param id id
	 * @return null if not found
	 */
	public EntityType findEntityTypeDefinition(int id) {
		for (final EntityType entityType : entityConfig.getEntityType()) {
			if (entityType.getTypeID().intValue() == id) {
				return entityType;
			}
		}
		return null;
	}

	public GenericEntityType findEntityTypeForCategoryType(int categoryTypeId) {
		for (CategoryType categoryType : entityConfig.getCategoryType()) {
			if (categoryType.getTypeID().intValue() == categoryTypeId) {
				return GenericEntityType.forCategoryType(categoryType.getTypeID().intValue());
			}
		}
		return null;
	}

	public String findPropertyDisplayName(final GenericEntityType entityType, final String name) {
		for (final EntityProperty entityProperty : findEntityTypeDefinition(entityType).getEntityProperty()) {
			if (entityProperty.getName().equals(name)) {
				return entityProperty.getDisplayName();
			}
		}
		return null;
	}

	public EntityPropertyType findPropertyType(final GenericEntityType entityType, final String name) {
		return ConfigUtil.findPropertyType(findEntityTypeDefinition(entityType), name);
	}

	/**
	 * 
	 * @param type type
	 * @return category type definition for <code>type</code>, if found; <code>null</code>, otherwise
	 * @since PowerEditor 3.1.0
	 */
	public CategoryType getCategoryDefinition(final GenericEntityType type) {
		EntityType typeDef = findEntityTypeDefinition(type);
		if (typeDef != null && typeDef.getCategoryType() != null && typeDef.getCategoryType().intValue() > 0) {
			return findCategoryTypeDefinition(typeDef.getCategoryType().intValue());
		}
		else {
			return null;
		}
	}

	public List<CategoryType> getCategoryTypeDefinitions() {
		return entityConfig.getCategoryType();
	}

	public List<EntityType> getEntityTypeDefinitions() {
		return entityConfig.getEntityType();
	}

	public EntityType getEntityTypeForMessageContext() {
		for (final EntityType entityType : entityConfig.getEntityType()) {
			if (ConfigUtil.isUseInMessageContext(entityType)) {
				return entityType;
			}
		}
		return null;
	}

	public boolean hasProperty(final GenericEntityType genericEntityType, final String name) {
		for (final EntityProperty entityProperty : findEntityTypeDefinition(genericEntityType).getEntityProperty()) {
			if (entityProperty.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tests if this has the specified property group definition.
	 * @param genericEntityType entity type
	 * @param groupName the name of property group to check for
	 * @return <code>true</code> if this has <code>groupName</code> property group; <code>false</code>, otherwise
	 * @since PowerEditor 4.3.1
	 */
	public boolean hasPropertyGroup(final GenericEntityType genericEntityType, String groupName) {
		for (final EntityProperty entityProperty : findEntityTypeDefinition(genericEntityType).getEntityProperty()) {
			if (entityProperty.getName().equals(groupName)) {
				return true;
			}
		}
		return false;
	}
}
