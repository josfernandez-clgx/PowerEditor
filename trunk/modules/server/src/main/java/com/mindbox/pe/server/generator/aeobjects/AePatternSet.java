package com.mindbox.pe.server.generator.aeobjects;

import java.util.Iterator;
import java.util.List;

import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Node;


public class AePatternSet extends AbstractAeCompoundCondition {

	public static final int CONDITION_AND = 1;
	public static final int CONDITION_OR = 2;
	
	
	private int conditionType;
	private List<AbstractAeCondition> conditionList;

	public AePatternSet(Node node) {
		super(node);
		conditionType = -1;
		conditionList = new java.util.ArrayList<AbstractAeCondition>();
	}

	public int getConditionType() {
		return conditionType;
	}

	public void setConditionType(int i) {
		this.conditionType = i;
	}

	public void addCondition(AbstractAeCondition abstractaecondition) {
		conditionList.add(abstractaecondition);
	}

	public String toString() {
		StringBuilder buff = new StringBuilder();
		buff.append("PatternSet[type=");
		buff.append(conditionType);
		buff.append(",size=");
		buff.append(conditionList.size());
		if (!conditionList.isEmpty()) {
			for (Iterator<AbstractAeCondition> iter = conditionList.iterator(); iter.hasNext();) {
				Object element = iter.next();
				buff.append("; ");
				buff.append(element);
			}
		}
		return buff.toString();
	}

	public int size() {
		return conditionList.size();
	}

	public List<AbstractAeCondition> getConditions() {
		return conditionList;
	}

	public void setConditions(List<AbstractAeCondition> list) {
		this.conditionList = list;
	}

	public void removeCondition(AbstractAeCondition abstractaecondition) {
		abstractaecondition.setParentCondition(null);
		conditionList.remove(abstractaecondition);
	}

}