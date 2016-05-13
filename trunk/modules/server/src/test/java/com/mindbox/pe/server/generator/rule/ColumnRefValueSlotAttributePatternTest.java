package com.mindbox.pe.server.generator.rule;

import static com.mindbox.pe.server.ServerTestObjectMother.createReference;
import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class ColumnRefValueSlotAttributePatternTest extends AbstractTestBase {

	@Test
	public void testConstructorSetsFieldsCorrectly() throws Exception {
		int columnNo = createInt();
		ColumnRefValueSlotAttributePattern attributePattern = new ColumnRefValueSlotAttributePattern("attr", "v", createReference(), 1, columnNo);
		assertTrue(attributePattern.hasValueSlot());
		assertTrue(attributePattern.getValueSlot() instanceof ColumnReferencePatternValueSlot);
		assertEquals(columnNo, ((ColumnReferencePatternValueSlot) attributePattern.getValueSlot()).getColumnNo());
	}

}
