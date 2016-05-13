package com.mindbox.pe.client.common.formatter;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.mindbox.pe.common.UtilBase;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public class SymbolDocumentFilter extends DocumentFilter {

	public void insertString(FilterBypass fb, int offset, String s, AttributeSet attr) throws BadLocationException {
		if (UtilBase.isValidSymbol(s)) {
			super.insertString(fb, offset, s, attr);
		}
	}

	public void replace(FilterBypass fb, int offset, int length, String s, AttributeSet attr) throws BadLocationException {
		if (UtilBase.isValidSymbol(s)) {
			super.replace(fb, offset, length, s, attr);
		}
	}
}
