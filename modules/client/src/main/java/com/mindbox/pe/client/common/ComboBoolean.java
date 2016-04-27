package com.mindbox.pe.client.common;

class ComboBoolean {

	private final Boolean value;

	ComboBoolean(Boolean boolean1) {
		value = boolean1;
	}

	Boolean getValue() {
		return value;
	}

	@Override
	public String toString() {
		if (value == null) return BooleanComboBox.GENERIC_ANY;
		if (value.booleanValue()) {
			return BooleanComboBox.GENERIC_YES;
		}
		else {
			return BooleanComboBox.GENERIC_NO;
		}
	}
}