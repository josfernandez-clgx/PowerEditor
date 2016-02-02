package com.mindbox.pe.server.generator.rule;

/**
 * Represents an attribute pattern of an object pattern in LHS of an A*E rule.
 * @author Geneho Kim
 * @since 5.1.0
 */
public interface AttributePattern {

	String getAttributeName();

	ValueSlot getValueSlot();

	String getValueText();

	String getVariableName();

	/**
	 * Tests if this has the same value as the specified pattern.
	 * @param pattern
	 * @return
	 * @throws NullPointerException if <code>pattern</code> is <code>null</code>
	 */
	boolean hasSameValue(AttributePattern pattern);
	
	/**
	 * Tests if this has a value slot.
	 * If this returns <code>true</code>, {@link #getValueSlot()} does not return <code>null</code>.
	 * If this returns <code>false</code>, {@link #getValueText()} does not return <code>null</code>.
	 * @return
	 */
	boolean hasValueSlot();

	/**
	 * Tests if this is an empty pattern; i.e., matches on any value
	 * @return <code>true</code> if this is an empty pattern; <code>false</code>, otherwise
	 */
	boolean isEmpty();

	/**
	 * Tests if this is more restrictive than the specified pattern.
	 * @param pattern
	 * @return <code>true</code> if is more restrictive than <code>pattern</code>; <code>false</code>, otherwise
	 */
	boolean isMoreRestrictive(AttributePattern pattern);

	boolean canBeSkipped();
	
	void setCanBeSkipped(boolean value);
}
