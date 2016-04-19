/**
 * 
 */
package com.mindbox.pe.server.generator.value.rhscolref;

import com.mindbox.pe.model.table.DynamicStringValue;
import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

class DynamicStringValueHelper extends AbstractWriteValueHelper<DynamicStringValue> {

	@Override
	public void writeValue(StringBuilder buff, DynamicStringValue dsValue, AbstractTemplateColumn column, boolean addQuotes, boolean multiEnumAsSequence) throws RuleGenerationException {
		String[] values = dsValue.getDeployValues();
		if (values == null || values.length == 0) {
			buff.append((dsValue == null ? RuleGeneratorHelper.AE_NIL : "\"" + dsValue.toString() + "\""));
		}
		else {
			buff.append(RuleGeneratorHelper.OPEN_PAREN);
			buff.append(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper().getMessageFormatConversionFunction());
			buff.append(' ');
			buff.append(RuleGeneratorHelper.QUOTE);
			String messageStr = RuleGeneratorHelper.formatForSprintf(values.length > 0 ? values[0] : dsValue.toString());
			buff.append(messageStr);
			buff.append(RuleGeneratorHelper.QUOTE);
			for (int i = 1; i < values.length; i++) {
				buff.append(" ");
				buff.append(values[i]);
			}
			buff.append(RuleGeneratorHelper.CLOSE_PAREN);
		}
	}
}