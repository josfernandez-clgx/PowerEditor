package com.mindbox.pe.server.generator.value;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.config.RuleGenerationConfigHelper;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.model.TimeSlice;
import com.mindbox.pe.xsd.config.RuleGenerationLHS.Value;

/**
 * Handles {@link com.mindbox.pe.model.rule.Condition#OP_IS_EMPTY} and {@link com.mindbox.pe.model.rule.Condition#OP_IS_NOT_EMPTY} operators.
 * @author Geneho Kim
 *
 */
class EmptyOperatorHelper extends AbstractOperatorHelper {

	EmptyOperatorHelper(RuleGenerationConfigHelper ruleGenerationConfiguration) {
		super(ruleGenerationConfiguration);
	}

	public ValueAndComment formatForPattern(Object value, int op, String attribVarName, boolean asString, Reference reference, TimeSlice timeSlice, final Integer precision)
			throws RuleGenerationException {
		StringBuilder buff = new StringBuilder();
		final Value valueConfig = ruleGenerationConfiguration.getLHSValueConfig(RuleGenerationConfigHelper.VAUE_TYPE_UNSPECIFIED);
		if (valueConfig == null) {
			throw new RuleGenerationException("LHS Value configuration for '" + RuleGenerationConfigHelper.VAUE_TYPE_UNSPECIFIED + "' not found in PowerEditor Configuraiton XML");
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
		appendFormatted(buff, valueConfig.getDeployValue(), UtilBase.asBoolean(valueConfig.isValueAsString(), false));
		if (op == Condition.OP_IS_NOT_EMPTY) {
			buff.append(")");
		}
		return new ValueAndComment(buff.toString());
	}

}
