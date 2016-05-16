package com.mindbox.pe.client.common;

import javax.swing.text.PlainDocument;

import com.mindbox.pe.common.ui.NumberTextField;

/**
 * @author deklerk
 *
 */
public class NumberOrTextField extends NumberTextField {
	private static final long serialVersionUID = -3951228734910107454L;

	/**
	 * @param size size
	 */
	public NumberOrTextField(int size) {
		super(size);
	}

	/**
	 * @param size size
	 * @param value value
	 */
	public NumberOrTextField(int size, int value) {
		super(size, value);
	}

	/**
	 * @param size size
	 * @param value value
	 * @param nullValue nullValue
	 * @param negativeAllowed negative allowed flag
	 */
	public NumberOrTextField(int size, int value, int nullValue, boolean negativeAllowed) {
		super(size, value, nullValue, negativeAllowed);
	}

	public void setNumberInputMode(boolean on) {
		if (on) {
			if (!(getDocument() instanceof NumberTextField.NumberDocument)) setDocument(new NumberTextField.NumberDocument());
		}
		else {
			if (getDocument() instanceof NumberTextField.NumberDocument) setDocument(new PlainDocument());
		}
	}
}
