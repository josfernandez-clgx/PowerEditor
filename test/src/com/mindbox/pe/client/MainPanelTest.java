package com.mindbox.pe.client;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.communication.ErrorResponse;
import com.mindbox.pe.communication.OperationFailedException;
import com.mindbox.pe.communication.ServerException;

/**
 * DO NOT include this as a part of automated unit testing.
 * @author kim
 *
 */
public class MainPanelTest extends AbstractClientTestBase {
	
	public static Test suite() {
		TestSuite suite = new TestSuite("MainPanelTest Tests");
		suite.addTestSuite(MainPanelTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public MainPanelTest(String name) {
		super(name);
	}
	
	/**
	 * Just to see the actual dialog. 
	 * Note: <b>Running this test will display a dialog. You must dismiss the dialog to continue.</b>
	 * @throws Exception
	 */
	public void testHandleRuntimeExceptionForNullPointerExceptionDisplaysDefaultmessage() throws Exception {
		ErrorResponse errorResponse = new ErrorResponse("ServerError", "");
		ServerException serverException = new OperationFailedException(errorResponse);
		ClientUtil.getInstance().showErrorDialog(serverException);
	}
}
