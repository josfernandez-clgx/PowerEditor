package com.mindbox.pe.server.generator.rule;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.server.AbstractTestWithGenericEntityType;

public class ContextElementAttributePatternTest extends AbstractTestWithGenericEntityType {

	@Test
	public void testConstructorSetsFieldsCorrectly() throws Exception {
		ContextElementAttributePattern attributePattern = new ContextElementAttributePattern("attr", "v", entityType, true);
		assertTrue(attributePattern.hasValueSlot());
		assertTrue(attributePattern.getValueSlot() instanceof ContextElementPatternValueSlot);
	}
}
