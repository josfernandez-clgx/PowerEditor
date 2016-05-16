package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.model.GenericEntityType;

/**
 * A concrete implementation of {@link ValueSlot} of {@link ValueSlot.Type#CATEGORY_NAME}.
 * The {@link ValueSlot#getSlotValue()} method of this returns an instance of {@link GenericEntityType}.
 * @author Geneho Kim
 * @since 5.1.0
 */
public final class CategoryNameValueSlot extends AbstractValueSlot {

	CategoryNameValueSlot(GenericEntityType entityType) {
		this(entityType, null);
	}

	CategoryNameValueSlot(GenericEntityType entityType, String slotText) {
		super(Type.CATEGORY_NAME, entityType, slotText);
		if (entityType == null) throw new NullPointerException("entityType cannot be null");
	}
}
