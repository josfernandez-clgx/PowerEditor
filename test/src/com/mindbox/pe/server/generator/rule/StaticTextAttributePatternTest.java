package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;


public class StaticTextAttributePatternTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("StaticTextAttributePatternTest Tests");
		suite.addTestSuite(StaticTextAttributePatternTest.class);
		return suite;
	}

	public StaticTextAttributePatternTest(String name) {
		super(name);
	}

	public void testConstructorSetsFieldsCorrectly() throws Exception {
		String str = "text"+ObjectMother.createInt();
		StaticTextAttributePattern attributePattern = new StaticTextAttributePattern("attr", "v", str);
		assertFalse(attributePattern.hasValueSlot());
		assertEquals(str, attributePattern.getValueText());
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
