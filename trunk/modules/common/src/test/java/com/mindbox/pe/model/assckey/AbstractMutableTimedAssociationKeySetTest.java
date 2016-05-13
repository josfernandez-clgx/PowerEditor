package com.mindbox.pe.model.assckey;

import static com.mindbox.pe.common.CommonTestObjectMother.attachEffectiveDateSynonym;
import static com.mindbox.pe.common.CommonTestObjectMother.attachExpirationDateSynonym;
import static com.mindbox.pe.common.CommonTestObjectMother.createDateSynonym;
import static com.mindbox.pe.common.CommonTestObjectMother.createMutableTimedAssociationKey;
import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.unittest.AbstractTestBase;

/**
 * Unit tests for {@link AbstractMutableTimedAssociationKeySet}.
 * 
 * @author Geneho Kim
 * @since 5.1.0
 */
public class AbstractMutableTimedAssociationKeySetTest extends AbstractTestBase {

	private static class TestImpl extends AbstractMutableTimedAssociationKeySet {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8235474469505977338L;

	}

	private AbstractMutableTimedAssociationKeySet keySet;

	@Before
	public void setUp() throws Exception {
		keySet = new TestImpl();
	}

	@Test
	public void testAddAllHappyCase() throws Exception {
		keySet.add(createMutableTimedAssociationKey());
		AbstractMutableTimedAssociationKeySet keySet2 = new TestImpl();
		keySet2.add(createMutableTimedAssociationKey());
		keySet2.add(createMutableTimedAssociationKey());

		keySet.addAll(keySet2);
		assertEquals(3, keySet.size());
	}

	@Test
	public void testAddAllTakesEmptySet() throws Exception {
		keySet.add(createMutableTimedAssociationKey());
		keySet.addAll(new TestImpl());
		assertEquals(1, keySet.size());
	}

	@Test
	public void testAddAllWithNullKeySetThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(keySet, "addAll", new Class[] { MutableTimedAssociationKeySet.class });
	}

	@Test
	public void testGetAllHappyCase() throws Exception {
		MutableTimedAssociationKey key = attachExpirationDateSynonym(attachEffectiveDateSynonym(createMutableTimedAssociationKey()));
		keySet.add(key);
		MutableTimedAssociationKey key2 = createMutableTimedAssociationKey();
		key2.setAssociableID(key.getAssociableID());
		key2.setEffectiveDate(key.getEffectiveDate());
		keySet.add(key2);
		keySet.add(createMutableTimedAssociationKey());

		List<MutableTimedAssociationKey> list = keySet.getAll(key.getAssociableID());
		assertEquals(2, list.size());
		assertEquals(key.getAssociableID(), list.get(0).getAssociableID());
		assertEquals(key.getAssociableID(), list.get(1).getAssociableID());
	}

	@Test
	public void testGetAllWithEmptySetReturnsEmptyList() throws Exception {
		assertEquals(0, keySet.getAll(createInt()).size());
	}

	@Test
	public void testGetAllWithInvalidIDReturnsEmptyList() throws Exception {
		keySet.add(createMutableTimedAssociationKey());
		assertEquals(0, keySet.getAll(createInt()).size());
	}

	@Test
	public void testIsInUseNegativeCaseWithEmptyKeySet() throws Exception {
		assertFalse(keySet.isInUse(createDateSynonym()));
	}

	@Test
	public void testIsInUseNegativeCaseWithNonEmptyKeySet() throws Exception {
		DefaultMutableTimedAssociationKey key = new DefaultMutableTimedAssociationKey(1, createDateSynonym(), createDateSynonym());
		keySet.add(key);
		assertFalse(keySet.isInUse(createDateSynonym()));
	}

	@Test
	public void testIsInUsePositiveCaseForEffectiveDate() throws Exception {
		DateSynonym dateSynonym = createDateSynonym();
		DefaultMutableTimedAssociationKey key = new DefaultMutableTimedAssociationKey(1, dateSynonym, null);
		keySet.add(key);
		assertTrue(keySet.isInUse(dateSynonym));
	}

	@Test
	public void testIsInUsePositiveCaseForExpirationDate() throws Exception {
		DateSynonym dateSynonym = createDateSynonym();
		DefaultMutableTimedAssociationKey key = new DefaultMutableTimedAssociationKey(1, null, dateSynonym);
		keySet.add(key);
		assertTrue(keySet.isInUse(dateSynonym));
	}

	@Test
	public void testIsInUseWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(keySet, "isInUse", new Class[] { DateSynonym.class });
	}

	@Test
	public void testRemoveAllHappyCase() throws Exception {
		MutableTimedAssociationKey key = attachExpirationDateSynonym(attachEffectiveDateSynonym(createMutableTimedAssociationKey()));
		keySet.add(key);
		MutableTimedAssociationKey key2 = createMutableTimedAssociationKey();
		key2.setAssociableID(key.getAssociableID());
		key2.setEffectiveDate(key.getEffectiveDate());
		keySet.add(key2);

		key2 = createMutableTimedAssociationKey();
		keySet.add(key2);

		keySet.removeAll(key.getAssociableID());
		assertEquals(1, keySet.size());
		assertTrue(keySet.contains(key2));
	}

	@Test
	public void testRemoveAllWithEmptySetIsNoOp() throws Exception {
		keySet.removeAll(createInt());
		assertEquals(0, keySet.size());
	}

	@Test
	public void testRemoveAllWithInvalidIDIsNoOp() throws Exception {
		keySet.add(createMutableTimedAssociationKey());
		keySet.removeAll(createInt());
		assertEquals(1, keySet.size());
	}

}
