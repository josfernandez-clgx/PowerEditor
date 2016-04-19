package com.mindbox.pe.client.applet.action;

import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.rule.TestTypeDefinition;

public final class TestDetailsPanel extends FunctionDetailsPanel<TestTypeDefinition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public TestDetailsPanel() {
		super(PeDataType.GUIDELINE_TEST_CONDITION);
	}

	public TestTypeDefinition createFunctionTypeDefinition() {
		return new TestTypeDefinition();
	}
}
