package com.mindbox.pe.server.generator.rule;


/**
 * A concrete implementation of {@link ValueSlot} of {@link ValueSlot#RULE_NAME}.
 * The {@link ValueSlot#getSlotValue()} method of this returns <code>null</code>.
 * @author Geneho Kim
 * @since 5.1.0
 */
public final class RuleNameValueSlot extends AbstractValueSlot {

	RuleNameValueSlot() {
		this(null);
	}

	RuleNameValueSlot(String slotText) {
		super(Type.RULE_NAME, null, slotText);
	}
}
