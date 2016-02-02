/**
 * 
 */
package com.mindbox.pe.server.generator.value.rhscolref;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.AbstractTemplateColumn;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.ColumnDataSpecDigest.EnumSourceType;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.server.cache.DeploymentManager;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

class EnumValuesHelper extends AbstractWriteValueHelper<EnumValues<?>> {

	@SuppressWarnings("unchecked")
	@Override
	public void writeValue(StringBuilder buff, EnumValues<?> enumValues, AbstractTemplateColumn column, boolean addQuotes,
			boolean multiEnumAsSequence) throws RuleGenerationException {
		logger.debug("writeVal: handling enum values...");

		// 1st pass at determining if attribute map or manually tyed list
		boolean printQuote = addQuotes;
		boolean enumListIsFromAttributeMap = column.getColumnDataSpecDigest().getEnumSourceType() == EnumSourceType.DOMAIN_ATTRIBUTE
				&& !UtilBase.isEmpty(column.getMappedAttribute());
		if (enumListIsFromAttributeMap) {
			DomainAttribute attribute = DomainManager.getInstance().getDomainAttribute(column.getMAClassName(), column.getMAAttributeName());
			enumListIsFromAttributeMap = (attribute != null);
			if (enumListIsFromAttributeMap) {
				printQuote = attribute.getDeployType() == DeployType.STRING && addQuotes;
			}
		}

		if (enumValues.isEmpty()) {
			return;
		}
		if (enumValues.get(0) instanceof EnumValue) {
			writeEnumValueEnumValues(buff, (EnumValues<EnumValue>) enumValues, column, printQuote, multiEnumAsSequence);
		}
		else {
			// Gene Kim, 2008-06-10: This shouldn't happen; i.e., valueObj should be an instanceof EnumValue for EnumList columns
			//                       This is here for backward-compatibility!
			writeOtherEnumValues(buff, enumValues, column, printQuote, multiEnumAsSequence, enumListIsFromAttributeMap);
		}
	}

	private void writeEnumValueEnumValues(StringBuilder buff, EnumValues<EnumValue> enumValues, AbstractTemplateColumn column,
			boolean printQuote, boolean multiEnumAsSequence) throws RuleGenerationException {
		// Generate multi enum as a sequence. This is the prefered deploy option
		String[] deployValues = new String[enumValues.size()];
		for (int i = 0; i < deployValues.length; i++) {
			deployValues[i] = enumValues.get(i).getDeployValue();
		}
		if (multiEnumAsSequence) {
			buff.append("(");
			buff.append(RuleGeneratorHelper.CREATE_SEQ_FUNCTION);
			buff.append(" ");
			buff = writeDeployValues(buff, deployValues, printQuote, enumValues.isSelectionExclusion());
			buff.append(')');
		}
		else {
			if (printQuote) {
				buff.append(RuleGeneratorHelper.QUOTE);
			}
			for (int i = 0; i < deployValues.length; i++) {
				buff.append(deployValues[i]);
				if (i != deployValues.length - 1) buff.append(" | ");
			}
			if (printQuote) {
				buff.append(RuleGeneratorHelper.QUOTE);
			}
		}
	}

	private void writeOtherEnumValues(StringBuilder buff, EnumValues<?> enumValues, AbstractTemplateColumn column, boolean printQuote,
			boolean multiEnumAsSequence, boolean enumListIsFromAttributeMap) throws RuleGenerationException {
		String[] deployValues = null;
		// Generate multi enum as a sequence. This is the prefered deploy option
		if (multiEnumAsSequence && column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENUM_LIST)) {
			deployValues = DeploymentManager.getInstance().getEnumDeployValues(
					column.getMAClassName(),
					column.getMAAttributeName(),
					enumValues);
			if (deployValues != null) {
				if (deployValues.length == 0) {//try manual list.this means user has value selected from attribute map but typed in a list manually to override that selection
					enumListIsFromAttributeMap = false;
					deployValues = (String[]) enumValues.toStringArray();
				}
			}
			else {//try manual list.this means user has value selected from attribute map but typed in a list manually to override that selection
				enumListIsFromAttributeMap = false;
				deployValues = (String[]) enumValues.toStringArray();
			}

			buff.append("(");
			buff.append(RuleGeneratorHelper.CREATE_SEQ_FUNCTION);
			buff.append(" ");
			buff = writeDeployValues(buff, deployValues, printQuote, enumValues.isSelectionExclusion());
			buff.append(')');
			return;
		}

		// Deploy multi enum as string, if applicable, or as a list of values separated
		// by "|"
		// This shouldn't be used; it's here for backward compatibility
		if (enumListIsFromAttributeMap && !multiEnumAsSequence
				&& column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENUM_LIST)) {//The list was from domain file and correct type was chosen
			logger.debug("writeVal: following attribute map for Enum...");
			String dispValue = null;
			if (printQuote) {
				buff.append(RuleGeneratorHelper.QUOTE);
			}
			for (int i = 0; i < enumValues.size(); i++) {
				dispValue = enumValues.getEnumValueAsString(i);
				String valueStr = DeploymentManager.getInstance().getEnumDeployValue(
						column.getMAClassName(),
						column.getMAAttributeName(),
						dispValue,
						false);
				logger.debug("writeVal: printing value = " + valueStr);
				buff.append((valueStr == null ? "ERROR-DeployValue-Not-Found-For-" + dispValue : valueStr));
				// report error
				if (valueStr == null) {
					throw new RuleGenerationException(dispValue + " has no deploy value for attribute " + column.getMAClassName() + "."
							+ column.getMAAttributeName() + "; Column=" + column);
				}
				if (i != enumValues.size() - 1) buff.append(" | ");
			}
			if (printQuote) {
				buff.append(RuleGeneratorHelper.QUOTE);
			}
		}
		else if (!enumListIsFromAttributeMap && !multiEnumAsSequence
				&& column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENUM_LIST)) {
			logger.debug("writeVal: manually typed Enum...");
			String value = null;
			if (printQuote) {
				buff.append(RuleGeneratorHelper.QUOTE);
			}
			for (int i = 0; i < enumValues.size(); i++) {
				value = enumValues.getEnumValueAsString(i);
				logger.debug("writeVal: printing value = " + value);
				buff.append((value == null ? "ERROR-DeployValue-Not-Found-For-" : value));
				// report error
				if (value == null) {
					throw new RuleGenerationException(value + " has no deploy value for attribute ");
				}
				if (i != enumValues.size() - 1) buff.append(" | ");
			}
			if (printQuote) {
				buff.append(RuleGeneratorHelper.QUOTE);
			}
		}
		else {
			if (printQuote) {
				buff.append(RuleGeneratorHelper.QUOTE);
			}
			for (int i = 0; i < enumValues.size(); i++) {
				buff.append(enumValues.getEnumValueAsString(i));
				if (i != enumValues.size() - 1) buff.append(" | ");
			}
			if (printQuote) {
				buff.append(RuleGeneratorHelper.QUOTE);
			}
		}
	}
}