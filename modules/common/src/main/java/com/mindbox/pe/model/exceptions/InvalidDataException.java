package com.mindbox.pe.model.exceptions;

/**
 * Indicates invalid date.
 *
 */
public class InvalidDataException extends SapphireException {

	private static final long serialVersionUID = -1992018772131516254L;
	
	public void setDataLabel(String s) {
		mDataLabel = s;
	}

	public String getDataLabel() {
		return mDataLabel;
	}

	public InvalidDataException() {}

	public InvalidDataException(String dataLabel, String dataValue, String s2) {
		super(s2);
		setDataLabel(dataLabel);
		setDataValue(dataValue);
	}

	public String getMessage() {
		return toString() + " - " + super.getMessage();
	}
	
	public String toString() {
		return "Invalid Data: " + getDataValue() + " for " + getDataLabel();
	}

	public void setDataValue(String s) {
		mDataValue = s;
	}

	public String getDataValue() {
		return mDataValue;
	}

	private String mDataLabel;
	private String mDataValue;
}
