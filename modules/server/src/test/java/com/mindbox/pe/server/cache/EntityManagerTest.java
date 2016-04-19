package com.mindbox.pe.server.cache;

import static com.mindbox.pe.server.ServerTestObjectMother.createDateSynonym;
import static com.mindbox.pe.server.ServerTestObjectMother.createMutableTimedAssociationKey;
import static com.mindbox.pe.server.ServerTestObjectMother.createParameterGrid;
import static com.mindbox.pe.unittest.UnitTestHelper.assertDateEquals;
import static com.mindbox.pe.unittest.UnitTestHelper.assertNotEquals;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.ContextContainer;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.model.GenericEntityIdentity;

public class EntityManagerTest extends AbstractTestWithTestConfig {

	private GenericEntityType entityType;
	private int categoryType;
	private GenericEntity entity1;

	public void setUp() throws Exception {
		super.setUp();
		config.initServer("src/test/config/PowerEditorConfiguration-NoProgram.xml");

		entityType = GenericEntityType.forName("product");
		categoryType = entityType.getCategoryType();

		EntityManager.getInstance().startLoading();
		EntityManager.getInstance().addGenericEntity(1, entityType.getID(), "entity1", -1, new HashMap<String, Object>());
		EntityManager.getInstance().addGenericEntity(2, entityType.getID(), "entity2", 1, new HashMap<String, Object>());
		EntityManager.getInstance().addGenericEntity(3, entityType.getID(), "entity3", 1, new HashMap<String, Object>());
		EntityManager.getInstance().addGenericEntity(4, entityType.getID(), "entity4", 2, new HashMap<String, Object>());
		entity1 = EntityManager.getInstance().getEntity(entityType, 1);

		EntityManager.getInstance().addGenericEntityCategory(categoryType, 1, "Root Category");
		EntityManager.getInstance().addGenericEntityCategory(categoryType, 100, "Category 100");
		EntityManager.getInstance().addParentAssociation(categoryType, 100, 1, -1, -1);
		EntityManager.getInstance().addGenericEntityCategory(categoryType, 111, "Category 111");
		EntityManager.getInstance().addParentAssociation(categoryType, 111, 100, -1, -1);
		EntityManager.getInstance().addGenericEntityCategory(categoryType, 200, "Category 200");
		EntityManager.getInstance().addParentAssociation(categoryType, 200, 1, -1, -1);
		EntityManager.getInstance().addGenericEntityCategory(categoryType, 220, "Category 200-220");
		EntityManager.getInstance().addParentAssociation(categoryType, 220, 200, -1, -1);
		EntityManager.getInstance().addGenericEntityCategory(categoryType, 222, "Category 200-222");
		EntityManager.getInstance().addParentAssociation(categoryType, 222, 200, -1, -1);

		EntityManager.getInstance().addGenericEntityToCategory(100, categoryType, 1, entityType.getID(), -1, -1);
		EntityManager.getInstance().addGenericEntityToCategory(200, categoryType, 1, entityType.getID(), -1, -1);
		EntityManager.getInstance().addGenericEntityToCategory(220, categoryType, 2, entityType.getID(), -1, -1);
		EntityManager.getInstance().addGenericEntityToCategory(220, categoryType, 3, entityType.getID(), -1, -1);
		EntityManager.getInstance().addGenericEntityToCategory(222, categoryType, 4, entityType.getID(), -1, -1);

		EntityManager.getInstance().addGenericEntityCategory(30, 1000, "Test Channel Category");
		EntityManager.getInstance().addGenericEntityCategory(40, 1000, "Test Investor Category");
		// finishLoading accesses DB if an entity has no categories; make sure each entity has at
		// least one category
		EntityManager.getInstance().finishLoading();
	}

	public void tearDown() throws Exception {
		EntityManager.getInstance().startLoading();
		DateSynonymManager.getInstance().startLoading();
		entityType = null;
		super.tearDown();
	}

