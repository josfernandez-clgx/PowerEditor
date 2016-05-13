package com.mindbox.pe.server.generator.value;

import org.apache.log4j.Logger;

import com.mindbox.pe.server.config.RuleGenerationConfigHelper;

abstract class AbstractLHSValueSlotHelper implements LHSValueSlotHelper {

	protected final RuleGenerationConfigHelper ruleGenerationConfiguration;
	protected final Logger logger;

	protected AbstractLHSValueSlotHelper(RuleGenerationConfigHelper ruleGenerationConfiguration) {
		this.ruleGenerationConfiguration = ruleGenerationConfiguration;
		this.logger = Logger.getLogger(getClass());
	}

}
