package com.mindbox.pe.model.table;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;

public class CategoryOrEntityValue implements Serializable, GridCellValue {

	private static final long serialVersionUID = 20070515000010L;

	public static String asString(GenericEntityType entityType, boolean forEntity, int id) {
		return entityType.getName() + ":" + forEntity + ":" + id;
	}

	public static CategoryOrEntityValue valueOf(String strValue) {
		String[] strs = strValue.split(":");
		try {
			if (strs.length != 3) throw new IllegalArgumentException("Invalid string format");
			CategoryOrEntityValue value = new CategoryOrEntityValue();
			value.entityType = GenericEntityType.forName(strs[0]);
			if (value.entityType == null) throw new IllegalArgumentException("Invalid entity type");
			value.forEntity = Boolean.valueOf(strs[1]).booleanValue();
			value.id = Integer.parseInt(strs[2]);
			return value;
		}
		catch (Exception exception) {
			Logger.getLogger(CategoryOrEntityValue.class).warn("Failed to parse for EnumValues: " + strValue, exception);
			return null;
		}
	}
	
	public static CategoryOrEntityValue valueOf(String strValue, String entityType, boolean allowEntity, boolean allowCategory) {
		if (entityType == null) throw new NullPointerException("entityType must be specified");
		if (!allowCategory && !allowEntity)
			throw new IllegalArgumentException("at least one of allowEntity or allowCategory must be true");
		if (UtilBase.isEmpty(strValue)) return null;
		String[] strs = strValue.split(":");
		try {
			if (strs.length != 3) throw new IllegalArgumentException("Invalid string format");
			CategoryOrEntityValue value = new CategoryOrEntityValue();
			value.entityType = GenericEntityType.forName(strs[0]);
			if (value.entityType == null) throw new IllegalArgumentException("Invalid entity type");
			value.forEntity = Boolean.valueOf(strs[1]).booleanValue();
			if ((allowEntity && value.isForEntity()) || (allowCategory && !value.isForEntity())) {
				value.id = Integer.parseInt(strs[2]);
				return value;
			}
			else {
				throw new IllegalArgumentException((allowEntity ? "category not allowed" : "entity not allowed"));
			}
		}
		catch (Exception exception) {
			Logger.getLogger(CategoryOrEntityValue.class).warn("Failed to parse for EnumValues: " + strValue, exception);
			return null;
		}
	}

	private GenericEntityType entityType;
	private boolean forEntity;
	private int id;

	public CategoryOrEntityValue() {
	}

	public CategoryOrEntityValue(GenericEntity entity) {
		this(entity.getType(), true, entity.getID());
	}

	public CategoryOrEntityValue(GenericEntityType entityType, boolean forEntity, int id) {
		this.entityType = entityType;
		this.forEntity = forEntity;
		this.id = id;
	}

	private CategoryOrEntityValue(CategoryOrEntityValue source) {
		this(source.entityType, source.forEntity, source.id);
	}

	public GridCellValue copy() {
		return new CategoryOrEntityValue(this);
	}

	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (!obj.getClass().getName().equals(this.getClass().getName())) // safe for subclasses and multiple classloaders
			return false;

		CategoryOrEntityValue that = (CategoryOrEntityValue) obj;
		return this.id == that.id && this.forEntity == that.forEntity && this.entityType == that.entityType;
	}

	public int hashCode() {
		int result = 31;
		result = result + 17 * id;
		result = result + 17 * (forEntity ? 1 : 0);
		result = result + 17 * (entityType == null ? 0 : entityType.hashCode());
		return result;
	}

	public GenericEntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(GenericEntityType entityType) {
		this.entityType = entityType;
	}

	public boolean isForEntity() {
		return forEntity;
	}

	public void setForEntity(boolean forEntity) {
		this.forEntity = forEntity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String toString() {
		return asString(entityType, forEntity, id);
	}
}
