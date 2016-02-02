package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.model.rule.Reference;

public class ColumnRefValueSlotAttributePattern extends AbstractAttributePattern {

	/**
	 * Creates a new attribute pattern with the specified value slot.
	 * @param attributeName
	 * @param varName
	 * @param valueSlot
	 */
	private ColumnRefValueSlotAttributePattern(String attributeName, String varName, ValueSlot valueSlot) {
		super(attributeName, varName, true, null, valueSlot, true);
	}

	/**
	 * Convenience constructor for a column refrence value slot.
	 * Equivalent to 
	 * <code>new AttributePatternWithValueSlot(attributeName, varName, new ColumnReferenceValueSlot(reference, operator, columnNo)</code>
	 * @param attributeName
	 * @param varName
	 * @param reference
	 * @param operator
	 * @param columnNo
	 */
	ColumnRefValueSlotAttributePattern(String attributeName, String varName, Reference reference, int operator, int columnNo) {
		this(attributeName, varName, new ColumnReferencePatternValueSlot(reference, operator, columnNo));
	}
	
	/**
	 * Convenience constructor for a column refrence value slot.
	 * Equivalent to 
	 * <code>new AttributePatternWithValueSlot(attributeName, varName, new ColumnReferenceValueSlot(reference, operator, columnNo, slotText)</code>
	 * @param attributeName
	 * @param varName
	 * @param reference
	 * @param operator
	 * @param columnNo
	 * @param slotText the slot text
	 */
	ColumnRefValueSlotAttributePattern(String attributeName, String varName, Reference reference, int operator, int columnNo, String slotText) {
		this(attributeName, varName, new ColumnReferencePatternValueSlot(reference, operator, columnNo, slotText));
	}
}
