/*
 * Created on 2004. 1. 28.
 *
 */
package com.mindbox.pe.model.rule;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public interface Condition extends LHSElement {
	public static final int OP_EQUAL = 0;
	public static final int OP_NOT_EQUAL = 1;
	public static final int OP_GREATER = 2;
	public static final int OP_GREATER_EQUAL = 3;
	public static final int OP_LESS = 4;
	public static final int OP_LESS_EQUAL = 5;
	public static final int OP_IN = 10;
	public static final int OP_NOT_IN = 11;
	public static final int OP_BETWEEN = 20;
	public static final int OP_NOT_BETWEEN = 21;
	public static final int OP_IS_EMPTY = 22;
	public static final int OP_ANY_VALUE= 23;
	public static final int OP_IS_NOT_EMPTY = 24;
    public static final int OP_ENTITY_MATCH_FUNC = 25;    
    public static final int OP_NOT_ENTITY_MATCH_FUNC = 26;

	public static final String OPSTR_EQUAL = "=";
	public static final String OPSTR_NOT_EQUAL = "!=";
	public static final String OPSTR_GREATER = ">";
	public static final String OPSTR_GREATER_EQUAL = ">=";
	public static final String OPSTR_LESS = "<";
	public static final String OPSTR_LESS_EQUAL = "<=";
	public static final String OPSTR_IN = "IN";
	public static final String OPSTR_NOT_IN = "NOT IN";
	public static final String OPSTR_BETWEEN = "BETWEEN";
	public static final String OPSTR_NOT_BETWEEN = "NOT BETWEEN";
	public static final String OPSTR_IS_EMPTY = "IS EMPTY";
	public static final String OPSTR_ANY_VALUE = "ANY VALUE";
	public static final String OPSTR_IS_NOT_EMPTY = "IS NOT EMPTY";
    public static final String OPSTR_ENTITY_MATCH_FUNC = "IN ENTITY LIST";
    public static final String OPSTR_NOT_ENTITY_MATCH_FUNC = "NOT IN ENTITY LIST";    

	public static final class Aux {
		
		public static final String toOpString(int op) {
			switch(op) {
				case OP_EQUAL: return OPSTR_EQUAL;
				case OP_NOT_EQUAL: return OPSTR_NOT_EQUAL;
				case OP_GREATER: return OPSTR_GREATER;
				case OP_GREATER_EQUAL: return OPSTR_GREATER_EQUAL;
				case OP_LESS: return OPSTR_LESS;
				case OP_LESS_EQUAL: return OPSTR_LESS_EQUAL;
				case OP_IN: return OPSTR_IN;
				case OP_NOT_IN: return OPSTR_NOT_IN;
				case OP_BETWEEN: return OPSTR_BETWEEN;
				case OP_NOT_BETWEEN: return OPSTR_NOT_BETWEEN;
				case OP_IS_EMPTY: return OPSTR_IS_EMPTY;
				case OP_ANY_VALUE: return OPSTR_ANY_VALUE;
				case OP_IS_NOT_EMPTY: return OPSTR_IS_NOT_EMPTY;
                case OP_ENTITY_MATCH_FUNC: return OPSTR_ENTITY_MATCH_FUNC;
                case OP_NOT_ENTITY_MATCH_FUNC: return OPSTR_NOT_ENTITY_MATCH_FUNC;                
				default:
					throw new IllegalArgumentException("Invalid op: "+ op);
			}
		}

		public static final String toOpStringForHTML(int op) {
			return toOpString(op).replaceAll("<","&lt;").replaceAll(">","&gt;");
		}
		
		public static final int toOpInt(String opStr) {
			if (opStr.equals(Condition.OPSTR_EQUAL)) {
				return Condition.OP_EQUAL;
			}
			else if (opStr.equals(Condition.OPSTR_NOT_EQUAL)) {
				return Condition.OP_NOT_EQUAL;
			}
			else if (opStr.equals(Condition.OPSTR_LESS)) {
				return Condition.OP_LESS;
			}
			else if (opStr.equals(Condition.OPSTR_LESS_EQUAL)) {
				return Condition.OP_LESS_EQUAL;
			}
			else if (opStr.equals(Condition.OPSTR_GREATER)) {
				return Condition.OP_GREATER;
			}
			else if (opStr.equals(Condition.OPSTR_GREATER_EQUAL)) {
				return Condition.OP_GREATER_EQUAL;
			}
			else if (opStr.equals(Condition.OPSTR_BETWEEN)) {
				return Condition.OP_BETWEEN;
			}
			else if (opStr.equals(Condition.OPSTR_NOT_BETWEEN)) {
				return Condition.OP_NOT_BETWEEN;
			}
			else if (opStr.equals(Condition.OPSTR_IN)) {
				return Condition.OP_IN;
			}
			else if (opStr.equals(Condition.OPSTR_NOT_IN)) {
				return Condition.OP_NOT_IN;
			}
			else if (opStr.equals(Condition.OPSTR_IS_EMPTY)) {
				return Condition.OP_IS_EMPTY;
			}
			else if (opStr.equals(Condition.OPSTR_ANY_VALUE)) {
				return Condition.OP_ANY_VALUE;
			}
			else if (opStr.equals(Condition.OPSTR_IS_NOT_EMPTY)) {
				return OP_IS_NOT_EMPTY;
			}
            else if (opStr.equals(Condition.OPSTR_ENTITY_MATCH_FUNC)) {
                return OP_ENTITY_MATCH_FUNC;
            }
            else if (opStr.equals(Condition.OPSTR_NOT_ENTITY_MATCH_FUNC)) {
                return OP_NOT_ENTITY_MATCH_FUNC;
            }
			throw new IllegalArgumentException("Invalid opStr: " + opStr);
		}
		private Aux() {}
	}

	Reference getReference();
	void setReference(Reference ref);

	/**
	 * Tests if this condition has a value that contains an attribute reference.
	 * 
	 * @return <code>true</code> if this has a value with attribute reference; <code>false</code>, otherwise
	 */
	boolean hasReferenceValue();
	
	int getOp();
	void setOp(int op);

	Value getValue();
	void setValue(Value value);

	boolean isUnary();
	
	/**
	 * Gets the object name to use.
	 * 
	 * @return object name for this condition; <code>null</code> if PE-generated name is to be used
	 */
	String getObjectName();
	void setObjectName(String objectName);
}
