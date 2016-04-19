package com.mindbox.pe.client.common.context;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;

public class GuidelineContextPanelTest extends AbstractClientTestBase {

	private GuidelineContextPanel panel;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		panel = new GuidelineContextPanel("button.edit.context", true, true, true);
	}

	@After
	public void tearDown() throws Exception {
		panel = null;
		super.tearDown();
	}

	@Test
	public void testAll() throws Exception {
		assertNotNull(panel);
		assertFalse(panel.includeChildrenCategories());
		assertFalse(panel.includeParentCategories());
		assertFalse(panel.searchInColumnCheckbox());
		assertFalse(panel.includeEmptyContexts());
	}

}
