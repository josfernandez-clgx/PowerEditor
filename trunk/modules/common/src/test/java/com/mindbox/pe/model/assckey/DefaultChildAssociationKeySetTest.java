package com.mindbox.pe.model.assckey;

import static com.mindbox.pe.common.CommonTestObjectMother.attachEffectiveDateSynonym;
import static com.mindbox.pe.common.CommonTestObjectMother.attachExpirationDateSynonym;
import static com.mindbox.pe.common.CommonTestObjectMother.createMutableTimedAssociationKey;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

/**
 * Tets for {@link DefaultChildAssociationKeySet} class.
 * 
 * @author Geneho Kim
 * @since 5.1.0
 */
public class DefaultChildAssociationKeySetTest extends AbstractTestBase {

	private DefaultChildAssociationKeySet keySet = null;

	@Before
	public void setUp() throws Exception {
		keySet = new DefaultChildAssociationKeySet();
	}

	@Test
	public void testAddAcceptsMoreThanOne() throws Exception {
		assertTrue(keySet.add(createMutableTimedAssociationKey()));
		assertTrue(keySet.add(createMutableTimedAssociationKey()));
		assertEquals(2, keySet.size());
	}

	@Test
	public void testAddWithExistingKeyReturnsFalse() throws Exception {
		MutableTimedAssociationKey key = createMutableTimedAssociationKey();
		keySet.add(key);
		assertFalse(keySet.add(key));
		assertEquals(1, keySet.size());
	}

	@Test
	public void testAddWithNullKeyThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(keySet, "add", new Class[] { MutableTimedAssociationKey.class });
	}

	@Test
	public void testAddWithOverlappingKeyThrowsInvalidAssociationKeyException() throws Exception {
		MutableTimedAssociationKey key = createMutableTimedAssociationKey();
		assertTrue(keySet.add(key));
		MutableTimedAssociationKey key2 = attachEffectiveDateSynonym(createMutableTimedAssociationKey());
		key2.setAssociableID(key.getAssociableID());
		assertThrowsException(
				keySet,
				"add",
				new Class[] { MutableTimedAssociationKey.class },
				new Object[] { key2 },
				InvalidAssociationKeyException.class);
	}

	@Test
	public void testGetAssociationsForChildHappyCase() throws Exception {
		MutableTimedAssociationKey key = attachExpirationDateSynonym(attachEffectiveDateSynonym(createMutableTimedAssociationKey()));
		keySet.add(key);
		MutableTimedAssociationKey key2 = attachExpirationDateSynonym(attachEffectiveDateSynonym(createMutableTimedAssociationKey()));
		keySet.add(key2);
		List<MutableTimedAssociationKey> list = keySet.getAssociationsForChild(key.getAssociableID());
		assertEquals(1, list.size());
		assertEquals(key, list.get(0));
	}

	@Test
	public void testGetAssociationsForChildWithEmptyKeySetReturnsEmptyList() throws Exception {
		assertTrue(((List<MutableTimedAssociationKey>) keySet.getAssociationsForChild(0)).isEmpty());
	}

	@Test
	public void testGetAssociationsForChildWithNotFoundIDReturnsEmptyList() throws Exception {
		MutableTimedAssociationKey key = attachExpirationDateSynonym(attachEffectiveDateSynonym(createMutableTimedAssociationKey()));
		keySet.add(key);
		assertTrue(((List<MutableTimedAssociationKey>) keySet.getAssociationsForChild(key.getAssociableID() + 1)).isEmpty());
	}

	@Test
	public void testGetChildrendAsOfHappyCase() throws Exception {
		MutableTimedAssociationKey key = attachExpirationDateSynonym(attachEffectiveDateSynonym(createMutableTimedAssociationKey()));
		key.getExpirationDate().setDate(new Date(key.getEffectiveDate().getDate().getTime() + 100));
		keySet.add(key);
		List<Integer> list = keySet.getChildrendAsOf(key.getEffectiveDate().getDate());
		assertEquals(1, list.size());
		assertEquals(key.getAssociableID(), list.get(0).intValue());
	}

	@Test
	public void testGetChildrendAsOfWithEmptyKeySetReturnsEmptyList() throws Exception {
		assertTrue(((List<Integer>) keySet.getChildrendAsOf(new Date())).isEmpty());
	}

	@Test
	public void testGetChildrendAsOfWithInvalidDateReturnsEmptyList() throws Exception {
		MutableTimedAssociationKey key = attachExpirationDateSynonym(attachEffectiveDateSynonym(createMutableTimedAssociationKey()));
		keySet.add(key);
		assertTrue(((List<Integer>) keySet.getChildrendAsOf(new Date(key.getExpirationDate().getDate().getTime() + 100))).isEmpty());
	}

	@Test
	public void testGetChildrendAsOfWithNullDateThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(keySet, "getChildrendAsOf", new Class[] { Date.class });
	}

	@Test
	public void testHasAnyChildAsOfNegativeCase() throws Exception {
		assertFalse(keySet.hasAnyChildAsOf(new Date()));

		MutableTimedAssociationKey key = attachExpirationDateSynonym(attachEffectiveDateSynonym(createMutableTimedAssociationKey()));
		keySet.add(key);
		assertFalse(keySet.hasAnyChildAsOf(new Date(key.getEffectiveDate().getDate().getTime() - 100)));
	}

	@Test
	public void testHasAnyChildAsOfPositiveCase() throws Exception {
		MutableTimedAssociationKey key = attachExpirationDateSynonym(attachEffectiveDateSynonym(createMutableTimedAssociationKey()));
		key.getExpirationDate().setDate(new Date(key.getEffectiveDate().getDate().getTime() + 100));
		keySet.add(key);
		assertTrue(keySet.hasAnyChildAsOf(key.getEffectiveDate().getDate()));
	}

	@Test
	public void testHasAnyChildAsOfWithNullDateThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(keySet, "hasAnyChildAsOf", new Class[] { Date.class });
	}

}
