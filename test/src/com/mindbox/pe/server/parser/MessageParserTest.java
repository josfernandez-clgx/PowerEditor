/*
 * Created on Jul 2, 2004
 *
 */
package com.mindbox.pe.server.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.mindbox.pe.server.generator.processor.MessageProcessor;
import com.mindbox.pe.server.generator.processor.PEMessageTreeFormatter;
import com.mindbox.pe.server.parser.jtb.message.MessageParser;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.Message;
import com.mindbox.pe.server.parser.jtb.message.visitor.TreeDumper;
import com.mindbox.pe.server.ServerTestBase;


/**
 * Message parser test.
 * This attempts to parse each line in 'test-messages.txt' file in the data directory 
 * that does not begin with a '#'.
 * @author Geneho
 * @since PowerEditor 3.2.0
 * @deprecated disabled until fixed
 */
public class MessageParserTest extends ServerTestBase {

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Server Repository Tests");
		suite.addTestSuite(MessageParserTest.class);
		return suite;
	}

	/**
	 * @param name
	 */
	public MessageParserTest(String name) {
		super(name);
	}
	
	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
	}
	
	public void testMessageParser() throws Exception {
		logBegin();
		
		File messageFile = config.getDataFile("test-messages.txt");
		BufferedReader reader = new BufferedReader(new FileReader(messageFile));
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			if (line.charAt(0) != '#') {
				parseMessage(line);
			}
		}
		reader.close();
		logEnd();
	}
	
	private void parseMessage(String messageStr) throws Exception {
		logger.info("Parsing message: " + messageStr);
		MessageParser messageParser = new MessageParser(new StringReader(messageStr));
		Message messageObj = messageParser.Message();
		
		logger.info("messageStr = " + messageStr);
		logger.info("parsed obj = " + messageObj);
		
		StringWriter writer = new StringWriter();
		messageObj.accept(new PEMessageTreeFormatter());
		messageObj.accept(new TreeDumper(writer));
		
		logger.info("tree-dump: " + writer.toString());
		
		MessageProcessor messageProcessor = new MessageProcessor();
		logger.info("processed: " + messageProcessor.process(messageObj, null, null));
	}

}