package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithGenericEntityType;

public class ContextElementPatternValueSlotTest extends AbstractTestWithGenericEntityType {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("ContextElementPatternValueSlotTest Tests");
		suite.addTestSuite(ContextElementPatternValueSlotTest.class);
		return suite;
	}

	public ContextElementPatternValueSlotTest(String name) {
		super(name);
	}

	public void testConstructorSetsCorrectType() throws Exception {
		assertEquals(ValueSlot.Type.CONTEXT_ELEMENT, new ContextElementPatternValueSlot(entityType, true).getType());
	}

	public void testConstructorWithNullEntityThrowsNullPointerException() throws Exception {
		try {
			new ContextElementPatternValueSlot(null, true);
			fail("Expected NullPointerException");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testGetValueSlotReturnsGenericEntityType() throws Exception {
		assertEquals(entityType, new ContextElementPatternValueSlot(entityType, true).getSlotValue());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
