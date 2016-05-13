package com.mindbox.pe.common.diff;

public abstract class AbstractValueChangeDetail {

	private final Object oldValue;
	private final Object newValue;

	protected AbstractValueChangeDetail(Object oldValue, Object newValue) {
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public Object getNewValue() {
		return newValue;
	}

	public Object getOldValue() {
		return oldValue;
	}

}
