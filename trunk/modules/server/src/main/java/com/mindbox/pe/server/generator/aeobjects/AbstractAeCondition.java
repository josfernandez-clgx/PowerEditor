package com.mindbox.pe.server.generator.aeobjects;

import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Node;



public abstract class AbstractAeCondition extends AbstractAeObject {

	private AbstractAeCompoundCondition parentCondition;

	public AbstractAeCondition(Node node) {
		super(node);
	}

	public void setParentCondition(AbstractAeCompoundCondition abstractaecompoundcondition) {
		this.parentCondition = abstractaecompoundcondition;
	}

	public String toString() {
		return "lhs: nested in=" + (getParentCondition() != null ? getParentCondition().toString() : "null");
	}

	public AeRule getParentRule() {
		AbstractAeCompoundCondition abstractaecompoundcondition = getParentCondition();
		if (abstractaecompoundcondition != null && (abstractaecompoundcondition instanceof AeRule))
			return (AeRule) abstractaecompoundcondition;
		if (abstractaecompoundcondition != null)
			return abstractaecompoundcondition.getParentRule();
		else
			return null;
	}

	public abstract int size();

	public AbstractAeCompoundCondition getParentCondition() {
		return parentCondition;
	}

}