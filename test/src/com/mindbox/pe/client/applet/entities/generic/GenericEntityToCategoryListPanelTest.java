package com.mindbox.pe.client.applet.entities.generic;

import java.util.Map;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;

import junit.framework.Test;
import junit.framework.TestSuite;

import javax.swing.JButton;

public class GenericEntityToCategoryListPanelTest extends AbstractClientTestBase {
	GenericEntityToCategoryListPanel panel;

	public GenericEntityToCategoryListPanelTest(String name) {
		super(name);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GenericEntityToCategoryListPanel Tests");
		suite.addTestSuite(GenericEntityToCategoryListPanelTest.class);

		return suite;
	}

	protected void setUp() throws Exception {
		super.setUp();
		panel = new GenericEntityToCategoryListPanel(GenericEntityType.forName("product"));
	}

	@SuppressWarnings("unchecked")
	protected void tearDown() throws Exception {
		super.tearDown();
		panel = null;
		Map map = ReflectionUtil.getPrivate(EntityModelCacheFactory.getInstance(), "cachedGenericCategoryTreeModelMap", Map.class);
		map.clear();
	}

	/**
	 * Test basic intialization with no entity.
	 * @throws Exception
	 */
	public void testInitializationWithNoEntity() throws Exception {
		assertNotNull(panel);
		assertNotNull(panel.getCategoryAssociations());
		assertFalse(((JButton) ReflectionUtil.getPrivate(panel, "newButton")).isEnabled());
		assertFalse(((JButton) ReflectionUtil.getPrivate(panel, "editButton")).isEnabled());
		assertFalse(((JButton) ReflectionUtil.getPrivate(panel, "removeButton")).isEnabled());
	}

	/**
	 * Test set entity.
	 * @throws Exception
	 */
	public void testSetEntity() throws Exception {
		assertNotNull(panel);
		assertNotNull(panel.getCategoryAssociations());
		assertTrue(panel.getCategoryAssociations().size() == 0);

		GenericEntity entity = ObjectMother.createGenericEntity(GenericEntityType.forName("product"));
		entity.addCategoryAssociation(ObjectMother.createMutableTimedAssociationKey());
		panel.setEntity(entity);

		assertTrue(((JButton) ReflectionUtil.getPrivate(panel, "newButton")).isEnabled());
		assertFalse(((JButton) ReflectionUtil.getPrivate(panel, "editButton")).isEnabled());
		assertFalse(((JButton) ReflectionUtil.getPrivate(panel, "removeButton")).isEnabled());

		assertTrue(panel.getCategoryAssociations().size() == 1);
	}

}
