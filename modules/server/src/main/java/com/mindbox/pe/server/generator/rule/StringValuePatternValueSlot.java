package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.model.rule.Reference;

public class StringValuePatternValueSlot extends AbstractPatternValueSlot {

	StringValuePatternValueSlot(Reference reference, int operator, String stringValue) {
		super(ValueSlot.Type.STRING, reference, operator, stringValue, null);
	}

}
