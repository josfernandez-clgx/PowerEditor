package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.rule.Reference;

/**
 * Abstract immutable implementation of {@link PatternValueSlot}.
 * @author Geneho Kim
 * @since 5.1.0
 */
public abstract class AbstractPatternValueSlot extends AbstractValueSlot implements PatternValueSlot {

	private int operator;
	private Reference reference;

	protected AbstractPatternValueSlot(ValueSlot.Type type, Reference reference, int operator, Object slotValue, String slotText) {
		super(type, slotValue, slotText);
		this.operator = operator;
		this.reference = reference;
	}

	public final int getOperator() {
		return operator;
	}

	public final Reference getReference() {
		return reference;
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof PatternValueSlot) {
			return super.equals(obj) && operator == ((PatternValueSlot) obj).getOperator() && UtilBase.isSame(reference, ((PatternValueSlot) obj).getReference());
		}
		else {
			return false;
		}
	}
}
