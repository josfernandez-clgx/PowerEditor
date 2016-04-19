package com.mindbox.pe.server.generator.value;

public final class ValueAndComment {

	private String value;
	private String comment;

	public ValueAndComment(String value) {
		this(value, null);
	}

	public ValueAndComment(String value, String comment) {
		this.value = value;
		this.comment = comment;
	}

	public String getValue() {
		return value;
	}

	public String getComment() {
		return comment;
	}

	/**
	 * Equivalent to {@link #getValue()}.
	 */
	public String toString() {
		return value;
	}
}
