package com.mindbox.pe.server.generator.aeobjects;

import java.util.List;

import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Node;

public class AeObjectPatternSet extends AbstractAeObjectCondition {

	public static final int CONDITION_AND = 1;
	public static final int CONDITION_OR = 2;


	private int conditionType;
	private List<AbstractAeObjectCondition> conditionList;
	
	public AeObjectPatternSet(Node node) {
		super(node);
		this.conditionType = -1;
		this.conditionList = new java.util.ArrayList<AbstractAeObjectCondition>();
	}

	public void setConditionType(int pConditionType) {
		this.conditionType = pConditionType;
	}

	public void addCondition(AbstractAeObjectCondition condition) {
		conditionList.add(condition);
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("ObjectPatternSet[size=" + getConditions().size() + "; " + " Conditions=");
		for (int i = 0; i < getConditions().size(); i++) {
			buf.append(getConditions().get(i).toString());
			buf.append("]");
		}
		return buf.toString();
	}

	public List<AbstractAeObjectCondition> getConditions() {
		return conditionList;
	}

	public void setConditions(List<AbstractAeObjectCondition> conditions) {
		this.conditionList = conditions;
	}

	public int getConditionType() {
		return conditionType;
	}

}