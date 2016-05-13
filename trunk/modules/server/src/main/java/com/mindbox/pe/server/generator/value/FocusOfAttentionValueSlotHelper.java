package com.mindbox.pe.server.generator.value;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.RuleGenerationConfigHelper;
import com.mindbox.pe.server.generator.AeMapper;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.rule.PatternValueSlot;
import com.mindbox.pe.server.model.TimeSlice;
import com.mindbox.pe.xsd.config.GuidelineTab;

class FocusOfAttentionValueSlotHelper extends AbstractLHSValueSlotHelper {

	FocusOfAttentionValueSlotHelper(RuleGenerationConfigHelper ruleGenerationConfiguration) {
		super(ruleGenerationConfiguration);
	}

	public ValueAndComment generateValue(PatternValueSlot patternValueSlot, GuidelineGenerateParams generateParams, String attribVarName, TimeSlice[] timeSlices, GenericEntityType messageContextType,
			int messageContextEntityID) throws RuleGenerationException {
		String focusOfAttentionString;
		if (UtilBase.asBoolean(ruleGenerationConfiguration.getRequestPatternConfig().isUsageTypeAsFocus(), false)) {
			focusOfAttentionString = generateParams.getUsage().toString().toUpperCase();
		}
		else {
			final GuidelineTab tabConfig = ConfigurationManager.getInstance().findConfigurationForRuleSet(generateParams.getUsage());
			focusOfAttentionString = (tabConfig == null ? ("UNKNOWN-" + AeMapper.getRuleset(generateParams)) : tabConfig.getDisplayName().toUpperCase());
		}
		return new ValueAndComment(focusOfAttentionString);
	}

}
