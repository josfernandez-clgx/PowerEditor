/**
 * 
 */
package com.mindbox.pe.server.generator.value.rhscolref;

import com.mindbox.pe.model.AbstractTemplateColumn;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

class IntegerValueHelper extends AbstractSimpleValueHelper<Integer>{

	@Override
	protected void appendValue(StringBuilder buff, Integer value, AbstractTemplateColumn column, boolean multiEnumAsSequence)
			throws RuleGenerationException {
		buff.append((value == null ? RuleGeneratorHelper.AE_NIL : value.toString()));
	}

}