package com.mindbox.pe.client.applet.entities.generic;

import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;

public class GenericEntityToCategoryPanelTest extends AbstractClientTestBase {

	private GenericCategory category;

	public GenericEntityToCategoryPanelTest(String name) {
		super(name);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GenericEntityToCategoryPanelTest Tests");
		suite.addTestSuite(GenericEntityToCategoryPanelTest.class);

		return suite;
	}

	/**
	 * tests the proper initialization of panel.
	 * 
	 * @throws Exception
	 */
	public void testCreateHappyCase() throws Exception {
		GenericEntityType genericEntityType = GenericEntityType.forName("product");
		GenericEntityToCategoryPanel panel = new GenericEntityToCategoryPanel(genericEntityType);
		assertNotNull(panel);
		assertNotNull(panel.getCategoryAssociations());
		assertTrue(panel.getCategoryAssociations().size() == 0);
	}

	public void testCreateWithNullThrowsException() throws Exception {
		try {
			new GenericEntityToCategoryPanel(null);
			fail("Expecting exception thrown");
		}
		catch (Exception e) {
			// expected
		}
	}

	public void testClearFields() throws Exception {
		GenericEntityType genericEntityType = GenericEntityType.forName("product");
		GenericEntityToCategoryPanel panel = new GenericEntityToCategoryPanel(genericEntityType);

		GenericEntity entity = ObjectMother.createGenericEntity(genericEntityType);
		entity.addCategoryAssociation(ObjectMother.createMutableTimedAssociationKey());
		panel.setEntity(entity);
		assertNotNull(panel.getCategoryAssociations());
		assertTrue(panel.getCategoryAssociations().size() == 1);

		panel.clearFields();
		assertTrue(panel.getCategoryAssociations().size() == 0);
	}

	public void testSetEntityWithNullClearFields() throws Exception {
		GenericEntityType genericEntityType = GenericEntityType.forName("product");
		GenericEntityToCategoryPanel panel = new GenericEntityToCategoryPanel(genericEntityType);

		GenericEntity entity = ObjectMother.createGenericEntity(genericEntityType);
		entity.addCategoryAssociation(ObjectMother.createMutableTimedAssociationKey());
		panel.setEntity(entity);
		assertNotNull(panel.getCategoryAssociations());
		assertTrue(panel.getCategoryAssociations().size() == 1);

		panel.setEntity(null);
		assertTrue(panel.getCategoryAssociations().size() == 0);
	}

	protected void setUp() throws Exception {
		super.setUp();
		category = ObjectMother.createGenericCategory(GenericEntityType.forName("product"));
		EntityModelCacheFactory.getInstance().addGenericCategory(category);
	}

	@SuppressWarnings("unchecked")
	protected void tearDown() throws Exception {
		super.tearDown();
		EntityModelCacheFactory.getInstance().removeGenericCategory(category);
		Map map = ReflectionUtil.getPrivate(EntityModelCacheFactory.getInstance(), "cachedGenericCategoryTreeModelMap", Map.class);
		map.clear();
	}
}
