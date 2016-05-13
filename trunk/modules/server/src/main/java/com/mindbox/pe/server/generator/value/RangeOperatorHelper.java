package com.mindbox.pe.server.generator.value;

import static com.mindbox.pe.common.LogUtil.logDebug;

import java.util.Date;

import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.table.IRange;
import com.mindbox.pe.server.config.RuleGenerationConfigHelper;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.model.TimeSlice;

class RangeOperatorHelper extends AbstractOperatorHelper {

	RangeOperatorHelper(RuleGenerationConfigHelper ruleGenerationConfiguration) {
		super(ruleGenerationConfiguration);
	}

	private void appendLowerValue(StringBuilder buff, Reference reference, IRange rangeObj, final Integer precision) throws RuleGenerationException {
		if (rangeObj.isForDate()) {
			buff.append(RuleGeneratorHelper.formatDateValueForLHS(new Date(rangeObj.getFloor().longValue())));
		}
		else {
			appendFormattedNumericValue(buff, reference, rangeObj.getFloor(), precision);
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
	private void appendRangeValue(StringBuilder buff, String attribVarName, Reference reference, IRange rangeObj, boolean doNegation, final Integer precision) throws RuleGenerationException {
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
				appendLowerValue(buff, reference, rangeObj, precision);
				buff.append(")");
			}
			else {
				buff.append(attribVarName + " & ");
				appendLowerValue(buff, reference, rangeObj, precision);
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
					appendLowerValue(buff, reference, rangeObj, precision);
					buff.append(" ");
					buff.append(attribVarName);
					buff.append(")");
					buff.append("(");
					buff.append(op2);
					buff.append(" ");
					buff.append(attribVarName);
					buff.append(" ");
					appendUpperValue(buff, reference, rangeObj, precision);
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
						appendLowerValue(buff, reference, rangeObj, precision);
						buff.append(" ");
						buff.append(attribVarName);
						buff.append(" ");
						appendUpperValue(buff, reference, rangeObj, precision);
					}
					else {
						buff.append("AND (");
						buff.append(op1);
						buff.append(" ");
						appendLowerValue(buff, reference, rangeObj, precision);
						buff.append(" ");
						buff.append(attribVarName);
						buff.append(")");
						buff.append("(");
						buff.append(op2);
						buff.append(" ");
						buff.append(attribVarName);
						buff.append(" ");
						appendUpperValue(buff, reference, rangeObj, precision);
						buff.append(")");
					}
				}
			}
			// only min specified
			else if (rangeObj.getFloor() != null) {
				buff.append((doNegation ? (rangeObj.isLowerValueInclusive()
						? RuleGeneratorHelper.toARTScriptOpString(Condition.OP_LESS)
						: RuleGeneratorHelper.toARTScriptOpString(Condition.OP_LESS_EQUAL)) : (rangeObj.isLowerValueInclusive()
						? RuleGeneratorHelper.toARTScriptOpString(Condition.OP_GREATER_EQUAL)
						: RuleGeneratorHelper.toARTScriptOpString(Condition.OP_GREATER))));
				buff.append(" ");
				buff.append(attribVarName);
				buff.append(" ");
				appendLowerValue(buff, reference, rangeObj, precision);
			}
			// only max specified
			else if (rangeObj.getCeiling() != null) {
				buff.append((doNegation ? (rangeObj.isUpperValueInclusive()
						? RuleGeneratorHelper.toARTScriptOpString(Condition.OP_GREATER)
						: RuleGeneratorHelper.toARTScriptOpString(Condition.OP_GREATER_EQUAL)) : (rangeObj.isUpperValueInclusive()
						? RuleGeneratorHelper.toARTScriptOpString(Condition.OP_LESS_EQUAL)
						: RuleGeneratorHelper.toARTScriptOpString(Condition.OP_LESS))));
				buff.append(" ");
				buff.append(attribVarName);
				buff.append(" ");
				appendUpperValue(buff, reference, rangeObj, precision);
			}

			buff.append(")");
		}
	}

	private void appendUpperValue(StringBuilder buff, Reference reference, IRange rangeObj, final Integer precision) throws RuleGenerationException {
		if (rangeObj.isForDate()) {
			buff.append(RuleGeneratorHelper.formatDateValueForLHS(new Date(rangeObj.getCeiling().longValue())));
		}
		else {
			appendFormattedNumericValue(buff, reference, rangeObj.getCeiling(), precision);
		}
	}

	@Override
	public ValueAndComment formatForPattern(Object valueObj, int op, String attribVarName, boolean asString, Reference reference, TimeSlice timeSlice, final Integer precision)
			throws RuleGenerationException {
		logDebug(logger, "writeValue: processing BETWEEN.... for %s", valueObj);
		StringBuilder buff = new StringBuilder();

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

		logDebug(logger, "writeValue: doNegation = %s", doNegation);
		logDebug(logger, "writeValue: rangeObj = %s", rangeObj);
		logDebug(logger, "writeValue: precision = %s", precision);
		if (rangeObj == null) {
			if (valueObj == null) {
				buff.append(attribVarName);
			}
			else {
				appendFormatted(buff, valueObj.toString(), asString);
			}
		}
		else {
			appendRangeValue(buff, attribVarName, reference, rangeObj, doNegation, precision);
		}
		return new ValueAndComment(buff.toString());
	}
}
