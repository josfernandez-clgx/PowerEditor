package com.mindbox.pe.server.servlet.handlers;

import java.util.Calendar;

import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.DateUtil;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletTest;

public class LoginAttemptTest extends ServletTest {
	public static TestSuite suite() {
		TestSuite suite = new TestSuite(LoginAttemptTest.class.getName());
		suite.addTestSuite(LoginAttemptTest.class);
		return suite;
	}

	public LoginAttemptTest(String name) {
		super(name);
	}
	
	public void testFailedAuthentication() throws Exception {
		getMockUserAuthenticationProvider().authenticate("user", "password");
		getMockUserAuthenticationProviderControl().setReturnValue(false);

		getMockSecurityCacheManager().hasUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(false);

		getMockSecurityCacheManager().hasUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(false);

		replay();
		LoginAttempt loginAttempt = new LoginAttempt("user", "password");

		verify();

		assertTrue(loginAttempt.failed());
		assertEquals("Invalid Id or Password. Please try again.", loginAttempt.getFailureReason());
	}

	public void testFailedUserStatusLockedOut() throws Exception {
		getMockSecurityCacheManager().hasUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(true);

		User user = ObjectMother.createUser();
		user.setStatus(Constants.LOCKOUT_STATUS);

		getMockSecurityCacheManager().getUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(user);

		replay();
		LoginAttempt loginAttempt = new LoginAttempt("user", "password");

		verify();

		assertTrue(loginAttempt.failed());
		assertEquals("This user has been locked out of the system. Please contact system administrator.", loginAttempt.getFailureReason());
	}

	public void testFailedLoginCountExceedsMax() throws Exception {
		getMockSecurityCacheManager().hasUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(true);

		User user = ObjectMother.createUser();
		user.setFailedLoginCounter(ConfigurationManager.getInstance().getUserPasswordPoliciesConfig().getLockoutConfig().getMaxAttempts());

		getMockSecurityCacheManager().getUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(user);

		replay();
		LoginAttempt loginAttempt = new LoginAttempt("user", "password");

		verify();

		assertTrue(loginAttempt.failed());
		assertEquals("This user has been locked out of the system. Please contact system administrator.", loginAttempt.getFailureReason());
	}

	public void testFailedUserPasswordExpired() throws Exception {
		getMockSecurityCacheManager().hasUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(true);

		User user = ObjectMother.createUser();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);
		user.setCurrentPassword("", cal.getTime());

		getMockSecurityCacheManager().getUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(user);

		replay();
		LoginAttempt loginAttempt = new LoginAttempt("user", "password");

		verify();

