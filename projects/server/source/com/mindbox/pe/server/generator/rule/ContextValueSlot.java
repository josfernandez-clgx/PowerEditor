package com.mindbox.pe.server.generator.rule;

/**
 * A concrete implementation of {@link ValueSlot} of {@link ValueSlot#CONTEXT}.
 * The {@link ValueSlot#getSlotValue()} method of this returns <code>null</code>.
 * @since 5.1.0
 */
public final class ContextValueSlot extends AbstractValueSlot {

	ContextValueSlot() {
		this(null);
	}

	ContextValueSlot(String slotText) {
		super(Type.CONTEXT, null, slotText);
	}
}
