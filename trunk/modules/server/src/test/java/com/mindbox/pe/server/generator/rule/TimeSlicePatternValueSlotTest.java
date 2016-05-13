package com.mindbox.pe.server.generator.rule;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class TimeSlicePatternValueSlotTest extends AbstractTestBase {

	@Test
	public void testConstructorSetsCorrectType() throws Exception {
		assertEquals(ValueSlot.Type.TIME_SLICE, new TimeSlicePatternValueSlot().getType());
	}

}
