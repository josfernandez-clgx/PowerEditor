package com.mindbox.pe.model.table;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.AbstractTestWithGenericEntityType;

public class CategoryOrEntityValuesTest extends AbstractTestWithGenericEntityType {

	private CategoryOrEntityValues entityValues;
	private CategoryOrEntityValues categoryValues;

	@Before
	public void setUp() throws Exception {
		entityValues = new CategoryOrEntityValues();

		entityValues.add(new CategoryOrEntityValue(entityType, true, 1));
		entityValues.add(new CategoryOrEntityValue(entityType, true, 2));
		entityValues.add(new CategoryOrEntityValue(entityType, true, 3));
		entityValues.add(new CategoryOrEntityValue(entityType, true, 4));

		categoryValues = new CategoryOrEntityValues();
		categoryValues.add(new CategoryOrEntityValue(entityType, false, 5));
		categoryValues.add(new CategoryOrEntityValue(entityType, false, 6));
		categoryValues.add(new CategoryOrEntityValue(entityType, false, 7));
		categoryValues.add(new CategoryOrEntityValue(entityType, false, 8));
	}

	@Test
	public void testGetCategoryIDsHappyCase() throws Exception {
		int[] categoryIDs = categoryValues.getCategoryIDs();
		assertArrayEquals(new int[] { 5, 6, 7, 8 }, categoryIDs);
	}

	@Test
	public void testGetCategoryIDsWithEmptyValuesReturnsEmptyArray() throws Exception {
		assertArrayEquals(new int[0], new CategoryOrEntityValues().getCategoryIDs());
	}

	@Test
	public void testGetEntityIDsHappyCase() throws Exception {
		int[] entityIDs = entityValues.getEntityIDs();
		assertArrayEquals(new int[] { 1, 2, 3, 4 }, entityIDs);
	}

	@Test
	public void testGetEntityIDsWithEmptyValuesReturnsEmptyArray() throws Exception {
		assertArrayEquals(new int[0], new CategoryOrEntityValues().getEntityIDs());
	}

	@Test
	public void testHasGenericCategoryReferenceNegativeCase() throws Exception {
		CategoryOrEntityValues value = new CategoryOrEntityValues();
		assertFalse(value.hasGenericCategoryReference());
		value.add(new CategoryOrEntityValue(entityType, true, 1));
		assertFalse(value.hasGenericCategoryReference());
	}

	@Test
	public void testHasGenericCategoryReferencePositiveCase() throws Exception {
		CategoryOrEntityValues value = new CategoryOrEntityValues();
		value.add(new CategoryOrEntityValue(entityType, true, 1));
		value.add(new CategoryOrEntityValue(entityType, false, 2));
		assertTrue(value.hasGenericCategoryReference());
	}

	@Test
	public void testHasIDForCategoryNegativeCase() throws Exception {
		assertFalse(categoryValues.hasID(false, 1));
		assertFalse(entityValues.hasID(false, 1));
	}

	@Test
	public void testHasIDForCategoryPositiveCase() throws Exception {
		assertTrue(categoryValues.hasID(false, 5));
	}

	@Test
	public void testHasIDForEntityNegativeCase() throws Exception {
		assertFalse(entityValues.hasID(true, 5));
		assertFalse(categoryValues.hasID(true, 5));
	}

	@Test
	public void testHasIDForEntityPositiveCase() throws Exception {
		assertTrue(entityValues.hasID(true, 1));
	}

	@Test
	public void testHasIDOnEmptyValueReturnsFalse() throws Exception {
		assertFalse(new CategoryOrEntityValues().hasID(true, 1));
		assertFalse(new CategoryOrEntityValues().hasID(false, 1));
	}

	@Test
	public void testRemoveCategoryIDHappyCase() throws Exception {
		categoryValues.removeCategoryID(5);
		assertEquals(3, categoryValues.size());

		categoryValues.removeCategoryID(6);
		categoryValues.removeCategoryID(7);
		assertEquals(1, categoryValues.size());
		assertTrue(categoryValues.hasID(false, 8));
	}

	@Test
	public void testRemoveCategoryIDWithEmptyValuesISNoOp() throws Exception {
		CategoryOrEntityValues values = new CategoryOrEntityValues();
		values.removeCategoryID(1);
		assertTrue(values.isEmpty());
	}

	@Test
	public void testRemoveCategoryIDWithEntityIDISNoOp() throws Exception {
		entityValues.removeCategoryID(1);
		assertEquals(4, entityValues.size());
	}

	@Test
	public void testRemoveCategoryIDWithNotFoundIDISNoOp() throws Exception {
		categoryValues.removeCategoryID(1);
		assertEquals(4, categoryValues.size());
	}

	@Test
	public void testRemoveEntityIDHappyCase() throws Exception {
		entityValues.removeEntityID(4);
		assertEquals(3, entityValues.size());

		entityValues.removeEntityID(3);
		entityValues.removeEntityID(2);
		assertEquals(1, entityValues.size());
		assertTrue(entityValues.hasID(true, 1));
	}

	@Test
	public void testRemoveEntityIDWithEmptyValuesISNoOp() throws Exception {
		CategoryOrEntityValues values = new CategoryOrEntityValues();
		values.removeEntityID(1);
		assertTrue(values.isEmpty());
	}

	@Test
	public void testRemoveEntityIDWithEntityIDISNoOp() throws Exception {
		categoryValues.removeEntityID(5);
		assertEquals(4, categoryValues.size());
	}

	@Test
	public void testRemoveEntityIDWithNotFoundIDISNoOp() throws Exception {
		entityValues.removeEntityID(5);
		assertEquals(4, entityValues.size());
	}
}
