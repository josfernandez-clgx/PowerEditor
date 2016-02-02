package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;

public class ColumnReferencePatternValueSlotTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("ColumnReferencePatternValueSlotTest Tests");
		suite.addTestSuite(ColumnReferencePatternValueSlotTest.class);
		return suite;
	}

	public ColumnReferencePatternValueSlotTest(String name) {
		super(name);
	}

	public void testConstructorSetsCorrectType() throws Exception {
		assertEquals(ValueSlot.Type.COLUMN_REFERENCE, new ColumnReferencePatternValueSlot(1, "text").getType());
	}

	public void testGetValueSlotReturnsInteger() throws Exception {
		int columnNo = ObjectMother.createInt();
		assertEquals(new Integer(columnNo), new ColumnReferencePatternValueSlot(columnNo).getSlotValue());
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
