package com.mindbox.pe.server.generator.value;

import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.server.config.RuleGenerationConfiguration;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.rule.PatternValueSlot;
import com.mindbox.pe.server.model.TimeSlice;

class TimeSliceValueSlotHelper extends AbstractLHSValueSlotHelper {

	TimeSliceValueSlotHelper(RuleGenerationConfiguration ruleGenerationConfiguration) {
		super(ruleGenerationConfiguration);
	}

	public ValueAndComment generateValue(PatternValueSlot patternValueSlot, GuidelineGenerateParams generateParams, String attribVarName,
			TimeSlice[] timeSlices, GenericEntityType messageContextType, int messageContextEntityID) throws RuleGenerationException {
		StringBuilder buff = new StringBuilder();
		buff.append(attribVarName);
		if (timeSlices.length == 1) {
			buff.append(" & ");
			buff.append(timeSlices[0].getName());
		}
		else {
			buff.append(" &:(<= ");
			buff.append(timeSlices[0].getName());
			buff.append(' ');
			buff.append(attribVarName);
			buff.append(' ');
			buff.append(timeSlices[timeSlices.length - 1].getName());
			buff.append(")");
		}
		return new ValueAndComment(buff.toString());
	}

}
