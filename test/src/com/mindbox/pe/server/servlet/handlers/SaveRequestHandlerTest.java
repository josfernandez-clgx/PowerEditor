package com.mindbox.pe.server.servlet.handlers;

import junit.framework.TestSuite;

import org.easymock.MockControl;

import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.SaveRequest;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.server.servlet.ServletTest;

public class SaveRequestHandlerTest extends ServletTest {
	private MockControl mockPersistentControl;
	private Persistent mockPersistent;
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite(SaveRequestHandlerTest.class.getName());
		suite.addTestSuite(SaveRequestHandlerTest.class);
		return suite;
	}
	
	public SaveRequestHandlerTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockPersistentControl = MockControl.createControl(Persistent.class);
		mockPersistent = (Persistent) mockPersistentControl.getMock();
	}
	
	protected void replay() {
		super.replay();
		mockPersistentControl.replay();
	}
	
	protected void verify() {
		super.verify();
		mockPersistentControl.verify();
	}
	
	public void testFailedAuthentication() throws Exception {
		// record
		getMockSessionManager().authenticateSession("user", "sessionId");
		getMockSessionManagerControl().setReturnValue(false);

		replay();
		ResponseComm response = doSave();

		verify();
		assertFailureResponse(response, "AuthenticationFailure", null, "AuthenticationFailureMsg");
	}
	
	public void testSuccess() throws Exception {
		// record
		getMockSessionManager().authenticateSession("user", "sessionId");
		getMockSessionManagerControl().setReturnValue(true);
		
		getMockSecurityCacheManager().getUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(null);
		
		replay();
		doSave();

		verify();
		// TODO: davies, Aug 21, 2006: finish test after refactoring SaveRequestHandler.processRequest(...), mock out BizActionCoordinator
//		assertSaveSuccess(response);
	}

	private ResponseComm doSave() {
		ResponseComm response = (ResponseComm) 
				new SaveRequestHandler().serviceRequest(new SaveRequest("user", "sessionId", mockPersistent, false, false), getMockHttpServletRequest());
		return response;
	}	

}
