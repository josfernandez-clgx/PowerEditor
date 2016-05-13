package com.mindbox.pe.server.generator.value;

import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.rule.PatternValueSlot;
import com.mindbox.pe.server.model.TimeSlice;

interface LHSValueSlotHelper {

	/**
	 * Generates a value string for the specified arguments.
	 * @param patternValueSlot
	 * @param generateParams
	 * @param attribVarName
	 * @param timeSlices
	 * @return string value formatted for LHS pattern
	 * @throws RuleGenerationException on error
	 */
	ValueAndComment generateValue(PatternValueSlot patternValueSlot, GuidelineGenerateParams generateParams, String attribVarName, TimeSlice[] timeSlices, GenericEntityType messageContextType, int messageContextEntityID) throws RuleGenerationException;

}
