package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;

public class ColumnRefValueSlotAttributePatternTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("ColumnRefValueSlotAttributePatternTest Tests");
		suite.addTestSuite(ColumnRefValueSlotAttributePatternTest.class);
		return suite;
	}

	public void testConstructorSetsFieldsCorrectly() throws Exception {
		int columnNo = ObjectMother.createInt();
		ColumnRefValueSlotAttributePattern attributePattern = new ColumnRefValueSlotAttributePattern("attr", "v", ObjectMother.createReference(), 1, columnNo);
		assertTrue(attributePattern.hasValueSlot());
		assertTrue(attributePattern.getValueSlot() instanceof ColumnReferencePatternValueSlot);
		assertEquals(columnNo, ((ColumnReferencePatternValueSlot) attributePattern.getValueSlot()).getColumnNo());
	}

	public ColumnRefValueSlotAttributePatternTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
