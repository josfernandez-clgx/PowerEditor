package com.mindbox.pe.server.generator.aemodel;

import java.util.List;

import com.mindbox.server.parser.jtb.rule.syntaxtree.Node;

public class AeObjectPatternSet extends AbstractAeObjectCondition {

	public void setConditionType(int pConditionType) {
		mConditionType = pConditionType;
	}

	public void addCondition(AbstractAeObjectCondition pCondition) {
		mConditions.add(pCondition);
	}

	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("ObjectPatternSet[size=" + getConditions().size() + "; " + " Conditions=");
		for (int i = 0; i < getConditions().size(); i++) {
			buf.append(getConditions().get(i).toString());
			buf.append("]");
		}
		return buf.toString();
	}

	public AeObjectPatternSet(Node pNode) {
		super(pNode);
		mConditionType = -1;
		mConditions = new java.util.ArrayList<AbstractAeObjectCondition>();
	}

	public List<AbstractAeObjectCondition> getConditions() {
		return mConditions;
	}

	public void setConditions(List<AbstractAeObjectCondition> pConditions) {
		mConditions = pConditions;
	}

	public int getConditionType() {
		return mConditionType;
	}

	public static final int CONDITION_AND = 1;
	public static final int CONDITION_OR = 2;
	private int mConditionType;
	private List<AbstractAeObjectCondition> mConditions;
}