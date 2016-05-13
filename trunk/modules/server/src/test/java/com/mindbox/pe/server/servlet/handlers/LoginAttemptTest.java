package com.mindbox.pe.server.servlet.handlers;

import static com.mindbox.pe.server.ServerTestObjectMother.createUser;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.DateUtil;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletTest;

public class LoginAttemptTest extends ServletTest {

	private String userName = null;
	private String password = null;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		userName = "user";
		password = "password";
	}

	@Test
	public void testFailedAuthentication() throws Exception {

		expect(getMockUserAuthenticationProvider().authenticate(userName, password)).andReturn(false);

		expect(getMockSecurityCacheManager().hasUser(userName)).andReturn(false);

		expect(getMockSecurityCacheManager().hasUser(userName)).andReturn(false);

		replayAllMocks();
		LoginAttempt loginAttempt = new LoginAttempt(userName, password);

		verifyAllMocks();

		assertTrue(loginAttempt.failed());
		assertEquals("Invalid Id or Password. Please try again.", loginAttempt.getFailureReason());
	}

	@Test
	public void testFailedLoginCountExceedsMax() throws Exception {
		expect(getMockSecurityCacheManager().hasUser(userName)).andReturn(true).anyTimes();

		User user = createUser();
		user.setFailedLoginCounter(ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper().getMaxAttempts());

		expect(getMockSecurityCacheManager().getUser(userName)).andReturn(user).anyTimes();

		replayAllMocks();
		LoginAttempt loginAttempt = new LoginAttempt(userName, password);

		verifyAllMocks();

		assertTrue(loginAttempt.failed());
		assertEquals("This user has been locked out of the system. Please contact system administrator.", loginAttempt.getFailureReason());
	}

	@Test
	public void testFailedUserPasswordExpired() throws Exception {
		expect(getMockSecurityCacheManager().hasUser(userName)).andReturn(true);

		User user = createUser();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);
		user.setCurrentPassword("", cal.getTime());

		expect(getMockSecurityCacheManager().getUser(userName)).andReturn(user).anyTimes();

		replayAllMocks();
		LoginAttempt loginAttempt = new LoginAttempt(userName, password);

		verifyAllMocks();

		assertTrue(loginAttempt.failed());
		assertEquals("This user has been locked out of the system. Please contact system administrator.", loginAttempt.getFailureReason());
	}

	@Test
	public void testFailedUserStatusLockedOut() throws Exception {
		expect(getMockSecurityCacheManager().hasUser(userName)).andReturn(true).anyTimes();

		User user = createUser();
		user.setStatus(Constants.LOCKOUT_STATUS);

		expect(getMockSecurityCacheManager().getUser(userName)).andReturn(user).anyTimes();

		replayAllMocks();
		LoginAttempt loginAttempt = new LoginAttempt(userName, password);

		verifyAllMocks();

		assertTrue(loginAttempt.failed());
		assertEquals("This user has been locked out of the system. Please contact system administrator.", loginAttempt.getFailureReason());
	}

	@Test
	public void testGetDaysUntilPasswordExpires_HappyPath() throws Exception {
		expect(getMockUserAuthenticationProvider().authenticate(userName, password)).andReturn(true);

		expect(getMockSecurityCacheManager().hasUser(userName)).andReturn(true);

		User user = createUser();
		Calendar cal = Calendar.getInstance();
		long timeToSubstract = 10 * DateUtil.MILLIS_PER_DAY;
		cal.setTimeInMillis(cal.getTimeInMillis() - timeToSubstract);
		user.setCurrentPassword("", cal.getTime());

		expect(getMockSecurityCacheManager().getUser(userName)).andReturn(user);

		expect(getMockSecurityCacheManager().getUser(userName)).andReturn(user);

		replayAllMocks();
		LoginAttempt loginAttempt = new LoginAttempt(userName, password);

		assertFalse(loginAttempt.failed());

		int expirationDays = ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper().getExpirationDays();
		int expectedDaysUntilExpiration = expirationDays - 10;
		assertEquals(expectedDaysUntilExpiration, loginAttempt.getDaysUntilPasswordExpires());
	}

	@Test
	public void testGetDaysUntilPasswordExpires_NullPasswordChangeDate() throws Exception {
		expect(getMockUserAuthenticationProvider().authenticate(userName, password)).andReturn(true);

		expect(getMockSecurityCacheManager().hasUser(userName)).andReturn(true);

		User user = createUser();
		user.setCurrentPassword("", null);

		expect(getMockSecurityCacheManager().getUser(userName)).andReturn(user);

		expect(getMockSecurityCacheManager().getUser(userName)).andReturn(user);

		replayAllMocks();
		LoginAttempt loginAttempt = new LoginAttempt(userName, password);

		assertFalse(loginAttempt.failed());

		assertEquals(Integer.MAX_VALUE, loginAttempt.getDaysUntilPasswordExpires());
	}

	@Test
	public void testGetDaysUntilPasswordExpiresAfterFailure() throws Exception {
		expect(getMockUserAuthenticationProvider().authenticate(userName, password)).andReturn(false);

		expect(getMockSecurityCacheManager().hasUser(userName)).andReturn(false);

		expect(getMockSecurityCacheManager().hasUser(userName)).andReturn(false);

		replayAllMocks();
		LoginAttempt loginAttempt = new LoginAttempt(userName, password);

		verifyAllMocks();

		assertTrue(loginAttempt.failed());

		try {
			loginAttempt.getDaysUntilPasswordExpires();
			fail("Expected " + IllegalStateException.class.getName());
		}
		catch (IllegalStateException e) {
			// pass
		}
	}

	@Test
	public void testGetUserAfterFailure() throws Exception {
		expect(getMockUserAuthenticationProvider().authenticate(userName, password)).andReturn(false);

		expect(getMockSecurityCacheManager().hasUser(userName)).andReturn(false);

		expect(getMockSecurityCacheManager().hasUser(userName)).andReturn(false);

		replayAllMocks();
		LoginAttempt loginAttempt = new LoginAttempt(userName, password);

		verifyAllMocks();

		assertTrue(loginAttempt.failed());

		try {
			loginAttempt.getUser();
			fail("Expected " + IllegalStateException.class.getName());
		}
		catch (IllegalStateException e) {
			// pass
		}
	}

	@Test
	public void testIsPasswordChangeRequiredAfterFailure() throws Exception {
		expect(getMockUserAuthenticationProvider().authenticate(userName, password)).andReturn(false);

		expect(getMockSecurityCacheManager().hasUser(userName)).andReturn(false);

		expect(getMockSecurityCacheManager().hasUser(userName)).andReturn(false);

		replayAllMocks();
		LoginAttempt loginAttempt = new LoginAttempt(userName, password);

		verifyAllMocks();

		assertTrue(loginAttempt.failed());

		try {
			loginAttempt.isPasswordChangeRequired();
			fail("Expected " + IllegalStateException.class.getName());
		}
		catch (IllegalStateException e) {
			// pass
		}
	}

	@Test
	public void testIsPasswordExpiryNotificationRequiredAfterFailure() throws Exception {
		useMockUserManagementProvider(3);
		expect(getMockUserAuthenticationProvider().authenticate(userName, password)).andReturn(false);

		expect(getMockSecurityCacheManager().hasUser(userName)).andReturn(false);

		expect(getMockSecurityCacheManager().hasUser(userName)).andReturn(false);

		expect(mockUserManagementProvider.cacheUserObjects()).andReturn(true).anyTimes();
		expect(mockUserManagementProvider.arePasswordsPersistable()).andReturn(false);

		replayAllMocks();
		LoginAttempt loginAttempt = new LoginAttempt(userName, password);

		assertTrue(loginAttempt.failed());

		try {
			loginAttempt.isPasswordExpiryNotificationRequired();
			fail("Expected " + IllegalStateException.class.getName());
		}
		catch (IllegalStateException e) {
			// pass
		}
	}

	@Test
	public void testPasswordExpiryNotificationHappyPath() throws Exception {
		useMockUserManagementProvider(2);
		expect(getMockUserAuthenticationProvider().authenticate(userName, password)).andReturn(true);

		expect(getMockSecurityCacheManager().hasUser(userName)).andReturn(true);

		User user = createUser();
		int expirationDays = ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper().getExpirationDays();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -expirationDays);
		user.setCurrentPassword("", cal.getTime());

		expect(getMockSecurityCacheManager().getUser(userName)).andReturn(user);

		expect(mockUserManagementProvider.cacheUserObjects()).andReturn(true);
		expect(mockUserManagementProvider.arePasswordsPersistable()).andReturn(true);

		expect(getMockSecurityCacheManager().getUser(userName)).andReturn(user);

		replayAllMocks();
		LoginAttempt loginAttempt = new LoginAttempt(userName, password);

		assertFalse(loginAttempt.failed());

		assertTrue(loginAttempt.isPasswordExpiryNotificationRequired());
	}

	@Test
	public void testPasswordExpiryNotificationNotRequiredWhenPasswordAreNotPersistable() throws Exception {
		useMockUserManagementProvider(2);

		expect(getMockUserAuthenticationProvider().authenticate(userName, password)).andReturn(true);

		expect(getMockSecurityCacheManager().hasUser(userName)).andReturn(true);

		User user = createUser();

		expect(getMockSecurityCacheManager().getUser(userName)).andReturn(user).anyTimes();
		;

		expect(mockUserManagementProvider.cacheUserObjects()).andReturn(true);
		expect(mockUserManagementProvider.arePasswordsPersistable()).andReturn(true);

		replayAllMocks();
		LoginAttempt loginAttempt = new LoginAttempt(userName, password);

		assertFalse(loginAttempt.failed());

		assertFalse(loginAttempt.isPasswordExpiryNotificationRequired());
	}
}
