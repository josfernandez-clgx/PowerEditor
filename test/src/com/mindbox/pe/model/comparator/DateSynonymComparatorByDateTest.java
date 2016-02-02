package com.mindbox.pe.model.comparator;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.DateSynonym;

public class DateSynonymComparatorByDateTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("DateSynonymComparatorByDateTest Tests");
		suite.addTestSuite(DateSynonymComparatorByDateTest.class);
		return suite;
	}

	public DateSynonymComparatorByDateTest(String name) {
		super(name);
	}

	public void testCompareToWithNullAndNullReturnsZero() {
		assertEquals(0, DateSynonymComparatorByDate.getInstance().compare(null, null));
	}

	public void testCompareToWithNullAndNotNullReturnsPositive() {
		assertTrue(0 < DateSynonymComparatorByDate.getInstance().compare(null, ObjectMother.createDateSynonym()));
	}

	public void testCompareToWithNotNullAndNullReturnsNegative() {
		assertTrue(0 > DateSynonymComparatorByDate.getInstance().compare(ObjectMother.createDateSynonym(), null));
	}

	public void testCompareToWithHappyCaseForEquality() {
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		ds1.setDate(getDate(2005, 10, 10, 00, 00, 00));
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ds2.setDate(ds1.getDate());
		assertEquals(0, DateSynonymComparatorByDate.getInstance().compare(ds1, ds2));
		assertEquals(0, DateSynonymComparatorByDate.getInstance().compare(ds2, ds1));
	}

	public void testCompareToHappyCaseForNonEquality() {
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		ds1.setDate(getDate(2005, 10, 10, 00, 00, 00));
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ds2.setDate(getDate(2005, 10, 10, 00, 30, 00));
		assertTrue(0 > DateSynonymComparatorByDate.getInstance().compare(ds1, ds2));
		assertTrue(0 < DateSynonymComparatorByDate.getInstance().compare(ds2, ds1));
	}

	protected void setUp() throws Exception {
		super.setUp();
		// Set up for DateSynonymComparatorByDateTest
	}

	protected void tearDown() throws Exception {
		// Tear downs for DateSynonymComparatorByDateTest
		super.tearDown();
	}
}
