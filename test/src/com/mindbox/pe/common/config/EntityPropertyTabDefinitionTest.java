package com.mindbox.pe.common.config;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class EntityPropertyTabDefinitionTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("EntityPropertyTabDefinitionTest Tests");
		suite.addTestSuite(EntityPropertyTabDefinitionTest.class);
		return suite;
	}

	public EntityPropertyTabDefinitionTest(String name) {
		super(name);
	}

	public void testIsContainedInTabWithNullRetursnFalse() throws Exception {
		assertFalse(EntityPropertyTabDefinition.isContainedInTab("name", null));
	}

	public void testIsContainedInTabWithEmptyArrayRetursnFalse() throws Exception {
		assertFalse(EntityPropertyTabDefinition.isContainedInTab("name", new EntityPropertyTabDefinition[0]));
	}

	public void testIsContainedInTabWithValidValuesReturnsTrue() throws Exception {
		EntityPropertyTabDefinition def = new EntityPropertyTabDefinition();
		def.setTitle("Title");
		def.addPropertyName("name");
		assertTrue(EntityPropertyTabDefinition.isContainedInTab("name", new EntityPropertyTabDefinition[] { def }));
	}
}
