/*
 * Created on 2004. 8. 9.
 */
package com.mindbox.pe.model.rule;

import java.io.Serializable;

/**
 * Implementation of Value.
 * @author kim
 * @author MindBox
 * @since PowerEditor 4.0
 */
class ValueImpl implements Value, Serializable {
	
	private static final long serialVersionUID = 20040809200002L;

	static final Value NULL_VALUE = new ValueImpl(null);
	
	static Value getInstance(Object value) {
		if (value == null) return NULL_VALUE;
		return new ValueImpl(value);
	}
	
	
	private final Object value;

	private ValueImpl(Object value) {
		this.value = value;
	}

	public String toString() {
		return (value == null ? "<null>" : value.toString());
	}

}