package com.mindbox.pe.server.generator.value;

import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.rule.ValueSlot;

interface RHSValueSlotHelper {

	String generateValue(GuidelineGenerateParams ruleParams, ValueSlot valueSlot) throws RuleGenerationException;
}
