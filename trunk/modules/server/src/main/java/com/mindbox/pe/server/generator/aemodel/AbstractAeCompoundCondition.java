package com.mindbox.pe.server.generator.aemodel;

import com.mindbox.server.parser.jtb.rule.syntaxtree.Node;

// Referenced classes of package com.mindbox.server.generator:
//            AbstractAeCondition

public abstract class AbstractAeCompoundCondition extends AbstractAeCondition {

	public abstract void addCondition(AbstractAeCondition abstractaecondition);

	public boolean isNegated() {
		return mNegated;
	}

	public AbstractAeCompoundCondition(Node node) {
		super(node);
	}

	public AbstractAeCompoundCondition() {
	}
	
	public void setNegated(boolean flag) {
		mNegated = flag;
	}

	public abstract void removeCondition(AbstractAeCondition abstractaecondition);

	private boolean mNegated;
}