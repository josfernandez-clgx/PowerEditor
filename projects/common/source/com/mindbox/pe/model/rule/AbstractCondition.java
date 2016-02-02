package com.mindbox.pe.model.rule;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public abstract class AbstractCondition extends AbstractRuleElement implements Condition {

	private Reference ref = null;
	private Value value = null;
	private int op;
	private String objectName;
	
	protected AbstractCondition(String dispName, int op) {
		super(dispName);
		setOp_internal(op);
	}

	private void setOp_internal(int op) {
		Condition.Aux.toOpString(op);
		this.op = op;
	}
	
	public Reference getReference() {
		return ref;
	}

	public void setReference(Reference ref) {
		this.ref = ref;
	}

	public boolean hasReferenceValue() {
		return value instanceof Reference || value instanceof MathExpressionValue;
	}
	
	public int getOp() {
		return op;
	}

	public void setOp(int op) {
		setOp_internal(op);
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}
	
	public String toString() {
		return (ref == null ? "null-ref" : ref.getClassName()+"."+ref.getAttributeName()) + " " + Condition.Aux.toOpString(op) + (value == null ? "" : " " + value.toString());
	}
	
	/* (non-Javadoc)
	 * @see com.mindbox.pe.model.adhoc.RuleElement#adjustChangedColumnReferences(int, int)
	 */
	public void adjustChangedColumnReferences(int originalColNo, int newColNo) {
		if (this.containsColumnReference(originalColNo)) ((ColumnReference)this.value).setColumnNo(newColNo);
	}

	public boolean containsColumnReference(int colNo) {
		boolean result = this.value instanceof ColumnReference && ((ColumnReference)this.value).getColumnNo() == colNo;
		return result;
	}

	public boolean isUnary() {
		return getOp() == Condition.OP_IS_EMPTY || getOp() == Condition.OP_IS_NOT_EMPTY || getOp() == Condition.OP_ANY_VALUE;
	}
	
	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

}
