package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;

public class StringValuePatternValueSlotTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("StringValuePatternValueSlotTest Tests");
		suite.addTestSuite(StringValuePatternValueSlotTest.class);
		return suite;
	}

	public StringValuePatternValueSlotTest(String name) {
		super(name);
	}

	public void testConstructorSetsCorrectType() throws Exception {
		assertEquals(ValueSlot.Type.STRING, new StringValuePatternValueSlot(ObjectMother.createReference(), 1, "str").getType());
	}

	public void testGetValueSlotReturnsString() throws Exception {
		assertEquals("str", new StringValuePatternValueSlot(ObjectMother.createReference(), 1, "str").getSlotValue());
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
