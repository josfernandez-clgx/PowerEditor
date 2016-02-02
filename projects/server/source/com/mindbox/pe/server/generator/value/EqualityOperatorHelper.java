package com.mindbox.pe.server.generator.value;

import java.util.Date;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.config.RuleGenerationConfiguration;
import com.mindbox.pe.server.generator.AeMapper;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.model.TimeSlice;

class EqualityOperatorHelper extends AbstractOperatorHelper {

	private static String getEqualifyOperatorString(DeployType deployType) {
		if (deployType == DeployType.CURRENCY || deployType == DeployType.DATE || deployType == DeployType.FLOAT
				|| deployType == DeployType.INTEGER || deployType == DeployType.PERCENT) {
			return RuleGeneratorHelper.EQUALIFY_FUNCTION_NUMERIC;
		}
		else {
			return RuleGeneratorHelper.EQUALIFY_FUNCTION;
		}
	}

	EqualityOperatorHelper(RuleGenerationConfiguration ruleGenerationConfiguration) {
		super(ruleGenerationConfiguration);
	}

	public ValueAndComment formatForPattern(Object valueObj, int op, String attribVarName, boolean asString, Reference reference,
			TimeSlice timeSlice) throws RuleGenerationException {
		StringBuffer buff = new StringBuffer();
		buff.append(attribVarName);
		DeployType deployType = findDeployType(reference); //  TT 1991
		if (valueObj != null && !UtilBase.isEmpty(valueObj.toString())) {
			logger.debug("--> formatForPattern: " + valueObj + ",op=" + op + ",var=" + attribVarName);
			boolean doNegation = (op == Condition.OP_NOT_EQUAL);
			if (valueObj instanceof CategoryOrEntityValue || valueObj instanceof CategoryOrEntityValues) {
				throw new RuleGenerationException("Validation error: EntityList column values cannot be used with "
						+ Condition.Aux.toOpString(op));
			}
			else if (EnumValues.class.isInstance(valueObj)) {
				logger.debug("    formatForPattern: value is enumValues: size = " + ((EnumValues<?>) valueObj).size());
				if (!((EnumValues<?>) valueObj).isEmpty()) {
					buff.append(" & ");
					appendEnumValues(buff, (EnumValues<?>) valueObj, op, reference);
				}
			}
			else {
				buff.append(" &:(");
				if (doNegation) {
					buff.append(RuleGeneratorHelper.INEQUALIFY_FUNCTION);
				}
				else {
					buff.append(getEqualifyOperatorString(deployType));
				}
				buff.append(" ");
				buff.append(attribVarName);
				buff.append(" ");
				if (valueObj instanceof Boolean) {
					appendFormatted(buff, (Boolean) valueObj);
				}
				else if (valueObj instanceof Date) {
					appendFormatted(buff, (Date) valueObj, asString);
				}
				// TT 2023
				else if (valueObj instanceof EnumValue) {
					appendFormatted(buff, ((EnumValue) valueObj).getDeployValue(), asString);
				}
				// TT 1991
				else if ((valueObj instanceof String) && (deployType == DeployType.DATE)) {
					try {
						appendFormatted(buff, RuleGeneratorHelper.parseToDate(valueObj.toString()), false);
					}
					catch (Exception ex) {
						throw new RuleGenerationException("Invalid date: can't convert " + valueObj + " to date: " + ex.getMessage());
					}
				}
				else if (valueObj instanceof String && ((String) valueObj).length() > 0
						&& (((String) valueObj).charAt(0) == '?' || ((String) valueObj).charAt(0) == '(')) {
					buff.append((String) valueObj);
				}
				else if (DomainManager.getInstance().hasEnumerationValues(reference.getClassName(), reference.getAttributeName())) {
					appendAsEnumValueIfAppropriate(buff, deployType, reference, valueObj, asString);
				}
				else {
					appendFormattedValueForReference(buff, deployType, reference, valueObj, asString);
				}

				buff.append(")");
			}
		}
		return new ValueAndComment(buff.toString());
	}

	private void appendAsEnumValueIfAppropriate(StringBuffer buff, DeployType deployType, Reference reference, Object value,
			boolean asString) throws RuleGenerationException {
		// getEnumAttributeIfApplicable quotes the returned value if necessary
		String valueStr = AeMapper.getEnumAttributeIfApplicable(
				reference.getClassName(),
				reference.getAttributeName(),
				value.toString(),
				true);
		if (valueStr == null) {
			appendFormattedValueForReference(buff, deployType, reference, value, asString);
		}
		else {
			buff.append(valueStr);
		}
	}

	/**
	 * 
	 * @param buff
	 * @param reference
	 * @param obj Not of type java.util.Date, EnumValues, or java.lang.Boolean, and not <code>null</code>
	 * @throws RuleGenerationException
	 */
	private void appendFormattedValueForReference(StringBuffer buff, DeployType deployType, Reference reference, Object obj,
			boolean asString) throws RuleGenerationException {
		try {
			if (deployType == null) {
				throw new RuleGenerationException("Deploy type not found for " + reference);
			}
			else if (deployType == DeployType.BOOLEAN) {
				Boolean boolValue = (obj.toString().equalsIgnoreCase("YES") || obj.toString().equalsIgnoreCase(RuleGeneratorHelper.AE_TRUE)
						? Boolean.TRUE
						: Boolean.valueOf(obj.toString()));
				appendFormatted(buff, boolValue);
			}
			else if (deployType == DeployType.DATE) {
				try {
					appendFormatted(buff, RuleGeneratorHelper.parseToDate(obj.toString()), false);
				}
				catch (Exception ex) {
					throw new RuleGenerationException("Invalid date: can't convert " + obj + " to date: " + ex.getMessage());
				}
			}
			else if (deployType == DeployType.CURRENCY) {
				buff.append(RuleGeneratorHelper.percentFormatter.format(Double.parseDouble(obj.toString())));
			}
			else if (deployType == DeployType.FLOAT) {
				buff.append(RuleGeneratorHelper.floatFormatter.format(Double.parseDouble(obj.toString())));
			}
			else if (deployType == DeployType.PERCENT) {
				buff.append(RuleGeneratorHelper.floatFormatter.format(Double.parseDouble(obj.toString())));
			}
			else if (deployType == DeployType.SYMBOL || deployType == DeployType.INTEGER || deployType == DeployType.CODE) {
				buff.append(obj.toString());
			}
			else if (deployType == DeployType.STRING) {
				appendFormatted(buff, obj.toString(), asString);
			}
			else {
				throw new RuleGenerationException("Invalid deployType: " + deployType);
			}
		}
		catch (Exception ex) {
			String msg = "Failed to print formatted value of " + obj + " for " + reference;
			logger.error(msg, ex);
			throw new RuleGenerationException(msg);
		}
	}
}
