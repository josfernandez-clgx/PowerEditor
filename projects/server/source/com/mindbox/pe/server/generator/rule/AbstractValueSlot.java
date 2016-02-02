package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.common.UtilBase;

/**
 * Abstract immutable implementation of {@link ValueSlot}.
 * @author Geneho Kim
 * @since 5.1.0
 */
abstract class AbstractValueSlot implements ValueSlot {

	private Type type;
	private Object slotValue;
	private String slotText;

	protected AbstractValueSlot(Type type, Object slotValue, String slotText) {
		this.type = type;
		this.slotValue = slotValue;
		this.slotText = slotText;
	}

	public String getSlotText() {
		return slotText;
	}

	public final Object getSlotValue() {

		return slotValue;
	}

	public final Type getType() {
		return type;
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof ValueSlot) {
			return type == ((ValueSlot) obj).getType() && UtilBase.isSame(slotValue, ((ValueSlot) obj).getSlotValue())
					&& UtilBase.isSame(slotText, ((ValueSlot) obj).getSlotText());
		}
		else {
			return false;
		}
	}

	public String toString() {
		return getClass() + "[type=" + type + ",text=" + slotText + ",value=" + slotValue + ']';
	}
}
