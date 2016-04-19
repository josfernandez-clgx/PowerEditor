package com.mindbox.pe.model.table;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class IntegerRangeTest extends AbstractTestBase {

	private IntegerRange range = null;

	@Before
	public void setUp() throws Exception {
		range = new IntegerRange();
		range.setLowerValue(new Integer(10));
		range.setUpperValue(new Integer(200));
	}

	@Test
	public void testImplementsSerializable() throws Exception {
		Serializable.class.isAssignableFrom(IntegerRange.class);
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
		assertFalse(range.isForDate());
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
