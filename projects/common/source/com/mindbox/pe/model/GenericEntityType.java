package com.mindbox.pe.model;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.util.HashMap;
import java.util.Map;

import com.mindbox.pe.common.config.EntityTypeDefinition;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public final class GenericEntityType extends EnumerationBase {

	private static final long serialVersionUID = 2004041510000L;

	private static final Map<Integer, GenericEntityType> instanceMap = new HashMap<Integer, GenericEntityType>();
	private static final Map<Integer, EntityTypeDefinition> entityTypeMap = new HashMap<Integer, EntityTypeDefinition>();

	public static synchronized GenericEntityType makeInstance(EntityTypeDefinition etDef) {
		GenericEntityType entityType = new GenericEntityType(
				etDef.getTypeID(),
				etDef.getName(),
				etDef.getDisplayName(),
				etDef.getCategoryType(),
				etDef.isUsedInContext());
		instanceMap.put(entityType.getID(), entityType);
		entityTypeMap.put(entityType.getID(), etDef);
		return entityType;
	}

	public static EntityTypeDefinition getEntityTypeDefinition(GenericEntityType type) {
		return type == null ? null : entityTypeMap.get(type.getID());
	}

	public static GenericEntityType valueOf(EntityTypeDefinition etDef) {
		return forID(etDef.getTypeID());
	}

	public static GenericEntityType[] getAllGenericEntityTypes() {
		return instanceMap.values().toArray(new GenericEntityType[0]);
	}

	public static boolean hasTypeFor(int id) {
		return instanceMap.containsKey(new Integer(id));
	}

	public static GenericEntityType forID(int id) {
		Integer key = new Integer(id);
		if (instanceMap.containsKey(key)) {
			return (GenericEntityType) instanceMap.get(key);
		}
		throw new IllegalArgumentException("Invalid generic entity type id: " + id);
	}

	public static GenericEntityType forName(String name) {
		if (name == null || name.length() == 0) return null;
		for (GenericEntityType element : instanceMap.values()) {
			if (element.name.equalsIgnoreCase(name)) {
				return element;
			}
		}
		return null;
	}

	public static GenericEntityType forCategoryType(int categoryType) {
		for (GenericEntityType element : instanceMap.values()) {
			if (element.getCategoryType() == categoryType) {
				return element;
			}
		}
		return null;
	}

	public static boolean isValidCategoryType(int categoryType) {
		if (categoryType < 1) return false;
		for (GenericEntityType element : instanceMap.values()) {
			if (element.getCategoryType() == categoryType) {
				return true;
			}
		}
		return false;
	}

	private final String displayName;
	private final int categoryType;
	private final boolean useInContext;

	private GenericEntityType(int id, String name, String displayName, int categoryType, boolean useInContext) {
		super(id, name);
		this.displayName = displayName;
		this.categoryType = categoryType;
		this.useInContext = useInContext;
	}

	public boolean isUsedInContext() {
		return useInContext;
	}

	public String getDisplayName() {
		return displayName;
	}

	public boolean hasCategory() {
		return categoryType > 0;
	}

	public int getCategoryType() {
		return categoryType;
	}

	private Object readResolve() throws ObjectStreamException {
		try {
			return forID(this.id);
		}
		catch (IllegalArgumentException ex) {
			throw new InvalidObjectException(ex.getMessage());
		}
	}

	public boolean equals(Object obj) {
		if (obj instanceof GenericEntityType) {
			return this.id == ((GenericEntityType) obj).id;
		}
		else {
			return false;
		}
	}
}