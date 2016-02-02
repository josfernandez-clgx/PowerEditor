package com.mindbox.pe.client.applet.entities.generic.category;

import javax.swing.JButton;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntityType;

public class CategoryToEntityListPanelTest extends AbstractClientTestBase {
    
    CategoryToEntityListPanel panel;

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("CategoryToEntityListPanelTest Tests");
		suite.addTestSuite(CategoryToEntityListPanelTest.class);
		return suite;
	}

	public CategoryToEntityListPanelTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
        GenericCategory category = ObjectMother.createGenericCategory(GenericEntityType.forName("product"));
        panel = new CategoryToEntityListPanel(category);
	}

	protected void tearDown() throws Exception {
        panel = null;
		super.tearDown();
	}
    
    /**
     * Test the basics.
     * @throws Exception
     */
    public void testAll() throws Exception {
        assertNotNull(panel);
        assertNotNull(panel.getEntityAssociations(-1));
        assertFalse(((JButton) ReflectionUtil.getPrivate(panel, "addButton")).isEnabled());
        assertFalse(((JButton) ReflectionUtil.getPrivate(panel, "editButton")).isEnabled());
        assertFalse(((JButton) ReflectionUtil.getPrivate(panel, "removeButton")).isEnabled());        
    }

    
}
