package com.mindbox.pe.server.generator.aemodel;

import com.mindbox.server.parser.jtb.rule.syntaxtree.Node;

// Referenced classes of package com.mindbox.server.generator:
//            AbstractAeCondition, AeObjectPattern, AbstractAeValue

public class AeAttributePattern extends AbstractAeCondition {

	public static final String COMPARE_GT = ">";
	public static final String COMPARE_GT_EQ = ">=";
	public static final String COMPARE_LT = "<";
	public static final String COMPARE_LT_EQ = "<=";
	public static final String COMPARE_EQ = "==";
	public static final String COMPARE_NOT_EQ = "!=";
	public static final String COMPARE_BETWEEN = "between";
	public static final String COMPARE_MEMBER = "in";

	public AeAttributePattern(Node node) {
		super(node);
	}

	public void setClassName(String s) {
		mClassName = s;
	}

	public String getComparatorFunction() {
		return mComparatorFunction;
	}

	public void setComparatorFunction(String s) {
		mComparatorFunction = s;
	}

	public AbstractAeValue getValue() {
		return mValue;
	}

	public void setValue(AbstractAeValue abstractaevalue) {
		mValue = abstractaevalue;
	}

	public boolean isNegated() {
		return mNegated;
	}

	public void setNegated(boolean flag) {
		mNegated = flag;
	}

	public boolean isNullPattern() {
		
		return (mClassName == null && mAttributeName == null && mComparatorFunction == null && mValue == null);
	}
	
	public String toString() {
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append("AttrPattern[");
		if (isNegated()) {
			stringbuffer.append("NOT ");
		}
		if (getClassName() != null)
			stringbuffer.append(getClassName() + ".");
		stringbuffer.append(getAttributeName());
		stringbuffer.append(" ");
		stringbuffer.append(getComparatorFunction());
		stringbuffer.append(" "); 
		stringbuffer.append(getValue());
		stringbuffer.append("]");
		return stringbuffer.toString();
	}

	public AeObjectPattern getParentObjectPattern() {
		AeObjectPattern aeobjectpattern = null;
		AbstractAeCompoundCondition abstractaecompoundcondition = getParentCondition();
		for (boolean flag = false;
			!flag && abstractaecompoundcondition != null;
			abstractaecompoundcondition = abstractaecompoundcondition.getParentCondition()) {
			if (!(abstractaecompoundcondition instanceof AeObjectPattern))
				continue;
			flag = true;
			aeobjectpattern = (AeObjectPattern) abstractaecompoundcondition;
			break;
		}

		return aeobjectpattern;
	}

	public String getAeNameVar() {
		return mAeVariable;
	}

	public void setAeNameVar(String s) {
		mAeVariable = s;
	}

	public int size() {
		return 1;
	}

	public String getAttributeName() {
		return mAttributeName;
	}

	public void setAttributeName(String s) {
		mAttributeName = s;
	}

	public String getClassName() {
		return mClassName;
	}

	private String mClassName;
	private String mAttributeName;
	private String mAeVariable;
	private String mComparatorFunction;
	private AbstractAeValue mValue;
	private boolean mNegated;
}