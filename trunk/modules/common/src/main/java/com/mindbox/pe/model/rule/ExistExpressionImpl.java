package com.mindbox.pe.model.rule;

/**
 * @author kim
 * @author MindBox
 * @since PowerEditor 
 */
class ExistExpressionImpl extends AbstractRuleElement implements ExistExpression {

	private static final long serialVersionUID = -2127830274911958937L;

	private String className;
	private String excludedObjectName;
	private String objectName;
	private CompoundLHSElement compoundElement;

	ExistExpressionImpl(String className, CompoundLHSElement compoundElement) {
		super("exists");
		if (className == null) throw new NullPointerException("className cannot be null");
		this.className = className;
		this.compoundElement = compoundElement;
	}

	public String toDisplayName() {
		return "exists " + className + " with ";
	}

	public void adjustChangedColumnReferences(int originalColNum, int newColNum) {
		compoundElement.adjustChangedColumnReferences(originalColNum, newColNum);
	}

	public void adjustDeletedColumnReferences(int colNo) {
		compoundElement.adjustDeletedColumnReferences(colNo);
	}

	/**
	 * @return Returns the className.
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className The className to set.
	 */
	public void setClassName(String className) {
		if (className == null) throw new NullPointerException("className cannot be null");
		this.className = className;
	}


	public String getExcludedObjectName() {
		return excludedObjectName;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setExcludedObjectName(String excludedObjectName) {
		this.excludedObjectName = excludedObjectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public CompoundLHSElement getCompoundLHSElement() {
		return compoundElement;
	}

	public void setCompoundLHSElement(CompoundLHSElement e) {
		compoundElement = e;
	}
}