package com.mindbox.pe.server.config;

import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.xsd.config.LHSPatternType;
import com.mindbox.pe.xsd.config.RuleGenerationLHS;
import com.mindbox.pe.xsd.config.RuleGenerationLHS.Pattern;

abstract class AbstractPatternConfigHelper {

	protected final RuleGenerationLHS.Pattern pattern;

	protected AbstractPatternConfigHelper(final Pattern pattern, final LHSPatternType acceptableType) {
		this.pattern = pattern;
		if (pattern == null || pattern.getType() != acceptableType) {
			throw new IllegalArgumentException(String.format("pattern cannot be null and must be of %s type.", acceptableType));
		}
	}

	public final boolean isPatternOn() {
		return ConfigUtil.isPatternOn(pattern);
	}

	public final RuleGenerationLHS.Pattern getPattern() {
		return pattern;
	}

}
