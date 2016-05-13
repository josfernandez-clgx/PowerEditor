package com.mindbox.pe.model.rule;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public interface RuleAction extends CompoundRuleElement<FunctionParameter> {

	ActionTypeDefinition getActionType();
	void setActionType(ActionTypeDefinition type);
	void clear();
}
