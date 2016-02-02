package com.mindbox.pe.model;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithGenericEntityType;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;

public class GenericEntityTest extends AbstractTestWithGenericEntityType {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GenericEntityTest Tests");
		suite.addTestSuite(GenericEntityTest.class);
		return suite;
	}

	private GenericEntity entity;

	public GenericEntityTest(String name) {
		super(name);
	}

	public void testMultiEnumPropertyValueRoundTripHappyCase() throws Exception {
		List<String> values = Arrays.asList(new String[] { "a|b", "C AND 'D'", ObjectMother.createString() });
		String result = GenericEntity.toMultiEnumPropertyValue(values);
		assertNotNull(result);
		List<String> valuesFromResult = GenericEntity.toMultiEnumValues(result);
		assertEquals(3, valuesFromResult.size());
		assertEquals(values.get(0), valuesFromResult.get(0));
		assertEquals(values.get(1), valuesFromResult.get(1));
		assertEquals(values.get(2), valuesFromResult.get(2));
	}

	public void testHasCategoryAssociationsPositiveCase() throws Exception {
		entity.addCategoryAssociation(ObjectMother.createMutableTimedAssociationKey());
		assertTrue(entity.hasCategoryAssociation());
	}

	public void testHasCategoryAssociationsNegativeCase() throws Exception {
		assertFalse(entity.hasCategoryAssociation());
	}

	public void testHasSameCategoryAssociationsWithNullCategoryThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(entity, "hasSameCategoryAssociations", new Class[] { GenericEntity.class });
	}

	public void testHasSameCategoryAssociationsPositiveCase() throws Exception {
		assertTrue(entity.hasSameCategoryAssociations(ObjectMother.createGenericEntity(entityType)));

		MutableTimedAssociationKey key = ObjectMother.createMutableTimedAssociationKey();
		entity.addCategoryAssociation(key);
		GenericEntity entity2 = ObjectMother.createGenericEntity(entityType);
		entity2.addCategoryAssociation(key);

		assertTrue(entity.hasSameCategoryAssociations(entity2));
	}

	public void testHasSameCategoryAssociationsNegativeCase() throws Exception {
		MutableTimedAssociationKey key = ObjectMother.createMutableTimedAssociationKey();
		entity.addCategoryAssociation(key);

		// check different size
		GenericEntity entity2 = ObjectMother.createGenericEntity(entityType);
		assertFalse(entity.hasSameCategoryAssociations(entity2));

		// check different id
		entity2.addCategoryAssociation(ObjectMother.createMutableTimedAssociationKey());
		assertFalse(entity.hasSameCategoryAssociations(entity2));
	}

	public void testSetCategoryAssociationsWithNullCategoryThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(entity, "setCategoryAssociations", new Class[] { GenericEntity.class });
	}

	public void testSetCategoryAssociationsWithCategoryOfNoCategoryClearsCategorySet() throws Exception {
		MutableTimedAssociationKey key = ObjectMother.createMutableTimedAssociationKey();
		entity.addCategoryAssociation(key);

		entity.setCategoryAssociations(ObjectMother.createGenericEntity(entityType));
		assertFalse(entity.getCategoryIterator().hasNext());
	}

	public void testSetCategoryAssociationsHappyCase() throws Exception {
		MutableTimedAssociationKey key = ObjectMother.createMutableTimedAssociationKey();
		GenericEntity entity2 = ObjectMother.createGenericEntity(entityType);
		entity2.addCategoryAssociation(key);

		entity.setCategoryAssociations(entity2);
		Iterator<MutableTimedAssociationKey> iter = entity.getCategoryIterator();
		assertEquals(key, iter.next());
	}

	public void testCopyWithSameInstanceIsNoOp() throws Exception {
		MutableTimedAssociationKey key = ObjectMother.createMutableTimedAssociationKey();
		entity.addCategoryAssociation(key);

		entity.copyFrom(entity);
		assertTrue(entity.hasCategoryAssociation());
	}

	public void testGetBooleanPropertyWithNullThrowsNullPointerException() throws Exception {
		try {
			entity.getBooleanProperty(null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testGetBooleanPropertyWithNoValueSetReturnsFalse() throws Exception {
		assertFalse(entity.getBooleanProperty("prop1"));
	}

	public void testGetBooleanPropertyWithNullValueThrowsNullPointerException() throws Exception {
		try {
			entity.setProperty("prop1", null);
			entity.getBooleanProperty("prop1");
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testGetBooleanPropertyWithNonNullValueSetReturnsTheValue() throws Exception {
		entity.setProperty("prop1", false);
		assertFalse(entity.getBooleanProperty("prop1"));
		entity.setProperty("prop1", true);
		assertTrue(entity.getBooleanProperty("prop1"));
	}

	public void testHasPropertyWithNullThrowsNullPointerException() throws Exception {
		try {
			entity.hasProperty(null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testHasPropertyWithNoValueSetReturnsFalse() throws Exception {
		assertFalse(entity.hasProperty("prop1"));
	}

	public void testHasPropertyWithNullValueSetReturnsFalse() throws Exception {
		entity.setProperty("prop1", null);
		assertFalse(entity.hasProperty("prop1"));
	}

	public void testHasPropertyWithNonNullValueSetReturnsTrue() throws Exception {
		entity.setProperty("prop1", false);
		assertTrue(entity.hasProperty("prop1"));
	}

	public void testGetDateSynonymsUsedInCategoryAssociationsWithNoAssocReturnsEmptySet() throws Exception {
		assertTrue(entity.getDateSynonymsUsedInCategoryAssociations().isEmpty());
	}

	public void testGetDateSynonymsUsedInCategoryAssociationsHappyCase() throws Exception {
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();

		MutableTimedAssociationKey key = ObjectMother.createMutableTimedAssociationKey();
		key.setExpirationDate(ds1);
		entity.addCategoryAssociation(key);

		MutableTimedAssociationKey key2 = ObjectMother.createMutableTimedAssociationKey();
		key2.setAssociableID(key.getAssociableID());
		key2.setEffectiveDate(ds1);
		entity.addCategoryAssociation(key2);

		key2 = ObjectMother.createMutableTimedAssociationKey();
		key.setEffectiveDate(ds1);
		key.setExpirationDate(ds2);
		entity.addCategoryAssociation(key2);

		Set<DateSynonym> set = entity.getDateSynonymsUsedInCategoryAssociations();
		assertEquals(2, set.size());
		assertTrue(set.contains(ds1));
		assertTrue(set.contains(ds2));
	}

	public void testRemoveAllCategoryAssociations() throws Exception {
		GenericEntity e = ObjectMother.createGenericEntity(GenericEntityType.forName("product"));
		e.addCategoryAssociation(ObjectMother.createMutableTimedAssociationKey());
		assertTrue(e.getCategoryIterator().hasNext());
		e.removeAllCategoryAssociations();
		assertFalse(e.getCategoryIterator().hasNext());
	}

	protected void setUp() throws Exception {
		super.setUp();
		entity = new GenericEntity(1, entityType, "name");
	}

	protected void tearDown() throws Exception {
		entity = null;
		super.tearDown();
	}

	public void testHasOverlappingCategoryAssociationPositiveCase() throws Exception {
		MutableTimedAssociationKey key = ObjectMother.createMutableTimedAssociationKey();
		entity.addCategoryAssociation(key);
		assertTrue(entity.hasOverlappingCategoryAssociation(ObjectMother.createMutableTimedAssociationKey()));
	}

	public void testHasOverlappingCategoryAssociationNegativeCase() throws Exception {
		assertFalse(entity.hasOverlappingCategoryAssociation(ObjectMother.createMutableTimedAssociationKey()));

		MutableTimedAssociationKey key = ObjectMother.createMutableTimedAssociationKey();
		key.setExpirationDate(ObjectMother.createDateSynonym());
		entity.addCategoryAssociation(key);

		MutableTimedAssociationKey futureKey = ObjectMother.createMutableTimedAssociationKey();
		futureKey.setEffectiveDate(ObjectMother.createDateSynonym());
		assertFalse(entity.hasOverlappingCategoryAssociation(futureKey));
	}

}
