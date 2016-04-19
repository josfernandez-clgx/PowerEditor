package com.mindbox.pe.server.generator.aemodel;

import com.mindbox.server.parser.jtb.rule.syntaxtree.Node;

// Referenced classes of package com.mindbox.server.generator:
//            AbstractAeCompoundCondition, AbstractAeCondition

public class AeObjectPattern extends AbstractAeCompoundCondition {

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
		StringBuilder stringbuffer = new StringBuilder();
		stringbuffer.append("objPattern..." + getClassName() + "/" + getAeObjectNameVar() + "/" + getObjectName() + "\n");
		if (mNestedCondition != null) {
			stringbuffer.append(mNestedCondition.toString());
			stringbuffer.append("\n");
		}
		return stringbuffer.toString();
	}

	public AeObjectPattern(Node node) {
		super(node);
		mCardinalityType = -1;
		mCardinalityLimit = -1;
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

	private String mObjectName;
	private String mAeVariable;
	private String mExcludedObjectName;
	private String mAeExcludedObjectNameVar;
	private String mClassName;
	public static final int CARDINALITY_ANY = 1;
	public static final int CARDINALITY_EXISTS = 2;
	public static final int CARDINALITY_AT_LEAST = 3;
	public static final int CARDINALITY_AT_MOST = 4;
	public static final int CARDINALITY_ALL = 5;
	public static final int CARDINALITY_UNDEFINED = -1;
	private int mCardinalityType;
	private int mCardinalityLimit;
	private AbstractAeCondition mNestedCondition;
}