package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.model.GenericEntityType;

/**
 * Concrete implementation of {@link AttributePattern} with a value slot of type {@link ContextElementPatternValueSlot}.
 *
 */
public class ContextElementAttributePattern extends AbstractAttributePattern {

	ContextElementAttributePattern(String attributeName, String variableName, GenericEntityType entityType, boolean asString) {
		super(attributeName, variableName, true, null, new ContextElementPatternValueSlot(entityType, asString));
	}

}
