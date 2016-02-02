package com.mindbox.pe.server.generator.value;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.model.TemplateUsageType;

public class LHSValueHelperFactoryTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("LHSValueHelperFactoryTest Tests");
		suite.addTestSuite(LHSValueHelperFactoryTest.class);
		return suite;
	}

	public LHSValueHelperFactoryTest(String name) {
		super(name);
	}

	public void testGetLHSValueHelperReturnsTheSameInstance() throws Exception {
		TemplateUsageType type = TemplateUsageType.getAllInstances()[0];
		LHSValueHelper helper = LHSValueHelperFactory.getLHSValueHelper(type);
		assertNotNull(helper);
		assertTrue(helper == LHSValueHelperFactory.getLHSValueHelper(type));
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
	}

	protected void tearDown() throws Exception {
		config.resetConfiguration();
		super.tearDown();
	}
}
