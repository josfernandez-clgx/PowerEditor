package com.mindbox.pe.server.generator.rule;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.server.generator.rule.ValueSlot.Type;
import com.mindbox.pe.unittest.AbstractTestBase;

public class AbstractValueSlotTest extends AbstractTestBase {

	private static class TestImpl extends AbstractValueSlot {

		protected TestImpl(Type type, Object slotValue, String slotText) {
			super(type, slotValue, slotText);
		}

	}

	@Test
	public void testEqualsWithNullReturnsFalse() throws Exception {
		assertFalse(new TestImpl(Type.CELL_VALUE, null, "").equals(null));
	}

	@Test
	public void testEqualsPositiveCase() throws Exception {
		TestImpl testImpl1 = new TestImpl(Type.COLUMN_REFERENCE, "SlotValue", "SlotText");
		TestImpl testImpl2 = new TestImpl(Type.COLUMN_REFERENCE, "SlotValue", "SlotText");
		assertTrue(testImpl1.equals(testImpl2));
		assertTrue(testImpl2.equals(testImpl1));
	}

	@Test
	public void testEqualsNegativeCase() throws Exception {
		TestImpl testImpl = new TestImpl(Type.COLUMN_REFERENCE, "SlotValue", "SlotText");
		assertFalse(testImpl.equals(new TestImpl(Type.CELL_VALUE, testImpl.getSlotValue(), testImpl.getSlotText())));
		assertFalse(testImpl.equals(new TestImpl(Type.COLUMN_REFERENCE, testImpl.getSlotValue() + "X", testImpl.getSlotText())));
		assertFalse(testImpl.equals(new TestImpl(Type.COLUMN_REFERENCE, testImpl.getSlotValue(), testImpl.getSlotText() + "X")));
	}

}
