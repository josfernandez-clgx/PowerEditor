package com.mindbox.pe.server.generator.aeobjects;

import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Node;


public class AeAttributePattern extends AbstractAeCondition {

	public static final String COMPARE_GT = ">";
	public static final String COMPARE_GT_EQ = ">=";
	public static final String COMPARE_LT = "<";
	public static final String COMPARE_LT_EQ = "<=";
	public static final String COMPARE_EQ = "==";
	public static final String COMPARE_NOT_EQ = "!=";
	public static final String COMPARE_BETWEEN = "between";
	public static final String COMPARE_MEMBER = "in";

	private String className;
	private String attributeName;
	private String aeVariableName;
	private String compartorFunction;
	private AbstractAeValue value;
	private boolean isNegated;

	public AeAttributePattern(Node node) {
		super(node);
	}

	public void setClassName(String s) {
		this.className = s;
	}

	public String getComparatorFunction() {
		return compartorFunction;
	}

	public void setComparatorFunction(String s) {
		this.compartorFunction = s;
	}

	public AbstractAeValue getValue() {
		return value;
	}

	public boolean hasValue() {
		return value != null;
	}
	
	public void setValue(AbstractAeValue abstractaevalue) {
		this.value = abstractaevalue;
	}

	public boolean isNegated() {
		return isNegated;
	}

	public void setNegated(boolean flag) {
		this.isNegated = flag;
	}

	/**
	 * Tests if this has not been used before.
	 * @return <code>true</code> if none of attributes of this is set; <code>false</code>, otherwise
	 */
	public boolean isNullPattern() {
		return (className == null && attributeName == null && compartorFunction == null && value == null);
	}
	
	public boolean isClassAttributeSet() {
		return (className != null || attributeName != null);
	}
	
	
	public String toString() {
		StringBuilder stringbuffer = new StringBuilder();
		stringbuffer.append("AttrPattern[");
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
		return aeVariableName;
	}

	public void setAeNameVar(String s) {
		this.aeVariableName = s;
	}

	public int size() {
		return 1;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String s) {
		this.attributeName = s;
	}

	public String getClassName() {
		return className;
	}

}