		assertTrue(loginAttempt.failed());
		assertEquals("This user has been locked out of the system. Please contact system administrator.", loginAttempt.getFailureReason());
	}

	public void testGetUserAfterFailure() throws Exception {
		getMockUserAuthenticationProvider().authenticate("user", "password");
		getMockUserAuthenticationProviderControl().setReturnValue(false);

		getMockSecurityCacheManager().hasUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(false);

		getMockSecurityCacheManager().hasUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(false);

		replay();
		LoginAttempt loginAttempt = new LoginAttempt("user", "password");

		verify();

		assertTrue(loginAttempt.failed());

		try {
			loginAttempt.getUser();
			fail("Expected " + IllegalStateException.class.getName());
		}
		catch (IllegalStateException e) {
			// pass
		}
	}

	public void testIsPasswordChangeRequiredAfterFailure() throws Exception {
		getMockUserAuthenticationProvider().authenticate("user", "password");
		getMockUserAuthenticationProviderControl().setReturnValue(false);

		getMockSecurityCacheManager().hasUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(false);

		getMockSecurityCacheManager().hasUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(false);

		replay();
		LoginAttempt loginAttempt = new LoginAttempt("user", "password");

		verify();

		assertTrue(loginAttempt.failed());

		try {
			loginAttempt.isPasswordChangeRequired();
			fail("Expected " + IllegalStateException.class.getName());
		}
		catch (IllegalStateException e) {
			// pass
		}
	}

	public void testIsPasswordExpiryNotificationRequiredAfterFailure() throws Exception {
		getMockUserAuthenticationProvider().authenticate("user", "password");
		getMockUserAuthenticationProviderControl().setReturnValue(false);

		getMockSecurityCacheManager().hasUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(false);

		getMockSecurityCacheManager().hasUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(false);

		getMockUserAuthenticationProvider().arePasswordsStoredExternally();
		getMockUserAuthenticationProviderControl().setReturnValue(false);

		replay();
		LoginAttempt loginAttempt = new LoginAttempt("user", "password");

		assertTrue(loginAttempt.failed());

		try {
			loginAttempt.isPasswordExpiryNotificationRequired();
			fail("Expected " + IllegalStateException.class.getName());
		}
		catch (IllegalStateException e) {
			// pass
		}
	}

	public void testGetDaysUntilPasswordExpiresAfterFailure() throws Exception {
		getMockUserAuthenticationProvider().authenticate("user", "password");
		getMockUserAuthenticationProviderControl().setReturnValue(false);

		getMockSecurityCacheManager().hasUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(false);

		getMockSecurityCacheManager().hasUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(false);

		replay();
		LoginAttempt loginAttempt = new LoginAttempt("user", "password");

		verify();

		assertTrue(loginAttempt.failed());

		try {
			loginAttempt.getDaysUntilPasswordExpires();
			fail("Expected " + IllegalStateException.class.getName());
		}
		catch (IllegalStateException e) {
			// pass
		}
	}

	public void testPasswordExpiryNotificationNotRequiredWhenPasswordAreNotPersistable() throws Exception {
		getMockUserAuthenticationProvider().authenticate("user", "password");
		getMockUserAuthenticationProviderControl().setReturnValue(true);

		getMockSecurityCacheManager().hasUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(true);

		User user = ObjectMother.createUser();

		getMockSecurityCacheManager().getUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(user);

		getMockUserAuthenticationProvider().arePasswordsStoredExternally();
		getMockUserAuthenticationProviderControl().setReturnValue(true);

		replay();
		LoginAttempt loginAttempt = new LoginAttempt("user", "password");

		assertFalse(loginAttempt.failed());

		assertFalse(loginAttempt.isPasswordExpiryNotificationRequired());
	}

	public void testPasswordExpiryNotificationHappyPath() throws Exception {
		getMockUserAuthenticationProvider().authenticate("user", "password");
		getMockUserAuthenticationProviderControl().setReturnValue(true);

		getMockSecurityCacheManager().hasUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(true);

		User user = ObjectMother.createUser();
		int expirationDays = ConfigurationManager.getInstance().getUserPasswordPoliciesConfig().getExpirationConfig().getExpirationDays();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -expirationDays);
		user.setCurrentPassword("", cal.getTime());

		getMockSecurityCacheManager().getUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(user);

		getMockUserAuthenticationProvider().arePasswordsStoredExternally();
		getMockUserAuthenticationProviderControl().setReturnValue(false);

		getMockSecurityCacheManager().getUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(user);

		replay();
		LoginAttempt loginAttempt = new LoginAttempt("user", "password");

		assertFalse(loginAttempt.failed());

		assertTrue(loginAttempt.isPasswordExpiryNotificationRequired());
	}

	public void testGetDaysUntilPasswordExpires_NullPasswordChangeDate() throws Exception {
		getMockUserAuthenticationProvider().authenticate("user", "password");
		getMockUserAuthenticationProviderControl().setReturnValue(true);

		getMockSecurityCacheManager().hasUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(true);

		User user = ObjectMother.createUser();
		user.setCurrentPassword("", null);

		getMockSecurityCacheManager().getUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(user);

		getMockUserAuthenticationProvider().arePasswordsStoredExternally();
		getMockUserAuthenticationProviderControl().setReturnValue(false);

		getMockSecurityCacheManager().getUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(user);

		replay();
		LoginAttempt loginAttempt = new LoginAttempt("user", "password");

		assertFalse(loginAttempt.failed());

		assertEquals(Integer.MAX_VALUE, loginAttempt.getDaysUntilPasswordExpires());
	}

	public void testGetDaysUntilPasswordExpires_HappyPath() throws Exception {
		getMockUserAuthenticationProvider().authenticate("user", "password");
		getMockUserAuthenticationProviderControl().setReturnValue(true);

		getMockSecurityCacheManager().hasUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(true);

		User user = ObjectMother.createUser();
		Calendar cal = Calendar.getInstance();
		long timeToSubstract = 10 * DateUtil.MILLIS_PER_DAY;
		cal.setTimeInMillis(cal.getTimeInMillis() - timeToSubstract);
		user.setCurrentPassword("", cal.getTime());

		getMockSecurityCacheManager().getUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(user);

		getMockUserAuthenticationProvider().arePasswordsStoredExternally();
		getMockUserAuthenticationProviderControl().setReturnValue(false);

		getMockSecurityCacheManager().getUser("user");
		getMockSecurityCacheManagerControl().setReturnValue(user);

		replay();
		LoginAttempt loginAttempt = new LoginAttempt("user", "password");

		assertFalse(loginAttempt.failed());

		int expirationDays = ConfigurationManager.getInstance().getUserPasswordPoliciesConfig().getExpirationConfig().getExpirationDays();
		int expectedDaysUntilExpiration = expirationDays - 10;
		assertEquals(expectedDaysUntilExpiration, loginAttempt.getDaysUntilPasswordExpires());
	}
}
