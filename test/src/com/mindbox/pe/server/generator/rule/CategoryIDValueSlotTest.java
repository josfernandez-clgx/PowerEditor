package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithGenericEntityType;

public class CategoryIDValueSlotTest extends AbstractTestWithGenericEntityType {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("CategoryIDValueSlotTest Tests");
		suite.addTestSuite(CategoryIDValueSlotTest.class);
		return suite;
	}

	public CategoryIDValueSlotTest(String name) {
		super(name);
	}

	public void testConstructorSetsCorrectType() throws Exception {
		assertEquals(ValueSlot.Type.CATEGORY_ID, new CategoryIDValueSlot(entityType).getType());
	}

	public void testConstructorWithNullEntityThrowsNullPointerException() throws Exception {
		try {
			new CategoryIDValueSlot(null);
			fail("Expected NullPointerException");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testGetValueSlotReturnsGenericEntityType() throws Exception {
		assertEquals(entityType, new CategoryIDValueSlot(entityType).getSlotValue());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
