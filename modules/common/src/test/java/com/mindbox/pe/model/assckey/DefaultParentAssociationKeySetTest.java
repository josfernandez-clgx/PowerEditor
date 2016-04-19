package com.mindbox.pe.model.assckey;

import static com.mindbox.pe.common.CommonTestObjectMother.attachEffectiveDateSynonym;
import static com.mindbox.pe.common.CommonTestObjectMother.attachExpirationDateSynonym;
import static com.mindbox.pe.common.CommonTestObjectMother.createMutableTimedAssociationKey;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.unittest.AbstractTestBase;

public class DefaultParentAssociationKeySetTest extends AbstractTestBase {

	private DefaultParentAssociationKeySet keySet;

	@Before
	public void setUp() throws Exception {
		keySet = new DefaultParentAssociationKeySet();
	}

	@Test
	public void testAddHappyCase() throws Exception {
		MutableTimedAssociationKey key = attachExpirationDateSynonym(createMutableTimedAssociationKey());
		keySet.add(key);

		MutableTimedAssociationKey key2 = attachEffectiveDateSynonym(createMutableTimedAssociationKey());
		key2.getEffectiveDate().setDate(key.getExpirationDate().getDate());
		assertTrue(keySet.add(key2));
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
		assertThrowsException(
				keySet,
				"add",
				new Class[] { MutableTimedAssociationKey.class },
				new Object[] { key2 },
				InvalidAssociationKeyException.class);
	}

	@Test
	public void testGetParentAssociationHappyCase() throws Exception {
		MutableTimedAssociationKey key = attachExpirationDateSynonym(attachEffectiveDateSynonym(createMutableTimedAssociationKey()));
		key.getExpirationDate().setDate(new Date(key.getEffectiveDate().getDate().getTime() + 100));
		keySet.add(key);
		TimedAssociationKey key2 = keySet.getParentAssociation(key.getEffectiveDate().getDate());
		assertNotNull(key2);
		assertEquals(key2, key);
		assertFalse(key2 instanceof MutableTimedAssociationKey);
	}

	@Test
	public void testGetParentAssociationWithEmptyKeySetReturnsNull() throws Exception {
		assertNull(keySet.getParentAssociation(new Date()));
	}

	@Test
	public void testGetParentAssociationWithInvalidDateReturnsNull() throws Exception {
		MutableTimedAssociationKey key = attachExpirationDateSynonym(attachEffectiveDateSynonym(createMutableTimedAssociationKey()));
		keySet.add(key);
		assertNull(keySet.getParentAssociation(new Date(key.getExpirationDate().getDate().getTime() + 100)));
	}

	@Test
	public void testGetParentAssociationWithNullDateThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(keySet, "getParentAssociation", new Class[] { Date.class });
	}

	@Test
	public void testGetParentHappyCase() throws Exception {
		MutableTimedAssociationKey key = attachExpirationDateSynonym(attachEffectiveDateSynonym(createMutableTimedAssociationKey()));
		key.getExpirationDate().setDate(new Date(key.getEffectiveDate().getDate().getTime() + 100));
		keySet.add(key);
		int id = keySet.getParent(key.getEffectiveDate().getDate());
		assertEquals(key.getAssociableID(), id);
	}

	@Test
	public void testGetParentWithEmptyKeySetReturnsUassignedID() throws Exception {
		assertEquals(Persistent.UNASSIGNED_ID, keySet.getParent(new Date()));
	}

	@Test
	public void testGetParentWithInvalidDateReturnsUassignedID() throws Exception {
		MutableTimedAssociationKey key = attachExpirationDateSynonym(attachEffectiveDateSynonym(createMutableTimedAssociationKey()));
		keySet.add(key);
		assertEquals(Persistent.UNASSIGNED_ID, keySet.getParent(new Date(key.getExpirationDate().getDate().getTime() + 100)));
	}

	@Test
	public void testGetParentWithNullDateThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(keySet, "getParent", new Class[] { Date.class });
	}

	@Test
	public void testHasParentNegativeCase() throws Exception {
		assertFalse(keySet.hasParent(new Date()));

		MutableTimedAssociationKey key = attachExpirationDateSynonym(attachEffectiveDateSynonym(createMutableTimedAssociationKey()));
		keySet.add(key);
		assertFalse(keySet.hasParent(new Date(key.getEffectiveDate().getDate().getTime() - 100)));
	}

	@Test
	public void testHasParentPositiveCase() throws Exception {
		MutableTimedAssociationKey key = attachExpirationDateSynonym(attachEffectiveDateSynonym(createMutableTimedAssociationKey()));
		key.getExpirationDate().setDate(new Date(key.getEffectiveDate().getDate().getTime() + 100));
		keySet.add(key);
		assertTrue(keySet.hasParent(key.getEffectiveDate().getDate()));
	}

	@Test
	public void testHasParentWithNullDateThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(keySet, "hasParent", new Class[] { Date.class });
	}

}
