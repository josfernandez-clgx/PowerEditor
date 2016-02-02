/*
 * Created on 2003. 12. 18.
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.mindbox.pe.client.common.formatter;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since TemplateEditor 1.1.0
 */
class DateTimeDocumentFilter extends DocumentFilter {

	public void insertString(FilterBypass arg0, int arg1, String str, AttributeSet arg3) throws BadLocationException {
		StringBuffer buf = new StringBuffer(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
		buf.insert(arg1, str);
		if (Validator.isValidDateTime(buf.toString())) {
			super.insertString(arg0, arg1, str, arg3);
		}
	}

	public void replace(FilterBypass arg0, int arg1, int arg2, String str, AttributeSet arg4)
		throws BadLocationException {
		String oldStr = arg0.getDocument().getText(0, arg0.getDocument().getLength());
		StringBuffer buf = new StringBuffer(oldStr);
		buf.replace(arg1,arg1+arg2,str);
		if (Validator.isValidDateTime(buf.toString())) {
			super.replace(arg0, arg1, arg2, str, arg4);
		}
	}

	public void remove(FilterBypass arg0, int arg1, int arg2) throws BadLocationException {
		super.remove(arg0, arg1, arg2);
	}
}
