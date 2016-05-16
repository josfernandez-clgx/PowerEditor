package com.mindbox.pe.server.generator.value;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.RuleGenerationConfigHelper;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.rule.ColumnReferencePatternValueSlot;
import com.mindbox.pe.server.generator.rule.ContextElementPatternValueSlot;
import com.mindbox.pe.server.generator.rule.FocusOfAttentionPatternValueSlot;
import com.mindbox.pe.server.generator.rule.PatternValueSlot;
import com.mindbox.pe.server.generator.rule.StringValuePatternValueSlot;
import com.mindbox.pe.server.generator.rule.TimeSlicePatternValueSlot;
import com.mindbox.pe.server.model.TimeSlice;

/**
 * Helper that generates string formatted for LHS patterns.
 * <p>
 * This is resposible for writing the part of the following ARTScript patter in:
 * </p>
 * <p>
 * <code>(PE:symbol-attr ?symbolattribute &amp;:(eq ?symbolattribute SubstantiallyRehabilitated))</code>
 * </p>
 */
public class LHSValueHelper {

	private final Logger logger = Logger.getLogger(getClass());
	private final FocusOfAttentionValueSlotHelper focusOfAttentionValueSlotHelper;
	private final TimeSliceValueSlotHelper timeSliceValueSlotHelper;
	private final ContextElementValueSlotHelper contextElementValueSlotHelper;
	private final OperatorBasedLHSValueSlotHelper operatorBasedValueSlotHelper;

	LHSValueHelper(TemplateUsageType usageType) {
		final RuleGenerationConfigHelper ruleGenerationConfiguration = ConfigurationManager.getInstance().getRuleGenerationConfigHelper(usageType);
		focusOfAttentionValueSlotHelper = new FocusOfAttentionValueSlotHelper(ruleGenerationConfiguration);
		timeSliceValueSlotHelper = new TimeSliceValueSlotHelper(ruleGenerationConfiguration);
		contextElementValueSlotHelper = new ContextElementValueSlotHelper(ruleGenerationConfiguration);
		operatorBasedValueSlotHelper = new OperatorBasedLHSValueSlotHelper(ruleGenerationConfiguration);
		logger.info("<init> completed for " + usageType);
	}

	/**
	 * Returns the specified value slot formatted for LHS pattern value.
	 * This should include the attribute variable name in the returned string.
	 * @param patternValueSlot pattern value slot
	 * @param generateParams generateParams
	 * @param attribVarName attribute variable name
	 * @param timeSlices time slices
	 * @param messageContextType messageContextType
	 * @param messageContextEntityID messageContextEntityID
	 * @return string representing formatted valueObj and optional comment string
	 * @throws RuleGenerationException on error
	 */
	public ValueAndComment generateValue(PatternValueSlot patternValueSlot, GuidelineGenerateParams generateParams, String attribVarName, TimeSlice[] timeSlices,
			GenericEntityType messageContextType, int messageContextEntityID) throws RuleGenerationException {
		return getValueSlotHelper(patternValueSlot).generateValue(patternValueSlot, generateParams, attribVarName, timeSlices, messageContextType, messageContextEntityID);
	}

	private LHSValueSlotHelper getValueSlotHelper(PatternValueSlot patternValueSlot) throws RuleGenerationException {
		if (patternValueSlot instanceof ColumnReferencePatternValueSlot || patternValueSlot instanceof StringValuePatternValueSlot) {
			return operatorBasedValueSlotHelper;
		}
		else if (patternValueSlot instanceof FocusOfAttentionPatternValueSlot) {
			return focusOfAttentionValueSlotHelper;
		}
		else if (patternValueSlot instanceof TimeSlicePatternValueSlot) {
			return timeSliceValueSlotHelper;
		}
		else if (patternValueSlot instanceof ContextElementPatternValueSlot) {
			return contextElementValueSlotHelper;
		}
		else {
			// we should never come here
			throw new RuleGenerationException("Unsupported patter value slot type " + patternValueSlot.getClass().getName());
		}
	}
}
