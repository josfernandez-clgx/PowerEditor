package com.mindbox.pe.common.config;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.ColumnMessageFragmentDigest;

public class MessageConfigurationTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("MessageConfigurationTest Tests");
		suite.addTestSuite(MessageConfigurationTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public MessageConfigurationTest(String name) {
		super(name);
	}

	public void testConstructorSetsInvariantsProperly() throws Exception {
		MessageConfiguration messageConfiguration = new MessageConfiguration();
		assertNull(messageConfiguration.getConditionalDelimiter());
		assertNull(messageConfiguration.getConditionalFinalDelimiter());
		ColumnMessageFragmentDigest digest = (ColumnMessageFragmentDigest) messageConfiguration.getDefaultObject();
		assertNotNull(digest);
		assertEquals(MessageConfiguration.ENUM_KEY, digest.getType());
		assertEquals(MessageConfiguration.TYPE_DEFAULT_KEY, digest.getCellSelection());
	}

	public void testCopyConstructorHappyCase() throws Exception {
		MessageConfiguration messageConfiguration = new MessageConfiguration();
		messageConfiguration.setConditionalDelimiter(ObjectMother.createString());
		messageConfiguration.setConditionalFinalDelimiter(ObjectMother.createString());

		MessageConfiguration copy = new MessageConfiguration(messageConfiguration);
		assertEquals(messageConfiguration.getConditionalDelimiter(), copy.getConditionalDelimiter());
		assertEquals(messageConfiguration.getConditionalFinalDelimiter(), copy.getConditionalFinalDelimiter());
		ColumnMessageFragmentDigest digest = (ColumnMessageFragmentDigest) messageConfiguration.getDefaultObject();
		assertNotNull(digest);
		assertEquals(MessageConfiguration.ENUM_KEY, digest.getType());
		assertEquals(MessageConfiguration.TYPE_DEFAULT_KEY, digest.getCellSelection());
	}

	public void testAddMessageDigestForDefaultEnumHappyCase() throws Exception {
		ColumnMessageFragmentDigest cmfDigest = new ColumnMessageFragmentDigest();
		cmfDigest.setType(MessageConfiguration.ENUM_KEY);
		cmfDigest.setCellSelection(MessageConfiguration.TYPE_DEFAULT_KEY);
		cmfDigest.setEnumDelimiter(ObjectMother.createString());
		cmfDigest.setEnumFinalDelimiter(ObjectMother.createString());
		cmfDigest.setText("text" + ObjectMother.createString());
		
		MessageConfiguration messageConfiguration = new MessageConfiguration();
		messageConfiguration.addMessageDigest(cmfDigest);
		
		ColumnMessageFragmentDigest digest = (ColumnMessageFragmentDigest) messageConfiguration.getDefaultObject();
		assertNotNull(digest);
		assertEquals(MessageConfiguration.ENUM_KEY, digest.getType());
		assertEquals(MessageConfiguration.TYPE_DEFAULT_KEY, digest.getCellSelection());
		assertEquals(cmfDigest.getEnumDelimiter(), digest.getEnumDelimiter());
		assertEquals(cmfDigest.getEnumFinalDelimiter(), digest.getEnumFinalDelimiter());
	}
}
