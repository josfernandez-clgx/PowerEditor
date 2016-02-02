package com.mindbox.pe.server.servlet.handlers;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.communication.BooleanResponse;
import com.mindbox.pe.communication.ResponseComm;

public class DateSynonymInUseRequestHandlerTest extends AbstractTestWithTestConfig {

	private static class HanlderImpl extends DateSynonymInUseRequestHandler {
		@SuppressWarnings("unused")
		protected boolean authorize(String userID, String sessionID, String privilegeStr) {
			return true;
		}
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("DateSynonymInUseRequestHandlerTest Tests");
		suite.addTestSuite(DateSynonymInUseRequestHandlerTest.class);
		return suite;
	}

	private DateSynonymInUseRequestHandler handler = null;

	public DateSynonymInUseRequestHandlerTest(String name) {
		super(name);
	}

	public void testHandleRequestReturnsBooleanResponse() throws Exception {
		ResponseComm response = handler.handleRequest(ObjectMother.createDateSynonymInUseRequest(), null);
		assertTrue(response instanceof BooleanResponse);
		assertFalse(((BooleanResponse) response).isTrue());
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		handler = new HanlderImpl();
	}

	protected void tearDown() throws Exception {
		// Tear downs for DateSynonymInUseRequestHandlerTest
		config.resetConfiguration();
		super.tearDown();
	}
}
