package com.mindbox.pe.server.generator.value;

import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.generator.rule.EntityIDValueSlot;
import com.mindbox.pe.server.generator.rule.ValueSlot;

public class EntityIDValueSlotHelper extends AbstractRHSValueSlotHelper {

	public String generateValue(GuidelineGenerateParams ruleParams, ValueSlot valueSlot) throws RuleGenerationException {
		if (!(valueSlot instanceof EntityIDValueSlot)) throw new RuleGenerationException("Invalid value slot type: " + valueSlot);
		GenericEntityType entityType = ((EntityIDValueSlot) valueSlot).getEntityType();
		String entityVarName = ((EntityIDValueSlot) valueSlot).getEntityVariableName();
		if (ruleParams.hasGenericCategoryContext(entityType) || ruleParams.hasGenericEntityContext(entityType)) {
			return entityVarName;
		}
		else {
			return RuleGeneratorHelper.AE_NIL;
		}
	}

}
