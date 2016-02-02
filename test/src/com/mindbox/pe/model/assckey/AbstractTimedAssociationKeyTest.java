package com.mindbox.pe.model.assckey;

import java.util.Date;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.DateSynonym;

/**
 * Unit tests for {@link AbstractTimedAssociationKey}.
 */
public class AbstractTimedAssociationKeyTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static TestSuite suite() {
		TestSuite suite = new TestSuite("AbstractTimedAssociationKeyTest Tests");
		suite.addTestSuite(AbstractTimedAssociationKeyTest.class);
		return suite;
	}

	@SuppressWarnings("serial")
	private static class TimedAssociationKeyImpl extends AbstractTimedAssociationKey {

		public TimedAssociationKeyImpl(int associableID, DateSynonym effDate, DateSynonym expDate) {
			super(associableID, effDate, expDate);
		}
	}

	private AbstractTimedAssociationKey associationKey = null;

	public AbstractTimedAssociationKeyTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		associationKey = new TimedAssociationKeyImpl(ObjectMother.createInt(), null, null);
	}

	public void testConstructorAccceptsNullEffAndExpDate() {
		assertNull(associationKey.getEffectiveDate());
		assertNull(associationKey.getExpirationDate());
	}

	public void testConstructorAccceptsNonNullEffAndNullExpDate() {
		TimedAssociationKeyImpl key = new TimedAssociationKeyImpl(1, DateSynonym.createUnnamedInstance(getDate(2000, 1, 1)), null);
		assertEquals(DateSynonym.createUnnamedInstance(getDate(2000, 1, 1)), key.getEffectiveDate());
		assertNull(key.getExpirationDate());
	}

	public void testConstructorAccceptsNullEffAndNonNullExpDate() {
		AbstractTimedAssociationKey key = new TimedAssociationKeyImpl(1, null, DateSynonym.createUnnamedInstance(getDate(2020, 1, 1)));
		assertNull(key.getEffectiveDate());
		assertEquals(DateSynonym.createUnnamedInstance(getDate(2020, 1, 1)), key.getExpirationDate());
	}

	public void testEqualsPositiveCaseReflexive() throws Exception {
		assertTrue(associationKey.equals(associationKey));
	}

	public void testEqualsPositiveCaseSymmetic() throws Exception {
		associationKey.setEffectiveDate(ObjectMother.createDateSynonym());
		AbstractTimedAssociationKey key2 = new TimedAssociationKeyImpl(associationKey.getAssociableID(), associationKey.getEffectiveDate(), null);
		assertTrue(associationKey.equals(key2));
		assertTrue(key2.equals(associationKey));
	}

	public void testEqualsPositiveCaseTransitive() throws Exception {
		associationKey.setExpirationDate(ObjectMother.createDateSynonym());
		AbstractTimedAssociationKey key2 = new TimedAssociationKeyImpl(associationKey.getAssociableID(), null, associationKey.getExpirationDate());
		AbstractTimedAssociationKey key3 = new TimedAssociationKeyImpl(associationKey.getAssociableID(), null, associationKey.getExpirationDate());
		assertTrue(associationKey.equals(key2));
		assertTrue(key2.equals(key3));
		assertTrue(associationKey.equals(key3));
	}

	public void testEqualsPositiveCaseConsistent() throws Exception {
		associationKey.setEffectiveDate(ObjectMother.createDateSynonym());
		associationKey.setExpirationDate(ObjectMother.createDateSynonym());
		AbstractTimedAssociationKey key2 = new TimedAssociationKeyImpl(
				associationKey.getAssociableID(),
				associationKey.getEffectiveDate(),
				associationKey.getExpirationDate());
		AbstractTimedAssociationKey key3 = new TimedAssociationKeyImpl(ObjectMother.createInt(), associationKey.getEffectiveDate(), associationKey.getExpirationDate());
		int threshold = 4;
		for (int i = 0; i < threshold; i++) {
			assertTrue(associationKey.equals(key2));
			assertFalse(associationKey.equals(key3));
		}
	}

	public void testEqualsNegativeCaseWithNull() throws Exception {
		assertFalse(associationKey.equals(null));
	}

	public void testEqualsNegativeCaseWithDifferentType() throws Exception {
		assertFalse(associationKey.equals(String.valueOf(associationKey.getAssociableID())));
	}

	public void testEqualsNegativeCaseWithUnequalInstance() throws Exception {
		assertFalse(associationKey.equals(new TimedAssociationKeyImpl(ObjectMother.createInt(), null, null)));

		associationKey.setEffectiveDate(ObjectMother.createDateSynonym());
		AbstractTimedAssociationKey key2 = new TimedAssociationKeyImpl(associationKey.getAssociableID(), null, null);
		assertFalse(associationKey.equals(key2));
		assertFalse(key2.equals(associationKey));

		associationKey.setExpirationDate(ObjectMother.createDateSynonym());
		AbstractTimedAssociationKey key3 = new TimedAssociationKeyImpl(associationKey.getAssociableID(), associationKey.getEffectiveDate(), null);
		assertFalse(associationKey.equals(key3));
		assertFalse(key3.equals(associationKey));
	}

	public void testHashCodeProducesSameResultOnEqualInstances() throws Exception {
		AbstractTimedAssociationKey key2 = new TimedAssociationKeyImpl(associationKey.getAssociableID(), null, null);
		// when both eff and exp dates are null
		AbstractTimedAssociationKey key3 = new TimedAssociationKeyImpl(associationKey.getAssociableID(), null, null);
		assertEquals(associationKey.hashCode(), key2.hashCode());
		assertEquals(associationKey.hashCode(), key3.hashCode());

		// when only exp date is null
		associationKey.setEffectiveDate(ObjectMother.createDateSynonym());
		key2.setEffectiveDate(associationKey.getEffectiveDate());
		key3.setEffectiveDate(associationKey.getEffectiveDate());
		assertEquals(associationKey.hashCode(), key2.hashCode());
		assertEquals(associationKey.hashCode(), key3.hashCode());

		// when both eff and exp is not null
		associationKey.setExpirationDate(ObjectMother.createDateSynonym());
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

	public void testHashCodeCacheIsSetProperlyOnModificationOfInvariants() throws Exception {
		int threshold = 3;

		int previousHashCode = associationKey.hashCode();
		for (int i = 0; i < threshold; i++) {
			assertEquals(previousHashCode, associationKey.hashCode());
		}

		// check that cached hashcode changes
		associationKey.setEffectiveDate(ObjectMother.createDateSynonym());
		assertNotEquals(previousHashCode, associationKey.hashCode());
		previousHashCode = associationKey.hashCode();
		for (int i = 0; i < threshold; i++) {
			assertEquals(previousHashCode, associationKey.hashCode());
		}

		associationKey.setExpirationDate(ObjectMother.createDateSynonym());
		assertNotEquals(previousHashCode, associationKey.hashCode());
		previousHashCode = associationKey.hashCode();
		for (int i = 0; i < threshold; i++) {
			assertEquals(previousHashCode, associationKey.hashCode());
		}

		associationKey.setAssociableID(ObjectMother.createInt());
		assertNotEquals(previousHashCode, associationKey.hashCode());
		previousHashCode = associationKey.hashCode();
		for (int i = 0; i < threshold; i++) {
			assertEquals(previousHashCode, associationKey.hashCode());
		}
	}

	public void testIsEffectiveAtWithNullDateThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(associationKey, "isEffectiveAt", new Class[]
			{ Date.class});
	}

	public void testIsEffectiveAtPositiveCaseWithNullDates() throws Exception {
		assertTrue(associationKey.isEffectiveAt(new Date()));
		assertTrue(associationKey.isEffectiveAt(getDate(1900 + (int) Math.round(Math.random() * 1000), 1, 1)));
	}

	public void testIsEffectiveAtPositiveCaseWithNoExpDate() throws Exception {
		associationKey.setEffectiveDate(ObjectMother.createDateSynonym());
		assertTrue(associationKey.isEffectiveAt(associationKey.getEffectiveDate().getDate()));
		assertTrue(associationKey.isEffectiveAt(new Date(associationKey.getEffectiveDate().getDate().getTime() + 1000000)));
	}

	public void testIsEffectiveAtPositiveCaseWithNoEffDate() throws Exception {
		associationKey.setExpirationDate(ObjectMother.createDateSynonym());
		assertTrue(associationKey.isEffectiveAt(new Date(associationKey.getExpirationDate().getDate().getTime() - 1000000)));
	}

	public void testIsEffectiveAtPositiveCaseWithBothEffAndExpDate() throws Exception {
		associationKey.setEffectiveDate(ObjectMother.createDateSynonym());
		associationKey.setExpirationDate(ObjectMother.createDateSynonym());
		associationKey.getExpirationDate().setDate(new Date(associationKey.getEffectiveDate().getDate().getTime() + 10000));
		assertTrue(associationKey.isEffectiveAt(associationKey.getEffectiveDate().getDate()));
		assertTrue(associationKey.isEffectiveAt(new Date(associationKey.getEffectiveDate().getDate().getTime() + 500)));
	}

	public void testIsEffectiveAtNegativeCaseBeforeEffectiveDate() throws Exception {
		associationKey.setEffectiveDate(ObjectMother.createDateSynonym());
		assertFalse(associationKey.isEffectiveAt(new Date(associationKey.getEffectiveDate().getDate().getTime() - 10)));
	}

	public void testIsEffectiveAtNegativeCaseOnExpirationDate() throws Exception {
		associationKey.setExpirationDate(ObjectMother.createDateSynonym());
		assertFalse(associationKey.isEffectiveAt(associationKey.getExpirationDate().getDate()));
	}

	public void testIsEffectiveAtNegativeCaseAfterExpirationDate() throws Exception {
		associationKey.setExpirationDate(ObjectMother.createDateSynonym());
		assertFalse(associationKey.isEffectiveAt(new Date(associationKey.getExpirationDate().getDate().getTime() + 10)));
	}

	public void testOverlapsWithWithNullDateThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(associationKey, "overlapsWith", new Class[]
			{ TimedAssociationKey.class});
	}

	public void testOverlapsWithHappyCaseWithNullDates() throws Exception {
		assertTrue(associationKey.overlapsWith(associationKey));

		AbstractTimedAssociationKey key2 = new TimedAssociationKeyImpl(ObjectMother.createInt(), null, null);
		assertTrue(associationKey.overlapsWith(key2));

		key2.setEffectiveDate(ObjectMother.createDateSynonym());
		assertTrue(associationKey.overlapsWith(key2));

		key2.setExpirationDate(ObjectMother.createDateSynonym());
		assertTrue(associationKey.overlapsWith(key2));

		key2.setEffectiveDate(null);
		assertTrue(associationKey.overlapsWith(key2));

	}

	public void testOverlapsWithHappyCaseWithNoEffDate() throws Exception {
		associationKey.setExpirationDate(ObjectMother.createDateSynonym());
		assertTrue(associationKey.overlapsWith(associationKey));

		// key2 has no dates
		AbstractTimedAssociationKey key2 = new TimedAssociationKeyImpl(ObjectMother.createInt(), null, null);
		assertTrue(associationKey.overlapsWith(key2));

		// key2 has only eff date
		key2.setEffectiveDate(ObjectMother.createDateSynonym());
		key2.getEffectiveDate().setDate(new Date(associationKey.getExpirationDate().getDate().getTime() + 100));
		assertFalse(associationKey.overlapsWith(key2));

		key2.getEffectiveDate().setDate(associationKey.getExpirationDate().getDate());
		assertFalse(associationKey.overlapsWith(key2));

		key2.getEffectiveDate().setDate(new Date(associationKey.getExpirationDate().getDate().getTime() - 100));
		assertTrue(associationKey.overlapsWith(key2));

		// key2 has both dates
		key2.setExpirationDate(ObjectMother.createDateSynonym());
		key2.getExpirationDate().setDate(new Date(associationKey.getExpirationDate().getDate().getTime() + 10000));
		key2.getEffectiveDate().setDate(associationKey.getExpirationDate().getDate());
		assertFalse(associationKey.overlapsWith(key2));

		key2.getEffectiveDate().setDate(new Date(associationKey.getExpirationDate().getDate().getTime() - 100));
		assertTrue(associationKey.overlapsWith(key2));

		key2.getEffectiveDate().setDate(new Date(associationKey.getExpirationDate().getDate().getTime() + 100));
		assertFalse(associationKey.overlapsWith(key2));

		// key2 has only exp date
		key2.setEffectiveDate(null);
		key2.setExpirationDate(ObjectMother.createDateSynonym());
		assertTrue(associationKey.overlapsWith(key2));

		key2.getExpirationDate().setDate(associationKey.getExpirationDate().getDate());
		assertTrue(associationKey.overlapsWith(key2));

		key2.getExpirationDate().setDate(new Date(associationKey.getExpirationDate().getDate().getTime() - 100));
		assertTrue(associationKey.overlapsWith(key2));

		key2.getExpirationDate().setDate(new Date(associationKey.getExpirationDate().getDate().getTime() + 100));
		assertTrue(associationKey.overlapsWith(key2));
	}

	public void testOverlapsWithHappyCaseWithNoExpDate() throws Exception {
		associationKey.setEffectiveDate(ObjectMother.createDateSynonym());
		assertTrue(associationKey.overlapsWith(associationKey));

		// key2 has no dates
		AbstractTimedAssociationKey key2 = new TimedAssociationKeyImpl(ObjectMother.createInt(), null, null);
		assertTrue(associationKey.overlapsWith(key2));

		// key2 has only eff date
		key2.setEffectiveDate(ObjectMother.createDateSynonym());
		key2.getEffectiveDate().setDate(new Date(associationKey.getEffectiveDate().getDate().getTime() + 100));
		assertTrue(associationKey.overlapsWith(key2));

		key2.getEffectiveDate().setDate(associationKey.getEffectiveDate().getDate());
		assertTrue(associationKey.overlapsWith(key2));

		key2.getEffectiveDate().setDate(new Date(associationKey.getEffectiveDate().getDate().getTime() - 100));
		assertTrue(associationKey.overlapsWith(key2));

		// key2 has both dates
		key2.setExpirationDate(ObjectMother.createDateSynonym());
		key2.getExpirationDate().setDate(associationKey.getEffectiveDate().getDate());
		assertFalse(associationKey.overlapsWith(key2));

		key2.getExpirationDate().setDate(new Date(associationKey.getEffectiveDate().getDate().getTime() - 100));
		assertFalse(associationKey.overlapsWith(key2));

		key2.getExpirationDate().setDate(new Date(associationKey.getEffectiveDate().getDate().getTime() + 100));
		assertTrue(associationKey.overlapsWith(key2));

		// key2 has only exp date
		key2.setEffectiveDate(null);
		key2.setExpirationDate(ObjectMother.createDateSynonym());
		key2.getExpirationDate().setDate(associationKey.getEffectiveDate().getDate());
		assertFalse(associationKey.overlapsWith(key2));

		key2.getExpirationDate().setDate(new Date(associationKey.getEffectiveDate().getDate().getTime() - 100));
		assertFalse(associationKey.overlapsWith(key2));

		key2.getExpirationDate().setDate(new Date(associationKey.getEffectiveDate().getDate().getTime() + 100));
		assertTrue(associationKey.overlapsWith(key2));
	}

	public void testOverlapsWithHappyCaseWithEffAndExpDates() throws Exception {
		associationKey.setEffectiveDate(ObjectMother.createDateSynonym());
		associationKey.setExpirationDate(ObjectMother.createDateSynonym());
		associationKey.getExpirationDate().setDate(new Date(associationKey.getEffectiveDate().getDate().getTime() + 1000));
		assertTrue(associationKey.overlapsWith(associationKey));
		
		// key2 has no dates
		AbstractTimedAssociationKey key2 = new TimedAssociationKeyImpl(ObjectMother.createInt(), null, null);
		assertTrue(associationKey.overlapsWith(key2));

		// key2 has only eff date
		key2.setEffectiveDate(ObjectMother.createDateSynonym());
		key2.getEffectiveDate().setDate(new Date(associationKey.getExpirationDate().getDate().getTime() + 100));
		assertFalse(associationKey.overlapsWith(key2));

		key2.getEffectiveDate().setDate(associationKey.getExpirationDate().getDate());
		assertFalse(associationKey.overlapsWith(key2));

		key2.getEffectiveDate().setDate(new Date(associationKey.getExpirationDate().getDate().getTime() - 100));
		assertTrue(associationKey.overlapsWith(key2));

		// key2 has both dates
		key2.setExpirationDate(ObjectMother.createDateSynonym());
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
		key2.setExpirationDate(ObjectMother.createDateSynonym());
		key2.getExpirationDate().setDate(associationKey.getEffectiveDate().getDate());
		assertFalse(associationKey.overlapsWith(key2));

		key2.getExpirationDate().setDate(new Date(associationKey.getEffectiveDate().getDate().getTime() - 100));
		assertFalse(associationKey.overlapsWith(key2));

		key2.getExpirationDate().setDate(new Date(associationKey.getEffectiveDate().getDate().getTime() + 100));
		assertTrue(associationKey.overlapsWith(key2));
	}
}
