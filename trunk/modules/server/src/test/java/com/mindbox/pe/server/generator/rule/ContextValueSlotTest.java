package com.mindbox.pe.server.generator.rule;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mindbox.pe.server.AbstractTestWithGenericEntityType;

public class ContextValueSlotTest extends AbstractTestWithGenericEntityType {

	@Test
	public void testConstructorSetsCorrectType() throws Exception {
		assertEquals(ValueSlot.Type.CONTEXT, new ContextValueSlot("text").getType());
	}
}
