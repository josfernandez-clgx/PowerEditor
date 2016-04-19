package com.mindbox.pe.server.generator.rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.mindbox.pe.server.AbstractTestWithGenericEntityType;

public class ContextElementPatternValueSlotTest extends AbstractTestWithGenericEntityType {

	@Test
	public void testConstructorSetsCorrectType() throws Exception {
		assertEquals(ValueSlot.Type.CONTEXT_ELEMENT, new ContextElementPatternValueSlot(entityType, true).getType());
	}

	@Test
	public void testConstructorWithNullEntityThrowsNullPointerException() throws Exception {
		try {
			new ContextElementPatternValueSlot(null, true);
			fail("Expected NullPointerException");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	@Test
	public void testGetValueSlotReturnsGenericEntityType() throws Exception {
		assertEquals(entityType, new ContextElementPatternValueSlot(entityType, true).getSlotValue());
	}

}
