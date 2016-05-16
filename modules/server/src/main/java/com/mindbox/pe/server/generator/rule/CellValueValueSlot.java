package com.mindbox.pe.server.generator.rule;


/**
 * A concrete implementation of {@link ValueSlot} of {@link ValueSlot.Type#CELL_VALUE}.
 * The {@link ValueSlot#getSlotValue()} method of this returns <code>null</code>.
 * @author Geneho Kim
 * @since 5.1.0
 */
public final class CellValueValueSlot extends AbstractValueSlot {

	CellValueValueSlot() {
		this(null);
	}

	CellValueValueSlot(String slotText) {
		super(Type.CELL_VALUE, null, slotText);
	}
}
