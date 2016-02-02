/**
 * 
 */
package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.model.rule.FunctionParameter;
import com.mindbox.pe.model.rule.FunctionTypeDefinition;
import com.mindbox.pe.model.rule.TestCondition;

final class TestConditionParamContainer implements FunctionParameterContainer {

	private final TestCondition testCondition;

	public TestConditionParamContainer(TestCondition testCondition) {
		this.testCondition = testCondition;
	}

	public FunctionTypeDefinition getFunctionTypeDefinition() {
		return testCondition.getTestType();
	}

	public FunctionParameter getParameterAt(int paramNo) {
		return (FunctionParameter) testCondition.get(paramNo);
	}

}