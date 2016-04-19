/*
 * Created on 2004. 8. 6.
 */
package com.mindbox.pe.model.template;

import com.mindbox.pe.model.rule.RuleDefinition;

/**
 * @author kim
 * @author MindBox
 * @since PowerEditor 4.0
 */
public interface RuleContainer {
	RuleDefinition getRuleDefinition();
	void setRuleDefinition(RuleDefinition ruleDef);
}
