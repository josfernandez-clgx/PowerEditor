package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.model.rule.Reference;

/**
 * Concrete implmentation of {@link AttributePattern} that has a value slot of type {@link StringValuePatternValueSlot}.
 *
 */
public class StringValueSlotAttributePattern extends AbstractAttributePattern {

	StringValueSlotAttributePattern(String attributeName, String varName, Reference reference, int operator, String strValue) {
		super(attributeName, varName, true, null, new StringValuePatternValueSlot(reference, operator, strValue));
	}

}
