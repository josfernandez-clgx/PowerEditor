package com.mindbox.pe.model.filter;

import java.util.Map;

import com.mindbox.pe.model.AbstractIDNameObject;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.Persistent;

/**
 * Filter with name that can be persisted.
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public abstract class AbstractPersistentFilterSpec<T extends Persistent> extends AbstractIDNameObject implements PersistentFilterSpec {

	private static final long serialVersionUID = 2003052016150201L;

	private final EntityType entityType;

	private final GenericEntityType genericEntityType;

	protected AbstractPersistentFilterSpec(EntityType entityType, GenericEntityType genericEntityType, String name) {
		super(name);
		if (entityType == null && genericEntityType == null) throw new IllegalArgumentException("entityType and genericEntityType are both null");
		if (entityType != null && genericEntityType != null) throw new IllegalArgumentException("One of entityType and genericEntityType must be null");
		this.entityType = entityType;
		this.genericEntityType = genericEntityType;
	}

	protected AbstractPersistentFilterSpec(EntityType entityType, GenericEntityType genericEntityType, int filterID, String name) {
		super(filterID, name);
		if (entityType == null && genericEntityType == null) throw new IllegalArgumentException("entityType and genericEntityType are both null");
		if (entityType != null && genericEntityType != null) throw new IllegalArgumentException("One of entityType and genericEntityType must be null");
		this.entityType = entityType;
		this.genericEntityType = genericEntityType;
	}

	public final boolean isForGenericEntity() {
		return genericEntityType != null;
	}
	
	public abstract SearchFilter<T> asSearchFilter();

	public abstract String toParamString();

	public void setInvariants(Map<String,String> paramMap) {
		setInvariants(paramMap, null);
	}

	public final EntityType getEntityType() {
		return entityType;
	}

	public final GenericEntityType getGenericEntityType() {
		return genericEntityType;
	}

	public final int getEntityTypeID() {
		return (genericEntityType == null ? entityType.getID() : genericEntityType.getID());
	}

	public boolean equals(Object obj) {
		return (obj instanceof AbstractPersistentFilterSpec) && super.equals(obj);
	}

	public String toString() {
		return getName(); // "PersistentFilter" + super.toString();
	}

	public final void setName(String name) {
		super.setName(name);
	}
}
