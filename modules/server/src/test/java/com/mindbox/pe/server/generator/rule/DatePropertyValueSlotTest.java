package com.mindbox.pe.server.generator.rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class DatePropertyValueSlotTest extends AbstractTestBase {

	@Test
	public void testConstructorSetsCorrectType() throws Exception {
		assertEquals(ValueSlot.Type.DATE_PROPERTY, new DatePropertyValueSlot(DatePropertyValueSlot.DATE_TYPE_ACTIVATION_DATE).getType());
	}

	@Test
	public void testConstructorWithNullTypeThrowsNullPointerException() throws Exception {
		try {
			new DatePropertyValueSlot(null);
			fail("Expected NullPointerException");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	@Test
	public void testGetValueSlotReturnsString() throws Exception {
		assertEquals(
				DatePropertyValueSlot.DATE_TYPE_ACTIVATION_DATE,
				new DatePropertyValueSlot(DatePropertyValueSlot.DATE_TYPE_ACTIVATION_DATE).getSlotValue());
	}
}