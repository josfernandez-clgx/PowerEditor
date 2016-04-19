package com.mindbox.pe.server.generator.rule;

/**
 * Concrete implmentation of {@link AttributePattern} that has a value slot of type {@link TimeSlicePatternValueSlot}.
 *
 */
public class TimeSliceAttributePattern extends AbstractAttributePattern {

	TimeSliceAttributePattern(String attributeName, String varName) {
		super(attributeName, varName, true, null, new TimeSlicePatternValueSlot());
	}

}
