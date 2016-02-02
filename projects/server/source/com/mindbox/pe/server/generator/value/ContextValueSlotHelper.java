package com.mindbox.pe.server.generator.value;

import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.generator.rule.ValueSlot;

public class ContextValueSlotHelper extends AbstractRHSValueSlotHelper {

	public String generateValue(GuidelineGenerateParams ruleParams, ValueSlot valueSlot) throws RuleGenerationException {
		if (ruleParams.hasAnyGenericCategoryContext() || ruleParams.hasAnyGenericEntityContext()) {
			StringBuffer sequences = new StringBuffer();
			sequences.append("(create$ ");
			GenericEntityType[] types = null;
			if (ruleParams.hasAnyGenericCategoryContext()) {
				types = ruleParams.getGenericCategoryEntityTypesInUse();
				for (int i = 0; i < types.length; i++) {
					sequences.append(RuleGeneratorHelper.generateContextSequence(types[i].getName(), "category", ruleParams.getGenericCategoryIDs(types[i])));
					sequences.append(" ");
				}
			}
			if (ruleParams.hasAnyGenericEntityContext()) {
				types = ruleParams.getGenericEntityTypesInUse();
				for (int i = 0; i < types.length; i++) {
					sequences.append(RuleGeneratorHelper.generateContextSequence(types[i].getName(), "entity", ruleParams.getGenericEntityIDs(types[i])));
					sequences.append(" ");
				}
			}
			sequences.append(")");
			return sequences.toString();
		}
		return RuleGeneratorHelper.AE_NIL;
	}

}
