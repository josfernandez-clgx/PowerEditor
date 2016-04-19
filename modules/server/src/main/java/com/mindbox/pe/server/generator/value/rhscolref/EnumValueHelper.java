/**
 * 
 */
package com.mindbox.pe.server.generator.value.rhscolref;

import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

class EnumValueHelper extends AbstractWriteValueHelper<EnumValue> {

	@Override
	public void writeValue(StringBuilder buff, EnumValue enumValue, AbstractTemplateColumn column, boolean addQuotes,
			boolean multiEnumAsSequence) throws RuleGenerationException {
		if (addQuotes) {
			buff.append(RuleGeneratorHelper.QUOTE + enumValue.getDeployValue() + RuleGeneratorHelper.QUOTE);
		}
		else {
			buff.append(enumValue.getDeployValue());
		}
	}
}