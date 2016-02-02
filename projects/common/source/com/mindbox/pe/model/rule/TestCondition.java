package com.mindbox.pe.model.rule;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public interface TestCondition extends CompoundRuleElement<FunctionParameter>, LHSElement {
	
	TestTypeDefinition getTestType();
	void setTestType(TestTypeDefinition type);
	void clear();
}
