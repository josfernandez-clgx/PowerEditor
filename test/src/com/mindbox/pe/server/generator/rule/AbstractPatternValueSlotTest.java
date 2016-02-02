package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.generator.rule.ValueSlot.Type;

public class AbstractPatternValueSlotTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractPatternValueSlotTest Tests");
		suite.addTestSuite(AbstractPatternValueSlotTest.class);
		return suite;
	}

	private static class TestImpl extends AbstractPatternValueSlot {

		protected TestImpl(Type type, Reference reference, int operator, Object slotValue, String slotText) {
			super(type, reference, operator, slotValue, slotText);
		}
	}

	public AbstractPatternValueSlotTest(String name) {
		super(name);
	}

	public void testEqualsWithNullReturnsFalse() throws Exception {
		assertFalse(new TestImpl(Type.CELL_VALUE, null, 1, null, "").equals(null));
	}

	public void testEqualsPositiveCase() throws Exception {
		TestImpl testImpl1 = new TestImpl(Type.COLUMN_REFERENCE, ObjectMother.createReference(), 2, "SlotValue", "SlotText");
		TestImpl testImpl2 = new TestImpl(testImpl1.getType(), testImpl1.getReference(), testImpl1.getOperator(), testImpl1.getSlotValue(), testImpl1.getSlotText());
		assertTrue(testImpl1.equals(testImpl2));
		assertTrue(testImpl2.equals(testImpl1));
	}

	public void testEqualsNegativeCase() throws Exception {
		TestImpl testImpl = new TestImpl(Type.COLUMN_REFERENCE, ObjectMother.createReference(), 2, "SlotValue", "SlotText");
		assertFalse(testImpl.equals(new TestImpl(
				testImpl.getType(),
				ObjectMother.createReference(),
				testImpl.getOperator(),
				testImpl.getSlotValue(),
				testImpl.getSlotText())));
		assertFalse(testImpl.equals(new TestImpl(testImpl.getType(), testImpl.getReference(), testImpl.getOperator() + 1, testImpl.getSlotValue(), testImpl.getSlotText())));
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
