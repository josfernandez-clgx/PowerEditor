package com.mindbox.pe.model.rule;

import com.mindbox.pe.model.Auditable;


/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class TestTypeDefinition extends FunctionTypeDefinition implements Auditable {

	private static final long serialVersionUID = 2007051500001L;

	public TestTypeDefinition() {
		super(-1, "", null);
	}

	public TestTypeDefinition(int id, String name, String desc) {
		super(id, name, desc);
	}

	public Auditable deepCopy() {
		TestTypeDefinition copy = new TestTypeDefinition();
		copy.copyFrom(this);
		return copy;
	}

	public String getAuditDescription() {
		return "test condition '" + getName() + "'";
	}
	
	public String toString() {
		return "TestType[" + getID() + "," + getName() + ",noParams=" + parameterSize() + "]";
	}
}