package com.mindbox.pe.server.generator.rule;

import static com.mindbox.pe.server.ServerTestObjectMother.createReference;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.generator.rule.ValueSlot.Type;
import com.mindbox.pe.unittest.AbstractTestBase;

public class AbstractPatternValueSlotTest extends AbstractTestBase {

	private static class TestImpl extends AbstractPatternValueSlot {

		protected TestImpl(Type type, Reference reference, int operator, Object slotValue, String slotText) {
			super(type, reference, operator, slotValue, slotText);
		}
	}

	@Test
	public void testEqualsWithNullReturnsFalse() throws Exception {
		assertFalse(new TestImpl(Type.CELL_VALUE, null, 1, null, "").equals(null));
	}

	@Test
	public void testEqualsPositiveCase() throws Exception {
		TestImpl testImpl1 = new TestImpl(Type.COLUMN_REFERENCE, createReference(), 2, "SlotValue", "SlotText");
		TestImpl testImpl2 = new TestImpl(
				testImpl1.getType(),
				testImpl1.getReference(),
				testImpl1.getOperator(),
				testImpl1.getSlotValue(),
				testImpl1.getSlotText());
		assertTrue(testImpl1.equals(testImpl2));
		assertTrue(testImpl2.equals(testImpl1));
	}

	@Test
	public void testEqualsNegativeCase() throws Exception {
		TestImpl testImpl = new TestImpl(Type.COLUMN_REFERENCE, createReference(), 2, "SlotValue", "SlotText");
		assertFalse(testImpl.equals(new TestImpl(testImpl.getType(), createReference(), testImpl.getOperator(), testImpl.getSlotValue(), testImpl
				.getSlotText())));
		assertFalse(testImpl.equals(new TestImpl(
				testImpl.getType(),
				testImpl.getReference(),
				testImpl.getOperator() + 1,
				testImpl.getSlotValue(),
				testImpl.getSlotText())));
	}

}
