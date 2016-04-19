package com.mindbox.pe.server.generator.rule;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class RuleIDValueSlotTest extends AbstractTestBase {

	@Test
	public void testConstructorSetsCorrectType() throws Exception {
		assertEquals(ValueSlot.Type.RULE_ID, new RuleIDValueSlot("text").getType());
	}
}
