package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class TimeSlicePatternValueSlotTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("TimeSlicePatternValueSlotTtest Tests");
		suite.addTestSuite(TimeSlicePatternValueSlotTest.class);
		return suite;
	}

	public TimeSlicePatternValueSlotTest(String name) {
		super(name);
	}

	public void testConstructorSetsCorrectType() throws Exception {
		assertEquals(ValueSlot.Type.TIME_SLICE, new TimeSlicePatternValueSlot().getType());
	}

	protected void setUp() throws Exception {
		super.setUp();
		// Set up for TimeSlicePatternValueSlotTtest
	}

	protected void tearDown() throws Exception {
		// Tear downs for TimeSlicePatternValueSlotTtest
		super.tearDown();
	}
}
