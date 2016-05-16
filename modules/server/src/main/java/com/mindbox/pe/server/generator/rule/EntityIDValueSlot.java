package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.model.GenericEntityType;

/**
 * A concrete implementation of {@link ValueSlot} of {@link ValueSlot.Type#ENTITY_ID}.
 * The {@link ValueSlot#getSlotValue()} method of this returns an instance of {@link GenericEntityType}.
 * @author Geneho Kim
 * @since 5.1.0
 */
public final class EntityIDValueSlot extends AbstractValueSlot {

	private String entityVariableName;

	EntityIDValueSlot(GenericEntityType entityType, String entityVariableName) {
		this(entityType, null, entityVariableName);
	}

	EntityIDValueSlot(GenericEntityType entityType, String slotText, String entityVariableName) {
		super(Type.ENTITY_ID, entityType, slotText);
		if (entityType == null) {
			throw new NullPointerException("entityType cannot be null");
		}
		this.entityVariableName = entityVariableName;
	}

	public GenericEntityType getEntityType() {
		return (GenericEntityType) getSlotValue();
	}

	public String getEntityVariableName() {
		return entityVariableName;
	}
}
