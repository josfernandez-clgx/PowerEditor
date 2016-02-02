package com.mindbox.pe.server.imexport.digest;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class EntityTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("EntityTest Tests");
		suite.addTestSuite(EntityTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public EntityTest(String name) {
		super(name);
	}
	
	public void testConstructorSetsInvariantsPropertly() throws Exception {
		assertFalse(new Entity().isImported());
	}
}
