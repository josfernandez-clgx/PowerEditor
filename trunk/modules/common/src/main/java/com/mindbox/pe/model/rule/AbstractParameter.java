package com.mindbox.pe.model.rule;

import com.mindbox.pe.common.DomainClassProvider;

/**
 * Immutable abstract implementation of FunctionParameter. 
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
abstract class AbstractParameter extends AbstractRuleElement implements FunctionParameter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3656090025287504162L;

	private int index = -1;

	protected AbstractParameter(String dispName, int index) {
		super(dispName);
		this.index = index;
	}

	public int index() {
		return index;
	}

	/*
	public void setIndex(int index) {
		this.index = index;
	}*/

	public String toString() {
		return "Parameter[index=" + index + "]";
	}

	public String displayString(DomainClassProvider domainClassProvider) {
		return valueString();
	}

}
