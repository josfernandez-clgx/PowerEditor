package com.mindbox.pe.server.generator.rule;

/**
 * A concrete implementation of {@link ValueSlot} of {@link ValueSlot.Type#ACTIVATION_SPANrul}.
 * The {@link ValueSlot#getSlotValue()} method of this returns <code>null</code>.
 * @author Geneho Kim
 * @since 5.1.0
 */
public final class ActivationSpanValueSlot extends AbstractValueSlot {

	ActivationSpanValueSlot() {
		this(null);
	}

	ActivationSpanValueSlot(String slotText) {
		super(Type.ACTIVATION_SPAN, null, slotText);
	}
}
