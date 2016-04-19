package com.mindbox.pe.client.applet.entities.generic.category;

import static org.junit.Assert.assertFalse;

import javax.swing.JButton;
import javax.swing.JDialog;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.GenericCategory;

public class GenericCategoryEditDialogTest extends AbstractClientTestBase {

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInitWithRootCategoryDisablesNewParentCategoryField() throws Exception {
		GenericCategory category = new GenericCategory(100, "category", 1);

		Object instance = ReflectionUtil.createInstance(
				"com.mindbox.pe.client.applet.entities.generic.category.GenericCategoryEditDialog",
				new Class[] { JDialog.class, GenericCategory.class },
				new Object[] { new JDialog(), category });

		CategoryToCategoryListPanel panel = (CategoryToCategoryListPanel) ReflectionUtil.getPrivate(instance, "categoryAssociationsPanel");
		assertFalse(((JButton) ReflectionUtil.getPrivate(panel, "newButton")).isEnabled());
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
