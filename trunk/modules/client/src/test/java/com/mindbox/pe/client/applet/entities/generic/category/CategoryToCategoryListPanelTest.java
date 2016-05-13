package com.mindbox.pe.client.applet.entities.generic.category;

import static com.mindbox.pe.client.ClientTestObjectMother.createGenericCategory;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import javax.swing.JButton;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.GenericCategory;

public class CategoryToCategoryListPanelTest extends AbstractClientTestBase {

	private CategoryToCategoryListPanel panel;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		GenericCategory category = createGenericCategory(entityType1);
		panel = new CategoryToCategoryListPanel(category);
	}

	@After
	public void tearDown() throws Exception {
		panel = null;
		super.tearDown();
	}

	/**
	 * Test the basics.
	 * @throws Exception
	 */
	@Test
	public void testAll() throws Exception {
		assertNotNull(panel);
		assertNotNull(panel.getParentCategoryAssociations());
		assertFalse(((JButton) ReflectionUtil.getPrivate(panel, "newButton")).isEnabled());
		assertFalse(((JButton) ReflectionUtil.getPrivate(panel, "editButton")).isEnabled());
		assertFalse(((JButton) ReflectionUtil.getPrivate(panel, "removeButton")).isEnabled());
	}


}
