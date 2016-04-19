package com.mindbox.pe.server.config;

import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.xsd.config.LHSPatternType;
import com.mindbox.pe.xsd.config.RuleGenerationLHS.Pattern;

/**
 * This uses generate as useTestFunction, class as testFunctionName, and prefix as variableSuffix.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public final class LinkPatternConfigHelper extends AbstractPatternConfigHelper {

	public LinkPatternConfigHelper(final Pattern pattern) {
		super(pattern, LHSPatternType.LINK);
	}

	public String getTestFunctionName() {
		return pattern.getClazz();
	}

	public String getVariableSuffix() {
		return pattern.getPrefix();
	}

	public boolean useTestFunction() {
		return ConfigUtil.isPatternOn(pattern);
	}
}