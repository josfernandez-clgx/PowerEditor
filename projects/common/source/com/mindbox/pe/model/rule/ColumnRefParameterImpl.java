package com.mindbox.pe.model.rule;

/**
 * @author kim
 * @author MindBox
 * @since PowerEditor 4.0
 */
class ColumnRefParameterImpl extends AbstractParameter implements ColumnReference {
	
	private static final long serialVersionUID = -2127830274911958939L;

	private static final String VALUE_PREFIX = "Column-";
	
	static boolean isValueString(String str) {
		return (str != null && str.startsWith(VALUE_PREFIX));
	}

	static int extractColumnNumber(String valueStr) {
		if (isValueString(valueStr)) {
			return Integer.parseInt(valueStr.substring(7));
		}
		else {
			return -1;
		}
	}
	
	private int columnNo;
	
	/**
	 * 
	 * @param index
	 * @param name
	 * @param columnNo
	 */
	ColumnRefParameterImpl(int index, String name, int columnNo) {
		super(name, index);
		assert (columnNo > 0); 
		this.columnNo = columnNo;
	}
	
	/**
	 * @param columnNo The columnNo to set.
	 */
	public void setColumnNo(int columnNo) {
		this.columnNo = columnNo;
	}
	public int getColumnNo() {
		return columnNo;
	}

	public String valueString() {
		return VALUE_PREFIX + columnNo;
	}

	public String toString() {
		return super.toString()+"[" + valueString() + "]";
	}
	
	public void adjustChangedColumnReferences(int originalColNo, int newColNo) {
		if (this.containsColumnReference(originalColNo)) setColumnNo(newColNo);
	}

	public boolean containsColumnReference(int colNo) {
		boolean result = this.getColumnNo() == colNo;
		return result;
	}

}
