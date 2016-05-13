package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.Reference;


public interface PatternValueSlot extends ValueSlot {

	/**
	 * Gets the operator for this slot. 
	 * Must return one of operator defined in {@link Condition}.
	 * @return operator
	 */
	int getOperator();
	
	/**
	 * Gets domain attribute reference for this slot.
	 * @return reference
	 */
	Reference getReference();
	
}
