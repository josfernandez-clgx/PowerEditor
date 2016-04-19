/*
 * Created on 2003. 12. 18.
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.mindbox.pe.client.common.formatter;

import java.text.ParseException;

import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.text.DocumentFilter;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since TemplateEditor 1.1.0
 */
public final class StringFormatter extends AbstractFormatter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private final DocumentFilter documentFiler;

	StringFormatter(DocumentFilter documentFilter) {
		this.documentFiler = documentFilter;
	}

	public Object stringToValue(String str) throws ParseException {
		return str;
	}

	public String valueToString(Object obj) throws ParseException {
		return (String) obj;
	}

	protected DocumentFilter getDocumentFilter() {
		return documentFiler;
	}

}
