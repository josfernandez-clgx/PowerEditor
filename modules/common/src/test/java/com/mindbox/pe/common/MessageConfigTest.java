/*
 * Created on Feb 28, 2006
 *
 */
package com.mindbox.pe.common;

import static com.mindbox.pe.common.CommonTestAssert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.config.MessageConfiguration;
import com.mindbox.pe.model.template.ColumnMessageFragmentDigest;
import com.mindbox.pe.unittest.AbstractTestBase;
import com.mindbox.pe.xsd.config.CellSelectionType;
import com.mindbox.pe.xsd.config.MessageConfigType;

/**
 * @author Geneho Kim
 * @since PowerEditor 4.4.0
 */
public class MessageConfigTest extends AbstractTestBase {

	private MessageConfiguration messageConfig;

	@Before
	public void setUp() throws Exception {
		messageConfig = new MessageConfiguration();
	}

	@After
	public void tearDown() throws Exception {
		messageConfig = null;
	}

	@Test
	public void testGetEnumConfiguration() throws Exception {
		ColumnMessageFragmentDigest cmd = new ColumnMessageFragmentDigest();
		cmd.setType(MessageConfigType.ENUM);
		cmd.setCellSelection(CellSelectionType.DEFAULT);
		cmd.setText("sample message text: " + System.currentTimeMillis());
		cmd.setEnumDelimiter("enum-delim");
		cmd.setEnumFinalDelimiter("final enum & delim");
		cmd.setEnumPrefix("enum-prefix");
		messageConfig.addColumnMessageFragmentDigest(cmd);

		assertEquals("Enum message fragment not equal for exclusion,multi-select", cmd, messageConfig.getEnumConfig(true, true));
		assertEquals("Enum message fragment not equal for exclusion,single-select", cmd, messageConfig.getEnumConfig(true, false));
		assertEquals("Enum message fragment not equal for inclusion,single-select", cmd, messageConfig.getEnumConfig(false, false));
		assertEquals("Enum message fragment not equal for inclusion,multi-select", cmd, messageConfig.getEnumConfig(false, true));
	}
}
