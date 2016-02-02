/*
 * Created on 2004. 2. 10.
 *
 */
package com.mindbox.pe.server.repository.adhoc;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.rule.FunctionParameter;
import com.mindbox.pe.model.rule.CompoundLHSElement;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.rule.RuleAction;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleElement;
import com.mindbox.pe.model.rule.Value;

/**
 * Writer for AdHoc Rules.
 * Behavior of this class after it has been closed is not defined.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
final class AdHocRuleWriter {

	static final String ROOT_TAG_NAME = "AdHocRules";

	private static String xmlify(String str) {
		return str.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"","&quot;");
	}

	private static byte[] toBytes(String str) {
		try {
			return str.getBytes("US-ASCII");
		}
		catch (Exception e) {
			return str.getBytes();
		}
	}

	private static final String NEW_LINE = System.getProperty("line.separator");

	private static final MessageFormat HEADER_FORMAT =
		new MessageFormat(
			"<?xml version=\"1.0\"?>"
				+ NEW_LINE
				+ "<"
				+ ROOT_TAG_NAME
				+ ">"
				+ NEW_LINE
				+ "<!-- Last update: {0,date,yyyy-MM-dd HH:mm:ss} -->");

	private static final String FOOTER = "</" + ROOT_TAG_NAME + ">";

	private final Logger logger = Logger.getLogger(getClass());
	//private PrintWriter writer = null;
	private int indentCount = 0;
	private RandomAccessFile raf = null;
	private ByteBuffer buffer = null;
	private FileChannel fileChannel = null;

	/**
	 * 
	 */
	public AdHocRuleWriter(File file) throws IOException {
		if (file.exists()) {
			file.delete();
			file.createNewFile();
		}

		//this.writer = new PrintWriter(new BufferedWriter(new FileWriter(file, append)));
		this.raf = new RandomAccessFile(file, "rw");
		this.buffer = ByteBuffer.allocate(2 * 1024);
	}

	private void checkBuffer(int size) throws IOException {
		if (buffer.remaining() <= size) {
			flush();
		}
	}

	private void indent() {
		for (int i = 0; i < indentCount; i++) {
			buffer.put(toBytes("  "));
		}
	}

	private void println(String str) throws IOException {
		checkBuffer(indentCount * 2 + str.length() + 2);
		indent();
		buffer.put(toBytes(str));
		if (str != null) {
			buffer.put(toBytes(NEW_LINE));
		}
	}

	private void print(String str) throws IOException {
		checkBuffer(indentCount * 2 + str.length());
		indent();
		if (str != null) {
			buffer.put(toBytes(str));
		}
	}

	private void append(String str) throws IOException {
		if (str != null) {
			checkBuffer(str.length());
			buffer.put(toBytes(str));
		}
	}

	private void appendln(String str) throws IOException {
		checkBuffer(str.length() + 2);
		if (str != null) {
			buffer.put(toBytes(str));
		}
		buffer.put(toBytes(NEW_LINE));
	}

	private void writeHeader() throws IOException {
		println(HEADER_FORMAT.format(new Object[] { new Date()}));
		flush();
	}

	private void writeFooter() throws IOException {
		println(FOOTER);
		flush();
	}

	String toXMLString(RuleDefinition ruleDefinition) throws IOException {
		write(ruleDefinition);
		// TBD write buffer to string
		return buffer.toString();
	}
	
	public synchronized void write(RuleDefinition[] rules) throws IOException {
		logger.debug(">>> write: " + rules.length + " rules");
		this.fileChannel = raf.getChannel();
		FileLock fileLock = fileChannel.lock();
		try {
			logger.debug("write: lock obtained. writing header");
			writeHeader();

			for (int i = 0; i < rules.length; i++) {
				logger.debug("write: writing rule " + i);

				write(rules[i]);
				buffer.put(toBytes(NEW_LINE));
				flush();
			}

			logger.debug("write: writing footer");
			writeFooter();

			logger.debug("<<< write");
		}
		finally {
			fileLock.release();
			fileChannel.close();
			fileChannel = null;
		}
	}

	private void write(RuleDefinition rule) throws IOException {
		logger.debug(">>> write(RuleDefinition): " + rule);
		++indentCount;
		print("<Rule id=\"");
		append(String.valueOf(rule.getID()));
		append("\" name=\"");
		append(rule.getName());
		appendln("\">");

		++indentCount;

		print("<Description>");
		append(rule.getDescription());
		appendln("</Description>");

		println("<LHS>");
		++indentCount;

		write(rule.getRootElement());

		--indentCount;
		println("</LHS>");

		write(rule.getRuleAction());

		writeMessages(rule);
		
		--indentCount;
		println("</Rule>");
		--indentCount;
	}

	private void writeMessages(RuleDefinition rule) throws IOException {
		Set<String> channelSet = rule.getMessageChannels();
		for (Iterator<String> iter = channelSet.iterator(); iter.hasNext();) {
			String channel = iter.next();
			print("<Message channel=\"");
			append(channel);
			append("\">");
			append((String)rule.getMessage(channel));
			appendln("</Message>");
		}
	}

	private void write(Condition condition) throws IOException {
		println("<Condition>");
		++indentCount;

		write(condition.getReference());

		print("<Operator>");
		append(xmlify(Condition.Aux.toOpString(condition.getOp())));
		appendln("</Operator>");

		write(condition.getValue());

		writeComment(condition.getComment());
		--indentCount;

		println("</Condition>");
	}

	private void writeComment(String comment) throws IOException {
		print("<Comment>");
		append(comment);
		appendln("</Comment>");
	}

	private void write(Reference ref) throws IOException {
		println("<Reference>");

		++indentCount;
		print("<Class>");
		append(ref.getClassName());
		appendln("</Class>");
		print("<Attribute>");
		append(ref.getAttributeName());
		appendln("</Attribute>");
		--indentCount;

		println("</Reference>");
	}

	private void write(Value value) throws IOException {
		print("<Value>");
		append(value.toString());
		appendln("</Value>");
	}

	private void write(CompoundLHSElement element) throws IOException {
		logger.debug(">>> write(CompoundLHSElement): " + element);
		String tagName = null;
		switch (element.getType()) {
			case CompoundLHSElement.TYPE_AND :
				tagName = "AND";
				break;
			case CompoundLHSElement.TYPE_OR :
				tagName = "OR";
				break;
			case CompoundLHSElement.TYPE_NOT :
				tagName = "NOT";
				break;
			default :
				logger.warn("Invalid compound element type in  " + element);
				tagName = "ERROR_INVALID_" + element.getType();
		}

		println("<" + tagName + ">");
		++indentCount;

		writeElements(element);

		writeComment(element.getComment());
		--indentCount;
		println("</" + tagName + ">");
	}

	private void writeElements(CompoundLHSElement parent) throws IOException {
		logger.debug(">>> writeElements(CompoundLHSElement): " + parent + ", size=" + parent.size());
		for (int i = 0; i < parent.size(); ++i) {
			RuleElement element = parent.get(i);
			if (element instanceof CompoundLHSElement) {
				write((CompoundLHSElement) element);
			}
			else if (element instanceof Condition) {
				write((Condition) element);
			}
		}
	}

	private void write(RuleAction action) throws IOException {
		logger.debug(">>> write(RuleAction):" + action);
		println("<Action >");

		if (action != null && action.getActionType() != null) {
			++indentCount;

			print("<Type>");
			append(String.valueOf(action.getActionType().getID()));
			appendln("</Type>");

			writeComment(action.getComment());

			println("<ParameterList>");
			++indentCount;

			for (int i = 0; i < action.size(); ++i) {
				write((FunctionParameter) action.get(i));
			}

			--indentCount;
			println("</ParameterList>");

			--indentCount;
		}
		println("</Action>");
	}

	private void write(FunctionParameter param) throws IOException {
		print("<Parameter index=\"");
		append(String.valueOf(param.index()));
		append("\" value=\"");
		append(param.valueString());
		appendln("\"/>");
	}

	/**
	 * This flushes any unsaved changes.
	 *
	 */
	public synchronized void close() throws IOException {
		try {
			if (raf != null) {
				flush();
				raf.close();
			}
		}
		finally {
			raf = null;
			buffer = null;
		}
	}

	private void flush() throws IOException {
		buffer.flip();
		while (buffer.hasRemaining()) {
			fileChannel.write(buffer);
		}
		buffer.clear();
	}

	public void finalize() {
		try {
			close();
		}
		catch (Exception e) {}
	}

}
