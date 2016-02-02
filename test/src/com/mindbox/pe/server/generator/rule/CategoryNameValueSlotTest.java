package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithGenericEntityType;

public class CategoryNameValueSlotTest extends AbstractTestWithGenericEntityType {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("CategoryNameValueSlotTest Tests");
		suite.addTestSuite(CategoryNameValueSlotTest.class);
		return suite;
	}

	public CategoryNameValueSlotTest(String name) {
		super(name);
	}

	public void testConstructorSetsCorrectType() throws Exception {
		assertEquals(ValueSlot.Type.CATEGORY_NAME, new CategoryNameValueSlot(entityType).getType());
	}

	public void testConstructorWithNullEntityThrowsNullPointerException() throws Exception {
		try {
			new CategoryNameValueSlot(null);
			fail("Expected NullPointerException");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testGetValueSlotReturnsGenericEntityType() throws Exception {
		assertEquals(entityType, new CategoryNameValueSlot(entityType).getSlotValue());
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
