package com.mindbox.pe.server.generator.value;

import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.rule.ValueSlot;

class CellValueRHSValueSlotHelper extends ColumnReferenceRHSValueSlotHelper {

	public String generateValue(GuidelineGenerateParams ruleParams, ValueSlot valueSlot) throws RuleGenerationException {
		return toPrintableRHSColumnValue(ruleParams.getColumnNum(), ruleParams, true);
	}
}
