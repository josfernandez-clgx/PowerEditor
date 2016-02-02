package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class FocusOfAttentionPatternValueSlotTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("FocusOfAttentionPatternValueSlotTest");
		suite.addTestSuite(FocusOfAttentionPatternValueSlotTest.class);
		return suite;
	}

	public FocusOfAttentionPatternValueSlotTest(String name) {
		super(name);
	}

	public void testConstructorSetsCorrectType() throws Exception {
		assertEquals(ValueSlot.Type.FOCUS_OF_ATTENTION, new FocusOfAttentionPatternValueSlot().getType());
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
