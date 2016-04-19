package com.mindbox.pe.model.rule;

/**
 * @author kim
 * @author MindBox
 * @since PowerEditor 4.0
 */
class ColumnRefParameterImpl extends AbstractParameter implements ColumnReference {

	private static final long serialVersionUID = -2127830274911958939L;

	private static final String VALUE_PREFIX = "Column-";

	static int extractColumnNumber(String valueStr) {
		if (isValueString(valueStr)) {
			return Integer.parseInt(valueStr.substring(7));
		}
		else {
			return -1;
		}
	}

	static boolean isValueString(String str) {
		return (str != null && str.startsWith(VALUE_PREFIX));
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

	@Override
	public void adjustChangedColumnReferences(int originalColNo, int newColNo) {
		if (this.containsColumnReference(originalColNo)) {
			setColumnNo(newColNo);
		}
	}

	@Override
	public boolean containsColumnReference(int colNo) {
		return this.getColumnNo() == colNo;
	}

	@Override
	public int getColumnNo() {
		return columnNo;
	}

	@Override
	public void setColumnNo(int columnNo) {
		this.columnNo = columnNo;
	}

	@Override
	public String toString() {
		return super.toString() + "[" + valueString() + "]";
	}

	public String valueString() {
		return VALUE_PREFIX + columnNo;
	}

}
