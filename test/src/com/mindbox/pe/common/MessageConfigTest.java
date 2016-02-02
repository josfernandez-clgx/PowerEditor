/*
 * Created on Feb 28, 2006
 *
 */
package com.mindbox.pe.common;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.common.config.MessageConfiguration;
import com.mindbox.pe.model.ColumnMessageFragmentDigest;
import com.mindbox.pe.AbstractTestWithTestConfig;


/**
 * @author Geneho Kim
 * @since PowerEditor 4.4.0
 */
public class MessageConfigTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("MessageConfigTest Tests");
		suite.addTestSuite(MessageConfigTest.class);
		return suite;
	}

	private MessageConfiguration messageConfig;
	
	public MessageConfigTest(String name) {
		super(name);
	}
	
	public void setUp() throws Exception {
		super.setUp();
		messageConfig = new MessageConfiguration();
	}
	
	public void tearDown() throws Exception {
		messageConfig = null;
	}

	public void testGetEnumConfiguration() throws Exception {
		ColumnMessageFragmentDigest cmd = new ColumnMessageFragmentDigest();
		cmd.setType("enum");
		cmd.setCellSelection("default");
		cmd.setText("sample message text: " + System.currentTimeMillis());
		cmd.setEnumDelimiter("enum-delim");
		cmd.setEnumFinalDelimiter("final enum & delim");
		cmd.setEnumPrefix("enum-prefix");
		messageConfig.addMessageDigest(cmd);
		
		assertEquals("Enum message fragment not equal for exclusion,multi-select", cmd, messageConfig.getEnumConfiguration(true,true));
		assertEquals("Enum message fragment not equal for exclusion,single-select", cmd, messageConfig.getEnumConfiguration(true,false));
		assertEquals("Enum message fragment not equal for inclusion,single-select", cmd, messageConfig.getEnumConfiguration(false,false));
		assertEquals("Enum message fragment not equal for inclusion,multi-select", cmd, messageConfig.getEnumConfiguration(false,true));
	}
}
