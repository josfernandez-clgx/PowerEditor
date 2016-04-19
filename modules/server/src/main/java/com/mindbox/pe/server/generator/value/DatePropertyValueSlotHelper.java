package com.mindbox.pe.server.generator.value;

import static com.mindbox.pe.model.Constants.THREADLOCAL_FORMAT_DATE_TIME_MIN;

import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.generator.rule.DatePropertyValueSlot;
import com.mindbox.pe.server.generator.rule.ValueSlot;

public class DatePropertyValueSlotHelper extends AbstractRHSValueSlotHelper {

	public String generateValue(GuidelineGenerateParams ruleParams, ValueSlot valueSlot) throws RuleGenerationException {
		DatePropertyValueSlot datePropertyValueSlot = (DatePropertyValueSlot) valueSlot;
		if (datePropertyValueSlot.getSlotValue().equals(DatePropertyValueSlot.DATE_TYPE_ACTIVATION_DATE)) {
			if (ruleParams.getSunrise() == null) {
				return RuleGeneratorHelper.AE_NIL;
			}
			else {
				return RuleGeneratorHelper.QUOTE + THREADLOCAL_FORMAT_DATE_TIME_MIN.get().format(ruleParams.getSunrise().getDate()) + RuleGeneratorHelper.QUOTE;
			}
		}
		else if (datePropertyValueSlot.getSlotValue().equals(DatePropertyValueSlot.DATE_TYPE_EXPIRATION_DATE)) {
			if (ruleParams.getSunset() == null) {
				return RuleGeneratorHelper.AE_NIL;
			}
			else {
				return RuleGeneratorHelper.QUOTE + THREADLOCAL_FORMAT_DATE_TIME_MIN.get().format(ruleParams.getSunset().getDate()) + RuleGeneratorHelper.QUOTE;
			}
		}
		else {
			throw new RuleGenerationException("Unsupported date type property: " + datePropertyValueSlot.getSlotValue());
		}
	}
}
