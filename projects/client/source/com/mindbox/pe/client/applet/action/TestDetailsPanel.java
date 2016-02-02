package com.mindbox.pe.client.applet.action;

import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.rule.TestTypeDefinition;

public final class TestDetailsPanel extends FunctionDetailsPanel<TestTypeDefinition> {

	public TestDetailsPanel() {
		super(EntityType.GUIDELINE_TEST_CONDITION);
	}
	
	public TestTypeDefinition createFunctionTypeDefinition() {
		return new TestTypeDefinition();
	}
}
