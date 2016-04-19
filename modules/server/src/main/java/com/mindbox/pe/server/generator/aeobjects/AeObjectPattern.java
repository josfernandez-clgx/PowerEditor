package com.mindbox.pe.server.generator.aeobjects;

import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Node;


public class AeObjectPattern extends AbstractAeCompoundCondition {

	public static final int CARDINALITY_ANY = 1;
	public static final int CARDINALITY_EXISTS = 2;
	public static final int CARDINALITY_AT_LEAST = 3;
	public static final int CARDINALITY_AT_MOST = 4;
	public static final int CARDINALITY_ALL = 5;
	public static final int CARDINALITY_UNDEFINED = -1;
	
	
	private int mCardinalityType;
	private int mCardinalityLimit;
	private AbstractAeCondition mNestedCondition;
	private String mObjectName;
	private String mAeVariable;
	private String mExcludedObjectName;
	private String mAeExcludedObjectNameVar;
	private String mClassName;

	public AeObjectPattern(Node node) {
		super(node);
		mCardinalityType = -1;
		mCardinalityLimit = -1;
	}

	public String getClassName() {
		return mClassName;
	}

	public void setClassName(String s) {
		mClassName = s;
	}

	public AbstractAeCondition getNestedCondition() {
		return mNestedCondition;
	}

	public void setNestedCondition(AbstractAeCondition abstractaecondition) {
		mNestedCondition = abstractaecondition;
	}

	public void addCondition(AbstractAeCondition abstractaecondition) {
		setNestedCondition(abstractaecondition);
	}

	public String toString() {
		StringBuilder buff = new StringBuilder();
		buff.append("ObjectPattern[cn=");
		buff.append(getClassName());
		buff.append(",var=");
		buff.append(getAeObjectNameVar());
		buff.append(",objName=");
		buff.append(getObjectName());
		buff.append(",card-type=");
		buff.append(mCardinalityType);
		buff.append(",exclude=");
		buff.append(mExcludedObjectName);
		buff.append("\n");
		if (mNestedCondition != null) {
			buff.append("  Cond=");
			buff.append(mNestedCondition.toString());
		}
		buff.append(" ]");
		return buff.toString();
	}

	public String getObjectName() {
		return mObjectName;
	}

	public void setObjectName(String s) {
		mObjectName = s;
	}

	public String getAeExcludedObjectNameVar() {
		return mAeExcludedObjectNameVar;
	}

	public void setAeExcludedObjectNameVar(String s) {
		mAeExcludedObjectNameVar = s;
	}

	public int size() {
		if (mNestedCondition == null)
			return 0;
		else
			return mNestedCondition.size();
	}

	public int getCardinalityLimit() {
		return mCardinalityLimit;
	}

	public void setCardinalityLimit(int i) {
		mCardinalityLimit = i;
	}

	public String getExcludedObjectName() {
		return mExcludedObjectName;
	}

	public void setExcludedObjectName(String s) {
		mExcludedObjectName = s;
	}

	public String getAeObjectNameVar() {
		return mAeVariable;
	}

	public void setAeObjectNameVar(String s) {
		mAeVariable = s;
	}

	public int getCardinalityType() {
		return mCardinalityType;
	}

	public void setCardinalityType(int i) {
		mCardinalityType = i;
	}

	public void removeCondition(AbstractAeCondition abstractaecondition) {
		abstractaecondition.setParentCondition(null);
		setNestedCondition(null);
	}

}