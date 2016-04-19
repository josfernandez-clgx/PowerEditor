package com.mindbox.pe.server.generator.rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.mindbox.pe.server.AbstractTestWithGenericEntityType;

public class CategoryNameValueSlotTest extends AbstractTestWithGenericEntityType {

	@Test
	public void testConstructorSetsCorrectType() throws Exception {
		assertEquals(ValueSlot.Type.CATEGORY_NAME, new CategoryNameValueSlot(entityType).getType());
	}

	@Test
	public void testConstructorWithNullEntityThrowsNullPointerException() throws Exception {
		try {
			new CategoryNameValueSlot(null);
			fail("Expected NullPointerException");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	@Test
	public void testGetValueSlotReturnsGenericEntityType() throws Exception {
		assertEquals(entityType, new CategoryNameValueSlot(entityType).getSlotValue());
	}
}
