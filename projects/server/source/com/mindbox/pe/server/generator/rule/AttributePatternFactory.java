package com.mindbox.pe.server.generator.rule;

import java.text.MessageFormat;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.DomainClassLink;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.ColumnReference;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.MathExpressionValue;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.rule.Value;
import com.mindbox.pe.server.config.RuleGenerationConfiguration;
import com.mindbox.pe.server.config.RuleLHSValueConfig;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.model.DomainClassLinkPattern;

/**
 * Factory for {@link AttributePattern}.
 *
 */
public final class AttributePatternFactory {

	private static final MessageFormat linkFunctionFormat = new MessageFormat("&:({0} {1} {2})");
	private static final MessageFormat mathExpSlotTextFormat = new MessageFormat("({0} '{'0'}' {1})");
	private static final String DEFAULT_DOMAINCLASSLINK_ATTRIBUTE_SUFFIX = "-link";

	private final PatternFactoryHelper helper;
	private final Logger logger = Logger.getLogger(getClass());

	public AttributePatternFactory(PatternFactoryHelper helper) {
		this.helper = helper;
	}

	public AttributePattern createAttributePattern(Condition condition, String variableOverride, TemplateUsageType usageType)
			throws RuleGenerationException {
		// if parent is NOT or OR and condition's value is attribute ref, wrap patterns with AND
		switch (condition.getOp()) {
		case Condition.OP_ANY_VALUE:
			return new StaticTextAttributePattern(helper.getDeployLabelForAttribute(condition.getReference()), helper.asVariableName(
					condition.getReference().getAttributeName(),
					variableOverride));
		case Condition.OP_IS_EMPTY:
		case Condition.OP_IS_NOT_EMPTY: {
			RuleLHSValueConfig valueConfig = helper.getRuleGenerationConfiguration(usageType).getLHSValueConfig(
					RuleGenerationConfiguration.VAUE_TYPE_UNSPECIFIED);
			if (valueConfig == null) {
				throw new RuleGenerationException("LHS Value configuration for '" + RuleGenerationConfiguration.VAUE_TYPE_UNSPECIFIED
						+ "' not found in PowerEditor Configuraiton XML");
			}

			String attribVarName = helper.asVariableName(condition.getReference().getAttributeName(), variableOverride);
			StringBuffer buff = new StringBuffer();
			if (condition.getOp() == Condition.OP_IS_NOT_EMPTY) {
				buff.append(" &:(/= ");
				buff.append(attribVarName);
				buff.append(" ");
			}
			else {
				buff.append(" & ");
			}
			RuleGeneratorHelper.appendFormattedForStringType(buff, valueConfig.getDeployValue(), valueConfig.isValueAsString());
			if (condition.getOp() == Condition.OP_IS_NOT_EMPTY) {
				buff.append(")");
			}
			return new StaticTextAttributePattern(
					helper.getDeployLabelForAttribute(condition.getReference()),
					attribVarName,
					buff.toString());
		}
		default:
			Value value = condition.getValue();
			if (value instanceof ColumnReference) {
				return new ColumnRefValueSlotAttributePattern(
						helper.getDeployLabelForAttribute(condition.getReference()),
						helper.asVariableName(condition.getReference().getAttributeName(), variableOverride),
						condition.getReference(),
						condition.getOp(),
						((ColumnReference) value).getColumnNo());
			}
			else if (value instanceof MathExpressionValue) {
				MathExpressionValue meValue = (MathExpressionValue) value;

				// Create a column reference attribute pattern with slot text for math expression in the format of (op {0} ?var)
				String slotText = buildValueForMathExpression(meValue);
				if (((MathExpressionValue) value).getColumnReference() != null) {
					return new ColumnRefValueSlotAttributePattern(
							helper.getDeployLabelForAttribute(condition.getReference()),
							helper.asVariableName(condition.getReference().getAttributeName(), variableOverride),
							condition.getReference(),
							condition.getOp(),
							((MathExpressionValue) value).getColumnReference().getColumnNo(),
							slotText);
				}
				else {
					return new StringValueSlotAttributePattern(
							helper.getDeployLabelForAttribute(condition.getReference()),
							helper.asVariableName(condition.getReference().getAttributeName(), variableOverride),
							condition.getReference(),
							condition.getOp(),
							MessageFormat.format(slotText, new Object[]{meValue.getValue()}));
				}
			}
			else if (value instanceof Reference) {
				// Generate the default variable name
				String attrVariable = helper.asVariableName(condition.getReference().getAttributeName(), variableOverride);
				return new StaticTextAttributePattern(
						helper.getDeployLabelForAttribute(condition.getReference()),
						attrVariable,
						linkFunctionFormat.format(new Object[] { RuleGeneratorHelper.toARTScriptOpString(condition.getOp()), attrVariable, helper.asVariableName(((Reference) value).getAttributeName()) }));
			}
			else {
				// TT 1970: strip off quotes if deploy type of the attribute is not string
				String strToUse = (value == null ? RuleGeneratorHelper.AE_NIL : RuleGeneratorHelper.formatForStringType(
						value.toString(),
						helper.isStringDeployTypeForAttribute(condition.getReference())));
				return new StringValueSlotAttributePattern(
						helper.getDeployLabelForAttribute(condition.getReference()),
						helper.asVariableName(condition.getReference().getAttributeName(), variableOverride),
						condition.getReference(),
						condition.getOp(),
						strToUse);
			}
		} // switch
	}

	public AttributePattern createLinkAttributePattern(DomainClassLinkPattern linkPattern, TemplateUsageType usageType) {
		return createLinkAttributePattern(linkPattern.getDomainClassLink(), linkPattern.getObjectName(), usageType);
	}

	public AttributePattern createLinkAttributePattern(DomainClassLink dcLink, String objectName, TemplateUsageType usageType) {
		// set attribute name
		String attributeName;
		if (dcLink.getDeployValueName() == null || dcLink.getDeployValueName().trim().length() == 0) {
			logger.warn("Domain class link " + dcLink + " has no deploy value set; using '" + dcLink.getChildName()
					+ DEFAULT_DOMAINCLASSLINK_ATTRIBUTE_SUFFIX + "'.");
			attributeName = dcLink.getChildName() + DEFAULT_DOMAINCLASSLINK_ATTRIBUTE_SUFFIX;
		}
		else {
			attributeName = dcLink.getDeployValueName();
		}

		// use link test function, if configured
		String variableName;
		String text;
		RuleGenerationConfiguration.LinkPatternConfig linkPatternConfig = helper.getRuleGenerationConfiguration(usageType).getLinkPatternConfig();
		if (linkPatternConfig != null && linkPatternConfig.useTestFunction()) {
			String linkObjectName = helper.asVariableName(dcLink.getChildName());
			variableName = (objectName != null && objectName.length() > 0 ? "?" + objectName : linkObjectName
					+ (UtilBase.isEmpty(linkPatternConfig.getVariableSuffix()) ? "-suffix" : linkPatternConfig.getVariableSuffix()));
			text = linkFunctionFormat.format(new Object[] { linkPatternConfig.getTestFunctionName(), linkObjectName, variableName });
		}
		else {
			variableName = (objectName == null || objectName.length() == 0 ? helper.asVariableName(dcLink.getChildName()) : "?"
					+ objectName);
			text = null;
		}
		return new StaticTextAttributePattern(attributeName, variableName, text);
	}

	private String buildValueForMathExpression(MathExpressionValue meValue) {
		return mathExpSlotTextFormat.format(new Object[] { meValue.getOperator(), helper.asVariableName(meValue.getAttributeReference().getAttributeName()) });
	}
}
