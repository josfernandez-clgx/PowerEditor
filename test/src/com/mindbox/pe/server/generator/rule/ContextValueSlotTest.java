package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithGenericEntityType;

public class ContextValueSlotTest extends AbstractTestWithGenericEntityType {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("ContextValueSlotTest Tests");
		suite.addTestSuite(ContextValueSlotTest.class);
		return suite;
	}

	public ContextValueSlotTest(String name) {
		super(name);
	}

	public void testConstructorSetsCorrectType() throws Exception {
		assertEquals(ValueSlot.Type.CONTEXT, new ContextValueSlot("text").getType());
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
