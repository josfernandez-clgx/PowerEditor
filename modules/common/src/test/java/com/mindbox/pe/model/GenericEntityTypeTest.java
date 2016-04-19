package com.mindbox.pe.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.mindbox.pe.common.AbstractTestWithGenericEntityType;
import com.mindbox.pe.xsd.config.EntityType;

public class GenericEntityTypeTest extends AbstractTestWithGenericEntityType {

	@Test
	public void testForCategoryTypeWithInvalidIDReturnsNull() throws Exception {
		assertNull(GenericEntityType.forCategoryType(999999));
	}

	@Test
	public void testForCategoryTypeWithValidIDReturnsCorrectInstance() throws Exception {
		assertTrue(GenericEntityType.forID(genericEntityTypeId) == GenericEntityType.forCategoryType(genericCategoryTypeId));
	}

	@Test
	public void testForIDWithInvalidIDThrowsIllegalArgumentException() throws Exception {
		try {
			GenericEntityType.forID(genericEntityTypeId + 1);
			fail("forID() with invalid id didn't throw IllegalArgumentException");
		}
		catch (IllegalArgumentException ex) {
		}
	}

	@Test
	public void testForIDWithValidIDReturnsIdenticalInstance() throws Exception {
		assertTrue(entityType == GenericEntityType.forID(genericEntityTypeId));
	}

	@Test
	public void testForNameWithEmptyNameReturnsNull() throws Exception {
		assertNull(GenericEntityType.forName(""));
	}

	@Test
	public void testForNameWithInvalidNameReturnsNull() throws Exception {
		assertNull(GenericEntityType.forName("unknown"));
	}

	@Test
	public void testForNameWithNullNameReturnsNull() throws Exception {
		assertNull(GenericEntityType.forName(null));
	}

	@Test
	public void testForNameWithValidNameReturnsIdenticalInstance() throws Exception {
		assertTrue(entityType == GenericEntityType.forName(entityType.getName()));
	}

	@Test
	public void testGetCategoryTypeReturnsCorrectValue() throws Exception {
		assertEquals(genericCategoryTypeId, GenericEntityType.forID(genericEntityTypeId).getCategoryType());
	}

	@Test
	public void testHasCategoryNegativeCase() throws Exception {
		EntityType etDef = new EntityType();
		etDef.setName(entityType.getName());
		etDef.setDisplayName("Test Entity2");
		etDef.setTypeID(genericEntityTypeId + 1);
		etDef.setCanClone(Boolean.TRUE);
		etDef.setCategoryType(0);
		GenericEntityType entityType = GenericEntityType.makeInstance(etDef);
		assertTrue(!entityType.hasCategory());
	}

	@Test
	public void testHasCategoryPositiveCase() throws Exception {
		assertTrue(entityType.hasCategory());
	}

	@Test
	public void testHasTypeWithInvalidIDReturnsFalse() throws Exception {
		assertFalse(GenericEntityType.hasTypeFor(genericEntityTypeId + 1));
	}

	@Test
	public void testHasTypeWithValidIDReturnsTrue() throws Exception {
		assertTrue(GenericEntityType.hasTypeFor(genericEntityTypeId));
	}

	@Test
	public void testIsValidCategoryTypeNegativeCase() throws Exception {
		assertFalse(GenericEntityType.isValidCategoryType(entityType.getCategoryType() + 1));
	}

	@Test
	public void testIsValidCategoryTypePositiveCase() throws Exception {
		assertTrue(GenericEntityType.isValidCategoryType(entityType.getCategoryType()));
	}

	@Test
	public void testIsValidCategoryTypeWithNonPostiveIDReturnsFalse() throws Exception {
		assertFalse(GenericEntityType.isValidCategoryType(0));
		assertFalse(GenericEntityType.isValidCategoryType(-1));
	}

	@Test
	public void testToStringReturnsName() throws Exception {
		assertEquals(entityType.getName(), entityType.toString());
	}
}
