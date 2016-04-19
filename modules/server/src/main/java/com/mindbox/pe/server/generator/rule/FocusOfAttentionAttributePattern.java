package com.mindbox.pe.server.generator.rule;

/**
 * Concrete implementation of {@link AttributePattern} with a value slot of type {@link FocusOfAttentionPatternValueSlot}}.
 *
 */
public class FocusOfAttentionAttributePattern extends AbstractAttributePattern {

	FocusOfAttentionAttributePattern(String attributeName) {
		super(attributeName, "", true, null, new FocusOfAttentionPatternValueSlot());
	}

}
