package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.server.generator.RuleGenerationException;

/**
 * Container of {@link AttributePattern} instances.
 * @author Geneho Kim
 * @since 5.1.0
 */
public interface AttributePatternList {

	/**
	 * Adds the specified attribute pattern to this.
	 * @param attributePattern the attribute pattern to add
	 * @throws RuleGenerationException on error
	 * @throws NullPointerException if <code>attributePattern</code> is <code>null</code>
	 */
	void add(AttributePattern attributePattern) throws RuleGenerationException;

	/**
	 * Gets the attribute pattern at the specified index.
	 * @param index zero-based
	 * @return the attribute pattern at <code>index</code>
	 */
	AttributePattern get(int index);

	/**
	 * Inserts the specified attribute pattern to the beginning of this.
	 * @param attributePattern the attribute pattern to insert
	 * @throws RuleGenerationException on error
	 * @throws NullPointerException if <code>attributePattern</code> is <code>null</code>
	 */
	void insert(AttributePattern attributePattern) throws RuleGenerationException;

	/**
	 * Tests if this is empty.
	 * @return <code>true</code> if this contains no attribute patterns; <code>false</code>, otherwise
	 */
	boolean isEmpty();

	/**
	 * Removes the specified attribute pattern from this.
	 * @param attributePattern attributePatternReportGenerato
	 * @throws NullPointerException if <code>attributePattern</code> is <code>null</code>
	 */
	void remove(AttributePattern attributePattern);

	/**
	 * Gets the number of attribute patterns in this.
	 * @return the number of attribute patterns
	 */
	int size();
}
