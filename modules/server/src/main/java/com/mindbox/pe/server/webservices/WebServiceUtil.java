package com.mindbox.pe.server.webservices;

import static com.mindbox.pe.common.LogUtil.logDebug;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.mindbox.pe.server.servlet.ServletActionException;
import com.mindbox.pe.server.servlet.handlers.LoginAttempt;

public class WebServiceUtil {

	private static final Logger LOG = Logger.getLogger(WebServiceUtil.class);
	private static final String WS_AUTH_USER_ATTRIBUTE = "com.mindbox.pe.ws.AuthUserId";

	/**
	 * Check the credentials against the PE database.
	 * @param un unReportGenerato
	 * @param pw pwReportGenerato
	 * @return true if credentials are valid.
	 */
	public static boolean checkCredentials(String un, String pw) {
		boolean authenticated = false;
		try {
			// SGS - Authenticate using PowerEditor mechanisms
			final LoginAttempt loginAttempt = new LoginAttempt(un, pw);
			logDebug(LOG, "Checking login attempt %s", loginAttempt);
			authenticated = !loginAttempt.failed();
		}
		catch (ServletActionException sae) {
			LOG.error("ServletActionException occurred in WebService PlainTextPasswordValidator: " + sae.getMessage());
		}
		catch (Exception ex) {
			LOG.error("Exception occurred in WebService PlainTextPasswordValidator: " + ex.getMessage());
		}
		return authenticated;
	}

	public static String getAuthenticatedUserId(final HttpServletRequest request) {
		return String.class.cast(request.getAttribute(WS_AUTH_USER_ATTRIBUTE));
	}

	public static void setAuthenticatedUserId(final HttpServletRequest request, final String userId) {
		request.setAttribute(WS_AUTH_USER_ATTRIBUTE, userId);
	}


	private WebServiceUtil() {
	}

}
