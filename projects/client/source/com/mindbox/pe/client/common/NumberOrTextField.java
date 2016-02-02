package com.mindbox.pe.client.common;

import javax.swing.text.PlainDocument;

/**
 * @author deklerk
 *
 */
public class NumberOrTextField extends NumberTextField {

	/**
	 * @param size
	 */
	public NumberOrTextField(int size) {
		super(size);
	}

	/**
	 * @param size
	 * @param value
	 */
	public NumberOrTextField(int size, int value) {
		super(size, value);
	}

	/**
	 * @param size
	 * @param value
	 * @param nullValue
	 */
	public NumberOrTextField(int size, int value, int nullValue, boolean negativeAllowed) {
		super(size, value, nullValue, negativeAllowed);
	}
	
	public void setNumberInputMode(boolean on) {
		if (on) {
			if (!(getDocument() instanceof NumberTextField.NumberDocument)) setDocument(new NumberTextField.NumberDocument()); 
		} else {
			if (getDocument() instanceof NumberTextField.NumberDocument) setDocument(new PlainDocument()); 
		}
	}
}
