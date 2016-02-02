package com.mindbox.pe.client.common;

import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import com.mindbox.pe.common.UtilBase;

public class FloatTextField extends JTextField {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9108758955987283998L;
	
	protected class FloatDocument extends PlainDocument {
		/**
		 * 
		 */
		private static final long serialVersionUID = -9108758955987283994L;

		public void insertString(int offset, String s, AttributeSet attributeset) throws BadLocationException {
			
			// TODO GKIM: May need to replace this with an implementation that builds the "eventual" string 
			//            and makes sure it's a number in the set Locale
			
			char ac[] = s.toCharArray();
			char ac1[] = new char[ac.length];
			int j = 0;
			int k = 0;

			for (int pos = 0; pos < ac.length; pos++) {
				if (Character.isDigit(ac[pos]) || ',' == ac[pos]) {
					// digits, comma, and 'e' always allowed
					ac1[j++] = ac[pos];
				}
				else if (('E' == ac[pos] || 'e' == ac[pos]) && (offset > 0 || pos > 0)) {
					// Allow e and E for scientific notation if it's position is not zero
					ac1[j++] = ac[pos];
				}
				else {
					char nonNumericChar = ac[pos];

					if (nonNumericChar == '.') {
						if (k == 0 && !prevTextHasDecimalPoint()) {
							ac1[j++] = nonNumericChar;
							k++;
						}
					}

					// TODO GKIM: This assumes Locale is US. Must use the correct symbol for the set Locale
					
					else if (nonNumericChar == '$') {
						if (forCurrency && pos == 0 && offset == 0) {
							// '$' allowed as first char in currency field
							ac1[j++] = nonNumericChar;
						}
					}
					else if (nonNumericChar == '-') {
						char prevFirstChar = getPrevFirstChar();
						if (offset == 0 && pos == 0 && prevFirstChar != '$' && prevFirstChar != '-') {
							// '-' always allowed as first char, unless it would replace an existing '$' or '-'
							ac1[j++] = nonNumericChar;
						}
						else if (forCurrency) {
							// '-' may also appear as second char in currency field, if first char is '$'
							if ((pos == 1 && ac1[0] == '$') || (offset == 1 && pos == 0 && prevFirstChar == '$')) {
								ac1[j++] = nonNumericChar;
							}
						}
					}
				}
			}
			super.insertString(offset, new String(ac1, 0, j), attributeset);
		}
	}

	public FloatTextField(int preferredWidthColumns, boolean forCurrency) {
		super(preferredWidthColumns);
		this.forCurrency = forCurrency;
		nf = NumberFormat.getNumberInstance();
	}

	public FloatTextField(float val, int preferredWidthColumns, boolean forCurrency) {
		this(preferredWidthColumns, forCurrency);
		setValue(val);
	}

	public FloatTextField(double val, int preferredWidthColumns, boolean forCurrency) {
		this(preferredWidthColumns, forCurrency);
		setValue(val);
	}

	public Float getValue() {
		Float value = null;
		try {
			String text = getText();
			// TODO GKIM: This assumes Locale is US. Must use the correct symbol for the set Locale
			if (!UtilBase.isEmpty(text) && text.charAt(0) == '$') {
				text = text.substring(1, text.length());
			}
			if (hasDigits(text)) {
				value = new Float(nf.parse(text).floatValue());
			}
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		return value;
	}

	public Double getDoubleValue() {
		Double value = null;
		try {
			String text = getText();
			// TODO GKIM: This assumes Locale is US. Must use the correct symbol for the set Locale
			if (!UtilBase.isEmpty(text) && text.charAt(0) == '$') {
				text = text.substring(1, text.length());
			}
			if (hasDigits(text)) {
				value = new Double(nf.parse(text).doubleValue());
			}
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		return value;
	}

	public float getFloatValue() {
		Float v = getValue();
		return (v == null ? 0.0f : v.floatValue());
	}

	public boolean hasValue() {
		return !UtilBase.isEmptyAfterTrim(getText());
	}

	public void setValue(float f) {
		setText(new Float(f).toString());
	}

	public void setValue(double value) {
		setText(new Double(value).toString());
	}

	protected Document createDefaultModel() {
		return new FloatDocument();
	}

	private boolean hasDigits(String s) {
		if (s != null) {
			char[] chars = s.toCharArray();
			for (int i = 0; i < chars.length; i++) {
				if (Character.isDigit(chars[i])) {
					return true;
				}
			}
		}
		return false;
	}

	private char getPrevFirstChar() {
		String prevText = getText();
		return prevText == null || prevText.length() == 0 ? '0' : prevText.charAt(0);
	}

	private boolean prevTextHasDecimalPoint() {
		String prevText = getText();
		return prevText != null && prevText.indexOf('.') > -1;
	}

	private boolean forCurrency;
	private NumberFormat nf;
}
