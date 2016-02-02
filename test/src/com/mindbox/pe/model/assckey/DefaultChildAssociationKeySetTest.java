package com.mindbox.pe.model.assckey;

import java.util.Date;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;

/**
 * Tets for {@link DefaultChildAssociationKeySet} class.
 * @author Geneho Kim
 * @since 5.1.0
 */
public class DefaultChildAssociationKeySetTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("DefaultChildAssociationKeySetTest Tests");
		suite.addTestSuite(DefaultChildAssociationKeySetTest.class);
		return suite;
	}

	private DefaultChildAssociationKeySet keySet = null;

	public DefaultChildAssociationKeySetTest(String name) {
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
		key2.setAssociableID(key.getAssociableID());
		assertThrowsException(keySet, "add", new Class[]
			{ MutableTimedAssociationKey.class}, new Object[]
			{ key2}, InvalidAssociationKeyException.class);
	}

	public void testAddAcceptsMoreThanOne() throws Exception {
		assertTrue(keySet.add(ObjectMother.createMutableTimedAssociationKey()));
		assertTrue(keySet.add(ObjectMother.createMutableTimedAssociationKey()));
		assertEquals(2, keySet.size());
	}

	public void testGetChildrendAsOfWithNullDateThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(keySet, "getChildrendAsOf", new Class[]
			{ Date.class});
	}

	public void testGetChildrendAsOfWithEmptyKeySetReturnsEmptyList() throws Exception {
		assertTrue(((List<Integer>) keySet.getChildrendAsOf(new Date())).isEmpty());
	}

	public void testGetChildrendAsOfWithInvalidDateReturnsEmptyList() throws Exception {
		MutableTimedAssociationKey key = ObjectMother.attachExpirationDateSynonym(ObjectMother.attachEffectiveDateSynonym(ObjectMother.createMutableTimedAssociationKey()));
		keySet.add(key);
		assertTrue(((List<Integer>) keySet.getChildrendAsOf(new Date(key.getExpirationDate().getDate().getTime() + 100))).isEmpty());
	}

	public void testGetChildrendAsOfHappyCase() throws Exception {
		MutableTimedAssociationKey key = ObjectMother.attachExpirationDateSynonym(ObjectMother.attachEffectiveDateSynonym(ObjectMother.createMutableTimedAssociationKey()));
		key.getExpirationDate().setDate(new Date(key.getEffectiveDate().getDate().getTime() + 100));
		keySet.add(key);
		List<Integer> list = keySet.getChildrendAsOf(key.getEffectiveDate().getDate());
		assertEquals(1, list.size());
		assertEquals(key.getAssociableID(), list.get(0).intValue());
	}

	public void testHasAnyChildAsOfWithNullDateThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(keySet, "hasAnyChildAsOf", new Class[]
			{ Date.class});
	}

	public void testHasAnyChildAsOfPositiveCase() throws Exception {
		MutableTimedAssociationKey key = ObjectMother.attachExpirationDateSynonym(ObjectMother.attachEffectiveDateSynonym(ObjectMother.createMutableTimedAssociationKey()));
		key.getExpirationDate().setDate(new Date(key.getEffectiveDate().getDate().getTime() + 100));
		keySet.add(key);
		assertTrue(keySet.hasAnyChildAsOf(key.getEffectiveDate().getDate()));
	}

	public void testHasAnyChildAsOfNegativeCase() throws Exception {
		assertFalse(keySet.hasAnyChildAsOf(new Date()));

		MutableTimedAssociationKey key = ObjectMother.attachExpirationDateSynonym(ObjectMother.attachEffectiveDateSynonym(ObjectMother.createMutableTimedAssociationKey()));
		keySet.add(key);
		assertFalse(keySet.hasAnyChildAsOf(new Date(key.getEffectiveDate().getDate().getTime() - 100)));
	}

	public void testGetAssociationsForChildWithEmptyKeySetReturnsEmptyList() throws Exception {
		assertTrue(((List<MutableTimedAssociationKey>) keySet.getAssociationsForChild(0)).isEmpty());
	}

	public void testGetAssociationsForChildWithNotFoundIDReturnsEmptyList() throws Exception {
		MutableTimedAssociationKey key = ObjectMother.attachExpirationDateSynonym(ObjectMother.attachEffectiveDateSynonym(ObjectMother.createMutableTimedAssociationKey()));
		keySet.add(key);
		assertTrue(((List<MutableTimedAssociationKey>) keySet.getAssociationsForChild(key.getAssociableID() + 1)).isEmpty());
	}

	public void testGetAssociationsForChildHappyCase() throws Exception {
		MutableTimedAssociationKey key = ObjectMother.attachExpirationDateSynonym(ObjectMother.attachEffectiveDateSynonym(ObjectMother.createMutableTimedAssociationKey()));
		keySet.add(key);
		MutableTimedAssociationKey key2 = ObjectMother.attachExpirationDateSynonym(ObjectMother.attachEffectiveDateSynonym(ObjectMother.createMutableTimedAssociationKey()));
		keySet.add(key2);
		List<MutableTimedAssociationKey> list = keySet.getAssociationsForChild(key.getAssociableID());
		assertEquals(1, list.size());
		assertEquals(key, list.get(0));
	}

	protected void setUp() throws Exception {
		super.setUp();
		keySet = new DefaultChildAssociationKeySet();
	}

	protected void tearDown() throws Exception {
		// Tear downs for DefaultChildAssociationKeySetTest
		super.tearDown();
	}
}
