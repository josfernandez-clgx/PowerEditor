package com.mindbox.pe.server.bizlogic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.filter.AllGenericCategorySearchFilter;
import com.mindbox.pe.model.filter.GenericEntityByCategoryFilter;
import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.cache.EntityManager;

public class SearchCoordinatorTest extends AbstractTestWithTestConfig {

	private static final String USER_NAME = "demo";

	private GenericEntityType entityType;
	private int categoryType;
	private GenericEntity entity1, entity2, entity3;

	public void setUp() throws Exception {
		super.setUp();
		config.initServer("src/test/config/PowerEditorConfiguration-NoProgram.xml");

		entityType = GenericEntityType.forName("product");
		categoryType = entityType.getCategoryType();

		EntityManager.getInstance().startLoading();
		EntityManager.getInstance().addGenericEntity(1, entityType.getID(), "entity1", -1, new HashMap<String, Object>());
		EntityManager.getInstance().addGenericEntity(2, entityType.getID(), "entity2", 1, new HashMap<String, Object>());
		EntityManager.getInstance().addGenericEntity(3, entityType.getID(), "entity3", 1, new HashMap<String, Object>());
		entity1 = EntityManager.getInstance().getEntity(entityType, 1);
		entity2 = EntityManager.getInstance().getEntity(entityType, 2);
		entity3 = EntityManager.getInstance().getEntity(entityType, 3);

		EntityManager.getInstance().addGenericEntityCategory(categoryType, 1, "Root Category");
		EntityManager.getInstance().addGenericEntityCategory(categoryType, 100, "Category 100");
		EntityManager.getInstance().addParentAssociation(categoryType, 100, 1, -1, -1);
		EntityManager.getInstance().addGenericEntityCategory(categoryType, 200, "Category 200");
		EntityManager.getInstance().addParentAssociation(categoryType, 200, 1, -1, -1);
		EntityManager.getInstance().addGenericEntityCategory(categoryType, 220, "Category 200-220");
		EntityManager.getInstance().addParentAssociation(categoryType, 220, 200, -1, -1);

		EntityManager.getInstance().addGenericEntityToCategory(100, categoryType, 1, entityType.getID(), -1, -1);
		EntityManager.getInstance().addGenericEntityToCategory(220, categoryType, 2, entityType.getID(), -1, -1);
		EntityManager.getInstance().addGenericEntityToCategory(220, categoryType, 3, entityType.getID(), -1, -1);

		EntityManager.getInstance().addGenericEntityCategory(30, 1000, "Test Channel Category");
		EntityManager.getInstance().addGenericEntityCategory(40, 1000, "Test Investor Category");

		// finishLoading accesses DB if an entity has no categories; make sure each entity has at least one category
		EntityManager.getInstance().finishLoading();
	}

	public void tearDown() throws Exception {
		EntityManager.getInstance().startLoading();
		entityType = null;
		super.tearDown();
	}

	@Test
	public void testProcessWithAbstractGenericCategorySearchFilterReturnsAllCategorysNotJustRoot() throws Exception {
		AllGenericCategorySearchFilter filter = new AllGenericCategorySearchFilter(categoryType, false);
		List<GenericCategory> list = SearchCooridinator.getInstance().process(filter, USER_NAME);
		assertEquals(EntityManager.getInstance().getAllCategories(categoryType).size(), list.size());
	}

	@Test
	public void testProcessWithGenericEntityByCategoryFilterNoRetulsReturnsEmpty() throws Exception {
		GenericEntityByCategoryFilter filter = new GenericEntityByCategoryFilter(entityType, new int[] { 300 }, new Date(), false);
		List<GenericEntity> list = SearchCooridinator.getInstance().process(filter, USER_NAME);
		assertNotNull(list);
		assertEquals(0, list.size());
	}

	@Test
	public void testProcessWithGenericEntityByCategoryFilterNullDateResultsEmpty() throws Exception {
		GenericEntityByCategoryFilter filter = new GenericEntityByCategoryFilter(entityType, new int[] { 300 }, null, false);
		List<GenericEntity> list = SearchCooridinator.getInstance().process(filter, USER_NAME);
		assertNotNull(list);
		assertEquals(0, list.size());
	}

	@Test
	public void testProcessWithGenericEntityByCategoryFilterReturnsGenericEntityAndDecendents() throws Exception {
		GenericEntityByCategoryFilter filter = new GenericEntityByCategoryFilter(entityType, new int[] { 200 }, new Date(), true);
		List<GenericEntity> list = SearchCooridinator.getInstance().process(filter, USER_NAME);
		assertEquals(2, list.size());
		assertFalse(list.contains(entity1));
		assertTrue(list.contains(entity2));
		assertTrue(list.contains(entity3));
	}

	@Test
	public void testProcessWithGenericEntityByCategoryFilterReturnsOnlyDirectGenericEntityChild() throws Exception {
		GenericEntityByCategoryFilter filter = new GenericEntityByCategoryFilter(entityType, new int[] { 200 }, new Date(), false);
		List<GenericEntity> list = SearchCooridinator.getInstance().process(filter, USER_NAME);
		assertEquals(0, list.size());
	}

}
