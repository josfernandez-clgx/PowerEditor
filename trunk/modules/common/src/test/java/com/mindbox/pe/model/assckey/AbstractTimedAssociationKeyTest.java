package com.mindbox.pe.model.assckey;

import static com.mindbox.pe.common.CommonTestObjectMother.createDateSynonym;
import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static com.mindbox.pe.unittest.UnitTestHelper.assertNotEquals;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static com.mindbox.pe.unittest.UnitTestHelper.getDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.unittest.AbstractTestBase;

/**
 * Unit tests for {@link AbstractTimedAssociationKey}.
 */
public class AbstractTimedAssociationKeyTest extends AbstractTestBase {

	@SuppressWarnings("serial")
	private static class TimedAssociationKeyImpl extends AbstractTimedAssociationKey {

		public TimedAssociationKeyImpl(int associableID, DateSynonym effDate, DateSynonym expDate) {
			super(associableID, effDate, expDate);
		}
	}

	private AbstractTimedAssociationKey associationKey = null;

	@Before
	public void setUp() throws Exception {
		associationKey = new TimedAssociationKeyImpl(createInt(), null, null);
	}

	@Test
	public void testConstructorAccceptsNonNullEffAndNullExpDate() {
		TimedAssociationKeyImpl key = new TimedAssociationKeyImpl(1, DateSynonym.createUnnamedInstance(getDate(2000, 1, 1)), null);
		assertEquals(DateSynonym.createUnnamedInstance(getDate(2000, 1, 1)), key.getEffectiveDate());
		assertNull(key.getExpirationDate());
	}

	@Test
	public void testConstructorAccceptsNullEffAndExpDate() {
		assertNull(associationKey.getEffectiveDate());
		assertNull(associationKey.getExpirationDate());
	}

	@Test
	public void testConstructorAccceptsNullEffAndNonNullExpDate() {
		AbstractTimedAssociationKey key = new TimedAssociationKeyImpl(1, null, DateSynonym.createUnnamedInstance(getDate(2020, 1, 1)));
		assertNull(key.getEffectiveDate());
		assertEquals(DateSynonym.createUnnamedInstance(getDate(2020, 1, 1)), key.getExpirationDate());
	}

	@Test
	public void testEqualsNegativeCaseWithDifferentType() throws Exception {
		assertFalse(associationKey.equals(String.valueOf(associationKey.getAssociableID())));
	}

	@Test
	public void testEqualsNegativeCaseWithNull() throws Exception {
		assertFalse(associationKey.equals(null));
	}

	@Test
	public void testEqualsNegativeCaseWithUnequalInstance() throws Exception {
		assertFalse(associationKey.equals(new TimedAssociationKeyImpl(createInt(), null, null)));

		associationKey.setEffectiveDate(createDateSynonym());
		AbstractTimedAssociationKey key2 = new TimedAssociationKeyImpl(associationKey.getAssociableID(), null, null);
		assertFalse(associationKey.equals(key2));
		assertFalse(key2.equals(associationKey));

		associationKey.setExpirationDate(createDateSynonym());
		AbstractTimedAssociationKey key3 = new TimedAssociationKeyImpl(associationKey.getAssociableID(), associationKey.getEffectiveDate(), null);
		assertFalse(associationKey.equals(key3));
		assertFalse(key3.equals(associationKey));
	}

	@Test
	public void testEqualsPositiveCaseConsistent() throws Exception {
		associationKey.setEffectiveDate(createDateSynonym());
		associationKey.setExpirationDate(createDateSynonym());
		AbstractTimedAssociationKey key2 = new TimedAssociationKeyImpl(
				associationKey.getAssociableID(),
				associationKey.getEffectiveDate(),
				associationKey.getExpirationDate());
		AbstractTimedAssociationKey key3 = new TimedAssociationKeyImpl(
				createInt(),
				associationKey.getEffectiveDate(),
				associationKey.getExpirationDate());
		int threshold = 4;
		for (int i = 0; i < threshold; i++) {
			assertTrue(associationKey.equals(key2));
			assertFalse(associationKey.equals(key3));
		}
	}

	@Test
	public void testEqualsPositiveCaseReflexive() throws Exception {
		assertTrue(associationKey.equals(associationKey));
	}

	@Test
	public void testEqualsPositiveCaseSymmetic() throws Exception {
		associationKey.setEffectiveDate(createDateSynonym());
		AbstractTimedAssociationKey key2 = new TimedAssociationKeyImpl(associationKey.getAssociableID(), associationKey.getEffectiveDate(), null);
		assertTrue(associationKey.equals(key2));
		assertTrue(key2.equals(associationKey));
	}

