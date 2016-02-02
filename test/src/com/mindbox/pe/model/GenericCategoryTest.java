package com.mindbox.pe.model;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithGenericEntityType;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;

public class GenericCategoryTest extends AbstractTestWithGenericEntityType {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GenericCategoryTest Tests");
		suite.addTestSuite(GenericCategoryTest.class);
		return suite;
	}

	public GenericCategoryTest(String name) {
		super(name);
	}

	private GenericCategory genericCategory;

	public void testHasSameParentAssociationsWithNullCategoryThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(genericCategory, "hasSameParentAssociations", new Class[]
			{ GenericCategory.class});
	}

	public void testHasSameParentAssociationsPositiveCase() throws Exception {
		assertTrue(genericCategory.hasSameParentAssociations(ObjectMother.createGenericCategory(entityType)));
		
		MutableTimedAssociationKey key = ObjectMother.createMutableTimedAssociationKey();
		genericCategory.addParentKey(key);
		GenericCategory category2 = ObjectMother.createGenericCategory(entityType);
		category2.addParentKey(key);
		
		assertTrue(genericCategory.hasSameParentAssociations(category2));
	}

	public void testHasSameParentAssociationsNegativeCase() throws Exception {
		MutableTimedAssociationKey key = ObjectMother.createMutableTimedAssociationKey();
		genericCategory.addParentKey(key);
		
		// check different size
		GenericCategory category2 = ObjectMother.createGenericCategory(entityType);
		assertFalse(genericCategory.hasSameParentAssociations(category2));
		
		// check different id
		category2.addChildAssociation(ObjectMother.createMutableTimedAssociationKey());
		assertFalse(genericCategory.hasSameParentAssociations(category2));
	}

	public void testSetParentAssociationsWithNullCategoryThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(genericCategory, "setParentAssociations", new Class[]
			{ GenericCategory.class});
	}

	public void testSetParentAssociationsWithCategoryOfNoParentClearsParentSet() throws Exception {
		MutableTimedAssociationKey key = ObjectMother.createMutableTimedAssociationKey();
		genericCategory.addParentKey(key);

		genericCategory.setParentAssociations(ObjectMother.createGenericCategory(entityType));
		assertTrue(genericCategory.isRoot());
	}

	public void testSetParentAssociationsHappyCase() throws Exception {
		MutableTimedAssociationKey key = ObjectMother.createMutableTimedAssociationKey();
		GenericCategory category2 = ObjectMother.createGenericCategory(entityType);
		category2.addParentKey(key);

		genericCategory.setParentAssociations(category2);
		Iterator<MutableTimedAssociationKey> iter = genericCategory.getParentKeyIterator();
		assertEquals(key, iter.next());
	}

	public void testIsRootPositiveCase() throws Exception {
		assertTrue(genericCategory.isRoot());
	}

	public void testIsRootNegativeCase() throws Exception {
		genericCategory.addParentKey(ObjectMother.createMutableTimedAssociationKey());
		assertFalse(genericCategory.isRoot());
	}

	public void testHasNoChildOnDate() throws Exception {
		MutableTimedAssociationKey childAssociation = 
			ObjectMother.attachExpirationDateSynonym(
					ObjectMother.attachEffectiveDateSynonym(
							ObjectMother.createMutableTimedAssociationKey()));
	
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
	
	public void testHasNoChild() throws Exception {
		assertTrue(genericCategory.hasNoChild()); 
		genericCategory.addChildAssociation(ObjectMother.createMutableTimedAssociationKey());
		assertFalse(genericCategory.hasNoChild());
	}
    
    public void testGetAllParentAssociations() throws Exception {
        GenericCategory category = ObjectMother.createGenericCategory(entityType);
        assertNotNull(category.getAllParentAssociations());
        assertTrue(category.getAllParentAssociations().size() == 0);
        
        MutableTimedAssociationKey key = ObjectMother.createMutableTimedAssociationKey();
        category.addParentKey(key);
        List<MutableTimedAssociationKey> list = category.getAllParentAssociations();
        assertTrue(list.size() == 1);
        assertTrue(list.get(0).equals(key));
    }

    public void testRemoveAllParentAssociations() throws Exception {
        GenericCategory category = ObjectMother.createGenericCategory(entityType);
        MutableTimedAssociationKey key = ObjectMother.createMutableTimedAssociationKey();
        category.addParentKey(key);
        assertTrue(category.getAllParentAssociations().size() == 1);
        category.removeAllParentAssociations();
        assertTrue(category.getAllParentAssociations().size() == 0);
    }
    
    
	protected void setUp() throws Exception {
		super.setUp();
		genericCategory = ObjectMother.createGenericCategory(entityType);
	}

	protected void tearDown() throws Exception {
		// Tear downs for GenericCategoryTest
		super.tearDown();
	}
}
