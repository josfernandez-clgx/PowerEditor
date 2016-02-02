package com.mindbox.pe.server.generator.value;

import java.util.Date;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.AbstractTemplateColumn;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.generator.AbstractGenerateParms;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.generator.rule.ColumnReferencePatternValueSlot;
import com.mindbox.pe.server.generator.rule.ValueSlot;
import com.mindbox.pe.server.generator.value.rhscolref.RHSColRefWriteValueHelper;
import com.mindbox.pe.server.generator.value.rhscolref.RHSColRefWriteValueHelperFactory;

class ColumnReferenceRHSValueSlotHelper extends AbstractRHSValueSlotHelper {


	public String generateValue(GuidelineGenerateParams ruleParams, ValueSlot valueSlot) throws RuleGenerationException {
		return toPrintableRHSColumnValue(
				((ColumnReferencePatternValueSlot) valueSlot).getColumnNo(),
				ruleParams,
				(((ColumnReferencePatternValueSlot) valueSlot).getParameterDeployType() == DeployType.STRING));
	}

	final String toPrintableRHSColumnValue(int column, AbstractGenerateParms ruleParams, boolean addQuotesIfNecessary)
			throws RuleGenerationException {
		Object colValue = ruleParams.getColumnValue(column);

		// TT 1991
		if (!addQuotesIfNecessary && (colValue != null) && (colValue instanceof Date)) {
			colValue = RuleGeneratorHelper.toRuleDateString((Date) colValue);
		}

		if (colValue != null) {
			return writeVal(
					ruleParams.getTemplate().getColumn(column),
					colValue,
					addQuotesIfNecessary,
					ConfigurationManager.getInstance().getRuleGenerationConfiguration(ruleParams.getUsage()).generateMultiEnumAsSequenceInRHS());
		}
		else {
			return RuleGeneratorHelper.AE_NIL;
		}

	}

	/**
	 * Used to generate value string for RHS.
	 * 
	 * @param generator
	 * @param column
	 * @param obj
	 * @param addQuotes
	 * @param multiEnumAsSequence
	 * @return value string
	 */
	@SuppressWarnings("unchecked")
	private String writeVal(AbstractTemplateColumn column, Object obj, boolean addQuotes, boolean multiEnumAsSequence)
			throws RuleGenerationException {
		Logger logger = Logger.getLogger(getClass());
		if (obj != null) logger.debug(">>> writeVal: " + column + ", " + obj + " (" + obj.getClass().getName() + ")");
		StringBuilder buff = new StringBuilder();
		// check for null or empty value
		if (obj == null || obj.toString().length() == 0) {
			buff.append(RuleGeneratorHelper.AE_NIL);
		}
		else {
			RHSColRefWriteValueHelper helper = RHSColRefWriteValueHelperFactory.getInstance().getRHSColRefWriteValueHelper(obj);
			helper.writeValue(buff, obj, column, addQuotes, multiEnumAsSequence);
		}		
		return buff.toString();
	}

}
