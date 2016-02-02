package com.mindbox.pe.client.applet.entities.generic.category;

import javax.swing.JButton;
import javax.swing.JDialog;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.GenericCategory;

public class GenericCategoryEditDialogTest extends AbstractClientTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GenericCategoryEditDialogTest Tests");
		suite.addTestSuite(GenericCategoryEditDialogTest.class);
		return suite;
	}

	public GenericCategoryEditDialogTest(String name) {
		super(name);
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testInitWithRootCategoryDisablesNewParentCategoryField() throws Exception {
		GenericCategory category = new GenericCategory(100, "cate",
				ClientUtil.getEntityConfiguration().getCategoryTypeDefinitions()[0].getTypeID());

		Object instance = ReflectionUtil.createInstance(
				"com.mindbox.pe.client.applet.entities.generic.category.GenericCategoryEditDialog",
				new Class[] { JDialog.class, GenericCategory.class },
				new Object[] { new JDialog(), category });

        CategoryToCategoryListPanel panel = (CategoryToCategoryListPanel) 
            ReflectionUtil.getPrivate(instance, "categoryAssociationsPanel");
        assertFalse(((JButton) ReflectionUtil.getPrivate(panel, "newButton")).isEnabled());
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
