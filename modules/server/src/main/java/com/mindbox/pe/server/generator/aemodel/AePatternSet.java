package com.mindbox.pe.server.generator.aemodel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.mindbox.server.parser.jtb.rule.syntaxtree.Node;


public class AePatternSet extends AbstractAeCompoundCondition {

	public AePatternSet(Node node) {
		super(node);
		mConditionType = -1;
		mConditions = new LinkedList<AbstractAeCondition>();
	}

	public int getConditionType() {
		return mConditionType;
	}

	public void setConditionType(int i) {
		mConditionType = i;
	}

	public void addCondition(AbstractAeCondition abstractaecondition) {
		//System.out.println(this +".addCondition: " + abstractaecondition);
		mConditions.add(abstractaecondition);
	}

	public String toString() {
		StringBuilder buff = new StringBuilder();
		buff.append("PatternSet[type=");
		buff.append(mConditionType);
		buff.append(",size=");
		buff.append(mConditions.size());
		if (!mConditions.isEmpty()) {
			for (Iterator<AbstractAeCondition> iter = mConditions.iterator(); iter.hasNext();) {
				Object element = iter.next();
				buff.append("; ");
				buff.append(element);
			}
		}
		return buff.toString();
	}

	public int size() {
		return mConditions.size();
	}

	public List<AbstractAeCondition> getConditions() {
		return mConditions;
	}

	public void setConditions(List<AbstractAeCondition> list) {
		mConditions = list;
	}

	public void removeCondition(AbstractAeCondition abstractaecondition) {
		abstractaecondition.setParentCondition(null);
		mConditions.remove(abstractaecondition);
	}

	public static final int CONDITION_AND = 1;
	public static final int CONDITION_OR = 2;
	private int mConditionType;
	private List<AbstractAeCondition> mConditions;
}