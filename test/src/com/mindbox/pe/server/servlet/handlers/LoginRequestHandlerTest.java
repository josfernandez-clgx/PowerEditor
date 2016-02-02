package com.mindbox.pe.server.servlet.handlers;

import java.util.Calendar;

import junit.framework.TestSuite;

import org.easymock.MockControl;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.DateUtil;
import com.mindbox.pe.communication.LoginRequest;
import com.mindbox.pe.communication.LoginResponse;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.UserPasswordPoliciesConfig;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletActionException;
import com.mindbox.pe.server.servlet.ServletTest;

public class LoginRequestHandlerTest extends ServletTest {

	private final String userID = "user";
	private final String passwordAsClearText = "password";

	public static TestSuite suite() {
		TestSuite suite = new TestSuite(LoginRequestHandlerTest.class.getName());
		suite.addTestSuite(LoginRequestHandlerTest.class);
		return suite;
	}

	public LoginRequestHandlerTest(String name) {
		super(name);
	}

	public void testFailedAuthentication() throws Exception {
		User user = ObjectMother.createUser();
		// record			
		getMockSecurityCacheManager().hasUser(userID);
		getMockSecurityCacheManagerControl().setReturnValue(true);

		getMockSecurityCacheManager().getUser(userID);
		getMockSecurityCacheManagerControl().setReturnValue(user);

		getMockUserAuthenticationProvider().authenticate(userID, passwordAsClearText);
		getMockUserAuthenticationProviderControl().setReturnValue(false);

		getMockSecurityCacheManager().getUser(userID);
		getMockSecurityCacheManagerControl().setReturnValue(user);

		getMockSecurityCacheManager().hasUser(userID);
		getMockSecurityCacheManagerControl().setReturnValue(true);

		replay();
		LoginResponse response = doLogin();

		verify();
		assertLoginFailure(response, "Invalid Id or Password. Please try again.");
	}

	public void testUserLockedOutBecauseOfStatus() throws Exception {
		User user = ObjectMother.createUser();
		user.setStatus(Constants.LOCKOUT_STATUS);

		// record        
		getMockSecurityCacheManager().hasUser(userID);
		getMockSecurityCacheManagerControl().setReturnValue(true);

		getMockSecurityCacheManager().getUser(userID);
		getMockSecurityCacheManagerControl().setReturnValue(user);

		replay();
		LoginResponse response = doLogin();

		verify();
		assertUserLockedOut(response, "This user has been locked out of the system. Please contact system administrator.");
	}

	public void testUserLockedOutBecauseOfPasswordExpiration() throws Exception {
		User user = ObjectMother.createUser();
		UserPasswordPoliciesConfig.ExpirationConfig expireConfig = ConfigurationManager
				.getInstance()
				.getUserPasswordPoliciesConfig()
				.getExpirationConfig();
		int expirationDays = expireConfig.getExpirationDays();
		Calendar cal = Calendar.getInstance();
		long timeToSubstract = (expirationDays + 1) * DateUtil.MILLIS_PER_DAY;
		cal.setTimeInMillis(cal.getTimeInMillis() - timeToSubstract);
		user.setCurrentPassword("", cal.getTime());

		// record
		getMockSecurityCacheManager().hasUser(userID);
		getMockSecurityCacheManagerControl().setReturnValue(true);

		getMockSecurityCacheManager().getUser(userID);
		getMockSecurityCacheManagerControl().setReturnValue(user);

		replay();
		LoginResponse response = doLogin();

		verify();
		assertUserLockedOut(response, "This user has been locked out of the system. Please contact system administrator.");
	}

