package com.mindbox.pe.server.generator.value;

import org.apache.log4j.Logger;

import com.mindbox.pe.server.config.RuleGenerationConfiguration;

abstract class AbstractLHSValueSlotHelper implements LHSValueSlotHelper {

	protected final RuleGenerationConfiguration ruleGenerationConfiguration;
	protected final Logger logger;

	protected AbstractLHSValueSlotHelper(RuleGenerationConfiguration ruleGenerationConfiguration) {
		this.ruleGenerationConfiguration = ruleGenerationConfiguration;
		this.logger = Logger.getLogger(getClass());
	}

}
