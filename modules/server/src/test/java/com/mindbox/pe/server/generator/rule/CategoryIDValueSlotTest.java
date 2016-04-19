package com.mindbox.pe.server.generator.rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.mindbox.pe.server.AbstractTestWithGenericEntityType;

public class CategoryIDValueSlotTest extends AbstractTestWithGenericEntityType {

	@Test
	public void testConstructorSetsCorrectType() throws Exception {
		assertEquals(ValueSlot.Type.CATEGORY_ID, new CategoryIDValueSlot(entityType).getType());
	}

	@Test
	public void testConstructorWithNullEntityThrowsNullPointerException() throws Exception {
		try {
			new CategoryIDValueSlot(null);
			fail("Expected NullPointerException");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	@Test
	public void testGetValueSlotReturnsGenericEntityType() throws Exception {
		assertEquals(entityType, new CategoryIDValueSlot(entityType).getSlotValue());
	}

}
