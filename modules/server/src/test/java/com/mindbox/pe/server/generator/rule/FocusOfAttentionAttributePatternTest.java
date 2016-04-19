package com.mindbox.pe.server.generator.rule;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class FocusOfAttentionAttributePatternTest extends AbstractTestBase {

	@Test
	public void testConstructorSetsFieldsCorrectly() throws Exception {
		FocusOfAttentionAttributePattern attributePattern = new FocusOfAttentionAttributePattern("attr");
		assertTrue(attributePattern.hasValueSlot());
		assertTrue(attributePattern.getValueSlot() instanceof FocusOfAttentionPatternValueSlot);
	}
}
