package com.mindbox.pe.server.model;

import static com.mindbox.pe.server.ServerTestObjectMother.createDateSynonym;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static com.mindbox.pe.unittest.UnitTestHelper.getDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.After;
import org.junit.Test;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.unittest.AbstractTestBase;

public class TimeSliceTest extends AbstractTestBase {

	@After
	public void tearDown() throws Exception {
		TimeSlice.resetNextID();
	}

	@Test
	public void testCompareToReturnsNegativeHappyCase() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));
		DateSynonym ds3 = createDateSynonym();
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

	@Test
	public void testCompareToReturnsPositiveHappyCase() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = createDateSynonym();
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

	@Test
	public void testCompareToReturnsZeroHappyCase() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = createDateSynonym();
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

	@Test
	public void testCompareToThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(TimeSlice.createInstance(null, createDateSynonym()), "compareTo", new Class[] { Object.class });
	}

	@Test
	public void testCreateInstanceAcceptsOneNullArgument() throws Exception {
		assertNotNull(TimeSlice.createInstance(null, createDateSynonym()));
		assertNull(TimeSlice.createInstance(null, createDateSynonym()).getStartDate());
		assertNotNull(TimeSlice.createInstance(createDateSynonym(), null));
		assertNull(TimeSlice.createInstance(createDateSynonym(), null).getEndDate());
	}

	@Test
	public void testCreateInstanceIncrementsNamePostfix() throws Exception {
		DateSynonym ds = createDateSynonym();
		for (int i = 0; i < 5; i++) {
			assertTrue("Name does not end with " + (i + 1000), TimeSlice.createInstance(null, ds).getName().endsWith(String.valueOf(i + 1000)));
		}
	}

	@Test
	public void testCreateInstanceWithNullAndNullThrowsIllegalArgumentException() throws Exception {
		assertThrowsException(
				TimeSlice.class,
				"createInstance",
				new Class[] { DateSynonym.class, DateSynonym.class },
				new Object[] { null, null },
				IllegalArgumentException.class);
	}

	@Test
	public void testEqualsIgnoresDates() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));

		TimeSlice timeSlice = TimeSlice.createInstance(null, ds1);
		TimeSlice.resetNextID();
		TimeSlice timeSlice2 = TimeSlice.createInstance(ds1, ds2);
		assertTrue(timeSlice.equals(timeSlice2));
		assertTrue(timeSlice2.equals(timeSlice));
	}

	@Test
	public void testEqualsWithDifferentNameReturnsFalse() throws Exception {
		TimeSlice timeSlice = TimeSlice.createInstance(null, createDateSynonym());
		TimeSlice timeSlice2 = TimeSlice.createInstance(timeSlice.getStartDate(), timeSlice.getEndDate());
		assertFalse(timeSlice.equals(timeSlice2));
		assertFalse(timeSlice2.equals(timeSlice));
	}

	@Test
	public void testEqualsWithNonTimeSliceObjectReturnsFalse() throws Exception {
		assertFalse(TimeSlice.createInstance(null, createDateSynonym()).equals("bogusObject"));
	}

	@Test
	public void testEqualsWithNullReturnsFalse() throws Exception {
		assertFalse(TimeSlice.createInstance(null, createDateSynonym()).equals(null));
	}

	@Test
	public void testGetAsOfDateHappyCaseWithNoStartDate() throws Exception {
		TimeSlice timeSlice = TimeSlice.createInstance(null, createDateSynonym());
		Date date = timeSlice.getAsOfDate();
		assertEquals((timeSlice.getEndDate().getDate().getTime() - 1L), date.getTime());
	}

	@Test
	public void testGetAsOfDateHappyCaseWithStartDateAndEndDate() throws Exception {
		TimeSlice timeSlice = TimeSlice.createInstance(createDateSynonym(), createDateSynonym());
		Date date = timeSlice.getAsOfDate();
		assertEquals(timeSlice.getStartDate().getDate(), date);
	}

	@Test
	public void testGetAsOfDateHappyCaseWithStartDateNoEndDate() throws Exception {
		TimeSlice timeSlice = TimeSlice.createInstance(createDateSynonym(), null);
		Date date = timeSlice.getAsOfDate();
		assertEquals(timeSlice.getStartDate().getDate(), date);
	}

	@Test
	public void testResetNextIDHappyCase() throws Exception {
		DateSynonym ds = createDateSynonym();
		for (int i = 0; i < 2; i++) {
			assertTrue(TimeSlice.createInstance(null, ds).getName().endsWith(String.valueOf(i + 1000)));
		}
		TimeSlice.resetNextID();
		assertTrue(TimeSlice.createInstance(null, ds).getName().endsWith(String.valueOf(1000)));
	}
}
