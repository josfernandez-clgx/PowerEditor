/**
 * 
 */
package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.model.rule.FunctionParameter;
import com.mindbox.pe.model.rule.FunctionTypeDefinition;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;

final class RuleDefinitionParamContainer implements FunctionParameterContainer {

	private RuleDefinition ruleDefinition;

	public RuleDefinitionParamContainer(RuleDefinition ruleDefinition) {
		this.ruleDefinition = ruleDefinition;
	}

	public FunctionTypeDefinition getFunctionTypeDefinition() {
		return GuidelineFunctionManager.getInstance().getActionTypeDefinition(ruleDefinition.getActionTypeID());
	}

	public FunctionParameter getParameterAt(int paramNo) {
		return ruleDefinition.getFunctionParameterAt(paramNo);
	}

}