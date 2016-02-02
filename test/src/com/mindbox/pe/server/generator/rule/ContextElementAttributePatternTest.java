package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithGenericEntityType;

public class ContextElementAttributePatternTest extends AbstractTestWithGenericEntityType {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("ContextElementAttributePatternTest Tests");
		suite.addTestSuite(ContextElementAttributePatternTest.class);
		return suite;
	}

	public ContextElementAttributePatternTest(String name) {
		super(name);
	}

	public void testConstructorSetsFieldsCorrectly() throws Exception {
		ContextElementAttributePattern attributePattern = new ContextElementAttributePattern("attr", "v", entityType, true);
		assertTrue(attributePattern.hasValueSlot());
		assertTrue(attributePattern.getValueSlot() instanceof ContextElementPatternValueSlot);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
