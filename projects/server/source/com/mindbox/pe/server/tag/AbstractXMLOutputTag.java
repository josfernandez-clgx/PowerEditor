package com.mindbox.pe.server.tag;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.jsp.tagext.TagSupport;

import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.server.Util;

public abstract class AbstractXMLOutputTag extends TagSupport {

	private static final String xmlify(String str) {
		return Util.xmlify(str);
	}
	
	protected final void writeOpen(String element) throws IOException {
		pageContext.getOut().println("<" + element + ">");
	}

	protected final void writeOpen(String element, String attr, int value) throws IOException {
		writeOpen_internal(element, attr, String.valueOf(value));
	}

	protected final void writeOpen(String element, String attr, String value) throws IOException {
		writeOpen_internal(element, attr, xmlify(value));
	}

	private final void writeOpen_internal(String element, String attr, String value) throws IOException {
		pageContext.getOut().println("<" + element + " " + attr + "=\"" + value + "\" >");
	}

	protected final void writeOpen(String element, String attr1, String value1, String attr2, String value2) throws IOException {
		pageContext.getOut().println("<" + element + " " + attr1 + "=\"" + xmlify(value1) + "\" " + attr2 + "=\"" + xmlify(value2) + "\">");
	}

	protected final void writeOpen(String element, String attr1, String value1, String attr2, String value2, String attr3, String value3) throws IOException {
		pageContext.getOut().println("<" + element + " " + attr1 + "=\"" + xmlify(value1) + "\" " + attr2 + "=\"" + xmlify(value2) + "\" " + attr3 + "=\"" + xmlify(value3) + "\">");
	}
		
	protected final void writeOpen(String element, Map<String, String> attriValueMap) throws IOException {
		if (attriValueMap == null || attriValueMap.isEmpty()) return;
		
		String outString = "<" + element + " " ;
		for (Iterator<String> attrIter = attriValueMap.keySet().iterator(); attrIter.hasNext();) {
			String attr = attrIter.next();
			String value = attriValueMap.get(attr);
			outString = outString + attr  + "=\"" + xmlify(value) + "\" ";
		}
		outString = outString + ">";
		pageContext.getOut().println(outString);
	}

	protected final void writeClose(String element) throws IOException {
		pageContext.getOut().println("</" + element + ">");
	}

	protected final void writeElement(String element, String value) throws IOException {
		writeElement_internal(element, xmlify(value));
	}
	
	private final void writeElement_internal(String element, String value) throws IOException {
		pageContext.getOut().println("<" + element + ">" + value + "</" + element + ">");
	}

	protected final void writeElement(String element, int value) throws IOException {
		writeElement_internal(element, String.valueOf(value));
	}

	protected final void writeElement(String element, boolean value) throws IOException {
		writeElement_internal(element, String.valueOf(value));
	}

	protected final void writeElement(String element, Date value) throws IOException {
		writeElement_internal(element, ConfigUtil.toDateXMLString(value));
	}


}
