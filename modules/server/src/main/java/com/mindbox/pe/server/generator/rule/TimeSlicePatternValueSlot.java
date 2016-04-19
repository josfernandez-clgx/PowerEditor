package com.mindbox.pe.server.generator.rule;


/**
 * Concrete implementation of {@link ValueSlot} of type {@link ValueSlot#TIME_SLICE}.
 *
 */
public class TimeSlicePatternValueSlot extends AbstractPatternValueSlot {

	TimeSlicePatternValueSlot() {
		super(ValueSlot.Type.TIME_SLICE, null, 0, null, null);
	}

}
