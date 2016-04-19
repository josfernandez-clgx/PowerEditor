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
abstract class AbstractRuleElement implements RuleElement {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3656090025287504162L;

	protected String name;

	private String comment = null;

	protected AbstractRuleElement(String dispName) {
		this.name = dispName;
	}

	public String toDisplayName() {
		return name;
	}

	public String toString() {
		return "[" + name + "]";
	}

	public String getComment() {
		return comment;
	}


	public void setComment(String string) {
		comment = string;
	}

	public void setName(String name) {
		this.name = name;
	}


	/* (non-Javadoc)
	 * @see com.mindbox.pe.model.adhoc.RuleElement#adjustChangedColumnReferences(int, int)
	 */
	public void adjustChangedColumnReferences(int originalColNum, int newColNum) {
	}

	/* (non-Javadoc)
	 * @see com.mindbox.pe.model.adhoc.RuleElement#adjustDeletedColumnReferences(int)
	 */
	public void adjustDeletedColumnReferences(int colNo) {
	}

	/* (non-Javadoc)
	 * @see com.mindbox.pe.model.adhoc.RuleElement#containsColumnReference(int)
	 */
	public boolean containsColumnReference(int colNo) {
		return false;
	}
}
