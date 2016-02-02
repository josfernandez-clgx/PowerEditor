package com.mindbox.pe.server.servlet;

import java.io.IOException;

import junit.framework.TestSuite;

import com.mindbox.pe.communication.ErrorResponse;
import com.mindbox.pe.communication.FetchUserProfileRequest;
import com.mindbox.pe.communication.LoginRequest;
import com.mindbox.pe.communication.RequestComm;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.server.bizlogic.AbstractServerTestBase;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.servlet.handlers.IRequestCommHandler;

public abstract class ServletTest extends AbstractServerTestBase {
	public static TestSuite suite() {
		TestSuite suite = new TestSuite(ServletTest.class.getName());
		suite.addTestSuite(ServletTest.class);
		return suite;
	}

	public ServletTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		initServlet();
	}

	private void initServlet() throws Exception, IOException {
		config.initServer();
		ConfigurationManager.getInstance().setServerBasePath(config.getServerResourceParentPath());
		ResourceUtil.initialize(ConfigurationManager.getInstance().getServerBasePath(), 
                new String[] { "resource.LabelsBundle",	"resource.MessagesBundle"});
	}

	protected void testRequestType(Class<?> correctRequestType, IRequestCommHandler<RequestComm<?>> handler, String expectedErrorMsg) throws Exception {
		RequestComm<?> requestOfWrongType = FetchUserProfileRequest.class.equals(correctRequestType) 
				? (RequestComm<?>) new LoginRequest(null, null)
				: (RequestComm<?>) new FetchUserProfileRequest(null);
				
		ErrorResponse response = (ErrorResponse) handler.serviceRequest(requestOfWrongType, null);
		
		assertEquals("InvalidRequestError", response.getErrorType());
		assertEquals(expectedErrorMsg, response.getErrorMessage());
	}

	protected void assertFailureResponse(ResponseComm response, String expectedErrorType, String expectedMsg, String expectedMsgKey) {
		ErrorResponse errResp = (ErrorResponse) response;
		assertEquals(expectedErrorType, errResp.getErrorType());
		assertEquals(expectedMsg, errResp.getErrorMessage());
		assertEquals(expectedMsgKey, errResp.getErrorResourceKey());
	}
}
