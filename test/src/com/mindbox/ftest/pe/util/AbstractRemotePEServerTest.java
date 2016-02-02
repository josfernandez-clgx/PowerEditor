package com.mindbox.ftest.pe.util;

import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.mindbox.pe.client.Communicator;
import com.mindbox.pe.client.DefaultCommunicator;
import com.mindbox.pe.client.MainApplication;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.EntityConfiguration;
import com.mindbox.pe.common.config.GuidelineTabConfig;
import com.mindbox.pe.common.validate.MessageDetail;
import com.mindbox.pe.communication.DeployResponse;
import com.mindbox.pe.communication.ImportResult;
import com.mindbox.pe.communication.LoginRequest;
import com.mindbox.pe.communication.LoginResponse;
import com.mindbox.pe.communication.RequestComm;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.ImportSpec;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.UserProfile;
import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.exceptions.CanceledException;
import com.mindbox.pe.model.filter.GuidelineReportFilter;

public abstract class AbstractRemotePEServerTest extends AbstractAppServerTest {

	private static final long MAX_WAIT_FOR_DEPLOYMENT = 10 * 60 * 1000L; // 10 minutes

	private static class TestMainApplication implements MainApplication {

		private String userID;
		private String sessionID;

		private TestMainApplication(String userID, String sessionID) {
			this.userID = userID;
			this.sessionID = sessionID;
		}

		public String getSessionID() {
			return sessionID;
		}

		public String getUserID() {
			return userID;
		}

		public boolean checkPermissionByPrivilegeName(String s) {
			return false;
		}

		public boolean checkViewOrEditGuidelinePermission(GuidelineTabConfig gtConfig) {
			return false;
		}

		public boolean checkViewOrEditGuidelinePermissionOnUsageType(TemplateUsageType usageType) {
			return false;
		}

		public boolean checkViewOrEditTemplatePermission(GuidelineTabConfig gtConfig) {
			return false;
		}

		public boolean checkViewOrEditTemplatePermissionOnUsageType(TemplateUsageType usageType) {
			return false;
		}

		public boolean confirmExit() {
			return false;
		}

		public void dispose() {
		}

		public Communicator getCommunicator() {
			return null;
		}

		public EntityConfiguration getEntityConfiguration() {
			return null;
		}

		public UserProfile getUserSession() {
			return null;
		}

		public void handleRuntimeException(Exception ex) {
		}

		public void reloadTemplates() throws ServerException {
		}

		public void setCursor(Cursor cursor) {
		}

		public void setStatusMsg(String msg) {
		}

		public void showTemplateEditPanel(GridTemplate template) throws CanceledException {
		}

	}

	protected String serverURLStr;
	protected String userID;
	protected String password;
	protected Communicator communicator;

