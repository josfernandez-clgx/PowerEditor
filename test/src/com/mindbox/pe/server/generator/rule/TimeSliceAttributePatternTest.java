package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class TimeSliceAttributePatternTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("TimeSliceAttributePatternTest Tests");
		suite.addTestSuite(TimeSliceAttributePatternTest.class);
		return suite;
	}

	public TimeSliceAttributePatternTest(String name) {
		super(name);
	}

	public void testConstructorSetsFieldsCorrectly() throws Exception {
		TimeSliceAttributePattern attributePattern = new TimeSliceAttributePattern("attr","v");
		assertTrue(attributePattern.hasValueSlot());
		assertTrue(attributePattern.getValueSlot() instanceof TimeSlicePatternValueSlot);
	}

	protected void setUp() throws Exception {
		super.setUp();
		// Set up for TimeSliceAttributePatternTest
	}

	protected void tearDown() throws Exception {
		// Tear downs for TimeSliceAttributePatternTest
		super.tearDown();
	}
}
