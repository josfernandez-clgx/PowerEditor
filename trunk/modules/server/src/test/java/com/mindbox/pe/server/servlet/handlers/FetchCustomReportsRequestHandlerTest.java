package com.mindbox.pe.server.servlet.handlers;

import static org.easymock.EasyMock.expect;

import org.junit.Test;

import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.communication.FetchCustomReportsRequest;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.server.servlet.ServletTest;

public class FetchCustomReportsRequestHandlerTest extends ServletTest {

	@Test
	public void testFailedAuthorization() throws Exception {
		final String sessionId = "session-id";

		// record
		expect(getMockSessionManager().hasSession(sessionId)).andReturn(true);
		expect(getMockSessionManager().authenticateSession("user", sessionId)).andReturn(true);

		expect(getMockSecurityCacheManager().authorize("user", PrivilegeConstants.PRIV_VIEW_REPORT)).andReturn(false);
		getMockHttpServletRequest();

		replayAllMocks();

		FetchCustomReportsRequest request = new FetchCustomReportsRequest("user", sessionId);
		ResponseComm response = new FetchCustomReportsRequestHandler().serviceRequest(request, getMockHttpServletRequest());

		verifyAllMocks();

		assertFailureResponse(response, "AuthorizationFailure", null, "AuthorizationFailureMsg");
	}

}
