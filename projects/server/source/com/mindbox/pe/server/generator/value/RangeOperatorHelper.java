package com.mindbox.pe.server.generator.value;

import java.util.Date;

import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.table.IRange;
import com.mindbox.pe.server.config.RuleGenerationConfiguration;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.model.TimeSlice;

class RangeOperatorHelper extends AbstractOperatorHelper {

	RangeOperatorHelper(RuleGenerationConfiguration ruleGenerationConfiguration) {
		super(ruleGenerationConfiguration);
	}

	public ValueAndComment formatForPattern(Object valueObj, int op, String attribVarName, boolean asString, Reference reference, TimeSlice timeSlice)
			throws RuleGenerationException {
		logger.debug("writeValue: processing BETWEEN.... for " + valueObj);
		StringBuffer buff = new StringBuffer();

		boolean doNegation = (op == Condition.OP_NOT_BETWEEN);

		IRange rangeObj = null;
		if (valueObj != null) {
			if (valueObj instanceof IRange) {
				rangeObj = (IRange) valueObj;
			}
			else if (valueObj instanceof String) {
				// parse the value as range
				String valueStr = (String) valueObj;
				DeployType deployType = findDeployType(reference);
				try {
					rangeObj = RuleGeneratorHelper.asIRangeValue(valueStr, deployType);
				}
				catch (InvalidDataException ex) {
					logger.error("Failed to get range value for " + valueObj, ex);
					throw new RuleGenerationException(ex.getMessage());
				}
			}
			else {
				throw new RuleGenerationException("Invalid value type for BETWEEN for " + attribVarName + ": " + valueObj);
			}
		}

		logger.debug("writeValue: doNegation = " + doNegation);
		logger.debug("writeValue: rangeObj = " + rangeObj);
		if (rangeObj == null) {
			if (valueObj == null) {
				buff.append(attribVarName);
			}
			else {
				appendFormatted(buff, valueObj.toString(), asString);
			}
		}
		else {
			appendRangeValue(buff, attribVarName, reference, rangeObj, doNegation);
		}
		return new ValueAndComment(buff.toString());
	}

	private void appendLowerValue(StringBuffer buff, Reference reference, IRange rangeObj) throws RuleGenerationException {
		if (rangeObj.isForDate()) {
			buff.append(RuleGeneratorHelper.formatDateValueForLHS(new Date(rangeObj.getFloor().longValue())));
		}
		else {
			appendFormattedNumericValue(buff, reference, rangeObj.getFloor());
		}
	}

	private void appendUpperValue(StringBuffer buff, Reference reference, IRange rangeObj) throws RuleGenerationException {
		if (rangeObj.isForDate()) {
			buff.append(RuleGeneratorHelper.formatDateValueForLHS(new Date(rangeObj.getCeiling().longValue())));
		}
		else {
			appendFormattedNumericValue(buff, reference, rangeObj.getCeiling());
		}
	}

