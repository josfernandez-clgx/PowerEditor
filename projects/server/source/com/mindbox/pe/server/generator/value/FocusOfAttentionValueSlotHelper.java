package com.mindbox.pe.server.generator.value;

import com.mindbox.pe.common.config.GuidelineTabConfig;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.RuleGenerationConfiguration;
import com.mindbox.pe.server.generator.AeMapper;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.rule.PatternValueSlot;
import com.mindbox.pe.server.model.TimeSlice;

class FocusOfAttentionValueSlotHelper extends AbstractLHSValueSlotHelper {

	FocusOfAttentionValueSlotHelper(RuleGenerationConfiguration ruleGenerationConfiguration) {
		super(ruleGenerationConfiguration);
	}

	public ValueAndComment generateValue(PatternValueSlot patternValueSlot, GuidelineGenerateParams generateParams, String attribVarName, TimeSlice[] timeSlices,
			GenericEntityType messageContextType, int messageContextEntityID) throws RuleGenerationException {
		String focusOfAttentionString;
		if (ruleGenerationConfiguration.getRequestPatternConfig().generateUsageTypeAsFocus()) {
			focusOfAttentionString = generateParams.getUsage().toString().toUpperCase();
		}
		else {
			GuidelineTabConfig tabConfig = ConfigurationManager.getInstance().getUIConfiguration().findConfigurationForRuleSet(generateParams.getUsage());
			focusOfAttentionString = (tabConfig == null ? ("UNKNOWN-" + AeMapper.getRuleset(generateParams)) : tabConfig.getTitle().toUpperCase());
		}
		return new ValueAndComment(focusOfAttentionString);
	}

}
