package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.model.GenericEntityType;

/**
 * Concrete implementation of {@link PatternValueSlot} of type {@link ValueSlot.Type#CONTEXT_ELEMENT}.
 * The {@link #getSlotValue()} method returns an instance of {@link GenericEntityType}.
 *
 */
public class ContextElementPatternValueSlot extends AbstractPatternValueSlot {

	private boolean asString;

	/**
	 * 
	 * @param entityType entityType
	 * @param asString asString
	 * @throws NullPointerException if entityType is <code>null</code>
	 */
	ContextElementPatternValueSlot(GenericEntityType entityType, boolean asString) {
		super(Type.CONTEXT_ELEMENT, null, 0, entityType, null);
		if (entityType == null) throw new NullPointerException("entityType cannot be null");
		this.asString = asString;
	}

	public boolean asString() {
		return asString;
	}

	public GenericEntityType getGenericEntityType() {
		return (GenericEntityType) getSlotValue();
	}
}
