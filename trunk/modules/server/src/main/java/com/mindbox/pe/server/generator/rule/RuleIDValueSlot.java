package com.mindbox.pe.server.generator.rule;

/**
 * A concrete implementation of {@link ValueSlot} of {@link ValueSlot#RULE_ID}.
 * The {@link ValueSlot#getSlotValue()} method of this returns <code>null</code>.
 * @author Geneho Kim
 * @since 5.1.0
 */
public final class RuleIDValueSlot extends AbstractValueSlot {

	RuleIDValueSlot() {
		this(null);
	}

	RuleIDValueSlot(String slotText) {
		super(Type.RULE_ID, null, slotText);
	}
}
