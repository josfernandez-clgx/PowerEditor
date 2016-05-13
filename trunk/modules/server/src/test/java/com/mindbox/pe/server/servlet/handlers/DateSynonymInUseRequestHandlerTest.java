package com.mindbox.pe.server.servlet.handlers;

import static com.mindbox.pe.server.ServerTestObjectMother.createDateSynonymInUseRequest;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.communication.BooleanResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.server.AbstractTestWithTestConfig;

public class DateSynonymInUseRequestHandlerTest extends AbstractTestWithTestConfig {

	private static class HanlderImpl extends DateSynonymInUseRequestHandler {
		@SuppressWarnings("unused")
		protected boolean authorize(String userID, String sessionID, String privilegeStr) {
			return true;
		}
	}

	private DateSynonymInUseRequestHandler handler = null;

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		handler = new HanlderImpl();
	}

	public void tearDown() throws Exception {
		// Tear downs for DateSynonymInUseRequestHandlerTest
		config.resetConfiguration();
		super.tearDown();
	}

	@Test
	public void testHandleRequestReturnsBooleanResponse() throws Exception {
		ResponseComm response = handler.handleRequest(createDateSynonymInUseRequest(), null);
		assertTrue(response instanceof BooleanResponse);
		assertFalse(((BooleanResponse) response).isTrue());
	}
}
