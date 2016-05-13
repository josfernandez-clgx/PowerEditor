package com.mindbox.pe.client.applet.guidelines.search;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.ClientUtil;


public class TemplateReportTableModelTest extends AbstractClientTestBase {

	/**
	 * Test to make sure the drop-down is not editable and that the toString() method contains
	 * the name
	 *
	 * @throws Exception
	 */
	@Test
	public void testAll() throws Exception {
		TemplateReportTableModel tableModel = new TemplateReportTableModel();
		String[] colNames = tableModel.getColumnNames();
		assertNotNull(colNames);
		assertTrue(colNames.length > 0);
		assertTrue(Arrays.asList(colNames).contains(ClientUtil.getInstance().getLabel("label.row")));
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
}
