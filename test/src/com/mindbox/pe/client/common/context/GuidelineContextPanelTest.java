package com.mindbox.pe.client.common.context;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.client.AbstractClientTestBase;

public class GuidelineContextPanelTest extends AbstractClientTestBase {
    
    GuidelineContextPanel panel;

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GuidelineContextPanelTest Tests");
		suite.addTestSuite(GuidelineContextPanelTest.class);
		return suite;
	}

	public GuidelineContextPanelTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
        panel = new GuidelineContextPanel("button.edit.context", true, true, true);        
	}

	protected void tearDown() throws Exception {
        panel = null;
		super.tearDown();
	}
    
    public void testAll() throws Exception {
        assertNotNull(panel);
        assertFalse(panel.includeChildrenCategories());
        assertFalse(panel.includeParentCategories());
        assertFalse(panel.searchInColumnCheckbox());
        assertFalse(panel.includeEmptyContexts());        
    }

    
}