	protected AbstractRemotePEServerTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		serverURLStr = config.getRequiredStringProperty("mindbox.test.pe.server");
		assertNotNull(serverURLStr);
		userID = config.getRequiredStringProperty("mindbox.test.pe.login.user");
		assertNotNull(userID);
		password = config.getRequiredStringProperty("mindbox.test.pe.login.pwd");
		assertNotNull(password);
	}

	protected void tearDown() throws Exception {
		if (communicator != null) {
			try {
				communicator.logout();
			}
			catch (ServerException ex) {
				logger.warn("Failed to log out " + userID, ex);
			}
		}
		super.tearDown();
	}

	protected final Communicator getCommunicator() throws Exception {
		if (communicator == null) {
			RequestComm.setServletURL(serverURLStr + "/PowerEditorServlet");
			LoginRequest loginRequest = new LoginRequest(userID, password);
			LoginResponse loginResponse = loginRequest.sendRequest();
			assertTrue(loginResponse.getLoginFailureMsg(), loginResponse.isAuthenticated());

			communicator = new DefaultCommunicator(new TestMainApplication(userID, loginResponse.getSessionID()));
		}
		return communicator;
	}

	protected void remoteImport(File fileToImport) throws Throwable {
		String xmlContent = config.getTextFileContext(fileToImport);
		ImportSpec importSpec = new ImportSpec(fileToImport.getAbsolutePath(), xmlContent, ImportSpec.IMPORT_DATA_REQUEST, false);
		ImportResult importResult = getCommunicator().importData(importSpec);
		assertNotNull(importResult);

		List<MessageDetail> errorMessages = importResult.getErrorMessages();
		assertTrue("There are " + errorMessages.size() + " import errors; see server logs for details; msgs = "
				+ UtilBase.toString(errorMessages), errorMessages.size() == 0);
	}

	protected void remoteDeploy(String status) throws Throwable {
		// deploy all rules & parameters & entities
		GuidelineReportFilter filter = new GuidelineReportFilter();
		filter.setIncludeGuidelines(true);
		filter.setIncludeParameters(true);
		filter.setIncludeProcessData(true);
		filter.setIncludeCBR(true);
		filter.setThisStatusAndAbove(status);
		filter.setIncludeEntities(true);
		filter.setIncludeDateSynonyms(true);

		DeployResponse deployResponse = (DeployResponse) getCommunicator().deploy(filter, false);
		assertTrue("Deployment errored out on the server; check server logs for details", deployResponse.getGenerateRunId() > 0);

		// check deploy status, until it's no longer running
		boolean isDone = false;
		long startTime = System.currentTimeMillis();
		List<GenerateStats> statsList = null;
		while (!isDone && (System.currentTimeMillis() - startTime) < MAX_WAIT_FOR_DEPLOYMENT) {
			Thread.sleep(500L);
			statsList = getCommunicator().retrieveDeployStats(deployResponse.getGenerateRunId());

			isDone = statsList != null && !GenerateStats.isRunning(statsList);
		}
		assertTrue("Deployment timed out; waited " + MAX_WAIT_FOR_DEPLOYMENT / 1000 + " seconds", isDone);
		assertEquals("There are deploy errors; check server logs for details", 0, GenerateStats.computeErrorCount(statsList));
	}

	protected final boolean isPowerEditorRunning() throws Exception {
		try {
			String response = sendHttpRequest("validate_config.jsp");
			return response != null;
		}
		catch (ConnectException ex) {
			logger.info("ConnectException while trying to connect to PE. It's probably down: " + ex.getMessage());
			return false;
		}
		catch (IOException ex) {
			logger.info("IOException while trying to connect to PE. It's probably down", ex);
			return false;
		}
	}

	protected final void assertPowerEditorIsRunning(long timeout) throws Exception {
		long startTime = System.currentTimeMillis();
		boolean isPERunning = false;
		while (!isPERunning && (System.currentTimeMillis() - startTime) < timeout) {
			try {
				Thread.sleep(500L);
				isPERunning = isPowerEditorRunning();
			}
			catch (InterruptedException e) {
			}
		}
		assertTrue("PETE PowerEditor is not running", isPERunning);
	}

	protected final String sendHttpRequest(String path) throws Exception {
		HttpClient httpClient = new HttpClient();
		HttpMethod method = new GetMethod(serverURLStr + "/" + path);
		return executeMethod(httpClient, method);
	}

	protected final String sendHttpRequest(String path, Map<String, Object> params) throws Exception {
		HttpClient httpClient = new HttpClient();
		PostMethod method = new PostMethod(serverURLStr + "/" + path);
		for (Map.Entry<String, Object> element : params.entrySet()) {
			method.addParameter(element.getKey(), element.getValue().toString());
		}
		return executeMethod(httpClient, method);
	}

	private String executeMethod(HttpClient httpClient, HttpMethod method) throws HttpException, IOException {
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(2, false));
		try {
			// Execute the method.
			int statusCode = httpClient.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				fail("Method failed: " + method.getStatusLine());
			}

			// Read the response body.
			byte[] responseBody = method.getResponseBody();
			return new String(responseBody);
		}
		finally {
			// Release the connection.
			method.releaseConnection();
		}
	}

}
