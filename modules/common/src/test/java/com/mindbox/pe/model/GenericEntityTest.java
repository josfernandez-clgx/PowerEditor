package com.mindbox.pe.model;

import static com.mindbox.pe.common.CommonTestObjectMother.createDateSynonym;
import static com.mindbox.pe.common.CommonTestObjectMother.createGenericEntity;
import static com.mindbox.pe.common.CommonTestObjectMother.createMutableTimedAssociationKey;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.AbstractTestWithGenericEntityType;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;

public class GenericEntityTest extends AbstractTestWithGenericEntityType {

	private GenericEntity entity;

	@Test
	public void testMultiEnumPropertyValueRoundTripHappyCase() throws Exception {
		List<String> values = Arrays.asList(new String[] { "a|b", "C AND 'D'", createString() });
		String result = GenericEntity.toMultiEnumPropertyValue(values);
		assertNotNull(result);
		List<String> valuesFromResult = GenericEntity.toMultiEnumValues(result);
		assertEquals(3, valuesFromResult.size());
		assertEquals(values.get(0), valuesFromResult.get(0));
		assertEquals(values.get(1), valuesFromResult.get(1));
		assertEquals(values.get(2), valuesFromResult.get(2));
	}

	@Test
	public void testHasCategoryAssociationsPositiveCase() throws Exception {
		entity.addCategoryAssociation(createMutableTimedAssociationKey());
		assertTrue(entity.hasCategoryAssociation());
	}

	@Test
	public void testHasCategoryAssociationsNegativeCase() throws Exception {
		assertFalse(entity.hasCategoryAssociation());
	}

