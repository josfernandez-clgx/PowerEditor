package com.mindbox.pe.server.servlet.handlers;

import static com.mindbox.pe.server.ServerTestObjectMother.createUser;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Test;

import com.mindbox.pe.common.DateUtil;
import com.mindbox.pe.communication.LoginRequest;
import com.mindbox.pe.communication.LoginResponse;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.model.PowerEditorSession;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletActionException;
import com.mindbox.pe.server.servlet.ServletTest;

public class LoginRequestHandlerTest extends ServletTest {

	private final String userID = "user";
	private final String passwordAsClearText = "password";

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

	private LoginResponse doLogin(final String userId) {
		LoginResponse response = (LoginResponse) new LoginRequestHandler().handleRequest(new LoginRequest(userId, passwordAsClearText), getMockHttpServletRequest());
		return response;
	}

	private LoginResponse doLogin() {
		return doLogin(userID);
	}

	@Test
	public void testAnyException() throws Exception {
		// record		
		expect(getMockUserAuthenticationProvider().authenticate(userID, passwordAsClearText)).andThrow(new Exception("Something went wrong.  Oh my!"));

		expect(getMockSecurityCacheManager().hasUser(userID)).andReturn(false);

		getMockHttpServletRequest();

		replayAllMocks();
		LoginResponse response = doLogin();

		verifyAllMocks();
		assertLoginFailure(response, "Failed to complete the request - Something went wrong.  Oh my!.");
	}

	@Test
	public void testFailedAuthentication() throws Exception {
		User user = createUser();
		// record			
		expect(getMockSecurityCacheManager().hasUser(userID)).andReturn(true).anyTimes();

		expect(getMockSecurityCacheManager().getUser(userID)).andReturn(user).anyTimes();

		expect(getMockUserAuthenticationProvider().authenticate(userID, passwordAsClearText)).andReturn(false);

		getMockHttpServletRequest();

		replayAllMocks();
		LoginResponse response = doLogin();

		verifyAllMocks();
		assertLoginFailure(response, "Invalid Id or Password. Please try again.");
	}

	@Test
	public void testNeedToResetPassword() throws Exception {
		User user = createUser();
		user.setPasswordChangeRequired(true);

		expect(getMockSecurityCacheManager().hasUser(userID)).andReturn(true);

		// record
		expect(getMockUserAuthenticationProvider().authenticate(userID, passwordAsClearText)).andReturn(true);

		expect(getMockSecurityCacheManager().getUser(userID)).andReturn(user).anyTimes();

		getMockHttpServletRequest();

		replayAllMocks();
		LoginResponse response = doLogin();

		verifyAllMocks();
		assertNeedToResetPassword(response, "You need to reset your password since this is either your first time logging into PowerEditor or your adminsistrator has reset your password.");
	}

	@Test
	public void testServletActionExceptionException() throws Exception {
		User user = createUser();

		// record
		expect(getMockSecurityCacheManager().hasUser(userID)).andReturn(true);

		expect(getMockUserAuthenticationProvider().authenticate(userID, passwordAsClearText)).andReturn(true);

		expect(getMockSecurityCacheManager().getUser(userID)).andReturn(user).anyTimes();

		expect(getMockHttpServletRequest().getSession()).andReturn(getMockHttpSession());

		expect(getMockHttpSession().getId()).andReturn("session-id").anyTimes();

		getMockSessionManager().registerSession(PowerEditorSession.class.cast(anyObject()));
		expectLastCall().andThrow(new ServletActionException("msg.error", "this string is never seen by client.  I wonder why"));

		// replay
		replayAllMocks();
		LoginResponse response = doLogin();

		// verify
		verifyAllMocks();
		assertLoginFailure(response, "Error: {0}"); // it'd be nice if the message formatter was used.
	}

