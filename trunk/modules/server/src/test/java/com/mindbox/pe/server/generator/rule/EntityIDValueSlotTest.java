package com.mindbox.pe.server.generator.rule;

import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.mindbox.pe.server.AbstractTestWithGenericEntityType;

/**
 * Unit test for {@link EntityIDValueSlot}.
 * 
 * @author kim
 * 
 */
public class EntityIDValueSlotTest extends AbstractTestWithGenericEntityType {

	@Test
	public void testConstructorSetsCorrectType() throws Exception {
		assertEquals(ValueSlot.Type.ENTITY_ID, new EntityIDValueSlot(entityType, "").getType());
	}

	@Test
	public void testConstructorSetsEntityVariableName() throws Exception {
		String str = createString();
		assertEquals(str, new EntityIDValueSlot(entityType, str).getEntityVariableName());
	}

	@Test
	public void testConstructorWithNullEntityThrowsNullPointerException() throws Exception {
		try {
			new EntityIDValueSlot(null, "");
			fail("Expected NullPointerException");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	@Test
	public void testGetValueSlotReturnsGenericEntityType() throws Exception {
		assertEquals(entityType, new EntityIDValueSlot(entityType, "").getSlotValue());
	}
}
