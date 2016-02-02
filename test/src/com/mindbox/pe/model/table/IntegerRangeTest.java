package com.mindbox.pe.model.table;

import java.io.Serializable;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class IntegerRangeTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("IntegerRangeTest Tests");
		suite.addTestSuite(IntegerRangeTest.class);
		return suite;
	}
	
	private IntegerRange range = null;

	public IntegerRangeTest(String name) {
		super(name);
	}

	public void testImplementsSerializable() throws Exception {
		Serializable.class.isAssignableFrom(IntegerRange.class);
	}
	
	public void testIsForDateHappyCase() throws Exception {
		assertFalse(range.isForDate());
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

	protected void setUp() throws Exception {
		super.setUp();
		range = new IntegerRange();
		range.setLowerValue(new Integer(10));
		range.setUpperValue(new Integer(200));
	}

	protected void tearDown() throws Exception {
		// Tear downs for IntegerRangeTest
		super.tearDown();
	}
}
