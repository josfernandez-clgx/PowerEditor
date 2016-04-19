package com.mindbox.pe.server.generator.rule;

import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class ColumnReferencePatternValueSlotTest extends AbstractTestBase {

	@Test
	public void testConstructorSetsCorrectType() throws Exception {
		assertEquals(ValueSlot.Type.COLUMN_REFERENCE, new ColumnReferencePatternValueSlot(1, "text").getType());
	}

	@Test
	public void testGetValueSlotReturnsInteger() throws Exception {
		int columnNo = createInt();
		assertEquals(new Integer(columnNo), new ColumnReferencePatternValueSlot(columnNo).getSlotValue());
	}
}
