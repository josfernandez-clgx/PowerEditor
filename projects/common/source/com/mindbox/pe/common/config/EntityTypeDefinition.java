/*
 * Created on 2004. 4. 15.
 *
 */
package com.mindbox.pe.common.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Entity type definition.
 * Represents &lt;EntityType&gt; tag in PowerEditorConfiguration.xml.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public class EntityTypeDefinition extends CategoryTypeDefinition {

	private static final long serialVersionUID = 200404109001L;

	private boolean useInContext = false;
	private boolean useInMessageContext = false;
	private boolean useInCompatibility = false;
	private int categoryType;
	private final List<EntityPropertyDefinition> propertyList = new ArrayList<EntityPropertyDefinition>();
	private final List<EntityPropertyGroupDefinition> propertyGroupList = new ArrayList<EntityPropertyGroupDefinition>();
	private String displayName = null;
	private boolean canClone = false;
	private boolean canBelongToMultipleCategories = true;
	private boolean uniqueCategoryNames = true;
	private boolean uniqueEntityNames = true;

	public void addPropertyDefinition(EntityPropertyDefinition def) {
		propertyList.add(def);
	}

	/**
	 * 
	 * @param def
	 * @since PowerEditor 4.3.1
	 */
	public void addPropertyGroup(EntityPropertyGroupDefinition def) {
		propertyGroupList.add(def);
	}

	/**
	 * Get all property group definitions for this.
	 * @return an array of entity property group definitions
	 * @since PowerEditor 4.3.1
	 */
	public EntityPropertyGroupDefinition[] getEntityPropertyGroupDefinitions() {
		return propertyGroupList.toArray(new EntityPropertyGroupDefinition[0]);
	}

	/**
	 * Tests if this has the specified property group definition.
	 * @param groupName the name of property group to check for
	 * @return <code>true</code> if this has <code>groupName</code> property group; <code>false</code>, otherwise
	 * @since PowerEditor 4.3.1
	 */
	public boolean hasPropertyGroup(String groupName) {
		for (Iterator<EntityPropertyGroupDefinition> iter = propertyGroupList.iterator(); iter.hasNext();) {
			EntityPropertyGroupDefinition element = iter.next();
			if (element.getName().equals(groupName)) {
				return true;
			}
		}
		return false;
	}

	public EntityPropertyDefinition[] getEntityPropertyDefinitions() {
		return propertyList.toArray(new EntityPropertyDefinition[0]);
	}

	public boolean hasProperty(String name) {
		for (Iterator<EntityPropertyDefinition> iter = propertyList.iterator(); iter.hasNext();) {
			EntityPropertyDefinition element = iter.next();
			if (element.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public String findPropertyType(String name) {
		for (Iterator<EntityPropertyDefinition> iter = propertyList.iterator(); iter.hasNext();) {
			EntityPropertyDefinition element = iter.next();
			if (element.getName().equals(name)) {
				return element.getType();
			}
		}
		return null;
	}

	public String findPropertyDisplayName(String name) {
		for (Iterator<EntityPropertyDefinition> iter = propertyList.iterator(); iter.hasNext();) {
			EntityPropertyDefinition element = iter.next();
			if (element.getName().equals(name)) {
				return element.getDisplayName();
			}
		}
		return null;
	}

	public boolean canClone() {
		return canClone;
	}

	public void setCanClone(String value) {
		canClone = ConfigUtil.asBoolean(value);
	}

	public boolean useInContext() {
		return useInContext;
	}

	/**
	 * For bean property support.
	 * 
	 * NOTE: DO NOT CHANGE the name of this method to <code>usUseInContext()</code>.
	 */
	public boolean isUsedInContext() {
		return useInContext;
	}

	public boolean useInMessageContext() {
		return useInMessageContext;
	}

	public void setUseInContext(String value) {
		useInContext = ConfigUtil.asBoolean(value);
	}

	public void setUseInMessageContext(String value) {
		useInMessageContext = ConfigUtil.asBoolean(value);
	}

	/**
	 * 
	 * @return <code>true</code> if this entity type is used in compatibility;
	 *         <code>false</code>, otherwise
	 * @since PowerEditor 3.0.1
	 */
	public boolean useInCompatibility() {
		return useInCompatibility;
	}

	/**
	 * 
	 * @param value
	 * @since PowerEditor 3.0.1
	 */
	public void setUseInCompatibility(String value) {
		useInCompatibility = ConfigUtil.asBoolean(value);
	}

	public boolean hasCategory() {
		return categoryType > 0;
	}

	// For bean property support
	public boolean isCategoryEnabled() {
		return hasCategory();
	}

	public int getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(int i) {
		categoryType = i;
	}

	/**
	 * @return Returns the displayName.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName The displayName to set.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean canBelongToMultipleCategories() {
		return canBelongToMultipleCategories;
	}

	public void setCanBelongToMultipleCategories(boolean canBelongToMultipleCategories) {
		this.canBelongToMultipleCategories = canBelongToMultipleCategories;
	}

	public String toString() {
		return "EntityTypeDef[" + getName() + "=" + getTypeID() + ",inContext?=" + useInContext + ",inMsgContext?=" + useInMessageContext
				+ ",inCompatibility?=" + useInCompatibility + ",clone?=" + canClone + ",cat=" + categoryType + "]";
	}

	public boolean uniqueCategoryNames() {
		return uniqueCategoryNames;
	}

	public void setUniqueCategoryNames(boolean uniqueCategoryNames) {
		this.uniqueCategoryNames = uniqueCategoryNames;
	}

	public boolean uniqueEntityNames() {
		return uniqueEntityNames;
	}

	public void setUniqueEntityNames(boolean uniqueEntityNames) {
		this.uniqueEntityNames = uniqueEntityNames;
	}

}