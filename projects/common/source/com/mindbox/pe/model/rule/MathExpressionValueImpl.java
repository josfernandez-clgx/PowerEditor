/*
 * Created on 2004. 8. 19.
 *
 */
package com.mindbox.pe.model.rule;

import java.io.Serializable;


/**
 * @author Geneho Kim
 * @since PowerEditor 4.0
 */
class MathExpressionValueImpl implements MathExpressionValue, Serializable {

	private static final long serialVersionUID = 20040818100020000L;

	private String operator;
	private String value;
	private ColumnReference colRef;
	private Reference attributeReference;

	MathExpressionValueImpl() {
		super();
	}

	/**
	 * 
	 * @param meValue
	 * @since PowerEditor 4.3.2
	 */
	MathExpressionValueImpl(MathExpressionValue meValue) {
		this.operator = meValue.getOperator();
		this.value = meValue.getValue();
		this.colRef = new ColumnRefImpl(meValue.getColumnReference().getColumnNo());
		this.attributeReference = new ReferenceImpl(meValue.getAttributeReference());
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public Reference getAttributeReference() {
		return attributeReference;
	}

	public ColumnReference getColumnReference() {
		return colRef;
	}

	public String getOperator() {
		return operator;
	}

	public void setAttributeReference(Reference attributeReference) {
		this.attributeReference = attributeReference;
	}

	public void setColumnReference(ColumnReference colRef) {
		this.colRef = colRef;
	}

	public void setOperator(String opStr) {
		if (opStr == null) throw new NullPointerException();
		this.operator = opStr;
	}

	public String toString() {
		return (colRef == null ? (value == null ? "" : value) : colRef.toString()) + " " + operator + " " + attributeReference.toString();
	}
}