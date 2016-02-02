package com.mindbox.pe.model;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithGenericEntityType;
import com.mindbox.pe.common.config.EntityTypeDefinition;

public class GenericEntityTypeTest extends AbstractTestWithGenericEntityType {

	public static Test suite() {
		TestSuite suite = new TestSuite("GenericEntityTypeTest Tests");
		suite.addTestSuite(GenericEntityTypeTest.class);
		return suite;
	}

	public GenericEntityTypeTest(String name) {
		super(name);
	}

	public void testIsValidCategoryTypeWithNonPostiveIDReturnsFalse() throws Exception {
		assertFalse(GenericEntityType.isValidCategoryType(0));
		assertFalse(GenericEntityType.isValidCategoryType(-1));
	}

	public void testIsValidCategoryTypePositiveCase() throws Exception {
		assertTrue(GenericEntityType.isValidCategoryType(entityType.getCategoryType()));
	}

	public void testIsValidCategoryTypeNegativeCase() throws Exception {
		assertFalse(GenericEntityType.isValidCategoryType(entityType.getCategoryType() + 1));
	}

	public void testForCategoryTypeWithInvalidIDReturnsNull() throws Exception {
		assertNull(GenericEntityType.forCategoryType(999));
	}

	public void testForCategoryTypeWithValidIDReturnsCorrectInstance() throws Exception {
		assertTrue(GenericEntityType.forID(TEST_ENTITY_TYPE_ID) == GenericEntityType.forCategoryType(TEST_CATEGORY_TYPE_ID));
	}

	public void testGetCategoryTypeReturnsCorrectValue() throws Exception {
		assertEquals(TEST_CATEGORY_TYPE_ID, GenericEntityType.forID(TEST_ENTITY_TYPE_ID).getCategoryType());
	}

	public void testToStringReturnsName() throws Exception {
		assertEquals("test", entityType.toString());
	}

	public void testHasTypeWithInvalidIDReturnsFalse() throws Exception {
		assertFalse(GenericEntityType.hasTypeFor(TEST_ENTITY_TYPE_ID + 1));
	}

	public void testHasTypeWithValidIDReturnsTrue() throws Exception {
		assertTrue(GenericEntityType.hasTypeFor(TEST_ENTITY_TYPE_ID));
	}

	public void testForIDWithInvalidIDThrowsIllegalArgumentException() throws Exception {
		try {
			GenericEntityType.forID(TEST_ENTITY_TYPE_ID + 1);
			fail("forID() with invalid id didn't throw IllegalArgumentException");
		}
		catch (IllegalArgumentException ex) {
		}
	}

	public void testForIDWithValidIDReturnsIdenticalInstance() throws Exception {
		assertTrue(entityType == GenericEntityType.forID(TEST_ENTITY_TYPE_ID));
	}

	public void testForNameWithNullNameReturnsNull() throws Exception {
		assertNull(GenericEntityType.forName(null));
	}

	public void testForNameWithEmptyNameReturnsNull() throws Exception {
		assertNull(GenericEntityType.forName(""));
	}

	public void testForNameWithInvalidNameReturnsNull() throws Exception {
		assertNull(GenericEntityType.forName("unknown"));
	}

	public void testForNameWithValidNameReturnsIdenticalInstance() throws Exception {
		assertTrue(entityType == GenericEntityType.forName(TEST_ENTITY_TYPE_NAME));
	}

	public void testHasCategoryPositiveCase() throws Exception {
		assertTrue(entityType.hasCategory());
	}

	public void testHasCategoryNegativeCase() throws Exception {
		EntityTypeDefinition etDef = new EntityTypeDefinition();
		etDef.setName(TEST_ENTITY_TYPE_NAME);
		etDef.setDisplayName("Test Entity2");
		etDef.setTypeID(TEST_ENTITY_TYPE_ID + 1);
		etDef.setCanClone("yes");
		etDef.setCategoryType(0);
		GenericEntityType entityType = GenericEntityType.makeInstance(etDef);
		assertTrue(!entityType.hasCategory());
	}
}
