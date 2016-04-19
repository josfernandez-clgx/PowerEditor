/**
 * 
 */
package com.mindbox.pe.server.generator.value.rhscolref;

import java.util.Date;

import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

class DateValueHelper extends AbstractSimpleValueHelper<Date> {
	@Override
	protected void appendValue(StringBuilder buff, Date value, AbstractTemplateColumn column, boolean multiEnumAsSequence)
			throws RuleGenerationException {
		buff.append(RuleGeneratorHelper.toRuleDateString(value));
	}
	
}