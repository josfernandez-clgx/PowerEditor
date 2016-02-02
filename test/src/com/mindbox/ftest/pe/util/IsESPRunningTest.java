package com.mindbox.ftest.pe.util;

import com.mindbox.pe.AbstractTestWithTestConfig;

public final class IsESPRunningTest extends AbstractTestWithTestConfig {


	public IsESPRunningTest(String name) {
		super(name);
	}

	protected void runTest() throws Throwable {
		assertTrue(new ESPTestUtil(config).isESPRunning());
	}
}
