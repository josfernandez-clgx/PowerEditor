/**
 * 
 */
package com.mindbox.pe.server.generator.value.rhscolref;

import com.mindbox.pe.model.AbstractTemplateColumn;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

abstract class AbstractSimpleValueHelper<V> extends AbstractWriteValueHelper<V> implements RHSColRefWriteValueHelper<V> {

	@Override
	public void writeValue(StringBuilder buff, V value, AbstractTemplateColumn column, boolean addQuotes, boolean multiEnumAsSequence)
			throws RuleGenerationException {
		if (addQuotes) {
			buff.append(RuleGeneratorHelper.QUOTE);
		}
		appendValue(buff, value, column, multiEnumAsSequence);
		if (addQuotes) {
			buff.append(RuleGeneratorHelper.QUOTE);
		}
	}

	/**
	 * Subclasses must override this when not overriding this {@link #writeValue(StringBuilder, Object, AbstractTemplateColumn, boolean, boolean)}.
	 * This default implementaion is a no-op.
	 * @param buff
	 * @param value
	 * @param column
	 * @param multiEnumAsSequence
	 */
	protected abstract void appendValue(StringBuilder buff, V value, AbstractTemplateColumn column, boolean multiEnumAsSequence)
			throws RuleGenerationException;
}