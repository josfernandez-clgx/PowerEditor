package com.mindbox.pe.client.applet.entities.generic;

import static com.mindbox.pe.client.ClientTestObjectMother.createGenericCategory;
import static com.mindbox.pe.client.ClientTestObjectMother.createGenericEntity;
import static com.mindbox.pe.client.ClientTestObjectMother.createMutableTimedAssociationKey;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;

public class GenericEntityToCategoryPanelTest extends AbstractClientTestBase {

	private GenericCategory category;

	/**
	 * tests the proper initialization of panel.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateHappyCase() throws Exception {
		GenericEntityType genericEntityType = entityType1;
		GenericEntityToCategoryPanel panel = new GenericEntityToCategoryPanel(genericEntityType);
		assertNotNull(panel);
		assertNotNull(panel.getCategoryAssociations());
		assertTrue(panel.getCategoryAssociations().size() == 0);
	}

	@Test
	public void testCreateWithNullThrowsException() throws Exception {
		try {
			new GenericEntityToCategoryPanel(null);
			fail("Expecting exception thrown");
		}
		catch (Exception e) {
			// expected
		}
	}

	@Test
	public void testClearFields() throws Exception {
		GenericEntityType genericEntityType = entityType1;
		GenericEntityToCategoryPanel panel = new GenericEntityToCategoryPanel(genericEntityType);

		GenericEntity entity = createGenericEntity(genericEntityType);
		entity.addCategoryAssociation(createMutableTimedAssociationKey());
		panel.setEntity(entity);
		assertNotNull(panel.getCategoryAssociations());
		assertTrue(panel.getCategoryAssociations().size() == 1);

		panel.clearFields();
		assertTrue(panel.getCategoryAssociations().size() == 0);
	}

	@Test
	public void testSetEntityWithNullClearFields() throws Exception {
		GenericEntityType genericEntityType = entityType1;
		GenericEntityToCategoryPanel panel = new GenericEntityToCategoryPanel(genericEntityType);

		GenericEntity entity = createGenericEntity(genericEntityType);
		entity.addCategoryAssociation(createMutableTimedAssociationKey());
		panel.setEntity(entity);
		assertNotNull(panel.getCategoryAssociations());
		assertTrue(panel.getCategoryAssociations().size() == 1);

		panel.setEntity(null);
		assertTrue(panel.getCategoryAssociations().size() == 0);
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		category = createGenericCategory(entityType1);
		EntityModelCacheFactory.getInstance().addGenericCategory(category);
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
		EntityModelCacheFactory.getInstance().removeGenericCategory(category);
		Map<?, ?> map = ReflectionUtil.getPrivate(EntityModelCacheFactory.getInstance(), "cachedGenericCategoryTreeModelMap", Map.class);
		map.clear();
	}
}
