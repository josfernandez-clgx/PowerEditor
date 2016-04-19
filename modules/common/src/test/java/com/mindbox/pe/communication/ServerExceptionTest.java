package com.mindbox.pe.communication;

import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class ServerExceptionTest extends AbstractTestBase {

	private static class TestImpl extends ServerException {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7531421972886706901L;

		TestImpl(ErrorResponse cause) {
			super(cause);
		}

		TestImpl(String messageKey, Object[] args) {
			super(messageKey, args);
		}

	}

	@Test
	public void testConstructorWithErrorResponseHappyCase() throws Exception {
		ErrorResponse errorResponse = new ErrorResponse("ServerError", "");
		ServerException serverException = new TestImpl(errorResponse);
		assertTrue(errorResponse == serverException.getErrorResponse());
		assertNull(serverException.getErrorMessageKey());
		assertNull(serverException.getErrorParams());
	}

	@Test
	public void testConstructorWithErrorMessageKeyHappyCase() throws Exception {
		String key = createString();
		Object[] objs = new Object[0];
		ServerException serverException = new TestImpl(key, objs);
		assertNull(serverException.getErrorResponse());
		assertEquals(key, serverException.getErrorMessageKey());
		assertEquals(0, serverException.getErrorParams().length);
	}
}
