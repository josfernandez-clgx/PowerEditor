package com.mindbox.pe.server.generator.value;

import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.config.RuleGenerationConfiguration;
import com.mindbox.pe.server.config.RuleLHSValueConfig;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.model.TimeSlice;

/**
 * Handles {@link com.mindbox.pe.model.rule.Condition#OP_IS_EMPTY} and {@link com.mindbox.pe.model.rule.Condition#OP_IS_NOT_EMPTY} operators.
 * @author Geneho Kim
 *
 */
class EmptyOperatorHelper extends AbstractOperatorHelper {

	EmptyOperatorHelper(RuleGenerationConfiguration ruleGenerationConfiguration) {
		super(ruleGenerationConfiguration);
	}

	public ValueAndComment formatForPattern(Object value, int op, String attribVarName, boolean asString, Reference reference, TimeSlice timeSlice) throws RuleGenerationException {
		StringBuffer buff = new StringBuffer();
		RuleLHSValueConfig valueConfig = ruleGenerationConfiguration.getLHSValueConfig(RuleGenerationConfiguration.VAUE_TYPE_UNSPECIFIED);
		if (valueConfig == null) {
			throw new RuleGenerationException("LHS Value configuration for '" + RuleGenerationConfiguration.VAUE_TYPE_UNSPECIFIED
					+ "' not found in PowerEditor Configuraiton XML");
		}

		logger.debug("formatForPattern): using " + valueConfig + " for " + attribVarName);

		buff.append(attribVarName);
		if (op == Condition.OP_IS_NOT_EMPTY) {
			buff.append(" &:(/= ");
			buff.append(attribVarName);
			buff.append(" ");
		}
		else {
			buff.append(" & ");
		}
		appendFormatted(buff, valueConfig.getDeployValue(), valueConfig.isValueAsString());
		if (op == Condition.OP_IS_NOT_EMPTY) {
			buff.append(")");
		}
		return new ValueAndComment(buff.toString());
	}

}
