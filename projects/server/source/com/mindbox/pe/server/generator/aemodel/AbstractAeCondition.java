package com.mindbox.pe.server.generator.aemodel;

import com.mindbox.server.parser.jtb.rule.syntaxtree.Node;

// Referenced classes of package com.mindbox.server.generator:
//            AbstractAeObject, AeRule, AbstractAeCompoundCondition

public abstract class AbstractAeCondition extends AbstractAeObject {

	public void setParentCondition(AbstractAeCompoundCondition abstractaecompoundcondition) {
		mParentCondition = abstractaecompoundcondition;
	}

	public String toString() {
		return "lhs: nested in=" + (getParentCondition() != null ? getParentCondition().toString() : "null");
	}

	public AbstractAeCondition(Node node) {
		super(node);
	}

	public AbstractAeCondition() {
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
		return mParentCondition;
	}

	private AbstractAeCompoundCondition mParentCondition;
}