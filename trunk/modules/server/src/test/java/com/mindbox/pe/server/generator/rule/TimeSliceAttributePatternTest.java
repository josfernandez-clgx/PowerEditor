package com.mindbox.pe.server.generator.rule;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class TimeSliceAttributePatternTest extends AbstractTestBase {

	@Test
	public void testConstructorSetsFieldsCorrectly() throws Exception {
		TimeSliceAttributePattern attributePattern = new TimeSliceAttributePattern("attr", "v");
		assertTrue(attributePattern.hasValueSlot());
		assertTrue(attributePattern.getValueSlot() instanceof TimeSlicePatternValueSlot);
	}
}
