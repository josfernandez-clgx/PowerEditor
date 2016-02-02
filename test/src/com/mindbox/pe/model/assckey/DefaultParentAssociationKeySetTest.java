package com.mindbox.pe.model.assckey;

import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.Persistent;

public class DefaultParentAssociationKeySetTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("DefaultParentAssociationKeySetTest Tests");
		suite.addTestSuite(DefaultParentAssociationKeySetTest.class);
		return suite;
	}

	private DefaultParentAssociationKeySet keySet;

	public DefaultParentAssociationKeySetTest(String name) {
		super(name);
	}

	public void testAddWithNullKeyThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(keySet, "add", new Class[]
			{ MutableTimedAssociationKey.class});
	}

	public void testAddWithExistingKeyReturnsFalse() throws Exception {
		MutableTimedAssociationKey key = ObjectMother.createMutableTimedAssociationKey();
		keySet.add(key);
		assertFalse(keySet.add(key));
		assertEquals(1, keySet.size());
	}

	public void testAddWithOverlappingKeyThrowsInvalidAssociationKeyException() throws Exception {
		MutableTimedAssociationKey key = ObjectMother.createMutableTimedAssociationKey();
		assertTrue(keySet.add(key));
		MutableTimedAssociationKey key2 = ObjectMother.attachEffectiveDateSynonym(ObjectMother.createMutableTimedAssociationKey());
		assertThrowsException(keySet, "add", new Class[]
			{ MutableTimedAssociationKey.class}, new Object[]
			{ key2}, InvalidAssociationKeyException.class);
	}

	public void testAddHappyCase() throws Exception {
		MutableTimedAssociationKey key = ObjectMother.attachExpirationDateSynonym(ObjectMother.createMutableTimedAssociationKey());
		keySet.add(key);

		MutableTimedAssociationKey key2 = ObjectMother.attachEffectiveDateSynonym(ObjectMother.createMutableTimedAssociationKey());
		key2.getEffectiveDate().setDate(key.getExpirationDate().getDate());
		assertTrue(keySet.add(key2));
	}

	public void testGetParentWithNullDateThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(keySet, "getParent", new Class[]
			{ Date.class});
	}

	public void testGetParentWithEmptyKeySetReturnsUassignedID() throws Exception {
		assertEquals(Persistent.UNASSIGNED_ID, keySet.getParent(new Date()));
	}

	public void testGetParentWithInvalidDateReturnsUassignedID() throws Exception {
		MutableTimedAssociationKey key = ObjectMother.attachExpirationDateSynonym(ObjectMother.attachEffectiveDateSynonym(ObjectMother.createMutableTimedAssociationKey()));
		keySet.add(key);
		assertEquals(Persistent.UNASSIGNED_ID, keySet.getParent(new Date(key.getExpirationDate().getDate().getTime() + 100)));
	}

	public void testGetParentHappyCase() throws Exception {
		MutableTimedAssociationKey key = ObjectMother.attachExpirationDateSynonym(ObjectMother.attachEffectiveDateSynonym(ObjectMother.createMutableTimedAssociationKey()));
		key.getExpirationDate().setDate(new Date(key.getEffectiveDate().getDate().getTime() + 100));
		keySet.add(key);
		int id = keySet.getParent(key.getEffectiveDate().getDate());
		assertEquals(key.getAssociableID(), id);
	}

	public void testGetParentAssociationWithNullDateThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(keySet, "getParentAssociation", new Class[]
			{ Date.class});
	}

	public void testGetParentAssociationWithEmptyKeySetReturnsNull() throws Exception {
		assertNull(keySet.getParentAssociation(new Date()));
	}

	public void testGetParentAssociationWithInvalidDateReturnsNull() throws Exception {
		MutableTimedAssociationKey key = ObjectMother.attachExpirationDateSynonym(ObjectMother.attachEffectiveDateSynonym(ObjectMother.createMutableTimedAssociationKey()));
		keySet.add(key);
		assertNull(keySet.getParentAssociation(new Date(key.getExpirationDate().getDate().getTime() + 100)));
	}

	public void testGetParentAssociationHappyCase() throws Exception {
		MutableTimedAssociationKey key = ObjectMother.attachExpirationDateSynonym(ObjectMother.attachEffectiveDateSynonym(ObjectMother.createMutableTimedAssociationKey()));
		key.getExpirationDate().setDate(new Date(key.getEffectiveDate().getDate().getTime() + 100));
		keySet.add(key);
		TimedAssociationKey key2 = keySet.getParentAssociation(key.getEffectiveDate().getDate());
		assertNotNull(key2);
		assertEquals(key2, key);
		assertFalse(key2 instanceof MutableTimedAssociationKey);
	}

	public void testHasParentWithNullDateThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(keySet, "hasParent", new Class[]
			{ Date.class});
	}

	public void testHasParentPositiveCase() throws Exception {
		MutableTimedAssociationKey key = ObjectMother.attachExpirationDateSynonym(ObjectMother.attachEffectiveDateSynonym(ObjectMother.createMutableTimedAssociationKey()));
		key.getExpirationDate().setDate(new Date(key.getEffectiveDate().getDate().getTime() + 100));
		keySet.add(key);
		assertTrue(keySet.hasParent(key.getEffectiveDate().getDate()));
	}

	public void testHasParentNegativeCase() throws Exception {
		assertFalse(keySet.hasParent(new Date()));

		MutableTimedAssociationKey key = ObjectMother.attachExpirationDateSynonym(ObjectMother.attachEffectiveDateSynonym(ObjectMother.createMutableTimedAssociationKey()));
		keySet.add(key);
		assertFalse(keySet.hasParent(new Date(key.getEffectiveDate().getDate().getTime() - 100)));
	}

	protected void setUp() throws Exception {
		super.setUp();
		keySet = new DefaultParentAssociationKeySet();
	}

	protected void tearDown() throws Exception {
		// Tear downs for DefaultParentAssociationKeySetTest
		super.tearDown();
	}
}
