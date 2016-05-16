package com.mindbox.pe.server.generator.rule;

/**
 * Represents an attribute pattern of an object pattern in LHS of an A*E rule.
 * @author Geneho Kim
 * @since 5.1.0
 */
public interface AttributePattern {

	boolean canBeSkipped();

	String getAttributeName();

	ValueSlot getValueSlot();

	String getValueText();

	String getVariableName();

	/**
	 * Tests if this has the same value as the specified pattern.
	 * @param pattern pattern
	 * @return true same value; false, otherwise
	 * @throws NullPointerException if <code>pattern</code> is <code>null</code>
	 */
	boolean hasSameValue(AttributePattern pattern);

	/**
	 * Tests if this has a value slot.
	 * If this returns <code>true</code>, {@link #getValueSlot()} does not return <code>null</code>.
	 * If this returns <code>false</code>, {@link #getValueText()} does not return <code>null</code>.
	 * @return true if has value slot; false, otherwise
	 */
	boolean hasValueSlot();

	/**
	 * Tests if this is an empty pattern; i.e., matches on any value
	 * @return <code>true</code> if this is an empty pattern; <code>false</code>, otherwise
	 */
	boolean isEmpty();

	/**
	 * Tests if this is more restrictive than the specified pattern.
	 * @param pattern pattern
	 * @return <code>true</code> if is more restrictive than <code>pattern</code>; <code>false</code>, otherwise
	 */
	boolean isMoreRestrictive(AttributePattern pattern);

	void setCanBeSkipped(boolean value);
}
