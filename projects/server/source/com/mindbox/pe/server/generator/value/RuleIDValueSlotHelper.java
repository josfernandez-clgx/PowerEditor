package com.mindbox.pe.server.generator.value;

import java.util.List;

import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.generator.rule.ValueSlot;


public class RuleIDValueSlotHelper extends AbstractRHSValueSlotHelper {

	public String generateValue(GuidelineGenerateParams ruleParams, ValueSlot valueSlot) throws RuleGenerationException {
		List<Long> ruleIDList = ruleParams.getRuleIDs();
		if (ruleIDList.isEmpty()) {
			return RuleGeneratorHelper.AE_NIL;
		}
		else if (ruleIDList.size() == 1) {
			return String.valueOf(ruleIDList.get(0));
		}
		else {
			StringBuilder buff = new StringBuilder();
			buff.append(RuleGeneratorHelper.OPEN_PAREN);
			buff.append(RuleGeneratorHelper.CREATE_SEQ_FUNCTION);
			for (Long ruleID : ruleIDList) {
				buff.append(" ");
				buff.append(ruleID);
			}
			buff.append(RuleGeneratorHelper.CLOSE_PAREN);
			return buff.toString();
		}
	}

}
