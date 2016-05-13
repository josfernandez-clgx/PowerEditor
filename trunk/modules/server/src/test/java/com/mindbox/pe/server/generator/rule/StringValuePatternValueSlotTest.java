package com.mindbox.pe.server.generator.rule;

import static com.mindbox.pe.server.ServerTestObjectMother.createReference;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class StringValuePatternValueSlotTest extends AbstractTestBase {

	@Test
	public void testConstructorSetsCorrectType() throws Exception {
		assertEquals(ValueSlot.Type.STRING, new StringValuePatternValueSlot(createReference(), 1, "str").getType());
	}

	@Test
	public void testGetValueSlotReturnsString() throws Exception {
		assertEquals("str", new StringValuePatternValueSlot(createReference(), 1, "str").getSlotValue());
	}
}
