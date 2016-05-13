package com.mindbox.pe.model.cbr;

import com.mindbox.pe.model.AbstractIDNameObject;

/**
 * @author deklerk
 *
 */
public class CBREnumeratedValue extends AbstractIDNameObject {

	private static final long serialVersionUID = 200410150103500L;
	
	// for digest support
	public CBREnumeratedValue() {
		super("");
	}
	
	/**
	 * @param name
	 */
	public CBREnumeratedValue(String name) {
		super(name);
	}

	/**
	 * @param id
	 * @param name
	 */
	public CBREnumeratedValue(int id, String name) {
		super(id, name);
	}

	@Override
	public void setName(String name) {
		super.setName(name);
	}
}
