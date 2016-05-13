package com.mindbox.pe.model.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class DateRangeTest extends AbstractTestBase {

	private DateRange range;
	private Date lowerVal, upperVal;

	@Before
	public void setUp() throws Exception {
		Calendar cal = Calendar.getInstance();
		upperVal = cal.getTime();
		cal.add(Calendar.DATE, 1);
		lowerVal = cal.getTime();

		range = new DateRange();
		range.setLowerValue(lowerVal);
		range.setUpperValue(upperVal);
	}

	@Test
	public void testGetCeilingHappyPath() throws Exception {
		assertEquals(new Long(upperVal.getTime()), range.getCeiling());
	}

	@Test
	public void testGetCeilingNull() throws Exception {
		range.setUpperValue(null);
		assertNull(range.getCeiling());
	}

	@Test
	public void testGetFloorHappyPath() throws Exception {
		assertEquals(new Long(lowerVal.getTime()), range.getFloor());
	}

	@Test
	public void testGetFloorNull() throws Exception {
		range.setLowerValue(null);
		assertNull(range.getFloor());
	}

	@Test
	public void testImplementsSerializable() throws Exception {
		Serializable.class.isAssignableFrom(DateRange.class);
	}

	@Test
	public void testIsEmptyNegativeCase() throws Exception {
		assertFalse(range.isEmpty());
		range.setLowerValue(null);
		assertFalse(range.isEmpty());
		range.setLowerValue(range.getUpperValue());
		range.setUpperValue(null);
		assertFalse(range.isEmpty());
	}

	@Test
	public void testIsEmptyPositiveCase() throws Exception {
		range.setLowerValue(null);
		range.setUpperValue(null);
		assertTrue(range.isEmpty());
	}

	@Test
	public void testIsForDateHappyCase() throws Exception {
		assertTrue(range.isForDate());
	}

	@Test
	public void testRepresentsSingleValueNegativeCase() throws Exception {
		assertFalse(range.representsSingleValue());
	}

	@Test
	public void testRepresentsSingleValuePositiveCase() throws Exception {
		range.setLowerValue(range.getUpperValue());
		assertTrue(range.representsSingleValue());
	}

	@Test
	public void testRepresentsSingleValueReturnsFalseIfIsEmpty() throws Exception {
		range.setLowerValue(null);
		range.setUpperValue(null);
		assertFalse(range.representsSingleValue());
	}
}
