package com.mindbox.pe.server.servlet;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;

import com.mindbox.pe.communication.ErrorResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.server.AbstractServerTestBase;
import com.mindbox.pe.server.config.ConfigurationManager;

public abstract class ServletTest extends AbstractServerTestBase {

	protected static final void assertFailureResponse(ResponseComm response, String expectedErrorType, String expectedMsg, String expectedMsgKey) {
		ErrorResponse errResp = (ErrorResponse) response;
		assertEquals(expectedErrorType, errResp.getErrorType());
		assertEquals(expectedMsg, errResp.getErrorMessage());
		assertEquals(expectedMsgKey, errResp.getErrorResourceKey());
	}

	private void initServlet() throws Exception, IOException {
		config.initServer();
		ConfigurationManager.getInstance().setServerBasePath(config.getServerResourceParentPath());
		ResourceUtil.initialize(ConfigurationManager.getInstance().getServerBasePath(), new String[] { "LabelsBundle", "MessagesBundle" });
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		initServlet();
	}

}
