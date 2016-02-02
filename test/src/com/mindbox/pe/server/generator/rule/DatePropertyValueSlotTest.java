package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class DatePropertyValueSlotTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("DatePropertyValueSlotTest Tests");
		suite.addTestSuite(DatePropertyValueSlotTest.class);
		return suite;
	}

	public DatePropertyValueSlotTest(String name) {
		super(name);
	}

	public void testConstructorSetsCorrectType() throws Exception {
		assertEquals(ValueSlot.Type.DATE_PROPERTY, new DatePropertyValueSlot(DatePropertyValueSlot.DATE_TYPE_ACTIVATION_DATE).getType());
	}

	public void testConstructorWithNullTypeThrowsNullPointerException() throws Exception {
		try {
			new DatePropertyValueSlot(null);
			fail("Expected NullPointerException");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testGetValueSlotReturnsString() throws Exception {
		assertEquals(DatePropertyValueSlot.DATE_TYPE_ACTIVATION_DATE, new DatePropertyValueSlot(DatePropertyValueSlot.DATE_TYPE_ACTIVATION_DATE).getSlotValue());
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
