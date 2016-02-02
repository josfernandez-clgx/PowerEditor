package com.mindbox.pe.server.generator.value;

import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.rule.ActivationSpanValueSlot;
import com.mindbox.pe.server.generator.rule.ValueSlot;

public final class ActivationSpanValueSlotHelper extends AbstractRHSValueSlotHelper {

	private static final String SINGLE_SPAN = "SINGLE";
	private static final String MULTIPLE_SPAN = "MULTIPLE";

	public String generateValue(GuidelineGenerateParams ruleParams, ValueSlot valueSlot) throws RuleGenerationException {
		if (!(valueSlot instanceof ActivationSpanValueSlot)) throw new RuleGenerationException("Invalid value slot type: " + valueSlot);
		return ruleParams.spansMultipleActivations() ? MULTIPLE_SPAN : SINGLE_SPAN;
	}

}
