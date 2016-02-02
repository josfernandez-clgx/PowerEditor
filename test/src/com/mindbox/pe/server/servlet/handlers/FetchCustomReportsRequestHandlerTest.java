package com.mindbox.pe.server.servlet.handlers;

import junit.framework.TestSuite;

import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.communication.FetchCustomReportsRequest;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.server.servlet.ServletTest;

public class FetchCustomReportsRequestHandlerTest extends ServletTest {
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite(FetchCustomReportsRequestHandlerTest.class.getName());
		suite.addTestSuite(FetchCustomReportsRequestHandlerTest.class);
		return suite;
	}

	public FetchCustomReportsRequestHandlerTest(String name) {
		super(name);
	}

	public void testFailedAuthorization() throws Exception {
		// record
		getMockSessionManager().authenticateSession("user", "sessionId");
		getMockSessionManagerControl().setReturnValue(true);
		
		getMockSecurityCacheManager().authorize("user", PrivilegeConstants.PRIV_VIEW_REPORT);
		getMockSecurityCacheManagerControl().setReturnValue(false);
		
		replay();
		
		FetchCustomReportsRequest request = new FetchCustomReportsRequest("user", "sessionId");
		ResponseComm response = new FetchCustomReportsRequestHandler().serviceRequest(request, getMockHttpServletRequest());

		verify();
		assertFailureResponse(response, "AuthorizationFailure", null, "AuthorizationFailureMsg");
	}
	

}
