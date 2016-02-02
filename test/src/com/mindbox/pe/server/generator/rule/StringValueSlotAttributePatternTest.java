package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;

public class StringValueSlotAttributePatternTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("StringValueSlotAttributePatternTest Tests");
		suite.addTestSuite(StringValueSlotAttributePatternTest.class);
		return suite;
	}

	public StringValueSlotAttributePatternTest(String name) {
		super(name);
	}

	public void testConstructorSetsFieldsCorrectly() throws Exception {
		StringValueSlotAttributePattern attributePattern = new StringValueSlotAttributePattern("attr", "v", ObjectMother.createReference(), 1, "str");
		assertTrue(attributePattern.hasValueSlot());
		assertTrue(attributePattern.getValueSlot() instanceof StringValuePatternValueSlot);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
