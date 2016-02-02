package com.mindbox.pe.model.table;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class DateRangeTest extends AbstractTestBase {
	private DateRange range;
	private Date lowerVal, upperVal;

	public static TestSuite suite() {
		TestSuite suite = new TestSuite(DateRangeTest.class.getName());
		suite.addTestSuite(DateRangeTest.class);
		return suite;
	}

	public DateRangeTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		Calendar cal = Calendar.getInstance();
		upperVal = cal.getTime();
		cal.add(Calendar.DATE, 1);
		lowerVal = cal.getTime();

		range = new DateRange();
		range.setLowerValue(lowerVal);
		range.setUpperValue(upperVal);
	}

	public void testIsForDateHappyCase() throws Exception {
		assertTrue(range.isForDate());
	}

	public void testIsEmptyNegativeCase() throws Exception {
		assertFalse(range.isEmpty());
		range.setLowerValue(null);
		assertFalse(range.isEmpty());
		range.setLowerValue(range.getUpperValue());
		range.setUpperValue(null);
		assertFalse(range.isEmpty());
	}

	public void testIsEmptyPositiveCase() throws Exception {
		range.setLowerValue(null);
		range.setUpperValue(null);
		assertTrue(range.isEmpty());
	}

	public void testRepresentsSingleValuePositiveCase() throws Exception {
		range.setLowerValue(range.getUpperValue());
		assertTrue(range.representsSingleValue());
	}

	public void testRepresentsSingleValueNegativeCase() throws Exception {
		assertFalse(range.representsSingleValue());
	}

	public void testRepresentsSingleValueReturnsFalseIfIsEmpty() throws Exception {
		range.setLowerValue(null);
		range.setUpperValue(null);
		assertFalse(range.representsSingleValue());
	}

	public void testImplementsSerializable() throws Exception {
		Serializable.class.isAssignableFrom(DateRange.class);
	}

	public void testGetCeilingHappyPath() throws Exception {
		assertEquals(new Long(upperVal.getTime()), range.getCeiling());
	}

	public void testGetCeilingNull() throws Exception {
		range.setUpperValue(null);
		assertNull(range.getCeiling());
	}

	public void testGetFloorHappyPath() throws Exception {
		assertEquals(new Long(lowerVal.getTime()), range.getFloor());
	}

	public void testGetFloorNull() throws Exception {
		range.setLowerValue(null);
		assertNull(range.getFloor());
	}
}
