package com.mindbox.pe.server.generator.rule;

import static com.mindbox.pe.server.ServerTestObjectMother.createReference;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class StringValueSlotAttributePatternTest extends AbstractTestBase {

	@Test
	public void testConstructorSetsFieldsCorrectly() throws Exception {
		StringValueSlotAttributePattern attributePattern = new StringValueSlotAttributePattern("attr", "v", createReference(), 1, "str");
		assertTrue(attributePattern.hasValueSlot());
		assertTrue(attributePattern.getValueSlot() instanceof StringValuePatternValueSlot);
	}

}
