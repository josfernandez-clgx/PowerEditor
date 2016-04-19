package com.mindbox.pe.server.servlet.handlers;

import static com.mindbox.pe.server.ServerTestObjectMother.createUser;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.mindbox.pe.communication.PasswordChangeRequest;
import com.mindbox.pe.communication.PasswordChangeResponse;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletTest;

/**
 * @author vineet khosla
 * @since PowerEditor 5.1
 */
public class PasswordChangeRequestHandlerTest extends ServletTest {

	private final String userID = "user";
	private final String passwordAsClearText = "password";
	private final String passwordAsOneWayHash = "5f4dcc3b5aa765d61d8327deb882cf99";

	private void assertFailedAuthentication(PasswordChangeResponse response, String expectedMsg) {
		assertFalse(response.succeeded());
		assertEquals(expectedMsg, response.getMsg());
	}

	private void assertFailedPasswordMatch(PasswordChangeResponse response, String expectedMsg) {
		assertFalse(response.succeeded());
		assertEquals(expectedMsg, response.getMsg());
	}

	private PasswordChangeResponse doPasswordChange(String user, String oldPwd, String newPwd, String confirmNewPwd) {
		PasswordChangeResponse response = (PasswordChangeResponse) new PasswordChangeRequestHandler().serviceRequest(
				new PasswordChangeRequest(user, oldPwd, newPwd, confirmNewPwd),
				getMockHttpServletRequest());
		return response;
	}

	@Test
	public void testFailedAuthentication() throws Exception {
		// record
		User user = createUser();

		expect(getMockSecurityCacheManager().hasUser(userID)).andReturn(true).anyTimes();

		expect(getMockSecurityCacheManager().getUser(userID)).andReturn(user).anyTimes();

		expect(getMockUserAuthenticationProvider().authenticate(userID, passwordAsClearText)).andReturn(false);

		getMockHttpServletRequest();

		replayAllMocks();
		PasswordChangeResponse response = doPasswordChange(userID, passwordAsClearText, passwordAsClearText, passwordAsClearText);

		verifyAllMocks();
		assertFailedAuthentication(response, "Invalid Id or Password. Please try again.");
	}

	@Test
	public void testFailedOldPasswordAndNewOldPasswordNotSame() throws Exception {
		// record
		User user = createUser();

		expect(getMockSecurityCacheManager().hasUser(userID)).andReturn(true).anyTimes();

		expect(getMockSecurityCacheManager().getUser(userID)).andReturn(user).anyTimes();

		expect(getMockUserAuthenticationProvider().authenticate(userID, passwordAsClearText)).andReturn(true);

		user.setPassword(passwordAsOneWayHash);

		getMockHttpServletRequest();

		replayAllMocks();

		PasswordChangeResponse response = doPasswordChange(userID, passwordAsClearText, passwordAsClearText, passwordAsClearText);

		verifyAllMocks();

		assertFailedPasswordMatch(response, "New password doesn't meet validation rules.");
	}

	@Test
	public void testFailedPasswordMatch() throws Exception {
		// record
		User user = createUser();

		expect(getMockSecurityCacheManager().hasUser(userID)).andReturn(true).anyTimes();

		expect(getMockSecurityCacheManager().getUser(userID)).andReturn(user).anyTimes();

		expect(getMockUserAuthenticationProvider().authenticate(userID, passwordAsClearText)).andReturn(true);

		getMockHttpServletRequest();

		replayAllMocks();
		PasswordChangeResponse response = doPasswordChange(userID, passwordAsClearText, "abcd", "aABs");

		verifyAllMocks();
		assertFailedPasswordMatch(response, "New passwords dont match. Please retype.");
	}

	@Test
	public void testFailPasswordValidatorRules() throws Exception {
		// record
		User user = createUser();

		expect(getMockSecurityCacheManager().hasUser(userID)).andReturn(true).anyTimes();

		expect(getMockSecurityCacheManager().getUser(userID)).andReturn(user).anyTimes();

		expect(getMockUserAuthenticationProvider().authenticate(userID, passwordAsClearText)).andReturn(true);

		getMockHttpServletRequest();

		replayAllMocks();
		PasswordChangeResponse response = doPasswordChange(userID, passwordAsClearText, "new", "new");

		verifyAllMocks();
		assertFailedPasswordMatch(response, "New password doesn't meet validation rules.");
	}

}
