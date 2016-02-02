package com.mindbox.pe.communication;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;

public class ServerExceptionTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("ServerExceptionTest Tests");
		suite.addTestSuite(ServerExceptionTest.class);
		return suite;
	}

	private static class TestImpl extends ServerException {

		TestImpl(ErrorResponse cause) {
			super(cause);
		}
		TestImpl(String messageKey, Object[] args) {
			super(messageKey, args);
		}
		
	}
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public ServerExceptionTest(String name) {
		super(name);
	}
	
	public void testConstructorWithErrorResponseHappyCase() throws Exception {
		ErrorResponse errorResponse = new ErrorResponse("ServerError", "");
		ServerException serverException = new TestImpl(errorResponse);
		assertTrue(errorResponse == serverException.getErrorResponse());
		assertNull(serverException.getErrorMessageKey());
		assertNull(serverException.getErrorParams());
	}
	
	public void testConstructorWithErrorMessageKeyHappyCase() throws Exception {
		String key = ObjectMother.createString();
		Object[] objs = new Object[0];
		ServerException serverException = new TestImpl(key, objs);
		assertNull(serverException.getErrorResponse());
		assertEquals(key, serverException.getErrorMessageKey());
		assertEquals(0, serverException.getErrorParams().length);
	}
}
