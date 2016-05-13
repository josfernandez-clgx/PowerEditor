package com.mindbox.pe.server.generator.value;

import java.util.Date;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.config.RuleGenerationConfigHelper;
import com.mindbox.pe.server.generator.AeMapper;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

abstract class AbstractOperatorHelper implements OperatorHelper {

	final Logger logger = Logger.getLogger(getClass());

	final RuleGenerationConfigHelper ruleGenerationConfiguration;

	AbstractOperatorHelper(RuleGenerationConfigHelper ruleGenerationConfiguration) {
		this.ruleGenerationConfiguration = ruleGenerationConfiguration;
	}

	private void appendAsEnumValue(StringBuilder buff, Reference reference, String enumStr, boolean asString) throws RuleGenerationException {
		logger.debug("writeValue(): writing enum '" + enumStr + "'");
		// mapEnumValue quotes the returned value if necessary
		String valueStr = AeMapper.getEnumAttributeIfApplicable(reference.getClassName(), reference.getAttributeName(), enumStr, true);
		if (valueStr != null) {
			buff.append(valueStr);
		}
		else {
			if (asString) {
				RuleGeneratorHelper.appendFormattedForStringType(buff, enumStr, true);
			}
			else {
				buff.append(enumStr);
			}
		}
	}

	private final void appendEnumValuePrefix(StringBuilder buff, int index, boolean doNegation) {
		if (index > 0) {
			buff.append((doNegation ? " & " : " | "));
		}
		if (doNegation) {
			buff.append(" ~ ");
		}
	}

	/**
	 * Write enum values.
	 * @param enumValues cannot be <code>null</code>
	 * @throws NullPointerException if <code>enumValues</code> is <code>null</code>
	 */
	protected final void appendEnumValues(StringBuilder buff, EnumValues<?> enumValues, int op, Reference reference) throws RuleGenerationException {
		boolean doNegation = ((op == Condition.OP_NOT_IN || op == Condition.OP_NOT_EQUAL) && !enumValues.isSelectionExclusion())
				|| ((op == Condition.OP_IN || op == Condition.OP_EQUAL) && enumValues.isSelectionExclusion());
		DomainClass domainclass = DomainManager.getInstance().getDomainClass(reference.getClassName());
		boolean asString = domainclass != null && domainclass.getDomainAttribute(reference.getAttributeName()) != null
				&& domainclass.getDomainAttribute(reference.getAttributeName()).getDeployType() == DeployType.STRING;
		for (int i = 0; i < enumValues.size(); i++) {
			appendEnumValuePrefix(buff, i, doNegation);
			Object enumObj = enumValues.getEnumValue(i);
			if (enumObj instanceof EnumValue) {
				RuleGeneratorHelper.appendFormattedForStringType(buff, ((EnumValue) enumObj).getDeployValue(), asString);
			}
			else {
				String enumStr = (enumObj == null ? "" : enumObj.toString());
				appendAsEnumValue(buff, reference, enumStr, asString);
			}
		}
	}

	protected final void appendFormatted(StringBuilder buff, Boolean value) {
		buff.append(value.booleanValue() ? RuleGeneratorHelper.AE_TRUE : RuleGeneratorHelper.AE_NIL);
	}

	protected final void appendFormatted(StringBuilder buff, Date date, boolean asString) {
		appendFormatted(buff, RuleGeneratorHelper.formatDateValueForLHS(date), asString);
	}

	protected final void appendFormatted(StringBuilder buff, String value, boolean asString) {
		RuleGeneratorHelper.appendFormattedForStringType(buff, value, asString);
	}

	protected final void appendFormattedNumericValue(final StringBuilder buff, final Reference reference, final Number number, final Integer precision) throws RuleGenerationException {
		DeployType deployType = findDeployType(reference);
		if (deployType == null) {
			throw new RuleGenerationException("Deploy type not found for " + reference);
		}
		else if (deployType == DeployType.CURRENCY) {
			buff.append(RuleGeneratorHelper.getFormatterFactoryForCurrentThread().getCurrencyFormatter(precision).format(number.doubleValue()));
		}
		else if (deployType == DeployType.FLOAT || deployType == DeployType.PERCENT) {
			buff.append(RuleGeneratorHelper.getFormatterFactoryForCurrentThread().getFloatFormatter(precision).format(number.doubleValue()));
		}
		else if (deployType == DeployType.INTEGER) {
			buff.append(number.intValue());
		}
		else {
			appendFormatted(buff, number.toString(), (deployType == DeployType.STRING));
		}
	}

	protected final DeployType findDeployType(Reference reference) throws RuleGenerationException {
		String className = reference.getClassName();
		String attribName = reference.getAttributeName();

		DomainClass domainclass = DomainManager.getInstance().getDomainClass(className);
		if (domainclass == null) {
			logger.error("Invalid class: " + className);
			throw new RuleGenerationException("No class " + className + " found");
		}
		DomainAttribute domainattribute = domainclass.getDomainAttribute(attribName);
		if (domainattribute == null) {
			logger.error("Invalid attr: " + className);
			throw new RuleGenerationException("Domain class " + className + " does not have attribute " + attribName);
		}
		return domainattribute.getDeployType();
	}
}
