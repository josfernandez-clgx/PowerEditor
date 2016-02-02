package com.mindbox.ftest.pe.util;


public class IsPERunningTest extends AbstractRemotePEServerTest {

	public IsPERunningTest(String name) {
		super(name);
	}

	protected void runTest() throws Throwable {
		assertPowerEditorIsRunning(5* 60 * 1000L);
	}
	
}
