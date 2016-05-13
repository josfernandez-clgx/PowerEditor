package com.mindbox.pe.common.config;

import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.mindbox.pe.model.template.ColumnMessageFragmentDigest;
import com.mindbox.pe.unittest.AbstractTestBase;
import com.mindbox.pe.xsd.config.CellSelectionType;
import com.mindbox.pe.xsd.config.MessageConfigType;

public class MessageConfigurationTest extends AbstractTestBase {

	@Test
	public void testConstructorSetsInvariantsProperly() throws Exception {
		MessageConfiguration messageConfiguration = new MessageConfiguration();
		assertNull(messageConfiguration.getConditionalDelimiter());
		assertNull(messageConfiguration.getConditionalFinalDelimiter());
		ColumnMessageFragmentDigest digest = messageConfiguration.getDefaultConfig();
		assertNotNull(digest);
		assertEquals(MessageConfigType.ENUM, digest.getType());
		assertEquals(CellSelectionType.DEFAULT, digest.getCellSelection());
	}

	@Test
	public void testCopyConstructorHappyCase() throws Exception {
		MessageConfiguration messageConfiguration = new MessageConfiguration();
		messageConfiguration.setConditionalDelimiter(createString());
		messageConfiguration.setConditionalFinalDelimiter(createString());

		MessageConfiguration copy = new MessageConfiguration(messageConfiguration);
		assertEquals(messageConfiguration.getConditionalDelimiter(), copy.getConditionalDelimiter());
		assertEquals(messageConfiguration.getConditionalFinalDelimiter(), copy.getConditionalFinalDelimiter());
		ColumnMessageFragmentDigest digest = messageConfiguration.getDefaultConfig();
		assertNotNull(digest);
		assertEquals(MessageConfigType.ENUM, digest.getType());
		assertEquals(CellSelectionType.DEFAULT, digest.getCellSelection());
	}

	@Test
	public void testAddMessageDigestForDefaultEnumHappyCase() throws Exception {
		ColumnMessageFragmentDigest cmfDigest = new ColumnMessageFragmentDigest();
		cmfDigest.setType(MessageConfigType.ENUM);
		cmfDigest.setCellSelection(CellSelectionType.DEFAULT);
		cmfDigest.setEnumDelimiter(createString());
		cmfDigest.setEnumFinalDelimiter(createString());
		cmfDigest.setText("text" + createString());

		MessageConfiguration messageConfiguration = new MessageConfiguration();
		messageConfiguration.addColumnMessageFragmentDigest(cmfDigest);

		ColumnMessageFragmentDigest digest = messageConfiguration.getDefaultConfig();
		assertNotNull(digest);
		assertEquals(MessageConfigType.ENUM, digest.getType());
		assertEquals(CellSelectionType.DEFAULT, digest.getCellSelection());
		assertEquals(cmfDigest.getEnumDelimiter(), digest.getEnumDelimiter());
		assertEquals(cmfDigest.getEnumFinalDelimiter(), digest.getEnumFinalDelimiter());
	}
}
