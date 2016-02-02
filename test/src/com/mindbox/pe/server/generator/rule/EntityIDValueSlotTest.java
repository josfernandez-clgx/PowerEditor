package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithGenericEntityType;
import com.mindbox.pe.ObjectMother;

/**
 * Unit test for {@link EntityIDValueSlot}.
 * @author kim
 *
 */
public class EntityIDValueSlotTest extends AbstractTestWithGenericEntityType {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("EntityIDValueSlotTest");
		suite.addTestSuite(EntityIDValueSlotTest.class);
		return suite;
	}

	public EntityIDValueSlotTest(String name) {
		super(name);
	}

	public void testConstructorSetsCorrectType() throws Exception {
		assertEquals(ValueSlot.Type.ENTITY_ID, new EntityIDValueSlot(entityType, "").getType());
	}

	public void testConstructorSetsEntityVariableName() throws Exception {
		String str = ObjectMother.createString();
		assertEquals(str, new EntityIDValueSlot(entityType, str).getEntityVariableName());
	}

	public void testConstructorWithNullEntityThrowsNullPointerException() throws Exception {
		try {
			new EntityIDValueSlot(null, "");
			fail("Expected NullPointerException");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testGetValueSlotReturnsGenericEntityType() throws Exception {
		assertEquals(entityType, new EntityIDValueSlot(entityType, "").getSlotValue());
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