	@Test
	public void testEqualsPositiveCaseTransitive() throws Exception {
		associationKey.setExpirationDate(createDateSynonym());
		AbstractTimedAssociationKey key2 = new TimedAssociationKeyImpl(associationKey.getAssociableID(), null, associationKey.getExpirationDate());
		AbstractTimedAssociationKey key3 = new TimedAssociationKeyImpl(associationKey.getAssociableID(), null, associationKey.getExpirationDate());
		assertTrue(associationKey.equals(key2));
		assertTrue(key2.equals(key3));
		assertTrue(associationKey.equals(key3));
	}

	@Test
	public void testHashCodeCacheIsSetProperlyOnModificationOfInvariants() throws Exception {
		int threshold = 3;

		int previousHashCode = associationKey.hashCode();
		for (int i = 0; i < threshold; i++) {
			assertEquals(previousHashCode, associationKey.hashCode());
		}

		// check that cached hashcode changes
		associationKey.setEffectiveDate(createDateSynonym());
		assertNotEquals(previousHashCode, associationKey.hashCode());
		previousHashCode = associationKey.hashCode();
		for (int i = 0; i < threshold; i++) {
			assertEquals(previousHashCode, associationKey.hashCode());
		}

		associationKey.setExpirationDate(createDateSynonym());
		assertNotEquals(previousHashCode, associationKey.hashCode());
		previousHashCode = associationKey.hashCode();
		for (int i = 0; i < threshold; i++) {
			assertEquals(previousHashCode, associationKey.hashCode());
		}

		associationKey.setAssociableID(createInt());
		assertNotEquals(previousHashCode, associationKey.hashCode());
		previousHashCode = associationKey.hashCode();
		for (int i = 0; i < threshold; i++) {
			assertEquals(previousHashCode, associationKey.hashCode());
		}
	}

	@Test
	public void testHashCodeProducesSameResultOnEqualInstances() throws Exception {
		AbstractTimedAssociationKey key2 = new TimedAssociationKeyImpl(associationKey.getAssociableID(), null, null);
		// when both eff and exp dates are null
		AbstractTimedAssociationKey key3 = new TimedAssociationKeyImpl(associationKey.getAssociableID(), null, null);
		assertEquals(associationKey.hashCode(), key2.hashCode());
		assertEquals(associationKey.hashCode(), key3.hashCode());

		// when only exp date is null
		associationKey.setEffectiveDate(createDateSynonym());
		key2.setEffectiveDate(associationKey.getEffectiveDate());
		key3.setEffectiveDate(associationKey.getEffectiveDate());
		assertEquals(associationKey.hashCode(), key2.hashCode());
		assertEquals(associationKey.hashCode(), key3.hashCode());

		// when both eff and exp is not null
		associationKey.setExpirationDate(createDateSynonym());
		key2.setExpirationDate(associationKey.getExpirationDate());
		key3.setExpirationDate(associationKey.getExpirationDate());
		assertEquals(associationKey.hashCode(), key2.hashCode());
		assertEquals(associationKey.hashCode(), key3.hashCode());

		// when only eff date is null
		associationKey.setExpirationDate(null);
		key2.setExpirationDate(null);
		key3.setExpirationDate(null);
		assertEquals(associationKey.hashCode(), key2.hashCode());
		assertEquals(associationKey.hashCode(), key3.hashCode());
	}

	@Test
	public void testIsEffectiveAtNegativeCaseAfterExpirationDate() throws Exception {
		associationKey.setExpirationDate(createDateSynonym());
		assertFalse(associationKey.isEffectiveAt(new Date(associationKey.getExpirationDate().getDate().getTime() + 10)));
	}

	@Test
	public void testIsEffectiveAtNegativeCaseBeforeEffectiveDate() throws Exception {
		associationKey.setEffectiveDate(createDateSynonym());
		assertFalse(associationKey.isEffectiveAt(new Date(associationKey.getEffectiveDate().getDate().getTime() - 10)));
	}

	@Test
	public void testIsEffectiveAtNegativeCaseOnExpirationDate() throws Exception {
		associationKey.setExpirationDate(createDateSynonym());
		assertFalse(associationKey.isEffectiveAt(associationKey.getExpirationDate().getDate()));
	}

	@Test
	public void testIsEffectiveAtPositiveCaseWithBothEffAndExpDate() throws Exception {
		associationKey.setEffectiveDate(createDateSynonym());
		associationKey.setExpirationDate(createDateSynonym());
		associationKey.getExpirationDate().setDate(new Date(associationKey.getEffectiveDate().getDate().getTime() + 10000));
		assertTrue(associationKey.isEffectiveAt(associationKey.getEffectiveDate().getDate()));
		assertTrue(associationKey.isEffectiveAt(new Date(associationKey.getEffectiveDate().getDate().getTime() + 500)));
	}

