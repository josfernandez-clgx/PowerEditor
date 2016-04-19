package com.mindbox.pe.model.rule;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
abstract class AbstractCompoundLHSElement extends AbstractCompoundRuleElement<LHSElement> implements CompoundLHSElement {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3656090025287504162L;

	private int type;

	/**
	 * 
	 * @param type
	 * @param dispName
	 * @throws IllegalArgumentException if type is invalid
	 */
	protected AbstractCompoundLHSElement(int type, String dispName) {
		super(dispName);
		setType_internal(type);
	}

	@Override
	public void adjustChangedColumnReferences(int originalColNum, int newColNum) {
		for (int i = 0; i < this.size(); i++) {
			this.get(i).adjustChangedColumnReferences(originalColNum, newColNum);
		}
	}

	@Override
	public void adjustDeletedColumnReferences(int colNo) {
		for (int i = 0; i < this.size();) {
			if (this.get(i).containsColumnReference(colNo)) {
				this.remove(i);
			}
			else {
				this.get(i++).adjustDeletedColumnReferences(colNo);
			}
		}
	}

	@Override
	public final int getType() {
		return this.type;
	}

	@Override
	public final void setType(int type) {
		setType_internal(type);
	}

	/**
	 * 
	 * @param type
	 * @throws IllegalArgumentException if type is invalid
	 */
	private void setType_internal(int type) {
		switch (type) {
		case TYPE_AND:
		case TYPE_NOT:
		case TYPE_OR:
			this.type = type;
			break;
		default:
			throw new IllegalArgumentException("Invalid type: " + type);
		}
	}

	@Override
	public String toString() {
		return name + "[size=" + size() + "]";
	}


}
