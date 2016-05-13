package com.mindbox.pe.model.rule;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public abstract class AbstractCondition extends AbstractRuleElement implements Condition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3656090025287504162L;

	private Reference ref = null;
	private Value value = null;
	private int op;
	private String objectName;

	protected AbstractCondition(String dispName, int op) {
		super(dispName);
		setOp_internal(op);
	}

	@Override
	public void adjustChangedColumnReferences(int originalColNo, int newColNo) {
		if (this.containsColumnReference(originalColNo)) {
			((ColumnReference) this.value).setColumnNo(newColNo);
		}
	}

	@Override
	public boolean containsColumnReference(int colNo) {
		boolean result = this.value instanceof ColumnReference && ((ColumnReference) this.value).getColumnNo() == colNo;
		return result;
	}

	@Override
	public String getObjectName() {
		return objectName;
	}

	@Override
	public int getOp() {
		return op;
	}

	@Override
	public Reference getReference() {
		return ref;
	}

	@Override
	public Value getValue() {
		return value;
	}

	@Override
	public boolean hasReferenceValue() {
		return value instanceof Reference || value instanceof MathExpressionValue;
	}

	@Override
	public boolean isUnary() {
		return getOp() == Condition.OP_IS_EMPTY || getOp() == Condition.OP_IS_NOT_EMPTY || getOp() == Condition.OP_ANY_VALUE;
	}

	@Override
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	@Override
	public void setOp(int op) {
		setOp_internal(op);
	}

	private void setOp_internal(int op) {
		Condition.Aux.toOpString(op);
		this.op = op;
	}

	@Override
	public void setReference(Reference ref) {
		this.ref = ref;
	}

	@Override
	public void setValue(Value value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return (ref == null ? "null-ref" : ref.getClassName() + "." + ref.getAttributeName()) + " " + Condition.Aux.toOpString(op) + (value == null ? "" : " " + value.toString());
	}

}