	@Test
	public void testIsInUseWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(EntityManager.getInstance(), "isInUse", new Class[] { DateSynonym.class });
	}

	@Test
	public void testIsInUsePositiveCaseForCompatibilityData() throws Exception {
		DateSynonym dateSynonym = createDateSynonym();
		GenericEntityCompatibilityData compatibilityData = new GenericEntityCompatibilityData(entityType, 1, entityType, 2, dateSynonym, null);
		EntityManager.getInstance().insertEntityCompatibility(compatibilityData);
		assertTrue(EntityManager.getInstance().isInUse(dateSynonym));

		dateSynonym = createDateSynonym();
		compatibilityData = new GenericEntityCompatibilityData(entityType, 1, entityType, 2, null, dateSynonym);
		EntityManager.getInstance().insertEntityCompatibility(compatibilityData);
		assertTrue(EntityManager.getInstance().isInUse(dateSynonym));
	}

	@Test
	public void testIsInUsePositiveCaseForParentChildCategoryAssocation() throws Exception {
		DateSynonym dateSynonym = createDateSynonym();
		GenericCategory category = EntityManager.getInstance().getGenericCategory(categoryType, 200);
		category.addChildAssociation(new DefaultMutableTimedAssociationKey(111, dateSynonym, null));
		assertTrue(EntityManager.getInstance().isInUse(dateSynonym));
	}

	@Test
	public void testIsInUsePositiveCaseForEntityCategoryAssociation() throws Exception {
		DateSynonym dateSynonym = createDateSynonym();
		DateSynonymManager.getInstance().insert(dateSynonym);
		EntityManager.getInstance().addGenericEntityToCategory(100, categoryType, 3, entityType.getID(), dateSynonym.getID(), -1);
		assertTrue(EntityManager.getInstance().isInUse(dateSynonym));

		dateSynonym = createDateSynonym();
		DateSynonymManager.getInstance().insert(dateSynonym);
		EntityManager.getInstance().addGenericEntityToCategory(100, categoryType, 4, entityType.getID(), -1, dateSynonym.getID());
		assertTrue(EntityManager.getInstance().isInUse(dateSynonym));
	}

	@Test
	public void testIsInUseNegativeCaseWithEmptyEntityManager() throws Exception {
		DateSynonym dateSynonym = createDateSynonym();
		assertFalse(EntityManager.getInstance().isInUse(dateSynonym));
	}

	@Test
	public void testIsInUseNegativeCase() throws Exception {
		DateSynonym dateSynonym = createDateSynonym();
		DateSynonymManager.getInstance().insert(dateSynonym);
		GenericEntityCompatibilityData compatibilityData = new GenericEntityCompatibilityData(entityType, 1, entityType, 2, dateSynonym, null);
		EntityManager.getInstance().insertEntityCompatibility(compatibilityData);
		GenericCategory category = EntityManager.getInstance().getGenericCategory(categoryType, 200);
		category.addChildAssociation(new DefaultMutableTimedAssociationKey(111, dateSynonym, null));
		EntityManager.getInstance().addGenericEntityToCategory(100, categoryType, 3, entityType.getID(), dateSynonym.getID(), -1);

		assertFalse(EntityManager.getInstance().isInUse(createDateSynonym()));
	}

	@Test
	public void testAddGenericEntityToCategoryWithInvalidIDThrowsIllegalArgumentException() throws Exception {
		assertThrowsException(EntityManager.getInstance(), "addParentAssociation", new Class[] { int.class, int.class, MutableTimedAssociationKey.class }, new Object[] {
				new Integer(categoryType),
				new Integer(-1),
				createMutableTimedAssociationKey() }, IllegalArgumentException.class);
	}

	@Test
	public void testAddGenericEntityToCategoryHappyCase() throws Exception {
		assertEquals(200, EntityManager.getInstance().getGenericCategory(categoryType, 222).getParentID(new Date()));
		assertEquals(2, EntityManager.getInstance().getGenericCategory(categoryType, 200).getChildIDs(new Date()).size());
		assertTrue(EntityManager.getInstance().getGenericCategory(categoryType, 200).getChildIDs(new Date()).contains(220));
		assertTrue(EntityManager.getInstance().getGenericCategory(categoryType, 200).getChildIDs(new Date()).contains(222));
		GenericEntityIdentity[] identities = EntityManager.getInstance().getGenericEntitiesInCategorySetAsOfWithDescendents(categoryType, new int[] { 220 }, new Date(), false);
		assertEquals(2, identities.length);
	}

	@Test
	public void testGetAllCategoriesWithInvalidCategoryTypeReturnsEmptyList() throws Exception {
		assertTrue(EntityManager.getInstance().getAllCategories(categoryType + 2).isEmpty());
	}

	@Test
	public void testGetAllCategoriesHappyCase() throws Exception {
		List<GenericCategory> list = EntityManager.getInstance().getAllCategories(categoryType);
		assertEquals(6, list.size());
		assertThrowsException(list, "add", new Class[] { Object.class }, new Object[] { "str" }, UnsupportedOperationException.class);
	}

	@Test
	public void testGetAllCategoriesByNameWithNullNameThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(EntityManager.getInstance(), "getAllGenericCategoriesByName", new Class<?>[] { int.class, String.class }, new Object[] { 0, null });
	}

	@Test
	public void testGetAllCategoriesByNameHappyCase() throws Exception {
		List<GenericCategory> list = EntityManager.getInstance().getAllGenericCategoriesByName(categoryType, "Category 111");
		assertEquals(1, list.size());
		assertEquals("Category 111", list.get(0).getName());
	}

	@Test
	public void testGetDateSynonymsForChangesInCategoryRelationshipsWithNoDatesReturnsEmptySet() throws Exception {
		assertTrue(EntityManager.getInstance().getDateSynonymsForChangesInCategoryRelationships(categoryType).isEmpty());
	}

	@Test
	public void testGetDateSynonymsForChangesInCategoryRelationshipsHappyCase() throws Exception {
		DateSynonym ds = createDateSynonym();
		GenericCategory category = EntityManager.getInstance().getGenericCategory(categoryType, 222);
		((MutableTimedAssociationKey) category.getParentKeyIterator().next()).setExpirationDate(ds);
		category.addParentKey(new DefaultMutableTimedAssociationKey(110, ds, null));
		category = EntityManager.getInstance().getGenericCategory(categoryType, 111);
		((MutableTimedAssociationKey) category.getParentKeyIterator().next()).setEffectiveDate(ds);
		Set<DateSynonym> set = EntityManager.getInstance().getDateSynonymsForChangesInCategoryRelationships(categoryType);
		assertEquals(1, set.size());
		assertTrue(set.contains(ds));

		ds = createDateSynonym();
		category = EntityManager.getInstance().getGenericCategory(categoryType, 220);
		((MutableTimedAssociationKey) category.getParentKeyIterator().next()).setExpirationDate(ds);
		set = EntityManager.getInstance().getDateSynonymsForChangesInCategoryRelationships(categoryType);
		assertEquals(2, set.size());
		assertTrue(set.contains(ds));
	}

	@Test
	public void testGroupForContextMatchCategoryArgWithInvalidCategoryTypeThrowsIllegalArgumentException() throws Exception {
		try {
			EntityManager.getInstance().groupForContextMatchCategoryArgAsOf(-1, new int[] { 1 }, new Date());
			fail("Expected IllegalArgumentException not thrown");
		}
		catch (IllegalArgumentException ex) {
			// expected
		}
	}

	@Test
	public void testGroupForContextMatchCategoryArgWithInvalidCategoryIDThrowsIllegalArgumentException() throws Exception {
		try {
			EntityManager.getInstance().groupForContextMatchCategoryArgAsOf(categoryType, new int[] { 9999 }, new Date());
			fail("Expected IllegalArgumentException not thrown");
		}
		catch (IllegalArgumentException ex) {
			// expected
		}
	}

	@Test
	public void testGroupForContextMatchCategoryArgWithNullCategoryIDsThrowsNullPointerException() throws Exception {
		try {
			EntityManager.getInstance().groupForContextMatchCategoryArgAsOf(categoryType, null, new Date());
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	@Test
	public void testGroupForContextMatchCategoryArgWithEmptyCategoryIDsRetunsEmptyList() throws Exception {
		assertTrue(EntityManager.getInstance().groupForContextMatchCategoryArgAsOf(categoryType, new int[0], new Date()).isEmpty());
	}

	@Test
	public void testGroupForContextMatchCategoryArgWithOneIDReturnsListWithOneCollection() throws Exception {
		List<Collection<Integer>> list = EntityManager.getInstance().groupForContextMatchCategoryArgAsOf(categoryType, new int[] { 1 }, new Date());
		assertEquals(1, list.size());
		assertTrue(Collection.class.isInstance(list.get(0)));
	}

	@Test
	public void testGroupForContextMatchCategoryArgSkipsOrphanCategories() throws Exception {
		DateSynonym ds = createDateSynonym();
		GenericCategory category = EntityManager.getInstance().getGenericCategory(categoryType, 222);
		((MutableTimedAssociationKey) category.getParentKeyIterator().next()).setExpirationDate(ds);

		List<Collection<Integer>> list = EntityManager.getInstance().groupForContextMatchCategoryArgAsOf(categoryType, new int[] { 220, 222 }, ds.getDate());
		assertEquals(1, list.size());
		assertTrue(list.get(0).contains(new Integer(220)));
	}

	@Test
	public void testGroupForContextMatchCategoryArgHappyCaseForSingleList() throws Exception {
		List<Collection<Integer>> list = EntityManager.getInstance().groupForContextMatchCategoryArgAsOf(categoryType, new int[] { 220, 222 }, new Date());
		assertEquals(1, list.size());
		assertEquals(2, list.get(0).size());

		list = EntityManager.getInstance().groupForContextMatchCategoryArgAsOf(categoryType, new int[] { 200, 220, 222 }, new Date());
		assertEquals(1, list.size());
		assertEquals(3, list.get(0).size());
	}

	@Test
	public void testGroupForContextMatchCategoryArgHappyCaseForFutureCategory() throws Exception {
		DateSynonym ds = createDateSynonym();
		ds.setDate(new Date(System.currentTimeMillis() + 10000));
		DateSynonymManager.getInstance().insert(ds);

		EntityManager.getInstance().addGenericEntityCategory(categoryType, 2000, "FutureCategory");
		EntityManager.getInstance().addParentAssociation(categoryType, 2000, 100, ds.getId(), -1);

		List<Collection<Integer>> list = EntityManager.getInstance().groupForContextMatchCategoryArgAsOf(categoryType, new int[] { 2000 }, new Date(System.currentTimeMillis() - 10000));
		assertEquals(0, list.size());
	}

	@Test
	public void testGroupForContextMatchCategoryArgHappyCaseForMultipleList() throws Exception {
		List<Collection<Integer>> list = EntityManager.getInstance().groupForContextMatchCategoryArgAsOf(categoryType, new int[] { 111, 222 }, new Date());
		assertEquals(2, list.size());
		assertEquals(1, list.get(0).size());
		assertEquals(1, list.get(1).size());
		assertNotEquals(list.get(0).iterator().next(), list.get(1).iterator().next());

		EntityManager.getInstance().addGenericEntityCategory(categoryType, 555, "Category 555");
		EntityManager.getInstance().addParentAssociation(categoryType, 555, 1, -1, -1);
		EntityManager.getInstance().addGenericEntityCategory(categoryType, 556, "Category 556");
		EntityManager.getInstance().addParentAssociation(categoryType, 556, 555, -1, -1);

		EntityManager.getInstance().finishLoading();
		list = EntityManager.getInstance().groupForContextMatchCategoryArgAsOf(categoryType, new int[] { 100, 200, 555, 556 }, new Date());
		assertEquals(3, list.size());
		for (Iterator<Collection<Integer>> iter = list.iterator(); iter.hasNext();) {
			Collection<Integer> element = iter.next();
			assertTrue(element.contains(new Integer(100)) && element.size() == 1 || element.contains(new Integer(200)) && element.size() == 1 || element.size() == 2);
		}
	}

	@Test
	public void testFinishLoadingUpdatesParentChildLinksForCategory() throws Exception {
		// finishLoading is called in {@link #setUp}
		GenericCategory category1 = EntityManager.getInstance().getGenericCategory(categoryType, 1);
		GenericCategory category100 = EntityManager.getInstance().getGenericCategory(categoryType, 100);
		GenericCategory category200 = EntityManager.getInstance().getGenericCategory(categoryType, 200);

		List<Integer> list = category1.getChildIDs(new Date());
		assertEquals(2, list.size());
		assertTrue(list.contains(new Integer(category100.getID())));
		assertTrue(list.contains(new Integer(category200.getID())));

		list = category100.getChildIDs(new Date());
		assertEquals(1, list.size());

		list = category200.getChildIDs(new Date());
		assertEquals(2, list.size());
		assertTrue(list.contains(new Integer(220)));
		assertTrue(list.contains(new Integer(222)));
	}

	@Test
	public void testAddGenericEntityCategoryWithTrueBuildsParentChildLinks() throws Exception {
		EntityManager.getInstance().addGenericEntityCategory(categoryType, 400, "Category 400");
		EntityManager.getInstance().addParentAssociation(categoryType, 400, 100, -1, -1);

		GenericCategory category100 = EntityManager.getInstance().getGenericCategory(categoryType, 100);
		GenericCategory category400 = EntityManager.getInstance().getGenericCategory(categoryType, 400);

		List<Integer> list = category100.getChildIDs(new Date());
		assertEquals(2, list.size());
		assertTrue(list.contains(new Integer(category400.getID())));
	}

	@Test
	public void testRemoveGenericCategoryUpdatesParentChildLinks() throws Exception {
		EntityManager.getInstance().removeCategory(categoryType, 222);
		// check category is not in the cache
		assertNull(EntityManager.getInstance().getGenericCategory(categoryType, 222));

		// check category is removed from parent-child links
		GenericCategory category200 = EntityManager.getInstance().getGenericCategory(categoryType, 200);

		List<Integer> list = category200.getChildIDs(new Date());
		assertEquals(1, list.size());
		assertTrue(list.contains(new Integer(220)));
	}

	@Test
	public void testRemoveGenericEntityRemovesItFromCategoryEntityMap() throws Exception {
		EntityManager.getInstance().removeEntity(entityType, 2);
		// checks entity is not in the cache
		assertNull(EntityManager.getInstance().getEntity(entityType, 2));

		// check entity is removed from category-entity map
		GenericEntityIdentity[] identities = EntityManager.getInstance().getGenericEntitiesInCategorySetAsOfWithDescendents(entityType.getCategoryType(), new int[] { 220 }, new Date(), false);
		assertEquals(1, identities.length);
		assertEquals(3, identities[0].getEntityID());
	}

	@Test
	public void testRemoveGenericEntityRemovesItFromCompatibilityMap() throws Exception {
		EntityManager.getInstance().addEntityCompatibility(entityType.getID(), 1, entityType.getID(), 2, null, null);
		EntityManager.getInstance().addEntityCompatibility(entityType.getID(), 3, entityType.getID(), 1, null, null);
		EntityManager.getInstance().addEntityCompatibility(entityType.getID(), 3, entityType.getID(), 4, null, null);
		List<GenericEntityCompatibilityData> list = EntityManager.getInstance().getAllCrossCompatibilities(entityType, entityType);
		assertEquals(3, list.size());
		EntityManager.getInstance().removeEntity(entityType, 1);

		list = EntityManager.getInstance().getAllCrossCompatibilities(entityType, entityType);
		assertEquals(1, list.size());
	}

	@Test
	public void testUpdateCacheOnGenericEntityWithNullThrowsNullPointerException() throws Exception {
		try {
			EntityManager.getInstance().updateCache((GenericEntity) null);
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	@Test
	public void testUpdateCacheOnGenericEntityUpdatesCategoryLinks() throws Exception {
		GenericEntityIdentity[] identities = EntityManager.getInstance().getGenericEntitiesInCategorySetAsOfWithDescendents(categoryType, new int[] { 222 }, new Date(), false);
		assertEquals(1, identities.length);

		entity1.addCategoryAssociation(new DefaultMutableTimedAssociationKey(222, null, null));
		EntityManager.getInstance().updateCache(entity1);

		identities = EntityManager.getInstance().getGenericEntitiesInCategorySetAsOfWithDescendents(categoryType, new int[] { 222 }, new Date(), false);
		assertEquals(2, identities.length);
		for (int i = 0; i < identities.length; i++) {
			if (identities[i].getEntityID() != 1 && identities[i].getEntityID() != 4) {
				fail("Id is expected to be 1 or 4, but was " + identities[i].getEntityID());
			}
		}
	}

	@Test
	public void testUpdateCacheOnGenericCategoryUpdatesParentChildLinks() throws Exception {
		GenericCategory category220 = new GenericCategory(220, "Update Category 220", categoryType);
		category220.addParentKey(new DefaultMutableTimedAssociationKey(100, null, null));
		EntityManager.getInstance().updateCache(EntityManager.getInstance().getGenericCategory(categoryType, 220), category220);

		GenericCategory category100 = EntityManager.getInstance().getGenericCategory(categoryType, 100);
		GenericCategory category200 = EntityManager.getInstance().getGenericCategory(categoryType, 200);
		List<Integer> list = category100.getChildIDs(new Date());
		assertEquals(2, list.size());
		assertTrue(list.contains(new Integer(220)));

		list = category200.getChildIDs(new Date());
		assertEquals(1, list.size());
		assertTrue(list.contains(new Integer(222)));
	}

	@Test
	public void testSetGenericEntityPropertiesWithDateObjectForDateProperty() throws Exception {
		String propName = "activation.date";
		Map<String, Object> map = new HashMap<String, Object>();
		Date date = new Date();
		map.put(propName, date);

		EntityManager.setGenericEntityProperties(entity1, map);
		assertEquals(date, entity1.getDateProperty(propName));
	}

	@Test
	public void testSetGenericEntityPropertiesWithStringForDateProperty() throws Exception {
		String propName = "activation.date";
		Map<String, Object> map = new HashMap<String, Object>();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2005);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		map.put(propName, "2005-01-01T00:00:00");

		EntityManager.setGenericEntityProperties(entity1, map);
		assertDateEquals(cal.getTime(), entity1.getDateProperty(propName));
	}

	@Test
	public void testGetGenericEntitiesInCategorySetAsOfWithNullCategoryIDsReturnsEmptyArray() throws Exception {
		GenericEntityIdentity[] results = EntityManager.getInstance().getGenericEntitiesInCategorySetAsOfWithDescendents(categoryType, null, new Date(), false);
		assertNotNull(results);
		assertEquals(0, results.length);
	}

	@Test
	public void testGetGenericEntitiesInCategorySetAsOfWithEmptyCategoryIDsReturnsEmptyArray() throws Exception {
		GenericEntityIdentity[] results = EntityManager.getInstance().getGenericEntitiesInCategorySetAsOfWithDescendents(categoryType, new int[0], new Date(), false);
		assertNotNull(results);
		assertEquals(0, results.length);
	}

	@Test
	public void testGetGenericEntitiesInCategorySetAsOfWithRootCategoryReturnsAllEntities() throws Exception {
		GenericEntityIdentity[] results = EntityManager.getInstance().getGenericEntitiesInCategorySetAsOfWithDescendents(categoryType, new int[] { 1 }, new Date(), false);
		assertEquals(4, results.length);
	}

	@Test
	public void testGetGenericEntitiesInCategorySetAsOfWithLeafCategoryReturnsEntitiesInItOnly() throws Exception {
		GenericEntityIdentity[] results = EntityManager.getInstance().getGenericEntitiesInCategorySetAsOfWithDescendents(categoryType, new int[] { 222 }, new Date(), false);
		assertEquals(1, results.length);
		assertEquals(4, results[0].getEntityID());
	}

	@Test
	public void testGetGenericEntitiesInCategorySetAsOfWithParentCategoryReturnsEntitiesInDescendents() throws Exception {
		GenericEntityIdentity[] results = EntityManager.getInstance().getGenericEntitiesInCategorySetAsOfWithDescendents(categoryType, new int[] { 200 }, new Date(), false);
		assertEquals(4, results.length);

		results = EntityManager.getInstance().getGenericEntitiesInCategorySetAsOfWithDescendents(categoryType, new int[] { 100 }, new Date(), false);
		assertEquals(1, results.length);
	}

	@Test
	public void testGetGenericEntitiesInCategorySetAsOfWithTopLevelCategoriesReturnsIntersection() throws Exception {
		GenericEntityIdentity[] results = EntityManager.getInstance().getGenericEntitiesInCategorySetAsOfWithDescendents(categoryType, new int[] { 100, 200 }, new Date(), false);
		assertEquals(1, results.length);
	}

	@Test
	public void testGetGenericEntitiesInCategorySetAsOfWithSameAncestorCategoriesReturnsUnion() throws Exception {
		GenericEntityIdentity[] results = EntityManager.getInstance().getGenericEntitiesInCategorySetAsOfWithDescendents(categoryType, new int[] { 220, 222 }, new Date(), false);
		assertEquals(3, results.length);
	}

	@Test
	public void testGetGenericEntitiesInCategorySetAsOfWithIgnoreEntityToCategoryDatesHappyCase() throws Exception {
		DateSynonym ds = createDateSynonym();
		DateSynonymManager.getInstance().insert(ds);
		GenericEntity entity = EntityManager.getInstance().getEntity(entityType, 4);
		((MutableTimedAssociationKey) entity.getCategoryAssociations(222).get(0)).setExpirationDate(ds);
		EntityManager.getInstance().addGenericEntityToCategory(100, categoryType, entity.getID(), entityType.getID(), ds.getID(), -1);

		GenericEntityIdentity[] results = EntityManager.getInstance().getGenericEntitiesInCategorySetAsOfWithDescendents(categoryType, new int[] { 100 }, new Date(), true);
		assertEquals(2, results.length);
	}

	@Test
	public void testGetGenericEntitiesInCategorySetAtAnyTimeWithNullIntArrayReturnsZeroLengthArray() throws Exception {
		assertEquals(0, EntityManager.getInstance().getGenericEntitiesInCategorySetAtAnyTime(categoryType, null, false).length);
		assertEquals(0, EntityManager.getInstance().getGenericEntitiesInCategorySetAtAnyTime(categoryType, null, true).length);
	}

	@Test
	public void testGetGenericEntitiesInCategorySetAtAnyTimeHappyCaseWithoutDescendents() throws Exception {
		GenericEntityIdentity[] identities = EntityManager.getInstance().getGenericEntitiesInCategorySetAtAnyTime(categoryType, new int[] { 220 }, false);
		assertEquals(2, identities.length);
	}

	@Test
	public void testGetGenericEntitiesInCategorySetAsOfWithOutDescendantsWithParentCategoryReturnsOnlyEntitiesNotInDescendents() throws Exception {
		GenericEntityIdentity[] results = EntityManager.getInstance().getGenericEntitiesInCategorySetAsOfWithOutDescendents(categoryType, new int[] { 200 }, new Date());
		assertEquals(1, results.length);

		results = EntityManager.getInstance().getGenericEntitiesInCategorySetAsOfWithOutDescendents(categoryType, new int[] { 100 }, new Date());
		assertEquals(1, results.length);
	}

	@Test
	public void testGetGenericEntitiesInCategorySetAtAnyTimeHappyCaseWithDescendents() throws Exception {
		GenericEntityIdentity[] identities = EntityManager.getInstance().getGenericEntitiesInCategorySetAtAnyTime(categoryType, new int[] { 200 }, true);
		assertEquals(4, identities.length);
	}

	@Test
	public void testGetDescendentsWithInvalidParentIDReturnsEmpty() throws Exception {
		List<GenericEntity> list = EntityManager.getInstance().getDescendents(entityType, 111, true);
		assertEquals(0, list.size());
		list = EntityManager.getInstance().getDescendents(entityType, 111, false);
		assertEquals(0, list.size());
	}

	@Test
	public void testGetDescendentsWithNegativeParentIDAndFalseReturnsRootEntities() throws Exception {
		List<GenericEntity> list = EntityManager.getInstance().getDescendents(entityType, -1, false);
		assertEquals(1, list.size());
		assertEquals(entity1, list.get(0));
	}

	@Test
	public void testGetDescendentsWithNegativeParentIDAndTrueReturnsAll() throws Exception {
		List<GenericEntity> list = EntityManager.getInstance().getDescendents(entityType, -1, true);
		assertEquals(4, list.size());
	}

	@Test
	public void testGetDescendentsWithValidParentIDAndFalseReturnsChildren() throws Exception {
		List<GenericEntity> list = EntityManager.getInstance().getDescendents(entityType, 1, false);
		assertEquals(2, list.size());
		list = EntityManager.getInstance().getDescendents(entityType, 2, false);
		assertEquals(1, list.size());
	}

	@Test
	public void testGetDescendentsWithValidParentIDAndTrueReturnsDescendents() throws Exception {
		List<GenericEntity> list = EntityManager.getInstance().getDescendents(entityType, 1, true);
		assertEquals(3, list.size());
		list = EntityManager.getInstance().getDescendents(entityType, 2, true);
		assertEquals(1, list.size());
	}

	@Test
	public void testGetDescendentsWithParentIDOfChildReturnsEmtpy() throws Exception {
		List<GenericEntity> list = EntityManager.getInstance().getDescendents(entityType, 3, true);
		assertEquals(0, list.size());
		list = EntityManager.getInstance().getDescendents(entityType, 4, false);
		assertEquals(0, list.size());
	}

	@Test
	public void testIsDescendentAsOfPostiveCase() throws Exception {
		GenericCategory parent = EntityManager.getInstance().getGenericCategory(categoryType, 1);
		GenericCategory child = EntityManager.getInstance().getGenericCategory(categoryType, 100);
		assertTrue(EntityManager.getInstance().isDescendentAsOf(child.getId(), parent.getId(), parent.getType(), new Date()));
	}

	@Test
	public void testIsDescendentAsOfNegativeCase() throws Exception {
		DateSynonym ds = createDateSynonym();
		GenericCategory category = EntityManager.getInstance().getGenericCategory(categoryType, 1);
		for (Iterator<MutableTimedAssociationKey> iter = category.getChildrenKeyIterator(); iter.hasNext();) {
			MutableTimedAssociationKey element = iter.next();
			element.setExpirationDate(ds);
		}

		GenericCategory parent = EntityManager.getInstance().getGenericCategory(categoryType, 1);
		GenericCategory child = EntityManager.getInstance().getGenericCategory(categoryType, 100);
		assertFalse(EntityManager.getInstance().isDescendentAsOf(child.getId(), parent.getId(), parent.getType(), new Date(ds.getDate().getTime() + 10)));
	}

	@Test
	public void testIsDescendentAtAnyTimeWithNullArrayThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(EntityManager.getInstance(), "isDescendentAtAnyTime", new Class[] { int.class, int[].class, int.class }, new Object[] {
				new Integer(1),
				null,
				new Integer(categoryType) });
	}

	@Test
	public void testIsDescendentAtAnyTimePositiveCaseForSingleParent() throws Exception {
		assertTrue(EntityManager.getInstance().isDescendentAtAnyTime(100, new int[] { 1 }, categoryType));
	}

	@Test
	public void testIsDescendentAtAnyTimePositiveCaseForMultipleParents() throws Exception {
		DateSynonym ds = createDateSynonym();
		GenericCategory category = EntityManager.getInstance().getGenericCategory(categoryType, 200);
		for (Iterator<MutableTimedAssociationKey> iter = category.getChildrenKeyIterator(); iter.hasNext();) {
			MutableTimedAssociationKey element = iter.next();
			element.setExpirationDate(ds);
		}
		category = EntityManager.getInstance().getGenericCategory(categoryType, 220);
		((MutableTimedAssociationKey) category.getParentKeyIterator().next()).setExpirationDate(ds);
		EntityManager.getInstance().addParentAssociation(categoryType, 220, new DefaultMutableTimedAssociationKey(100, ds, null));

		assertTrue(EntityManager.getInstance().isDescendentAtAnyTime(category.getId(), new int[] { 200, 100 }, categoryType));
	}

	@Test
	public void testIsDescendentAtAnyTimeNegativeCase() throws Exception {
		assertFalse(EntityManager.getInstance().isDescendentAtAnyTime(222, new int[] { 100, 200 }, categoryType));
		assertFalse(EntityManager.getInstance().isDescendentAtAnyTime(100, new int[] { 1, 100 }, categoryType));
	}

	@Test
	public void testIsEntityDescendentOfCategorySetAtAnyTimeWithNullIntArrayThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(EntityManager.getInstance(), "isEntityDescendentOfCategorySetAtAnyTime", new Class[] { int.class, int[].class, int.class }, new Object[] {
				new Integer(1),
				null,
				new Integer(categoryType) });
	}

	@Test
	public void testIsEntityDescendentOfCategorySetAtAnyTimePositiveCaseForSingleCategory() throws Exception {
		assertTrue(EntityManager.getInstance().isEntityDescendentOfCategorySetAtAnyTime(4, new int[] { 200 }, categoryType));
	}

	@Test
	public void testIsEntityDescendentOfCategorySetAtAnyTimePositiveCaseForMultipleCategories() throws Exception {
		DateSynonym ds = createDateSynonym();
		DateSynonymManager.getInstance().insert(ds);
		GenericEntity entity = EntityManager.getInstance().getEntity(entityType, 4);
		((MutableTimedAssociationKey) entity.getCategoryAssociations(222).get(0)).setExpirationDate(ds);
		entity.addCategoryAssociation(new DefaultMutableTimedAssociationKey(100, ds, null));
		EntityManager.getInstance().updateCache(entity);
		assertTrue(EntityManager.getInstance().isEntityDescendentOfCategorySetAtAnyTime(entity.getID(), new int[] { 222, 100 }, categoryType));
	}

	@Test
	public void testIsEntityDescendentOfCategorySetAtAnyTimeNegativeCaseForInvalidCategoryID() throws Exception {
		assertFalse(EntityManager.getInstance().isEntityDescendentOfCategorySetAtAnyTime(4, new int[] { 200 }, categoryType + 1));
	}

	@Test
	public void testIsEntityDescendentOfCategorySetAtAnyTimeNegativeCaseForInvalidEntityID() throws Exception {
		assertFalse(EntityManager.getInstance().isEntityDescendentOfCategorySetAtAnyTime(1, new int[] { 220 }, categoryType));
	}

	@Test
	public void testIsParentCategoryAtAnyTimePositiveCase() {
		DateSynonym ds = createDateSynonym();
		GenericCategory category = EntityManager.getInstance().getGenericCategory(categoryType, 1);
		for (Iterator<MutableTimedAssociationKey> iter = category.getChildrenKeyIterator(); iter.hasNext();) {
			MutableTimedAssociationKey element = iter.next();
			element.setExpirationDate(ds);
		}
		assertTrue(EntityManager.getInstance().isParentCategoryAtAnyTime(1, new int[] { 100, 200 }, categoryType));
	}

	@Test
	public void testIsParentCategoryAtAnyTimeNegativeCase() {
		assertFalse(EntityManager.getInstance().isParentCategoryAtAnyTime(100, new int[] { 1, 200 }, categoryType));
	}

	@Test
	public void testIsParentCategoriesAtAnyTimePositiveCase() {
		DateSynonym ds = createDateSynonym();
		GenericCategory category = EntityManager.getInstance().getGenericCategory(categoryType, 200);
		for (Iterator<MutableTimedAssociationKey> iter = category.getChildrenKeyIterator(); iter.hasNext();) {
			MutableTimedAssociationKey element = iter.next();
			element.setExpirationDate(ds);
		}
		category = EntityManager.getInstance().getGenericCategory(categoryType, 220);
		((MutableTimedAssociationKey) category.getParentKeyIterator().next()).setExpirationDate(ds);

		EntityManager.getInstance().addParentAssociation(categoryType, 220, new DefaultMutableTimedAssociationKey(100, ds, null));
		assertTrue(EntityManager.getInstance().isParentCategoriesAtAnyTime(new int[] { 100, 200 }, new int[] { 220 }, categoryType));
	}

	@Test
	public void testIsParentCategoriesAtAnyTimeNegativeCase() {
		assertFalse(EntityManager.getInstance().isParentCategoriesAtAnyTime(new int[] { 200 }, new int[] { 1 }, categoryType));
	}

	@Test
	public void testFindGenericCategoryUnqualifiedName() {
		GenericCategory cat = EntityManager.getInstance().getGenericCategory(categoryType, 1);
		GenericCategory[] found = EntityManager.getInstance().findGenericCategoryByName(cat.getType(), cat.getName());
		assertNotNull(found);
		assertTrue(found.length == 1);
		assertEquals(cat, found[0]);

		found = EntityManager.getInstance().findGenericCategoryByName(cat.getType(), "junk");
		assertNull(found);
	}

	@Test
	public void testFindGenericCategoryQualifiedNameHappyCase() {
		GenericCategory parent = EntityManager.getInstance().getGenericCategory(categoryType, 1);
		GenericCategory child = EntityManager.getInstance().getGenericCategory(categoryType, 100);
		String fullyQualifiedName = parent.getName() + Constants.CATEGORY_PATH_DELIMITER + child.getName();
		GenericCategory[] found = EntityManager.getInstance().findGenericCategoryByName(child.getType(), fullyQualifiedName);
		assertNotNull(found);
		assertTrue(found.length == 1);
		assertEquals(child, found[0]);
	}

	@Test
	public void testFindGenericCategoryQualifiedNameConsidersAllDates() {
		GenericCategory parent = EntityManager.getInstance().getGenericCategory(categoryType, 1);
		GenericCategory child = EntityManager.getInstance().getGenericCategory(categoryType, 100);

		DateSynonym ds1 = createDateSynonym();
		DateSynonym ds2 = createDateSynonym();
		ds2.setDate(new Date(ds1.getDate().getTime() + 1));
		((MutableTimedAssociationKey) child.getParentKeyIterator().next()).setEffectiveDate(ds1);
		((MutableTimedAssociationKey) child.getParentKeyIterator().next()).setExpirationDate(ds2);

		String fullyQualifiedName = parent.getName() + Constants.CATEGORY_PATH_DELIMITER + child.getName();
		GenericCategory[] found = EntityManager.getInstance().findGenericCategoryByName(child.getType(), fullyQualifiedName);
		assertNotNull(found);
		assertTrue(found.length == 1);
		assertEquals(child, found[0]);
	}

	@Test
	public void testGetAllCategoriesUnmodifiable() throws Exception {
		List<GenericCategory> allCats = EntityManager.getInstance().getAllCategories(categoryType);
		assertFalse(allCats.isEmpty());
		try {
			allCats.add(new GenericCategory(-1, "test", 0));
			fail("Expected " + UnsupportedOperationException.class.getName());
		}
		catch (UnsupportedOperationException e) {
			// pass
		}
	}

	@Test
	public void testGetDateSynonymsForChangesInCategoryToEntityRelationshipsWithNullContextContainerThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(EntityManager.getInstance(), "getDateSynonymsForChangesInCategoryToEntityRelationships", new Class[] {
				ContextContainer.class,
				DateSynonym.class,
				DateSynonym.class }, new Object[] { null, null, null });
	}

	@Test
	public void testGetDateSynonymsForChangesInCategoryToEntityRelationshipsHappyCase() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		DateSynonym ds2 = createDateSynonym();
		DateSynonym ds3 = createDateSynonym();
		DateSynonym ds4 = createDateSynonym();
		GenericCategory category = EntityManager.getInstance().getGenericCategory(categoryType, 222);
		((MutableTimedAssociationKey) category.getParentKeyIterator().next()).setEffectiveDate(ds1);
		((MutableTimedAssociationKey) category.getParentKeyIterator().next()).setExpirationDate(ds2);

		GenericEntity entity = EntityManager.getInstance().getEntity(entityType, 2);
		((MutableTimedAssociationKey) entity.getCategoryAssociations(220).iterator().next()).setEffectiveDate(ds4);
		entity = EntityManager.getInstance().getEntity(entityType, 1);
		((MutableTimedAssociationKey) entity.getCategoryAssociations(200).iterator().next()).setEffectiveDate(null);
		((MutableTimedAssociationKey) entity.getCategoryAssociations(200).iterator().next()).setExpirationDate(ds2);

		ParameterGrid grid = createParameterGrid();
		grid.addGenericCategoryID(GenericEntityType.forName("channel"), 100);
		grid.addGenericCategoryID(entityType, 100);

		DateSynonym[] dateSynonyms = EntityManager.getInstance().getDateSynonymsForChangesInCategoryToEntityRelationships(grid, null, ds3);
		assertEquals(4, dateSynonyms.length);
		assertNull(dateSynonyms[0]);
		assertEquals(ds1, dateSynonyms[1]);
		assertEquals(ds2, dateSynonyms[2]);
		assertEquals(ds3, dateSynonyms[3]);
	}
}
