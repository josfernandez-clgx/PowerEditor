/*
 * Created on 2004. 8. 19.
 *
 */
package com.mindbox.pe.model.rule;


/**
 * @author Geneho Kim
 * @since PowerEditor 
 */
public interface MathExpressionValue extends Value {
	
	public static final String OP_ADD = "+";
	public static final String OP_DIVIDE = "/";
	public static final String OP_MULTIPLY = "*";
	public static final String OP_SUBTRACT = "-";
	
	ColumnReference getColumnReference();
	
	Reference getAttributeReference();
	
	String getOperator();
	
	String getValue();
	
	void setValue(String value);
}
