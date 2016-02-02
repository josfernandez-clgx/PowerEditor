package com.mindbox.ftest.pe.util;

import com.mindbox.pe.AbstractTestWithTestConfig;

public final class ShutdownESPTest extends AbstractTestWithTestConfig {

	public ShutdownESPTest(String name) {
		super(name);
	}

	protected void runTest() throws Throwable {
		try {
			new ESPTestUtil(config).shutDownESP();
		}
		catch (Exception ex) {
			logger.error("Faield to shut down", ex);
			fail("Failed to shutdown: " + ex.getMessage());
		}
	}
}