	public void testNeedToResetPassword() throws Exception {
		User user = ObjectMother.createUser();
		user.setPasswordChangeRequired(true);

		getMockSecurityCacheManager().hasUser(userID);
		getMockSecurityCacheManagerControl().setReturnValue(true);

		// record
		getMockUserAuthenticationProvider().authenticate(userID, passwordAsClearText);
		getMockUserAuthenticationProviderControl().setReturnValue(true);

		getMockSecurityCacheManager().getUser(userID); // login attempt
		getMockSecurityCacheManagerControl().setReturnValue(user);

		getMockSecurityCacheManager().getUser(userID); // is pwd change req'd
		getMockSecurityCacheManagerControl().setReturnValue(user);

		replay();
		LoginResponse response = doLogin();

		verify();
		assertNeedToResetPassword(
				response,
				"You need to reset your password since this is either your first time logging into PowerEditor or your adminsistrator has reset your password.");
	}

	public void testSuccess() throws Exception {
		User user = ObjectMother.createUser();

		assertFalse(user.getPasswordChangeRequired()); // sanity check

		// record
		getMockSecurityCacheManager().hasUser(userID);
		getMockSecurityCacheManagerControl().setReturnValue(true);

		getMockUserAuthenticationProvider().authenticate(userID, passwordAsClearText);
		getMockUserAuthenticationProviderControl().setReturnValue(true);

		getMockSecurityCacheManager().getUser(userID); // login attempt
		getMockSecurityCacheManagerControl().setReturnValue(user);

		getMockSecurityCacheManager().getUser(userID); // is pwd change req'd
		getMockSecurityCacheManagerControl().setReturnValue(user);

		getMockSecurityCacheManager().getUser(userID); // get user in handler
		getMockSecurityCacheManagerControl().setReturnValue(user);

		getMockHttpServletRequest().getSession();
		getMockHttpServletRequestControl().setReturnValue(getMockHttpSession());

		getMockSessionManager().registerSession(null);
		getMockSessionManagerControl().setMatcher(MockControl.ALWAYS_MATCHER);

		getMockHttpSession().getId();
		getMockHttpSessionControl().setReturnValue("sessionId");

		getMockUserAuthenticationProvider().arePasswordsStoredExternally();
		getMockUserAuthenticationProviderControl().setReturnValue(true); // pwds not persisted, thus no warning

		getMockAuditLogger().logLogIn(user.getUserID());

		// replay
		replay();
		LoginResponse response = doLogin();

		// verify
		verify();
		assertLoginSuccess(response);
	}

	public void testWarnExpiration() throws Exception {
		User user = ObjectMother.createUser();
		UserPasswordPoliciesConfig.ExpirationConfig expireConfig = ConfigurationManager
				.getInstance()
				.getUserPasswordPoliciesConfig()
				.getExpirationConfig();
		int expirationDays = expireConfig.getExpirationDays();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 0 - expirationDays);
		user.setCurrentPassword("", cal.getTime());
		// record
		getMockSecurityCacheManager().hasUser(userID);
		getMockSecurityCacheManagerControl().setReturnValue(true);

		getMockUserAuthenticationProvider().authenticate(userID, passwordAsClearText);
		getMockUserAuthenticationProviderControl().setReturnValue(true);

		getMockHttpServletRequest().getSession();
		getMockHttpServletRequestControl().setReturnValue(getMockHttpSession());

		getMockSessionManager().registerSession(null);
		getMockSessionManagerControl().setMatcher(MockControl.ALWAYS_MATCHER);

		getMockUserAuthenticationProvider().arePasswordsStoredExternally();
		getMockUserAuthenticationProviderControl().setReturnValue(false); // pwds persisted by PE

		getMockSecurityCacheManager().getUser(userID); // login attempt
		getMockSecurityCacheManagerControl().setReturnValue(user);

		getMockSecurityCacheManager().getUser(userID); // is pwd change req'd
		getMockSecurityCacheManagerControl().setReturnValue(user);

		getMockSecurityCacheManager().getUser(userID); // get user in handler
		getMockSecurityCacheManagerControl().setReturnValue(user);

		getMockHttpSession().getId();
		getMockHttpSessionControl().setReturnValue("sessionId");

