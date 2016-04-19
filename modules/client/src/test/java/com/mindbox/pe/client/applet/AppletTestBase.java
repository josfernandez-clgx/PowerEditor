/*
 * Created on Sep 10, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.client.applet;

import static org.junit.Assert.assertTrue;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.communication.DeployRequest;
import com.mindbox.pe.communication.DeployResponse;
import com.mindbox.pe.communication.LoginRequest;
import com.mindbox.pe.communication.LoginResponse;
import com.mindbox.pe.communication.LogoutRequest;
import com.mindbox.pe.communication.RequestComm;
import com.mindbox.pe.model.filter.GuidelineReportFilter;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since 
 */
public abstract class AppletTestBase extends AbstractClientTestBase {

	/**
	 * URL of the servlet.
	 */
	protected final String serverURL;

	/**
	 * User ID used for the last login.
	 * Reset only whenever a login method has been called.
	 */
	protected String userID = null;

	/**
	 * Session ID of the last login.
	 * Reset only after a successful invocation of a login method.
	 */
	protected String sessionID = null;

	protected AppletTestBase() {
		this.serverURL = "http://localhost:9999/powereditor";
		RequestComm.setServletURL(this.serverURL);
	}

	/**
	 * Login to the channel specified in the test configuration using the credentials in test configuration.
	 * @throws Exception on error
	 */
	protected final void login() throws Exception {
		login_internal("demo", "demo");
	}

	/**
	 * Login to the specified channel using the specified credentials.
	 * @param userID the user ID to login as
	 * @param password the password of the user
	 * @throws Exception on error
	 */
	protected final void login(String userID, String password) throws Exception {
		login_internal(userID, password);
	}

	private void login_internal(String userID, String password) throws Exception {
		log(">>> login");
		this.userID = userID;
		LoginRequest loginRequestComm = new LoginRequest(userID, password);

		LoginResponse loginResponse = loginRequestComm.sendRequest(null);

		log("reponse obtained: " + loginResponse);

		assertTrue("Login failed: " + loginResponse.getLoginFailureMsg(), loginResponse.isAuthenticated());

		this.sessionID = loginResponse.getSessionID();
		log("<<< login");
	}

	protected final void logout() throws Exception {
		log(">>> logout");
		(new LogoutRequest(userID, sessionID)).sendRequest(null);
		log("<<< logout");
	}

	protected final void runDeployment() throws Exception {
		log(">>> testRunDeployment");
		login();
		log("testRunDeployment: logged in - session = " + sessionID);

		GuidelineReportFilter filter = new GuidelineReportFilter();
		filter.setIncludeGuidelines(true);
		filter.setIncludeParameters(true);
		filter.setIncludeProcessData(true);
		filter.setIncludeCBR(true);
		filter.setDaysAgo(30);
		filter.setThisStatusAndAbove("Draft");
		DeployRequest deployrequestcomm = new DeployRequest(userID, sessionID, filter, false);

		DeployResponse deployresponsecomm = deployrequestcomm.sendRequest(null);
		int runID = deployresponsecomm.getGenerateRunId();
		log("runID = " + runID);
		log("<<< testRunDeployment");
	}

}