	@Test
	public void testSuccess() throws Exception {
		User user = createUser();

		assertFalse(user.getPasswordChangeRequired()); // sanity check

		useMockUserManagementProvider(2);

		expect(mockUserManagementProvider.cacheUserObjects()).andReturn(true).anyTimes();

		// record
		expect(getMockSecurityCacheManager().hasUser(userID)).andReturn(true).anyTimes();

		expect(getMockUserAuthenticationProvider().authenticate(userID, passwordAsClearText)).andReturn(true);

		expect(getMockSecurityCacheManager().getUser(userID)).andReturn(user).anyTimes();

		expect(getMockHttpServletRequest().getSession()).andReturn(getMockHttpSession());

		getMockSessionManager().registerSession(PowerEditorSession.class.cast(anyObject()));
		expectLastCall().anyTimes();

		expect(getMockHttpSession().getId()).andReturn("sessionId");

		expect(mockUserManagementProvider.arePasswordsPersistable()).andReturn(true); // pwds not persisted, thus no warning

		getMockAuditLogger().logLogIn(user.getUserID());

		// replay
		replayAllMocks();

		LoginResponse response = doLogin();

		// verify
		verifyAllMocks();
		assertLoginSuccess(response);
	}

	@Test
	public void testUserLockedOutBecauseOfPasswordExpiration() throws Exception {
		User user = createUser();
		int expirationDays = ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper().getExpirationDays();
		Calendar cal = Calendar.getInstance();
		long timeToSubstract = (expirationDays + 1) * DateUtil.MILLIS_PER_DAY;
		cal.setTimeInMillis(cal.getTimeInMillis() - timeToSubstract);
		user.setCurrentPassword("", cal.getTime());

		useMockUserManagementProvider();

		expect(mockUserManagementProvider.cacheUserObjects()).andReturn(true).anyTimes();

		// record
		expect(getMockSecurityCacheManager().hasUser(user.getUserID())).andReturn(true).anyTimes();

		expect(getMockSecurityCacheManager().getUser(user.getUserID())).andReturn(user).anyTimes();

		getMockHttpServletRequest();

		replayAllMocks();
		LoginResponse response = doLogin(user.getUserID());

		assertUserLockedOut(response, "This user has been locked out of the system. Please contact system administrator.");
		verifyAllMocks();
	}

	@Test
	public void testUserLockedOutBecauseOfStatus() throws Exception {
		User user = createUser();
		user.setStatus(Constants.LOCKOUT_STATUS);

		// record        
		expect(getMockSecurityCacheManager().hasUser(userID)).andReturn(true).anyTimes();

		expect(getMockSecurityCacheManager().getUser(userID)).andReturn(user).anyTimes();

		getMockHttpServletRequest();

		replayAllMocks();
		LoginResponse response = doLogin();

		verifyAllMocks();
		assertUserLockedOut(response, "This user has been locked out of the system. Please contact system administrator.");
	}

	@Test
	public void testWarnExpiration() throws Exception {
		User user = createUser();
		int expirationDays = ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper().getExpirationDays();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 0 - expirationDays);
		user.setCurrentPassword("", cal.getTime());
		// record
		expect(getMockSecurityCacheManager().hasUser(userID)).andReturn(true);

		expect(getMockUserAuthenticationProvider().authenticate(userID, passwordAsClearText)).andReturn(true);

		expect(getMockHttpServletRequest().getSession()).andReturn(getMockHttpSession());

		getMockSessionManager().registerSession(null);
		expectLastCall().anyTimes();

		useMockUserManagementProvider(2);

		expect(mockUserManagementProvider.cacheUserObjects()).andReturn(true);
		expect(mockUserManagementProvider.arePasswordsPersistable()).andReturn(true); // pwds not persisted, thus no warning

		expect(getMockSecurityCacheManager().getUser(userID)).andReturn(user).anyTimes();

		expect(getMockHttpSession().getId()).andReturn("sessionId");

		getMockAuditLogger().logLogIn(user.getUserID());

		getMockSessionManager().registerSession(PowerEditorSession.class.cast(anyObject()));

		// replay
		replayAllMocks();
		LoginResponse response = doLogin();

		// verify
		verifyAllMocks();
		assertLoginSuccess(response);
		assertEquals(true, response.getNotifyPasswordExpiration());
	}
}