	@Test
	public void testIsEffectiveAtPositiveCaseWithNoEffDate() throws Exception {
		associationKey.setExpirationDate(createDateSynonym());
		assertTrue(associationKey.isEffectiveAt(new Date(associationKey.getExpirationDate().getDate().getTime() - 1000000)));
	}

	@Test
	public void testIsEffectiveAtPositiveCaseWithNoExpDate() throws Exception {
		associationKey.setEffectiveDate(createDateSynonym());
		assertTrue(associationKey.isEffectiveAt(associationKey.getEffectiveDate().getDate()));
		assertTrue(associationKey.isEffectiveAt(new Date(associationKey.getEffectiveDate().getDate().getTime() + 1000000)));
	}

	@Test
	public void testIsEffectiveAtPositiveCaseWithNullDates() throws Exception {
		assertTrue(associationKey.isEffectiveAt(new Date()));
		assertTrue(associationKey.isEffectiveAt(getDate(1900 + (int) Math.round(Math.random() * 1000), 1, 1)));
	}

	@Test
	public void testIsEffectiveAtWithNullDateThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(associationKey, "isEffectiveAt", new Class[] { Date.class });
	}

	@Test
	public void testOverlapsWithHappyCaseWithEffAndExpDates() throws Exception {
		associationKey.setEffectiveDate(createDateSynonym());
		associationKey.setExpirationDate(createDateSynonym());
		associationKey.getExpirationDate().setDate(new Date(associationKey.getEffectiveDate().getDate().getTime() + 1000));
		assertTrue(associationKey.overlapsWith(associationKey));

		// key2 has no dates
		AbstractTimedAssociationKey key2 = new TimedAssociationKeyImpl(createInt(), null, null);
		assertTrue(associationKey.overlapsWith(key2));

		// key2 has only eff date
		key2.setEffectiveDate(createDateSynonym());
		key2.getEffectiveDate().setDate(new Date(associationKey.getExpirationDate().getDate().getTime() + 100));
		assertFalse(associationKey.overlapsWith(key2));

		key2.getEffectiveDate().setDate(associationKey.getExpirationDate().getDate());
		assertFalse(associationKey.overlapsWith(key2));

		key2.getEffectiveDate().setDate(new Date(associationKey.getExpirationDate().getDate().getTime() - 100));
		assertTrue(associationKey.overlapsWith(key2));

		// key2 has both dates
		key2.setExpirationDate(createDateSynonym());
		key2.getEffectiveDate().setDate(associationKey.getEffectiveDate().getDate());
		key2.getExpirationDate().setDate(associationKey.getExpirationDate().getDate());
		assertTrue(associationKey.overlapsWith(key2));

		key2.getEffectiveDate().setDate(new Date(associationKey.getEffectiveDate().getDate().getTime() - 100));
		key2.getExpirationDate().setDate(associationKey.getEffectiveDate().getDate());
		assertFalse(associationKey.overlapsWith(key2));

		key2.getExpirationDate().setDate(new Date(associationKey.getExpirationDate().getDate().getTime() + 100));
		key2.getEffectiveDate().setDate(associationKey.getExpirationDate().getDate());
		assertFalse(associationKey.overlapsWith(key2));

		// key2 has only exp date
		key2.setEffectiveDate(null);
		key2.setExpirationDate(createDateSynonym());
		key2.getExpirationDate().setDate(associationKey.getEffectiveDate().getDate());
		assertFalse(associationKey.overlapsWith(key2));

		key2.getExpirationDate().setDate(new Date(associationKey.getEffectiveDate().getDate().getTime() - 100));
		assertFalse(associationKey.overlapsWith(key2));

		key2.getExpirationDate().setDate(new Date(associationKey.getEffectiveDate().getDate().getTime() + 100));
		assertTrue(associationKey.overlapsWith(key2));
	}

	@Test
	public void testOverlapsWithHappyCaseWithNoEffDate() throws Exception {
		associationKey.setExpirationDate(createDateSynonym());
		assertTrue(associationKey.overlapsWith(associationKey));

		// key2 has no dates
		AbstractTimedAssociationKey key2 = new TimedAssociationKeyImpl(createInt(), null, null);
		assertTrue(associationKey.overlapsWith(key2));

		// key2 has only eff date
		key2.setEffectiveDate(createDateSynonym());
		key2.getEffectiveDate().setDate(new Date(associationKey.getExpirationDate().getDate().getTime() + 100));
		assertFalse(associationKey.overlapsWith(key2));

		key2.getEffectiveDate().setDate(associationKey.getExpirationDate().getDate());
		assertFalse(associationKey.overlapsWith(key2));

		key2.getEffectiveDate().setDate(new Date(associationKey.getExpirationDate().getDate().getTime() - 100));
		assertTrue(associationKey.overlapsWith(key2));

		// key2 has both dates
		key2.setExpirationDate(createDateSynonym());
		key2.getExpirationDate().setDate(new Date(associationKey.getExpirationDate().getDate().getTime() + 10000));
		key2.getEffectiveDate().setDate(associationKey.getExpirationDate().getDate());
		assertFalse(associationKey.overlapsWith(key2));

		key2.getEffectiveDate().setDate(new Date(associationKey.getExpirationDate().getDate().getTime() - 100));
		assertTrue(associationKey.overlapsWith(key2));

		key2.getEffectiveDate().setDate(new Date(associationKey.getExpirationDate().getDate().getTime() + 100));
		assertFalse(associationKey.overlapsWith(key2));

		// key2 has only exp date
		key2.setEffectiveDate(null);
		key2.setExpirationDate(createDateSynonym());
		assertTrue(associationKey.overlapsWith(key2));

		key2.getExpirationDate().setDate(associationKey.getExpirationDate().getDate());
		assertTrue(associationKey.overlapsWith(key2));

		key2.getExpirationDate().setDate(new Date(associationKey.getExpirationDate().getDate().getTime() - 100));
		assertTrue(associationKey.overlapsWith(key2));

		key2.getExpirationDate().setDate(new Date(associationKey.getExpirationDate().getDate().getTime() + 100));
		assertTrue(associationKey.overlapsWith(key2));
	}

	@Test
	public void testOverlapsWithHappyCaseWithNoExpDate() throws Exception {
		associationKey.setEffectiveDate(createDateSynonym());
		assertTrue(associationKey.overlapsWith(associationKey));

		// key2 has no dates
		AbstractTimedAssociationKey key2 = new TimedAssociationKeyImpl(createInt(), null, null);
		assertTrue(associationKey.overlapsWith(key2));

		// key2 has only eff date
		key2.setEffectiveDate(createDateSynonym());
		key2.getEffectiveDate().setDate(new Date(associationKey.getEffectiveDate().getDate().getTime() + 100));
		assertTrue(associationKey.overlapsWith(key2));

		key2.getEffectiveDate().setDate(associationKey.getEffectiveDate().getDate());
		assertTrue(associationKey.overlapsWith(key2));

		key2.getEffectiveDate().setDate(new Date(associationKey.getEffectiveDate().getDate().getTime() - 100));
		assertTrue(associationKey.overlapsWith(key2));

		// key2 has both dates
		key2.setExpirationDate(createDateSynonym());
		key2.getExpirationDate().setDate(associationKey.getEffectiveDate().getDate());
		assertFalse(associationKey.overlapsWith(key2));

		key2.getExpirationDate().setDate(new Date(associationKey.getEffectiveDate().getDate().getTime() - 100));
		assertFalse(associationKey.overlapsWith(key2));

		key2.getExpirationDate().setDate(new Date(associationKey.getEffectiveDate().getDate().getTime() + 100));
		assertTrue(associationKey.overlapsWith(key2));

		// key2 has only exp date
		key2.setEffectiveDate(null);
		key2.setExpirationDate(createDateSynonym());
		key2.getExpirationDate().setDate(associationKey.getEffectiveDate().getDate());
		assertFalse(associationKey.overlapsWith(key2));

		key2.getExpirationDate().setDate(new Date(associationKey.getEffectiveDate().getDate().getTime() - 100));
		assertFalse(associationKey.overlapsWith(key2));

		key2.getExpirationDate().setDate(new Date(associationKey.getEffectiveDate().getDate().getTime() + 100));
		assertTrue(associationKey.overlapsWith(key2));
	}

	@Test
	public void testOverlapsWithHappyCaseWithNullDates() throws Exception {
		assertTrue(associationKey.overlapsWith(associationKey));

		AbstractTimedAssociationKey key2 = new TimedAssociationKeyImpl(createInt(), null, null);
		assertTrue(associationKey.overlapsWith(key2));

		key2.setEffectiveDate(createDateSynonym());
		assertTrue(associationKey.overlapsWith(key2));

		key2.setExpirationDate(createDateSynonym());
		assertTrue(associationKey.overlapsWith(key2));

		key2.setEffectiveDate(null);
		assertTrue(associationKey.overlapsWith(key2));

	}

	@Test
	public void testOverlapsWithWithNullDateThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(associationKey, "overlapsWith", new Class[] { TimedAssociationKey.class });
	}
}
