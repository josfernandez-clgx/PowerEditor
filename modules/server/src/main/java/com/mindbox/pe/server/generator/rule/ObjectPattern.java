package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.server.generator.RuleGenerationException;

/**
 * Represents an object pattern of an A*E rule.
 * @author Geneho Kim
 * @since 5.1.0
 * @see AttributePatternList
 * @see AttributePattern
 */
public interface ObjectPattern extends AttributePatternList, LHSPattern {

	/**
	 * Adds all attribute patterns in the specified object pattern to this.
	 * @param objectPattern
	 * @throws RuleGenerationException on error
	 * @throws NullPointerException if <code>objectPattern</code> is <code>null</code>
	 */
	void addAll(ObjectPattern objectPattern) throws RuleGenerationException;

	/**
	 * Tests if this contains an attribute pattern with the specified variable name.
	 * @param variableName the variable name
	 * @return
	 * @throws NullPointerException if <code>variableName</code> is <code>null</code>
	 */
	boolean containsAttribute(String variableName);

	/**
	 * Tests if this contains an attribute pattern with the specified attribute name.
	 * @param attributeName the variable name
	 * @return
	 * @throws NullPointerException if <code>attributeName</code> is <code>null</code>
	 */
	boolean containsAttributeName(String attributeName);

	/**
	 * Tests if this contains an non-empty attribute pattern with the specified variable name.
	 * @param attributeName the variable name
	 * @return
	 * @throws NullPointerException if <code>attributeName</code> is <code>null</code>
	 */
	boolean containsNonEmptyAttribute(String variableName);

	String getClassName();

	String getVariableName();
	
	boolean shouldBeFirst();
	
	void addMustBeBeforeVariable(String variableName);
	
	boolean canBeAfter(String variableName);
	
	boolean canBeSkipped();
	
	void setCanBeSkipped(boolean value);
}
