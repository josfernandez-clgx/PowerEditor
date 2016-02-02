package com.mindbox.pe.server.imexport;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.ArrayStack;

/**
 * A stateful XML writer.
 * This is <b>not</b> thread-safe.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public abstract class XMLWriter {
	
	private static String xmlify(String str) {
		String str2 = str.replaceAll("&", "&amp;").replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("\"", "&quot;").replaceAll("'", "&apos;");
		StringBuffer buff = new StringBuffer(str2.length());
		for (int i = 0; i < str2.length(); i++) {
			int ci = str2.charAt(i);
			if (ci > 255) {
				buff.append("&#x");
				buff.append(Integer.toHexString(ci));
				buff.append(';');
			}
			else {
				buff.append(str2.charAt(i));
			}
		}
		return buff.toString();
	}

	private final PrintWriter writer;
	private int indent = 0;
	private final int increment;
	private final ArrayStack tagStack;

	protected XMLWriter(Writer writer, int indentIncrement) {
		this.writer = new PrintWriter(writer, false);
		this.increment = (indentIncrement > 0 ? indentIncrement : 2);
		this.tagStack = new ArrayStack();
	}

	public final void close() {
		writer.flush();
		writer.close();
	}

	private final void incrementIndent() {
		indent += increment;
	}

	private final void decrementIndent() {
		indent -= increment;
	}

	private final void openTag(String tag) {
		tagStack.push(tag);
		indentWrite("<" + tag);
	}

	private final void closeTag() {
		writeln("</" + tagStack.pop() + ">");
	}

	private final void writeAttributes(Map<String,Object> attributeMap) {
		for (Iterator<String> iter = attributeMap.keySet().iterator(); iter.hasNext();) {
			String key = iter.next();
			writeAttribute(key, attributeMap.get(key));
		}
	}

	private final void writeAttribute(String attribute, Object value) {
		if (attribute != null && attribute.length() > 0) {
			write(" ");
			write(attribute);
			write("=\"");
			write((value == null ? "" : xmlify(value.toString())));
			write("\"");
		}
	}


	private final void writeAttributes(String[] attributes, Object[] values, boolean skipEmptyValue) {
		for (int i = 0; i < attributes.length; i++) {
			if (!skipEmptyValue || (values[i] != null && values[i].toString().trim().length() > 0)) {
				writeAttribute(attributes[i], values[i]);
			}
		}
	}

	private final void writeAttributes(String[] attributes, Object[] values) {
		writeAttributes(attributes, values, false);
	}

	protected final void writeOpenTag(String tag) {
		openTag(tag);
		writeln(">");
		incrementIndent();
	}

	protected final void writeOpenTag(String tag, String attribute, Object value) {
		writeOpenTag(tag, new String[] { attribute}, new Object[] { value});
	}

	protected final void writeOpenTag(String tag, String[] attributes, Object[] values, boolean skipEmptyValue) {
		openTag(tag);
		writeAttributes(attributes, values, skipEmptyValue);
		writeln(">");
		incrementIndent();
	}

	protected final void writeOpenTag(String tag, String[] attributes, Object[] values) {
		writeOpenTag(tag, attributes, values, false);
	}

	protected final void writeOpenTag(String tag, Map<String, Object> attributeMap) {
		openTag(tag);
		writeAttributes(attributeMap);
		writeln(">");
		incrementIndent();
	}

	protected final void writeOpenCloseTag(String tag, String attribute, Object value) {
		writeOpenCloseTag(tag, new String[] { attribute}, new Object[] { value});
	}

	protected final void writeOpenCloseTag(String tag, String[] attributes, Object[] values) {
		openTag(tag);
		writeAttributes(attributes, values);
		writeln("/>");
		tagStack.pop();
	}

	protected final void writeOpenCloseTag(String tag, Map<String, Object> attributeMap) {
		openTag(tag);
		writeAttributes(attributeMap);
		writeln("/>");
		tagStack.pop();
	}

	protected final void writeBodyTag(String tag, String body, boolean skipIfEmpty) {
		if (skipIfEmpty && (body == null || body.trim().length() == 0)) return;
		openTag(tag);
		write(">");
		write(xmlify(body));
		closeTag();
	}

	protected final void writeBodyTag(String tag, String body) {
		writeBodyTag(tag, body, false);
	}

	protected final void writeBodyTag(String tag, boolean body) {
		writeBodyTag(tag, String.valueOf(body), false);
	}

	protected final void writeBodyTag(String tag, int body) {
		writeBodyTag(tag, String.valueOf(body), false);
	}

	protected final void writeBodyTag(String tag, long body) {
		writeBodyTag(tag, String.valueOf(body), false);
	}

	protected final void writeBodyTag(String tag, float body) {
		writeBodyTag(tag, String.valueOf(body), false);
	}

	protected final void writeBodyTag(String tag, double body) {
		writeBodyTag(tag, String.valueOf(body), false);
	}

	protected final void writeCloseTag() {
		decrementIndent();
		indent();
		closeTag();
	}

	// row-level write methods

	private final void indent() {
		for (int i = 0; i < indent; i++) {
			this.writer.print(' ');
		}
	}

	protected final void indentWrite(String str) {
		indent();
		write(str);
	}

	protected final void indentWriteln(String str) {
		indent();
		writeln(str);
	}

	protected final void write(String str) {
		this.writer.print(str);
	}

	protected final void writeln(String str) {
		this.writer.println(str);
	}

	protected final void writeComment(String comment) {
		this.writer.println("<!-- " + comment + " -->");
	}
	
	protected final void writelnXMLified(String str) {
		this.writer.println(xmlify(str));
	}
	
	protected final void writeCDATA(String str) {
		this.writer.println("<![CDATA[");
		this.writer.print(str);
		this.writer.println("]]>");
	}
}