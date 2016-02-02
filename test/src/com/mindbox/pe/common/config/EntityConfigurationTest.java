package com.mindbox.pe.common.config;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class EntityConfigurationTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("EntityConfigurationTest Tests");
		suite.addTestSuite(EntityConfigurationTest.class);
		return suite;
	}

	private EntityConfiguration entityConfiguration;

	public EntityConfigurationTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		entityConfiguration = new EntityConfiguration();
	}

	protected void tearDown() throws Exception {
		entityConfiguration = null;
		super.tearDown();
	}

	public void testGetEntityTypeForMessageContextWithNoEntityConfiguredForMessageContextReturnsNull() throws Exception {
		EntityTypeDefinition etDef = new EntityTypeDefinition();
		etDef.setCanClone("no");
		etDef.setCategoryType(20);
		etDef.setDisplayName("Program");
		etDef.setName("program");
		etDef.setTypeID(2);
		etDef.setUseInCompatibility("false");
		etDef.setUseInContext("yes");
		etDef.setUseInMessageContext("no");
		entityConfiguration.addObject(etDef);

		etDef = new EntityTypeDefinition();
		etDef.setCanClone("no");
		etDef.setCategoryType(30);
		etDef.setDisplayName("Entity");
		etDef.setName("entity");
		etDef.setTypeID(3);
		etDef.setUseInCompatibility("false");
		etDef.setUseInContext("yes");
		etDef.setUseInMessageContext("no");
		entityConfiguration.addObject(etDef);

		assertNull(entityConfiguration.getEntityTypeForMessageContext());
	}

	public void testGetEntityTypeForMessageContextWithEntityConfiguredForMessageContextReturnsCorrectOne() throws Exception {
		EntityTypeDefinition etDef = new EntityTypeDefinition();
		etDef.setCanClone("no");
		etDef.setCategoryType(20);
		etDef.setDisplayName("Program");
		etDef.setName("program");
		etDef.setTypeID(2);
		etDef.setUseInCompatibility("false");
		etDef.setUseInContext("true");
		etDef.setUseInMessageContext("no");
		entityConfiguration.addObject(etDef);

		etDef = new EntityTypeDefinition();
		etDef.setCanClone("no");
		etDef.setCategoryType(30);
		etDef.setDisplayName("Entity");
		etDef.setName("entity");
		etDef.setTypeID(3);
		etDef.setUseInCompatibility("false");
		etDef.setUseInContext("yes");
		etDef.setUseInMessageContext("yes");
		entityConfiguration.addObject(etDef);

		assertEquals(etDef, entityConfiguration.getEntityTypeForMessageContext());
	}
}
