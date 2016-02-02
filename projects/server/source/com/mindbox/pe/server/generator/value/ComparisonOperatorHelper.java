package com.mindbox.pe.server.generator.value;

import java.util.Date;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.config.RuleGenerationConfiguration;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.model.TimeSlice;

class ComparisonOperatorHelper extends AbstractOperatorHelper {

	ComparisonOperatorHelper(RuleGenerationConfiguration ruleGenerationConfiguration) {
		super(ruleGenerationConfiguration);
	}

	public ValueAndComment formatForPattern(Object value, int op, String attribVarName, boolean asString, Reference reference, TimeSlice timeSlice)
			throws RuleGenerationException {
		logger.debug("   handling comparator " + op);
		try {
			DeployType deployType = findDeployType(reference); //  TT 1991
			StringBuffer buff = new StringBuffer();
			buff.append(attribVarName);
			if (value != null && !UtilBase.isEmpty(value.toString())) {
				buff.append(" &:(");
				buff.append(RuleGeneratorHelper.toARTScriptOpString(op));
				buff.append(" ");
				buff.append(attribVarName);
				buff.append(" ");

				if (value instanceof String) {
					String strValue = (String) value;
					// write as is if it's a variable (attribute to attribute
					// comparison)
					if (strValue.startsWith("?")) {
						buff.append(value.toString());
					}
					// write as is if it's a function call (math expression
					// value)
					else if (strValue.startsWith("(")) {
						buff.append(value.toString());
					}					
					// TT 1991
					else if (deployType == DeployType.DATE) {
						try {
							appendFormatted(buff, RuleGeneratorHelper.parseToDate(value.toString()), false);
						}
						catch (Exception ex) {
							throw new RuleGenerationException("Invalid date: can't convert " + value + " to date: " + ex.getMessage());
						}
					}					
					else {
						Number number = Double.valueOf(value.toString());
						appendFormattedNumericValue(buff, reference, number);
					}
				}
				// TT 1991
				else if (value instanceof Date) {
					appendFormatted(buff, (Date) value, asString);
				}
				else if (value instanceof Number) {
					appendFormattedNumericValue(buff, reference, (Number) value);
				}
				else {
					throw new RuleGenerationException("number or reference variable expected for " + attribVarName + "; " + value
							+ " is neither");
				}

				buff.append(")");
			}
			return new ValueAndComment(buff.toString());
		}
		catch (NumberFormatException e) {
			throw new RuleGenerationException("number expected for " + attribVarName + "; " + value + " is not a number: " + e.getMessage());
		}
	}

}
