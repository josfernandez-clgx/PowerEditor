package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.server.generator.rule.ValueSlot.Type;

public class AbstractValueSlotTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractValueSlotTest Tests");
		suite.addTestSuite(AbstractValueSlotTest.class);
		return suite;
	}

	private static class TestImpl extends AbstractValueSlot {

		protected TestImpl(Type type, Object slotValue, String slotText) {
			super(type, slotValue, slotText);
		}

	}

	public AbstractValueSlotTest(String name) {
		super(name);
	}

	public void testEqualsWithNullReturnsFalse() throws Exception {
		assertFalse(new TestImpl(Type.CELL_VALUE, null, "").equals(null));
	}

	public void testEqualsPositiveCase() throws Exception {
		TestImpl testImpl1 = new TestImpl(Type.COLUMN_REFERENCE, "SlotValue", "SlotText");
		TestImpl testImpl2 = new TestImpl(Type.COLUMN_REFERENCE, "SlotValue", "SlotText");
		assertTrue(testImpl1.equals(testImpl2));
		assertTrue(testImpl2.equals(testImpl1));
	}

	public void testEqualsNegativeCase() throws Exception {
		TestImpl testImpl = new TestImpl(Type.COLUMN_REFERENCE, "SlotValue", "SlotText");
		assertFalse(testImpl.equals(new TestImpl(Type.CELL_VALUE, testImpl.getSlotValue(), testImpl.getSlotText())));
		assertFalse(testImpl.equals(new TestImpl(Type.COLUMN_REFERENCE, testImpl.getSlotValue() + "X", testImpl.getSlotText())));
		assertFalse(testImpl.equals(new TestImpl(Type.COLUMN_REFERENCE, testImpl.getSlotValue(), testImpl.getSlotText() + "X")));
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
