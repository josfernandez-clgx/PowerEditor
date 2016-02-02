package com.mindbox.pe.server.model;

import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.DateSynonym;

public class TimeSliceTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("TimeSliceTest Tests");
		suite.addTestSuite(TimeSliceTest.class);
		return suite;
	}

	public TimeSliceTest(String name) {
		super(name);
	}

	public void testCreateInstanceWithNullAndNullThrowsIllegalArgumentException() throws Exception {
		assertThrowsException(TimeSlice.class, "createInstance", new Class[]
			{ DateSynonym.class, DateSynonym.class}, new Object[]
			{ null, null}, IllegalArgumentException.class);
	}

	public void testCreateInstanceAcceptsOneNullArgument() throws Exception {
		assertNotNull(TimeSlice.createInstance(null, ObjectMother.createDateSynonym()));
		assertNull(TimeSlice.createInstance(null, ObjectMother.createDateSynonym()).getStartDate());
		assertNotNull(TimeSlice.createInstance(ObjectMother.createDateSynonym(), null));
		assertNull(TimeSlice.createInstance(ObjectMother.createDateSynonym(), null).getEndDate());
	}

	public void testCreateInstanceIncrementsNamePostfix() throws Exception {
		DateSynonym ds = ObjectMother.createDateSynonym();
		for (int i = 0; i < 5; i++) {
			assertTrue("Name does not end with " + (i + 1000), TimeSlice.createInstance(null, ds).getName().endsWith(String.valueOf(i + 1000)));
		}
	}

	public void testResetNextIDHappyCase() throws Exception {
		DateSynonym ds = ObjectMother.createDateSynonym();
		for (int i = 0; i < 2; i++) {
			assertTrue(TimeSlice.createInstance(null, ds).getName().endsWith(String.valueOf(i + 1000)));
		}
		TimeSlice.resetNextID();
		assertTrue(TimeSlice.createInstance(null, ds).getName().endsWith(String.valueOf(1000)));
	}

	public void testCompareToThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(TimeSlice.createInstance(null, ObjectMother.createDateSynonym()), "compareTo", new Class[]
			{ Object.class});
	}

	public void testCompareToReturnsZeroHappyCase() throws Exception {
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));

		TimeSlice timeSlice = TimeSlice.createInstance(null, ds1);
		assertEquals(0, timeSlice.compareTo(timeSlice));
		TimeSlice timeSlice2 = TimeSlice.createInstance(timeSlice.getStartDate(), timeSlice.getEndDate());
		assertEquals(0, timeSlice.compareTo(timeSlice2));
		assertEquals(0, timeSlice2.compareTo(timeSlice));

		timeSlice = TimeSlice.createInstance(ds1, null);
		assertEquals(0, timeSlice.compareTo(timeSlice));
		timeSlice2 = TimeSlice.createInstance(timeSlice.getStartDate(), timeSlice.getEndDate());
		assertEquals(0, timeSlice.compareTo(timeSlice2));
		assertEquals(0, timeSlice2.compareTo(timeSlice));

		timeSlice = TimeSlice.createInstance(ds1, ds2);
		assertEquals(0, timeSlice.compareTo(timeSlice));
		timeSlice2 = TimeSlice.createInstance(timeSlice.getStartDate(), timeSlice.getEndDate());
		assertEquals(0, timeSlice.compareTo(timeSlice2));
		assertEquals(0, timeSlice2.compareTo(timeSlice));
	}

	public void testCompareToReturnsNegativeHappyCase() throws Exception {
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));
		DateSynonym ds3 = ObjectMother.createDateSynonym();
		ds3.setDate(getDate(2006, 10, 10, 10, 10, 59));

		TimeSlice timeSlice = TimeSlice.createInstance(null, ds1);
		TimeSlice timeSlice2 = TimeSlice.createInstance(timeSlice.getEndDate(), ds2);
		assertTrue(timeSlice.compareTo(timeSlice2) < 0);
		assertTrue(timeSlice2.compareTo(timeSlice) > 0);

		timeSlice = TimeSlice.createInstance(ds1, ds2);
		timeSlice2 = TimeSlice.createInstance(timeSlice.getEndDate(), ds3);
		assertTrue(timeSlice.compareTo(timeSlice2) < 0);
		assertTrue(timeSlice2.compareTo(timeSlice) > 0);
	}

	public void testCompareToReturnsPositiveHappyCase() throws Exception {
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));

		TimeSlice timeSlice = TimeSlice.createInstance(ds1, null);
		TimeSlice timeSlice2 = TimeSlice.createInstance(null, timeSlice.getStartDate());
		assertTrue(timeSlice.compareTo(timeSlice2) > 0);
		assertTrue(timeSlice2.compareTo(timeSlice) < 0);

		timeSlice2 = TimeSlice.createInstance(ds1, ds2);
		timeSlice = TimeSlice.createInstance(timeSlice2.getEndDate(), null);
		assertTrue(timeSlice.compareTo(timeSlice2) > 0);
		assertTrue(timeSlice2.compareTo(timeSlice) < 0);
	}

	public void testEqualsWithNullReturnsFalse() throws Exception {
		assertFalse(TimeSlice.createInstance(null, ObjectMother.createDateSynonym()).equals(null));
	}

	public void testEqualsWithNonTimeSliceObjectReturnsFalse() throws Exception {
		assertFalse(TimeSlice.createInstance(null, ObjectMother.createDateSynonym()).equals("bogusObject"));
	}

	public void testEqualsWithDifferentNameReturnsFalse() throws Exception {
		TimeSlice timeSlice = TimeSlice.createInstance(null, ObjectMother.createDateSynonym());
		TimeSlice timeSlice2 = TimeSlice.createInstance(timeSlice.getStartDate(), timeSlice.getEndDate());
		assertFalse(timeSlice.equals(timeSlice2));
		assertFalse(timeSlice2.equals(timeSlice));
	}

	public void testEqualsIgnoresDates() throws Exception {
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));

		TimeSlice timeSlice = TimeSlice.createInstance(null, ds1);
		TimeSlice.resetNextID();
		TimeSlice timeSlice2 = TimeSlice.createInstance(ds1, ds2);
		assertTrue(timeSlice.equals(timeSlice2));
		assertTrue(timeSlice2.equals(timeSlice));
	}

	public void testGetAsOfDateHappyCaseWithNoStartDate() throws Exception {
		TimeSlice timeSlice = TimeSlice.createInstance(null, ObjectMother.createDateSynonym());
		Date date = timeSlice.getAsOfDate();
		assertEquals((timeSlice.getEndDate().getDate().getTime() - 1L), date.getTime());
	}

	public void testGetAsOfDateHappyCaseWithStartDateNoEndDate() throws Exception {
		TimeSlice timeSlice = TimeSlice.createInstance(ObjectMother.createDateSynonym(), null);
		Date date = timeSlice.getAsOfDate();
		assertEquals(timeSlice.getStartDate().getDate(), date);
	}

	public void testGetAsOfDateHappyCaseWithStartDateAndEndDate() throws Exception {
		TimeSlice timeSlice = TimeSlice.createInstance(ObjectMother.createDateSynonym(), ObjectMother.createDateSynonym());
		Date date = timeSlice.getAsOfDate();
		assertEquals(timeSlice.getStartDate().getDate(), date);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		TimeSlice.resetNextID();
		super.tearDown();
	}
}
