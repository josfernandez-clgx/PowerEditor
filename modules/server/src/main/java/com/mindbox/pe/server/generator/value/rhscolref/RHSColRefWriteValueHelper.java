/**
 * 
 */
package com.mindbox.pe.server.generator.value.rhscolref;

import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.server.generator.RuleGenerationException;

/**
 * RHS Column Reference value helper.
 *
 * @param <V> Value Type
 */
public interface RHSColRefWriteValueHelper<V> {
	/**
	 * Appends a string representation of the specified value to <code>buff</code>
	 * @param buff buff
	 * @param value guaranteed to be not <code>null</code>
	 * @param column column
	 * @param addQuotes addQuotes
	 * @param multiEnumAsSequence multiEnumAsSequence
	 * @throws RuleGenerationException on error
	 */
	void writeValue(StringBuilder buff, V value, AbstractTemplateColumn column, boolean addQuotes, boolean multiEnumAsSequence) throws RuleGenerationException;
}