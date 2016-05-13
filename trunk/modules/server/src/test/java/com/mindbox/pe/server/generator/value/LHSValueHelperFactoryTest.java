package com.mindbox.pe.server.generator.value;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.server.AbstractTestWithTestConfig;

public class LHSValueHelperFactoryTest extends AbstractTestWithTestConfig {

	@Test
	public void testGetLHSValueHelperReturnsTheSameInstance() throws Exception {
		TemplateUsageType type = TemplateUsageType.getAllInstances()[0];
		LHSValueHelper helper = LHSValueHelperFactory.getLHSValueHelper(type);
		assertNotNull(helper);
		assertTrue(helper == LHSValueHelperFactory.getLHSValueHelper(type));
	}

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
	}

	public void tearDown() throws Exception {
		config.resetConfiguration();
		super.tearDown();
	}
}