	@Test
	public void testHasSameCategoryAssociationsWithNullCategoryThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(entity, "hasSameCategoryAssociations", new Class[] { GenericEntity.class });
	}

	@Test
	public void testHasSameCategoryAssociationsPositiveCase() throws Exception {
		assertTrue(entity.hasSameCategoryAssociations(createGenericEntity(entityType)));

		MutableTimedAssociationKey key = createMutableTimedAssociationKey();
		entity.addCategoryAssociation(key);
		GenericEntity entity2 = createGenericEntity(entityType);
		entity2.addCategoryAssociation(key);

		assertTrue(entity.hasSameCategoryAssociations(entity2));
	}

	@Test
	public void testHasSameCategoryAssociationsNegativeCase() throws Exception {
		MutableTimedAssociationKey key = createMutableTimedAssociationKey();
		entity.addCategoryAssociation(key);

		// check different size
		GenericEntity entity2 = createGenericEntity(entityType);
		assertFalse(entity.hasSameCategoryAssociations(entity2));

		// check different id
		entity2.addCategoryAssociation(createMutableTimedAssociationKey());
		assertFalse(entity.hasSameCategoryAssociations(entity2));
	}

	@Test
	public void testSetCategoryAssociationsWithNullCategoryThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(entity, "setCategoryAssociations", new Class[] { GenericEntity.class });
	}

	@Test
	public void testSetCategoryAssociationsWithCategoryOfNoCategoryClearsCategorySet() throws Exception {
		MutableTimedAssociationKey key = createMutableTimedAssociationKey();
		entity.addCategoryAssociation(key);

		entity.setCategoryAssociations(createGenericEntity(entityType));
		assertFalse(entity.getCategoryIterator().hasNext());
	}

	@Test
	public void testSetCategoryAssociationsHappyCase() throws Exception {
		MutableTimedAssociationKey key = createMutableTimedAssociationKey();
		GenericEntity entity2 = createGenericEntity(entityType);
		entity2.addCategoryAssociation(key);

		entity.setCategoryAssociations(entity2);
		Iterator<MutableTimedAssociationKey> iter = entity.getCategoryIterator();
		assertEquals(key, iter.next());
	}

	@Test
	public void testCopyWithSameInstanceIsNoOp() throws Exception {
		MutableTimedAssociationKey key = createMutableTimedAssociationKey();
		entity.addCategoryAssociation(key);

		entity.copyFrom(entity);
		assertTrue(entity.hasCategoryAssociation());
	}

	@Test
	public void testGetBooleanPropertyWithNullThrowsNullPointerException() throws Exception {
		try {
			entity.getBooleanProperty(null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	@Test
	public void testGetBooleanPropertyWithNoValueSetReturnsFalse() throws Exception {
		assertFalse(entity.getBooleanProperty("prop1"));
	}

	@Test
	public void testGetBooleanPropertyWithNullValueThrowsNullPointerException() throws Exception {
		try {
			entity.setProperty("prop1", (Object) null);
			entity.getBooleanProperty("prop1");
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	@Test
	public void testGetBooleanPropertyWithNonNullValueSetReturnsTheValue() throws Exception {
		entity.setProperty("prop1", false);
		assertFalse(entity.getBooleanProperty("prop1"));
		entity.setProperty("prop1", true);
		assertTrue(entity.getBooleanProperty("prop1"));
	}

	@Test
	public void testHasPropertyWithNullThrowsNullPointerException() throws Exception {
		try {
			entity.hasProperty(null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	@Test
	public void testHasPropertyWithNoValueSetReturnsFalse() throws Exception {
		assertFalse(entity.hasProperty("prop1"));
	}

	@Test
	public void testHasPropertyWithNullValueSetReturnsFalse() throws Exception {
		entity.setProperty("prop1", (Object) null);
		assertFalse(entity.hasProperty("prop1"));
	}

	@Test
	public void testHasPropertyWithNonNullValueSetReturnsTrue() throws Exception {
		entity.setProperty("prop1", false);
		assertTrue(entity.hasProperty("prop1"));
	}

	@Test
	public void testGetDateSynonymsUsedInCategoryAssociationsWithNoAssocReturnsEmptySet() throws Exception {
		assertTrue(entity.getDateSynonymsUsedInCategoryAssociations().isEmpty());
	}

	@Test
	public void testGetDateSynonymsUsedInCategoryAssociationsHappyCase() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		DateSynonym ds2 = createDateSynonym();

		MutableTimedAssociationKey key = createMutableTimedAssociationKey();
		key.setExpirationDate(ds1);
		entity.addCategoryAssociation(key);

		MutableTimedAssociationKey key2 = createMutableTimedAssociationKey();
		key2.setAssociableID(key.getAssociableID());
		key2.setEffectiveDate(ds1);
		entity.addCategoryAssociation(key2);

		key2 = createMutableTimedAssociationKey();
		key.setEffectiveDate(ds1);
		key.setExpirationDate(ds2);
		entity.addCategoryAssociation(key2);

		Set<DateSynonym> set = entity.getDateSynonymsUsedInCategoryAssociations();
		assertEquals(2, set.size());
		assertTrue(set.contains(ds1));
		assertTrue(set.contains(ds2));
	}

	@Test
	public void testRemoveAllCategoryAssociations() throws Exception {
		GenericEntity e = createGenericEntity(GenericEntityType.forName("product"));
		e.addCategoryAssociation(createMutableTimedAssociationKey());
		assertTrue(e.getCategoryIterator().hasNext());
		e.removeAllCategoryAssociations();
		assertFalse(e.getCategoryIterator().hasNext());
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		entity = new GenericEntity(1, entityType, "name");
	}

	@Test
	public void testHasOverlappingCategoryAssociationPositiveCase() throws Exception {
		MutableTimedAssociationKey key = createMutableTimedAssociationKey();
		entity.addCategoryAssociation(key);
		assertTrue(entity.hasOverlappingCategoryAssociation(createMutableTimedAssociationKey()));
	}

	@Test
	public void testHasOverlappingCategoryAssociationNegativeCase() throws Exception {
		assertFalse(entity.hasOverlappingCategoryAssociation(createMutableTimedAssociationKey()));

		MutableTimedAssociationKey key = createMutableTimedAssociationKey();
		key.setExpirationDate(createDateSynonym());
		entity.addCategoryAssociation(key);

		MutableTimedAssociationKey futureKey = createMutableTimedAssociationKey();
		futureKey.setEffectiveDate(createDateSynonym());
		assertFalse(entity.hasOverlappingCategoryAssociation(futureKey));
	}

}