	/**
	 * 
	 * @param buff
	 *            buffer to append to
	 * @param rangeObj
	 *            cannot be <code>null</code>
	 * @throws RuleGenerationException
	 */
	private void appendRangeValue(StringBuffer buff, String attribVarName, Reference reference, IRange rangeObj, boolean doNegation)
			throws RuleGenerationException {
		// empty range
		if (rangeObj.isEmpty()) {
			buff.append(attribVarName);
		}
		// equal range
		else if (rangeObj.representsSingleValue()) {
			if (doNegation) {
				buff.append(attribVarName);
				buff.append(" &:(/= ");
				buff.append(attribVarName);
				buff.append(" ");
				appendLowerValue(buff, reference, rangeObj);
				buff.append(")");
			}
			else {
				buff.append(attribVarName + " & ");
				appendLowerValue(buff, reference, rangeObj);
			}
		}
		else {
			buff.append(attribVarName + " &:(");
			// when both lower and upper limits are specified
			if (rangeObj.getCeiling() != null && rangeObj.getFloor() != null) {
				if (doNegation) {
					String op1 = RuleGeneratorHelper.toARTScriptOpString(Condition.OP_GREATER);
					String op2 = RuleGeneratorHelper.toARTScriptOpString(Condition.OP_GREATER);
					if (!rangeObj.isLowerValueInclusive()) {
						op1 = RuleGeneratorHelper.toARTScriptOpString(Condition.OP_GREATER_EQUAL);
					}
					if (!rangeObj.isUpperValueInclusive()) {
						op2 = RuleGeneratorHelper.toARTScriptOpString(Condition.OP_GREATER_EQUAL);
					}

					buff.append("OR (");
					buff.append(op1);
					buff.append(" ");
					appendLowerValue(buff, reference, rangeObj);
					buff.append(" ");
					buff.append(attribVarName);
					buff.append(")");
					buff.append("(");
					buff.append(op2);
					buff.append(" ");
					buff.append(attribVarName);
					buff.append(" ");
					appendUpperValue(buff, reference, rangeObj);
					buff.append(")");
				}
				else {
					String op1 = RuleGeneratorHelper.toARTScriptOpString(Condition.OP_LESS_EQUAL);
					String op2 = RuleGeneratorHelper.toARTScriptOpString(Condition.OP_LESS_EQUAL);
					if (!rangeObj.isLowerValueInclusive()) {
						op1 = RuleGeneratorHelper.toARTScriptOpString(Condition.OP_LESS);
					}
					if (!rangeObj.isUpperValueInclusive()) {
						op2 = RuleGeneratorHelper.toARTScriptOpString(Condition.OP_LESS);
					}

					if (op1.equals(op2)) {
						buff.append(op1);
						buff.append(" ");
						appendLowerValue(buff, reference, rangeObj);
						buff.append(" ");
						buff.append(attribVarName);
						buff.append(" ");
						appendUpperValue(buff, reference, rangeObj);
					}
					else {
						buff.append("AND (");
						buff.append(op1);
						buff.append(" ");
						appendLowerValue(buff, reference, rangeObj);
						buff.append(" ");
						buff.append(attribVarName);
						buff.append(")");
						buff.append("(");
						buff.append(op2);
						buff.append(" ");
						buff.append(attribVarName);
						buff.append(" ");
						appendUpperValue(buff, reference, rangeObj);
						buff.append(")");
					}
				}
			}
			// only min specified
			else if (rangeObj.getFloor() != null) {
				buff.append((doNegation ? (rangeObj.isLowerValueInclusive() ? RuleGeneratorHelper.toARTScriptOpString(Condition.OP_LESS)
						: RuleGeneratorHelper.toARTScriptOpString(Condition.OP_LESS_EQUAL))
						: (rangeObj.isLowerValueInclusive() ? RuleGeneratorHelper.toARTScriptOpString(Condition.OP_GREATER_EQUAL)
								: RuleGeneratorHelper.toARTScriptOpString(Condition.OP_GREATER))));
				buff.append(" ");
				buff.append(attribVarName);
				buff.append(" ");
				appendLowerValue(buff, reference, rangeObj);
			}
			// only max specified
			else if (rangeObj.getCeiling() != null) {
				buff.append((doNegation ? (rangeObj.isUpperValueInclusive() ? RuleGeneratorHelper.toARTScriptOpString(Condition.OP_GREATER)
						: RuleGeneratorHelper.toARTScriptOpString(Condition.OP_GREATER_EQUAL))
						: (rangeObj.isUpperValueInclusive() ? RuleGeneratorHelper.toARTScriptOpString(Condition.OP_LESS_EQUAL)
								: RuleGeneratorHelper.toARTScriptOpString(Condition.OP_LESS))));
				buff.append(" ");
				buff.append(attribVarName);
				buff.append(" ");
				appendUpperValue(buff, reference, rangeObj);
			}

			buff.append(")");
		}
	}
}
