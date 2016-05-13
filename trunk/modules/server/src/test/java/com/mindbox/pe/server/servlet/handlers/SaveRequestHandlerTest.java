package com.mindbox.pe.server.servlet.handlers;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.SaveRequest;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.server.servlet.ServletTest;

public class SaveRequestHandlerTest extends ServletTest {

	private Persistent mockPersistent;

	private ResponseComm doSave(HttpServletRequest httpServletRequest) {
		ResponseComm response = (ResponseComm) new SaveRequestHandler().serviceRequest(new SaveRequest("user", "sessionId", mockPersistent, false, false), httpServletRequest);
		return response;
	}

	public void setUp() throws Exception {
		super.setUp();
		mockPersistent = createMock(Persistent.class);
	}

	@Test
	public void testFailedAuthentication() throws Exception {
		// record
		expect(getMockSessionManager().hasSession("sessionId")).andReturn(true);

		expect(getMockSessionManager().authenticateSession("user", "sessionId")).andReturn(false);

		HttpServletRequest httpServletRequest = getMockHttpServletRequest();

		replayAllMocks();
		replay(mockPersistent);

		ResponseComm response = doSave(httpServletRequest);

		verifyAllMocks();
		verify(mockPersistent);
		assertFailureResponse(response, "AuthenticationFailure", null, "AuthenticationFailureMsg");
	}

	@Test
	public void testSuccess() throws Exception {
		// record
		expect(getMockSessionManager().hasSession("sessionId")).andReturn(true);

		expect(getMockSessionManager().authenticateSession("user", "sessionId")).andReturn(true);

		expect(getMockSecurityCacheManager().getUser("user")).andReturn(null);

		HttpServletRequest httpServletRequest = getMockHttpServletRequest();

		replayAllMocks();
		replay(mockPersistent);

		doSave(httpServletRequest);

		verifyAllMocks();
		verify(mockPersistent);
		// TODO: davies, Aug 21, 2006: finish test after refactoring SaveRequestHandler.processRequest(...), mock out BizActionCoordinator
		//		assertSaveSuccess(response);
	}

}
