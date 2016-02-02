package com.mindbox.pe.model.table;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithGenericEntityType;

public class CategoryOrEntityValuesTest extends AbstractTestWithGenericEntityType {

	private CategoryOrEntityValues entityValues;
	private CategoryOrEntityValues categoryValues;

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("CategoryOrEntityValues Tests");
		suite.addTestSuite(CategoryOrEntityValuesTest.class);
		return suite;
	}

	public CategoryOrEntityValuesTest(String name) {
		super(name);
	}

	public void testHasGenericCategoryReferencePositiveCase() throws Exception {
		CategoryOrEntityValues value = new CategoryOrEntityValues();
		value.add(new CategoryOrEntityValue(entityType, true, 1));
		value.add(new CategoryOrEntityValue(entityType, false, 2));
		assertTrue(value.hasGenericCategoryReference());
	}
	
	public void testHasGenericCategoryReferenceNegativeCase() throws Exception {
		CategoryOrEntityValues value = new CategoryOrEntityValues();
		assertFalse(value.hasGenericCategoryReference());
		value.add(new CategoryOrEntityValue(entityType, true, 1));
		assertFalse(value.hasGenericCategoryReference());
	}
	
	public void testHasIDOnEmptyValueReturnsFalse() throws Exception {
		assertFalse(new CategoryOrEntityValues().hasID(true,1));
		assertFalse(new CategoryOrEntityValues().hasID(false,1));
	}

	public void testHasIDForCategoryPositiveCase() throws Exception {
		assertTrue(categoryValues.hasID(false, 5));
	}

	public void testHasIDForCategoryNegativeCase() throws Exception {
		assertFalse(categoryValues.hasID(false, 1));
		assertFalse(entityValues.hasID(false, 1));
	}

	public void testHasIDForEntityPositiveCase() throws Exception {
		assertTrue(entityValues.hasID(true, 1));
	}

	public void testHasIDForEntityNegativeCase() throws Exception {
		assertFalse(entityValues.hasID(true, 5));
		assertFalse(categoryValues.hasID(true, 5));
	}

	public void testGetEntityIDsWithEmptyValuesReturnsEmptyArray() throws Exception {
		assertEquals(new int[0], new CategoryOrEntityValues().getEntityIDs());
	}
	
	public void testGetEntityIDsHappyCase() throws Exception {
		int[] entityIDs = entityValues.getEntityIDs();
		assertEquals(new int[] { 1, 2, 3, 4 }, entityIDs);
	}

	public void testGetCategoryIDsWithEmptyValuesReturnsEmptyArray() throws Exception {
		assertEquals(new int[0], new CategoryOrEntityValues().getCategoryIDs());
	}
	
	public void testGetCategoryIDsHappyCase() throws Exception {
		int[] categoryIDs = categoryValues.getCategoryIDs();
		assertEquals(new int[] { 5, 6, 7, 8 }, categoryIDs);
	}

	public void testRemoveCategoryIDWithEmptyValuesISNoOp() throws Exception {
		CategoryOrEntityValues values = new CategoryOrEntityValues();
		values.removeCategoryID(1);
		assertTrue(values.isEmpty());
	}
	
	public void testRemoveCategoryIDWithNotFoundIDISNoOp() throws Exception {
		categoryValues.removeCategoryID(1);
		assertEquals(4, categoryValues.size());
	}
	
	public void testRemoveCategoryIDWithEntityIDISNoOp() throws Exception {
		entityValues.removeCategoryID(1);
		assertEquals(4, entityValues.size());
	}
	
	public void testRemoveCategoryIDHappyCase() throws Exception {
		categoryValues.removeCategoryID(5);
		assertEquals(3, categoryValues.size());
		
		categoryValues.removeCategoryID(6);
		categoryValues.removeCategoryID(7);
		assertEquals(1, categoryValues.size());
		assertTrue(categoryValues.hasID(false, 8));
	}
	
	public void testRemoveEntityIDWithEmptyValuesISNoOp() throws Exception {
		CategoryOrEntityValues values = new CategoryOrEntityValues();
		values.removeEntityID(1);
		assertTrue(values.isEmpty());
	}
	
	public void testRemoveEntityIDWithNotFoundIDISNoOp() throws Exception {
		entityValues.removeEntityID(5);
		assertEquals(4, entityValues.size());
	}
	
	public void testRemoveEntityIDWithEntityIDISNoOp() throws Exception {
		categoryValues.removeEntityID(5);
		assertEquals(4, categoryValues.size());
	}
	
	public void testRemoveEntityIDHappyCase() throws Exception {
		entityValues.removeEntityID(4);
		assertEquals(3, entityValues.size());
		
		entityValues.removeEntityID(3);
		entityValues.removeEntityID(2);
		assertEquals(1, entityValues.size());
		assertTrue(entityValues.hasID(true, 1));
	}
	
	protected void setUp() throws Exception {
		super.setUp();
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

	protected void tearDown() throws Exception {
		// Tear downs for CategoryOrEntityValueTest
		super.tearDown();
	}
}
