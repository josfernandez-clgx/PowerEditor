package com.mindbox.pe.model;

import static com.mindbox.pe.common.CommonTestObjectMother.createDateSynonym;
import static com.mindbox.pe.unittest.UnitTestHelper.getDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.unittest.AbstractTestBase;

public class DateSynonymTest extends AbstractTestBase {

	public void testDefaultContructorSetsFields() throws Exception {
		assertFalse("DateSynonym default constructor shoud set isNotInUse to false", new DateSynonym().isNotInUse());
	}

	public void testAfterWithNullReturnsTrue() throws Exception {
		assertTrue(createDateSynonym().after(null));
	}

	public void testAfterHappyCase() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));
		assertFalse(ds1.after(ds2));
		assertTrue(ds2.after(ds1));
	}

	public void testBeforeWithNullReturnsFalse() throws Exception {
		assertFalse(createDateSynonym().before(null));
	}

	public void testBeforeHappyCase() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));
		assertTrue(ds1.before(ds2));
		assertFalse(ds2.before(ds1));
	}

	public void testNotAfterWithNullReturnsFalse() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		assertFalse(ds1.notAfter(null));
	}

	public void testNotAfterPositiveCase() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		DateSynonym ds2 = createDateSynonym();
		ds2.setDate(new Date(ds1.getDate().getTime() + 100));
		assertTrue(ds1.notAfter(ds2));

		ds2.setDate(ds1.getDate());
		assertTrue(ds1.notAfter(ds2));
	}

	public void testNotAfterNegativeCase() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		DateSynonym ds2 = createDateSynonym();
		ds2.setDate(new Date(ds1.getDate().getTime() - 100));
		assertFalse(ds1.notAfter(ds2));
	}

	public void testNotBeforeWithNullReturnsTrue() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		assertTrue(ds1.notBefore(null));
	}

	public void testNotBeforePositiveCase() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		DateSynonym ds2 = createDateSynonym();
		ds2.setDate(new Date(ds1.getDate().getTime() - 100));
		assertTrue(ds1.notBefore(ds2));

		ds2.setDate(ds1.getDate());
		assertTrue(ds1.notBefore(ds2));
	}

	public void testNotBeforeNegativeCase() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		DateSynonym ds2 = createDateSynonym();
		ds2.setDate(new Date(ds1.getDate().getTime() + 100));
		assertFalse(ds1.notBefore(ds2));
	}

	public void testEqualsWithNullReturnsFalse() throws Exception {
		assertFalse(createDateSynonym().equals(null));
	}

	public void testEqualsPositiveCase() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		DateSynonym ds2 = createDateSynonym();
		ds2.setDate(new Date(ds1.getDate().getTime() + 100));
		ds2.setID(ds1.getID());
		assertTrue(ds1.equals(ds2));
		assertTrue(ds2.equals(ds1));
	}

	public void testEqualsNegativeCase() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		DateSynonym ds2 = createDateSynonym();
		ds2.setDate(ds1.getDate());
		assertFalse(ds1.equals(ds2));
		assertFalse(ds2.equals(ds1));
	}

	/**
	 * Make sure the toString() method returns the name embedded in the String the name
	 * 
	 * @throws Exception
	 */
	public void testToString() throws Exception {
		DateSynonym ds1 = new DateSynonym(1, "Test 1 Name", "Test 1 Desc", Calendar.getInstance().getTime());
		DateSynonym ds2 = new DateSynonym(2, "Test 2 Name", "Test 2 Desc", Calendar.getInstance().getTime());
		DateSynonym ds3 = new DateSynonym(3, "Test 3 Name", "Test 3 Desc", Calendar.getInstance().getTime());
		assertTrue(ds1.toString().equals(ds1.getName()));
		assertTrue(ds2.toString().equals(ds2.getName()));
		assertTrue(ds3.toString().equals(ds3.getName()));
	}

	/**
	 * Make sure the toString() method returns the name embedded in the String the name
	 * 
	 * @throws Exception
	 */
	public void testSetDateString() throws Exception {
		Date now = Calendar.getInstance().getTime();
		String dateString = ConfigUtil.toDateXMLString(now);
		DateSynonym ds = new DateSynonym(1, "Test 1 Name", "Test 1 Desc", Calendar.getInstance().getTime());
		ds.setDateString(dateString);
		assertNotNull(ds.getDate());
		assertEquals(ds.getDate(), now);
	}

	public void testIsSameDatePositiveCaseWithSameID() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		assertTrue(ds1.isSameDate(ds1)); // same instance

		DateSynonym ds2 = createDateSynonym();
		ds2.setID(ds1.getID());
		assertTrue(ds1.isSameDate(ds2)); // same id
	}

	public void testIsSameDatePositiveCaseWithSameDate() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		DateSynonym ds2 = createDateSynonym();
		ds2.setID(ds1.getID() + 1);
		ds2.setDate(ds1.getDate());
		assertTrue(ds1.isSameDate(ds2)); // same date
	}

	public void testIsSameDateNegativeCase() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		assertFalse(ds1.isSameDate(null));

		DateSynonym ds2 = createDateSynonym();
		assertFalse(ds1.isSameDate(ds2));
	}

}
