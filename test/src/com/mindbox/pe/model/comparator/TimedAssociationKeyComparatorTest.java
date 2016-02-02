package com.mindbox.pe.model.comparator;

import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;

public class TimedAssociationKeyComparatorTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("TimedAssociationKeyComparatorTest Tests");
		suite.addTestSuite(TimedAssociationKeyComparatorTest.class);
		return suite;
	}

	private TimedAssociationKeyComparator comparator;

	public TimedAssociationKeyComparatorTest(String name) {
		super(name);
	}

	public void testCompareWithNullsReturnsZero() throws Exception {
		assertEquals(0, comparator.compare(null, null));
	}

	public void testCompareWithOneNullThrowsNullPointerException() throws Exception {
		MutableTimedAssociationKey key = new DefaultMutableTimedAssociationKey(1, null, null); 
		try {
			comparator.compare(key, null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException e) {
			// expected
		}
		try {
			comparator.compare(null, key);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException e) {
			// expected
		}
	}

	public void testCompareWithNoDatesReturnsZero() throws Exception {
        MutableTimedAssociationKey key1 = new DefaultMutableTimedAssociationKey(1, null, null);
        MutableTimedAssociationKey key2 = new DefaultMutableTimedAssociationKey(2, null, null);        
		assertEquals(0, comparator.compare(key1, key2));
	}

	public void testCompareNoDatesComparedToSomeDate() throws Exception {
		DateSynonym ds = new DateSynonym(1, "1", "desc", new Date());
        MutableTimedAssociationKey key1 = new DefaultMutableTimedAssociationKey(1, null, null);
        MutableTimedAssociationKey key2 = new DefaultMutableTimedAssociationKey(2, ds, null);        

		assertTrue(0 < comparator.compare(key1, key2));
		assertTrue(0 > comparator.compare(key2, key1));

		key2 = new DefaultMutableTimedAssociationKey(2, ds, new DateSynonym(1, "1", "desc", new Date()));
		assertTrue(0 < comparator.compare(key1, key2));
		assertTrue(0 > comparator.compare(key2, key1));
	}

	/**
	 * @throws Exception
     * Earliest/most recent should be more than older dates. So null effective dates are
     * older than non-null effective dates.  
	 */
	public void testCompareOpenEffectiveDateIsLessThanNonNullEffectiveDate() throws Exception {
		DateSynonym ds1 = new DateSynonym(1, "1", "desc1", new Date());
		DateSynonym ds2 = new DateSynonym(2, "2", "desc2", new Date(System.currentTimeMillis() + 10000));
        MutableTimedAssociationKey key1 = new DefaultMutableTimedAssociationKey(1, ds1, null);
        MutableTimedAssociationKey key2 = new DefaultMutableTimedAssociationKey(2, null, ds2);
		assertTrue(0 > comparator.compare(key1, key2));
		assertTrue(0 < comparator.compare(key2, key1));
	}

	/**
	 * @throws Exception
     * later/more recent is more
	 */
	public void testCompareLaterIsMore() throws Exception {
		DateSynonym ds1 = new DateSynonym(1, "1", "desc1", new Date(System.currentTimeMillis() - 10000));
		DateSynonym ds2 = new DateSynonym(2, "2", "desc2", new Date(System.currentTimeMillis()));
		DateSynonym ds3 = new DateSynonym(3, "3", "desc2", new Date(System.currentTimeMillis() + 10000));
        MutableTimedAssociationKey key1 = new DefaultMutableTimedAssociationKey(1, ds1, ds3);
        MutableTimedAssociationKey key2 = new DefaultMutableTimedAssociationKey(2, ds1, ds2);
		assertTrue(0 > comparator.compare(key1, key2));
		assertTrue(0 < comparator.compare(key2, key1));

		key1.setEffectiveDate(ds2);
		assertTrue(0 > comparator.compare(key1, key2));
		assertTrue(0 < comparator.compare(key2, key1));

		key1 = new DefaultMutableTimedAssociationKey(1, ds1, ds3);
		key2 = new DefaultMutableTimedAssociationKey(2, ds2, ds3);
		assertTrue(0 < comparator.compare(key1, key2));
		assertTrue(0 > comparator.compare(key2, key1));
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.comparator = TimedAssociationKeyComparator.getInstance();
	}

	protected void tearDown() throws Exception {
		// Tear downs for ActivationsComparatorTest
		super.tearDown();
	}
}
