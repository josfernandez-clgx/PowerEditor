package com.mindbox.pe.server.generator.aeobjects;

import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Node;


public class AbstractAeObjectCondition extends AbstractAeObject {

	private AeObjectPatternSet parentPatternSet;
	private AeRule parentRule;

	public AbstractAeObjectCondition(Node node) {
		super(node);
	}

	public String toString() {
		return "lhs: nested in=" + (getParentPatternSet() != null ? getParentPatternSet().toString() : "null");
	}

	public AeRule getParentRule() {
		if (parentRule != null)
			return parentRule;
		if (getParentPatternSet() != null)
			return getParentPatternSet().getParentRule();
		else
			return null;
	}

	public void setParentRule(AeRule parent) {
		this.parentRule = parent;
	}

	public AeObjectPatternSet getParentPatternSet() {
		return parentPatternSet;
	}

	public void setParentPatternSet(AeObjectPatternSet parentPatternSet) {
		this.parentPatternSet = parentPatternSet;
	}

}