		getMockSecurityCacheManager().getUser(userID); // is pwd expiry notification req'd
		getMockSecurityCacheManagerControl().setReturnValue(user);

		getMockSecurityCacheManager().getUser(userID); // get days until pwd expires
		getMockSecurityCacheManagerControl().setReturnValue(user);

		getMockAuditLogger().logLogIn(user.getUserID());

		// replay
		replay();
		LoginResponse response = doLogin();

		// verify
		verify();
		assertLoginSuccess(response);
		assertEquals(true, response.getNotifyPasswordExpiration());
	}

	public void testAnyException() throws Exception {
		// record		
		getMockUserAuthenticationProvider().authenticate(userID, passwordAsClearText);
		getMockUserAuthenticationProviderControl().setThrowable(new Exception("Something went wrong.  Oh my!"));

		getMockSecurityCacheManager().hasUser(userID);
		getMockSecurityCacheManagerControl().setReturnValue(false);

		replay();
		LoginResponse response = doLogin();

		verify();
		assertLoginFailure(response, "Failed to complete the request - Something went wrong.  Oh my!.");
	}


	public void testServletActionExceptionException() throws Exception {
		User user = ObjectMother.createUser();

		// record

		getMockSecurityCacheManager().hasUser(userID); // login attempt
		getMockSecurityCacheManagerControl().setReturnValue(true);

		getMockUserAuthenticationProvider().authenticate(userID, passwordAsClearText);
		getMockUserAuthenticationProviderControl().setReturnValue(true);

		getMockSecurityCacheManager().getUser(userID); // login attempt
		getMockSecurityCacheManagerControl().setReturnValue(user);

		getMockSecurityCacheManager().getUser(userID); // is pwd change req'd
		getMockSecurityCacheManagerControl().setReturnValue(user);

		getMockSecurityCacheManager().getUser(userID); // get user in handler
		getMockSecurityCacheManagerControl().setReturnValue(user);

		getMockHttpServletRequest().getSession();
		getMockHttpServletRequestControl().setReturnValue(getMockHttpSession());

		getMockSessionManager().registerSession(null);
		getMockSessionManagerControl().setMatcher(MockControl.ALWAYS_MATCHER);
		getMockSessionManagerControl().setThrowable(
				new ServletActionException("msg.error", "this string is never seen by client.  I wonder why"));

		// replay
		replay();
		LoginResponse response = doLogin();

		// verify
		verify();
		assertLoginFailure(response, "Error: {0}"); // it'd be nice if the message formatter was used.
	}

	private LoginResponse doLogin() {
		LoginResponse response = (LoginResponse) new LoginRequestHandler().handleRequest(
				new LoginRequest(userID, passwordAsClearText),
				getMockHttpServletRequest());
		return response;
	}

	private void assertLoginFailure(LoginResponse response, String expectedMsg) {
		assertFalse(response.isAuthenticated());
		assertNull(response.getSessionID());
		assertEquals(expectedMsg, response.getLoginFailureMsg());
		assertFalse(response.isPasswordNeedsReset());
	}

	private void assertLoginSuccess(LoginResponse response) {
		assertTrue(response.isAuthenticated());
		assertNotNull(response.getSessionID());
		assertNull(response.getLoginFailureMsg());
		assertFalse(response.isPasswordNeedsReset());
	}

	private void assertNeedToResetPassword(LoginResponse response, String expectedMsg) {
		assertNull(response.getSessionID());
		assertEquals(expectedMsg, response.getLoginFailureMsg());
		assertTrue(response.isAuthenticated());
		assertTrue(response.isPasswordNeedsReset());
	}

	private void assertUserLockedOut(LoginResponse response, String expectedMsg) {
		assertNull(response.getSessionID());
		assertFalse(response.isAuthenticated());
		assertEquals(expectedMsg, response.getLoginFailureMsg());
	}
}
