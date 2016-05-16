package com.mindbox.pe.server.generator.rule;


/**
 * A concrete implementation of {@link ValueSlot} of {@link ValueSlot.Type#DATE_PROPERTY}.
 * The {@link ValueSlot#getSlotValue()} method of this returns one of {@link #DATE_TYPE_ACTIVATION_DATE} and {@link #DATE_TYPE_EXPIRATION_DATE}.
 * @author Geneho Kim
 * @since 5.1.0
 */
public final class DatePropertyValueSlot extends AbstractValueSlot {

	public static final String DATE_TYPE_ACTIVATION_DATE = "act";
	public static final String DATE_TYPE_EXPIRATION_DATE = "exp";

	DatePropertyValueSlot(String dateType) {
		this(dateType, null);
	}

	/**
	 * 
	 * @param dateType dateType
	 * @param slotText slotText
	 * @throws NullPointerException if dateType is <code>null</code>
	 */
	DatePropertyValueSlot(String dateType, String slotText) {
		super(Type.DATE_PROPERTY, dateType, slotText);
		if (dateType == null) {
			throw new NullPointerException("dateType cannot be null");
		}
		if (!dateType.equals(DATE_TYPE_ACTIVATION_DATE) && !dateType.equals(DATE_TYPE_EXPIRATION_DATE)) {
			throw new IllegalArgumentException("Invalid date type: " + dateType);
		}
	}
}
