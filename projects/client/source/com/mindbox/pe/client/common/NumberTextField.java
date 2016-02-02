package com.mindbox.pe.client.common;

import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * Number entry field.
 * @since PowerEditor 1.0
 */
public class NumberTextField extends JTextField {
	protected class NumberDocument extends PlainDocument {

		public void insertString(int i, String s, AttributeSet attributeset) throws BadLocationException {
			char ac[] = s.toCharArray();
			char ac1[] = new char[ac.length];
			int j = 0;
			for (int k = 0; k < ac.length; k++)
				if (Character.isDigit(ac[k]) || (negativeAllowed && (k == 0 && ac[k] == '-' && i == 0)))
					ac1[j++] = ac[k];

			super.insertString(i, new String(ac1, 0, j), attributeset);
		}

		public NumberDocument() {}
	}
	public Integer getValue() {
		Integer value = null;
		try {
			value = new Integer(nf.parse(getText()).intValue());
		}
		catch (ParseException parseexception) {
			//parseexception.printStackTrace();
		}
		return value;
	}

	public int getIntValue() {
		return (getValue() == null ? nullValue : getValue().intValue());
	}
	
	public Long getLongValue() {
		Long value = null;
		try {
			value = new Long(nf.parse(getText()).longValue());
		}
		catch (ParseException parseexception) {
			// ignore
		}
		return value;
	}
	
	public void clearValue() {
		setText("");
	}

	public boolean hasValue() {
		return getText() != null && getText().trim().length() > 0;
	}
	
	public void setValue(int i) {
		setText(nf.format(i));
	}

	public void setValue(long value) {
		setText(nf.format(value));
	}

	protected Document createDefaultModel() {
		return new NumberDocument();
	}

	public NumberTextField(int size) {
		super(size);
		nf = NumberFormat.getNumberInstance();
	}

	public NumberTextField(int size, int value) {
		this(size);
		setValue(value);
	}
	
	public NumberTextField(int size, int value, int nullValue, boolean negativeAllowed) {
		this(size);
		if (value != nullValue) setValue(value);
		this.nullValue = nullValue;
		this.negativeAllowed = negativeAllowed;
	}
	
	private NumberFormat nf;
	private int nullValue = 0;
	private boolean negativeAllowed = true;
}
