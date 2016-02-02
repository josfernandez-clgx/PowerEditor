package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class FocusOfAttentionAttributePatternTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("FocusOfAttentionAttributePatternTest Tests");
		suite.addTestSuite(FocusOfAttentionAttributePatternTest.class);
		return suite;
	}

	public FocusOfAttentionAttributePatternTest(String name) {
		super(name);
	}

	public void testConstructorSetsFieldsCorrectly() throws Exception {
		FocusOfAttentionAttributePattern attributePattern = new FocusOfAttentionAttributePattern("attr");
		assertTrue(attributePattern.hasValueSlot());
		assertTrue(attributePattern.getValueSlot() instanceof FocusOfAttentionPatternValueSlot);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
