package com.mindbox.ftest.pe.util;


import com.mindbox.pe.AbstractTestWithTestConfig;

public abstract class AbstractAppServerTest extends AbstractTestWithTestConfig {

	private static final long COMMAND_TIMEOUT = 20 * 1000L; // 20 seconds

	protected String appServerServiceName;
	protected String espServiceName;
	private TimeOutCommandExecutor commandExecutor;

	protected AbstractAppServerTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		appServerServiceName = config.getRequiredStringProperty("mindbox.test.app.server.service");
		assertNotNull(appServerServiceName);
		espServiceName = config.getRequiredStringProperty("mindbox.test.esp.service");
		assertNotNull(espServiceName);
	}

	protected final void startAppServerService() throws Exception {
		assertEquals(0, executeWithTimeOut(new String[] { "net", "start", appServerServiceName }));
	}

	protected final void stopAppServerService() throws Exception {
		executeWithTimeOut(new String[] { "net", "stop", appServerServiceName });
	}

	protected final void startESPService() throws Exception {
		assertEquals(0, executeWithTimeOut(new String[] { "net", "start", espServiceName }));
	}

	protected final void stopESPService() throws Exception {
		assertEquals(0, executeWithTimeOut(new String[] { "net", "stop", espServiceName }));
	}

	protected final int executeWithTimeOut(String[] cmdArray) throws Exception {
		return getTimeOutCommandExecutor().execute(cmdArray);
	}

	private TimeOutCommandExecutor getTimeOutCommandExecutor() {
		if (commandExecutor == null) {
			commandExecutor = new TimeOutCommandExecutor(COMMAND_TIMEOUT);
		}
		return commandExecutor;
	}
}
