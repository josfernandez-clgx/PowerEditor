package com.mindbox.pe.server.generator.rule;


/**
 * A concrete implementation of {@link ValueSlot} of {@link ValueSlot#ROW_NUMBER}.
 * The {@link ValueSlot#getSlotValue()} method of this returns <code>null</code>.
 * @author Geneho Kim
 * @since 5.1.0
 */
public final class RowNumberValueSlot extends AbstractValueSlot {

	RowNumberValueSlot() {
		this(null);
	}

	RowNumberValueSlot(String slotText) {
		super(Type.ROW_NUMBER, null, slotText);
	}
}
