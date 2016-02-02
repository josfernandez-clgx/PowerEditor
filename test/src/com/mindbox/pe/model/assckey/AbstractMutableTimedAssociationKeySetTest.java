package com.mindbox.pe.model.assckey;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.DateSynonym;

/**
 * Unit tests for {@link AbstractMutableTimedAssociationKeySet}.
 * @author Geneho Kim
 * @since 5.1.0
 */
public class AbstractMutableTimedAssociationKeySetTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractMutableTimedAssociationKeySetTest Tests");
		suite.addTestSuite(AbstractMutableTimedAssociationKeySetTest.class);
		return suite;
	}

	private static class TestImpl extends AbstractMutableTimedAssociationKeySet {

	}

	private AbstractMutableTimedAssociationKeySet keySet;

	public AbstractMutableTimedAssociationKeySetTest(String name) {
		super(name);
	}

	public void testIsInUseWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(keySet, "isInUse", new Class[] { DateSynonym.class });
	}
	
	public void testIsInUsePositiveCaseForEffectiveDate() throws Exception {
		DateSynonym dateSynonym = ObjectMother.createDateSynonym();
		DefaultMutableTimedAssociationKey key = new DefaultMutableTimedAssociationKey(1, dateSynonym, null);
		keySet.add(key);
		assertTrue(keySet.isInUse(dateSynonym));
	}
	
	public void testIsInUsePositiveCaseForExpirationDate() throws Exception {
		DateSynonym dateSynonym = ObjectMother.createDateSynonym();
		DefaultMutableTimedAssociationKey key = new DefaultMutableTimedAssociationKey(1, null, dateSynonym);
		keySet.add(key);
		assertTrue(keySet.isInUse(dateSynonym));
	}
	
	public void testIsInUseNegativeCaseWithEmptyKeySet() throws Exception {
		assertFalse(keySet.isInUse(ObjectMother.createDateSynonym()));
	}

	public void testIsInUseNegativeCaseWithNonEmptyKeySet() throws Exception {
		DefaultMutableTimedAssociationKey key = new DefaultMutableTimedAssociationKey(1, ObjectMother.createDateSynonym(), ObjectMother.createDateSynonym());
		keySet.add(key);
		assertFalse(keySet.isInUse(ObjectMother.createDateSynonym()));
	}

	public void testAddAllWithNullKeySetThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(keySet, "addAll", new Class[] { MutableTimedAssociationKeySet.class });
	}

	public void testAddAllTakesEmptySet() throws Exception {
		keySet.add(ObjectMother.createMutableTimedAssociationKey());
		keySet.addAll(new TestImpl());
		assertEquals(1, keySet.size());
	}

	public void testAddAllHappyCase() throws Exception {
		keySet.add(ObjectMother.createMutableTimedAssociationKey());
		AbstractMutableTimedAssociationKeySet keySet2 = new TestImpl();
		keySet2.add(ObjectMother.createMutableTimedAssociationKey());
		keySet2.add(ObjectMother.createMutableTimedAssociationKey());

		keySet.addAll(keySet2);
		assertEquals(3, keySet.size());
	}

	public void testGetAllWithEmptySetReturnsEmptyList() throws Exception {
		assertEquals(0, keySet.getAll(ObjectMother.createInt()).size());
	}

	public void testGetAllWithInvalidIDReturnsEmptyList() throws Exception {
		keySet.add(ObjectMother.createMutableTimedAssociationKey());
		assertEquals(0, keySet.getAll(ObjectMother.createInt()).size());
	}

	public void testGetAllHappyCase() throws Exception {
		MutableTimedAssociationKey key = ObjectMother.attachExpirationDateSynonym(ObjectMother.attachEffectiveDateSynonym(ObjectMother.createMutableTimedAssociationKey()));
		keySet.add(key);
		MutableTimedAssociationKey key2 = ObjectMother.createMutableTimedAssociationKey();
		key2.setAssociableID(key.getAssociableID());
		key2.setEffectiveDate(key.getEffectiveDate());
		keySet.add(key2);
		keySet.add(ObjectMother.createMutableTimedAssociationKey());

		List<MutableTimedAssociationKey> list = keySet.getAll(key.getAssociableID());
		assertEquals(2, list.size());
		assertEquals(key.getAssociableID(), list.get(0).getAssociableID());
		assertEquals(key.getAssociableID(), list.get(1).getAssociableID());
	}

	protected void setUp() throws Exception {
		super.setUp();
		keySet = new TestImpl();
	}

	public void testRemoveAllWithEmptySetIsNoOp() throws Exception {
		keySet.removeAll(ObjectMother.createInt());
		assertEquals(0, keySet.size());
	}

	public void testRemoveAllWithInvalidIDIsNoOp() throws Exception {
		keySet.add(ObjectMother.createMutableTimedAssociationKey());
		keySet.removeAll(ObjectMother.createInt());
		assertEquals(1, keySet.size());
	}

	public void testRemoveAllHappyCase() throws Exception {
		MutableTimedAssociationKey key = ObjectMother.attachExpirationDateSynonym(ObjectMother.attachEffectiveDateSynonym(ObjectMother.createMutableTimedAssociationKey()));
		keySet.add(key);
		MutableTimedAssociationKey key2 = ObjectMother.createMutableTimedAssociationKey();
		key2.setAssociableID(key.getAssociableID());
		key2.setEffectiveDate(key.getEffectiveDate());
		keySet.add(key2);

		key2 = ObjectMother.createMutableTimedAssociationKey();
		keySet.add(key2);

		keySet.removeAll(key.getAssociableID());
		assertEquals(1, keySet.size());
		assertTrue(keySet.contains(key2));
	}

	protected void tearDown() throws Exception {
		// Tear downs for AbstractMutableTimedAssociationKeySetTest
		super.tearDown();
	}
}
