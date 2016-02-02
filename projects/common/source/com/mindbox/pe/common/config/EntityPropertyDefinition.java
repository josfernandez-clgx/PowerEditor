package com.mindbox.pe.common.config;

import java.io.Serializable;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public class EntityPropertyDefinition implements Serializable {

	private static final long serialVersionUID = 200404109002L;

	private String name;
	private String displayName;
	private String type;
	private boolean useInSelectionTable = false;
	private boolean isRequired = false;
	private boolean isSearchable = false;
	private String enumType;
	private String attributeMap;
	private String autoUpdatedDateProperty;
	private boolean sort;
	private boolean allowMultiple;

	public String getAutoUpdatedDateProperty() {
		return autoUpdatedDateProperty;
	}

	public void setAutoUpdatedDateProperty(String autoUpdatedDateProperty) {
		this.autoUpdatedDateProperty = autoUpdatedDateProperty;
	}

	public boolean isAttributeMapSet() {
		return attributeMap != null && attributeMap.trim().length() > 0;
	}
	
	public boolean allowMultiple() {
		return allowMultiple;
	}

	public void setAllowMultiple(String value) {
		this.allowMultiple = ConfigUtil.asBoolean(value);
	}

	public String getAttributeMap() {
		return attributeMap;
	}

	public void setAttributeMap(String attributeMap) {
		this.attributeMap = attributeMap;
	}

	public boolean sort() {
		return sort;
	}

	public void setSort(String value) {
		this.sort = ConfigUtil.asBoolean(value);
	}

	public String getEnumType() {
		return enumType;
	}

	public void setEnumType(String enumType) {
		this.enumType = enumType;
	}

	public void setShowInSelectionTable(String value) {
		useInSelectionTable = ConfigUtil.asBoolean(value);
	}

	public void setIsRequired(String value) {
		isRequired = ConfigUtil.asBoolean(value);
	}

	public void setIsSearchable(String value) {
		isSearchable = ConfigUtil.asBoolean(value);
	}

	public boolean isRequired() {
		return isRequired;
	}

	public boolean isSearchable() {
		return isSearchable;
	}

	public boolean useInSelectionTable() {
		return useInSelectionTable;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public void setDisplayName(String string) {
		displayName = string;
	}

	public void setName(String string) {
		name = string;
	}

	public void setType(String string) {
		type = ConfigUtil.asGenericEntityPropertyType(string);
	}

	public String toString() {
		return "Property[" + name + ",type=" + type + ",dispName=" + displayName + ",required?=" + isRequired + ",inSelectionTbl?="
				+ useInSelectionTable + ",searchable?=" + isSearchable + "]";
	}

}