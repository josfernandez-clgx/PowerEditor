package com.mindbox.pe.model;

import static com.mindbox.pe.common.CommonTestObjectMother.attachEffectiveDateSynonym;
import static com.mindbox.pe.common.CommonTestObjectMother.attachExpirationDateSynonym;
import static com.mindbox.pe.common.CommonTestObjectMother.createGenericCategory;
import static com.mindbox.pe.common.CommonTestObjectMother.createMutableTimedAssociationKey;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.AbstractTestWithGenericEntityType;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;

public class GenericCategoryTest extends AbstractTestWithGenericEntityType {

	private GenericCategory genericCategory;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		genericCategory = createGenericCategory(entityType);
	}

	@Test
	public void testGetAllParentAssociations() throws Exception {
		GenericCategory category = createGenericCategory(entityType);
		assertNotNull(category.getAllParentAssociations());
		assertTrue(category.getAllParentAssociations().size() == 0);

		MutableTimedAssociationKey key = createMutableTimedAssociationKey();
		category.addParentKey(key);
		List<MutableTimedAssociationKey> list = category.getAllParentAssociations();
		assertTrue(list.size() == 1);
		assertTrue(list.get(0).equals(key));
	}

	@Test
	public void testHasNoChild() throws Exception {
		assertTrue(genericCategory.hasNoChild());
		genericCategory.addChildAssociation(createMutableTimedAssociationKey());
		assertFalse(genericCategory.hasNoChild());
	}

	@Test
	public void testHasNoChildOnDate() throws Exception {
		MutableTimedAssociationKey childAssociation = attachExpirationDateSynonym(attachEffectiveDateSynonym(createMutableTimedAssociationKey()));

		assertTrue(genericCategory.hasNoChild(new Date())); // before any children

		genericCategory.addChildAssociation(childAssociation); // add a child association

		Date atActivation = childAssociation.getEffectiveDate().getDate();
		Date atExpiration = childAssociation.getExpirationDate().getDate();
		Date beforeActivation = new Date(atActivation.getTime() - 1000);
		Date afterExpiration = new Date(atExpiration.getTime() + 1000);

		assertFalse(genericCategory.hasNoChild(atActivation));
		assertTrue(genericCategory.hasNoChild(beforeActivation));
		assertTrue(genericCategory.hasNoChild(atExpiration));
		assertTrue(genericCategory.hasNoChild(afterExpiration));
	}

	@Test
	public void testHasSameParentAssociationsNegativeCase() throws Exception {
		MutableTimedAssociationKey key = createMutableTimedAssociationKey();
		genericCategory.addParentKey(key);

		// check different size
		GenericCategory category2 = createGenericCategory(entityType);
		assertFalse(genericCategory.hasSameParentAssociations(category2));

		// check different id
		category2.addChildAssociation(createMutableTimedAssociationKey());
		assertFalse(genericCategory.hasSameParentAssociations(category2));
	}

	@Test
	public void testHasSameParentAssociationsPositiveCase() throws Exception {
		assertTrue(genericCategory.hasSameParentAssociations(createGenericCategory(entityType)));

		MutableTimedAssociationKey key = createMutableTimedAssociationKey();
		genericCategory.addParentKey(key);
		GenericCategory category2 = createGenericCategory(entityType);
		category2.addParentKey(key);

		assertTrue(genericCategory.hasSameParentAssociations(category2));
	}

	@Test
	public void testHasSameParentAssociationsWithNullCategoryThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(genericCategory, "hasSameParentAssociations", new Class[] { GenericCategory.class });
	}

	@Test
	public void testIsRootNegativeCase() throws Exception {
		genericCategory.addParentKey(createMutableTimedAssociationKey());
		assertFalse(genericCategory.isRoot());
	}

	@Test
	public void testIsRootPositiveCase() throws Exception {
		assertTrue(genericCategory.isRoot());
	}

	@Test
	public void testRemoveAllParentAssociations() throws Exception {
		GenericCategory category = createGenericCategory(entityType);
		MutableTimedAssociationKey key = createMutableTimedAssociationKey();
		category.addParentKey(key);
		assertTrue(category.getAllParentAssociations().size() == 1);
		category.removeAllParentAssociations();
		assertTrue(category.getAllParentAssociations().size() == 0);
	}

	@Test
	public void testSetParentAssociationsHappyCase() throws Exception {
		MutableTimedAssociationKey key = createMutableTimedAssociationKey();
		GenericCategory category2 = createGenericCategory(entityType);
		category2.addParentKey(key);

		genericCategory.setParentAssociations(category2);
		Iterator<MutableTimedAssociationKey> iter = genericCategory.getParentKeyIterator();
		assertEquals(key, iter.next());
	}

	@Test
	public void testSetParentAssociationsWithCategoryOfNoParentClearsParentSet() throws Exception {
		MutableTimedAssociationKey key = createMutableTimedAssociationKey();
		genericCategory.addParentKey(key);

		genericCategory.setParentAssociations(createGenericCategory(entityType));
		assertTrue(genericCategory.isRoot());
	}

	@Test
	public void testSetParentAssociationsWithNullCategoryThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(genericCategory, "setParentAssociations", new Class[] { GenericCategory.class });
	}
}
