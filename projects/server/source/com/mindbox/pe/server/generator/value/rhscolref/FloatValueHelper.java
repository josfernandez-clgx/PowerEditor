/**
 * 
 */
package com.mindbox.pe.server.generator.value.rhscolref;

import com.mindbox.pe.model.AbstractTemplateColumn;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

class FloatValueHelper extends AbstractSimpleValueHelper<Number> {

	@Override
	protected void appendValue(StringBuilder buff, Number value, AbstractTemplateColumn column, boolean multiEnumAsSequence)
			throws RuleGenerationException {
		buff.append(RuleGeneratorHelper.floatFormatter.format(value));
	}

}