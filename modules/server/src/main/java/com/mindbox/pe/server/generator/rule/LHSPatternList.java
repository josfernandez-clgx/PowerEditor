package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.generator.RuleGenerationException;

public interface LHSPatternList extends LHSPattern {

	int TYPE_AND = 0;
	int TYPE_OR = 1;
	int TYPE_NOT = -1;

	/**
	 * Adds the specified test pattern to the end.
	 * @param testPattern the test pattern to append
	 * @throws NullPointerException if <code>testPattern</code> is <code>null</code>
	 */
	void append(FunctionCallPattern testPattern);

	/**
	 * Adds the specified test pattern list to the end.
	 * @param patternList the test pattern to append
	 * @throws NullPointerException if <code>testPattern</code> is <code>null</code>
	 */
	void append(LHSPatternList patternList);

	/**
	 * Adds the specified object pattern to the end.
	 * @param objectPattern the object pattern to append
	 * @throws NullPointerException if <code>objectPattern</code> is <code>null</code>
	 * @throws RuleGenerationException on error
	 */
	void append(ObjectPattern objectPattern) throws RuleGenerationException;

	/**
	 * Gets the LHS pattern at the specified index.
	 * @param index zero-based
	 * @return the object pattern at <code>index</code>
	 */
	LHSPattern get(int index);

	/**
	 * Gets the type of this.
	 * 
	 * @return one of {@link #TYPE_AND}, {@link #TYPE_OR}, or {@link #TYPE_NOT}
	 */
	int getType();

	/**
	 * Tests if this contains an object pattern that has a conflicting attribute pattern.
	 * This returns true if there is an object patttern for the specified objectVarName that has a attribute pattern that has
	 * the same attribute name but with a different variable name.
	 * @param objectVarName object variable name of an object pattern
	 * @param attributePattern attribute pattern to check conflicts against
	 * @return <code>true</code> if this has a conflicting pattern for the specified attributePattern; <code>false</code>, otherwise
	 * @throws NullPointerException if attributePattern is <code>null</code>
	 */
	boolean hasConflictingAttributePattern(String objectVarName, AttributePattern attributePattern);

	/**
	 * 
	 * @param reference reference
	 * @return <code>true</code> if this has a pattern for the specified reference; <code>false</code>, otherwise
	 * @throws NullPointerException if reference is <code>null</code>
	 */
	boolean hasPatternForReference(Reference reference);

	/**
	 * Equivalent to <code>insert(objectPattern, false)</code>.
	 * @param objectPattern objectPattern
	 * @see #insert(ObjectPattern, boolean)
	 * @throws RuleGenerationException on error
	 */
	void insert(ObjectPattern objectPattern) throws RuleGenerationException;

	/**
	 * Inserts the specified object pattern to the beginning.
	 * @param objectPattern the object pattern to insert
	 * @param preserveOrderIfPatternExists if <code>true</code>, this should not modify existing
	 *                                     pattern order in any way;
	 *                                     if <code>false</code>, this may modify existing pattern
	 *                                     order to ensure the inserted patterns is positioned first
	 * @throws NullPointerException if <code>objectPattern</code> is <code>null</code>
	 * @throws RuleGenerationException on error
	 */
	void insert(ObjectPattern objectPattern, boolean preserveOrderIfPatternExists) throws RuleGenerationException;

	/**
	 * Adds the specified object pattern before the object pattern with the specified variable name.
	 * If no such object pattern is found, <code>objectPattern</code> is appended to the end.
	 * @param objectPattern objectPattern
	 * @param objectVariableName objectVariableName
	 * @throws RuleGenerationException on error
	 */
	void insertBefore(ObjectPattern objectPattern, String objectVariableName) throws RuleGenerationException;

	/**
	 * Tests if this is empty.
	 * @return <code>true</code> if this contains no object patterns; <code>false</code>, otherwise
	 */
	boolean isEmpty();

	/**
	 * Removes the specified object pattern from this.
	 * @param objectPattern objectPattern
	 * @throws NullPointerException if <code>objectPattern</code> is <code>null</code>
	 */
	void remove(ObjectPattern objectPattern);

	/**
	 * Gets the number of object patterns in this.
	 * @return the number of object patterns
	 */
	int size();
}
