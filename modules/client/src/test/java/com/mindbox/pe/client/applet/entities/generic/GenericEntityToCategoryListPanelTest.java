package com.mindbox.pe.client.applet.entities.generic;

import static com.mindbox.pe.client.ClientTestObjectMother.createGenericEntity;
import static com.mindbox.pe.client.ClientTestObjectMother.createMutableTimedAssociationKey;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import javax.swing.JButton;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.GenericEntity;

public class GenericEntityToCategoryListPanelTest extends AbstractClientTestBase {

	private GenericEntityToCategoryListPanel panel;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		panel = new GenericEntityToCategoryListPanel(entityType1);
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
		panel = null;
		Map<?, ?> map = ReflectionUtil.getPrivate(EntityModelCacheFactory.getInstance(), "cachedGenericCategoryTreeModelMap", Map.class);
		map.clear();
	}

	/**
	 * Test basic intialization with no entity.
	 * @throws Exception
	 */
	@Test
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
	@Test
	public void testSetEntity() throws Exception {
		assertNotNull(panel);
		assertNotNull(panel.getCategoryAssociations());
		assertTrue(panel.getCategoryAssociations().size() == 0);

		GenericEntity entity = createGenericEntity(entityType1);
		entity.addCategoryAssociation(createMutableTimedAssociationKey());
		panel.setEntity(entity);

		assertTrue(((JButton) ReflectionUtil.getPrivate(panel, "newButton")).isEnabled());
		assertFalse(((JButton) ReflectionUtil.getPrivate(panel, "editButton")).isEnabled());
		assertFalse(((JButton) ReflectionUtil.getPrivate(panel, "removeButton")).isEnabled());

		assertTrue(panel.getCategoryAssociations().size() == 1);
	}

}
