package com.mindbox.pe.server.generator.aeobjects;

import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Node;


public abstract class AbstractAeCompoundCondition extends AbstractAeCondition {

	private boolean negated;

	public AbstractAeCompoundCondition(Node node) {
		super(node);
	}

	abstract void addCondition(AbstractAeCondition abstractaecondition);

	public boolean isNegated() {
		return negated;
	}

	public void setNegated(boolean flag) {
		this.negated = flag;
	}

	public abstract void removeCondition(AbstractAeCondition abstractaecondition);

}