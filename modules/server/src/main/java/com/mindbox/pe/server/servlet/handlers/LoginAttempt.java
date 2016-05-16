package com.mindbox.pe.server.servlet.handlers;

import java.util.Date;

import com.mindbox.pe.common.DateUtil;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ResourceUtil;
import com.mindbox.pe.server.servlet.ServletActionException;
import com.mindbox.pe.server.spi.ServiceProviderFactory;

/**
 * A helper class for performing user login logic on behalf of request handlers,
 * and for holding attributes about the user's login.
 * This class is NOT threadsafe.
 */
public class LoginAttempt {

	private final String userId;
	private String failureReason;
	private boolean failed = true;
	private Integer cachedDaysUntilPwdExpires = null;

	public LoginAttempt(String userId, String password) throws ServletActionException, Exception {
		this.userId = userId;
		this.failed = false;
		if (ServiceProviderFactory.getUserManagementProvider().cacheUserObjects() && SecurityCacheManager.getInstance().hasUser(userId)
				&& isLockedOut(SecurityCacheManager.getInstance().getUser(userId))) {
			this.failureReason = ResourceUtil.getInstance().getResource("msg.user.lockedout");
			this.failed = true;
		}
		else {
			try {
				if (!ServiceProviderFactory.getUserAuthenticationProvider().authenticate(userId, password)) {
					this.failed = true;
					// check if the user is just locked out because of the last failure
					if (ServiceProviderFactory.getUserManagementProvider().cacheUserObjects() && SecurityCacheManager.getInstance().hasUser(userId)
							&& isLockedOut(SecurityCacheManager.getInstance().getUser(userId))) {
						this.failureReason = ResourceUtil.getInstance().getResource("msg.user.lockedout");
					}
					else {
						this.failureReason = ResourceUtil.getInstance().getResource("InvalidLoginMsg");
					}
				}
			}
			catch (ServletActionException e) {
				this.failed = true;
				this.failureReason = ResourceUtil.getInstance().getResource(e.getResourceKey(), e.getResourceParams());
			}
		}
	}

	public boolean failed() {
		return failed;
	}

	public int getDaysUntilPasswordExpires() {
		return getDaysUntilPasswordExpires(getUser());
	}

	private int getDaysUntilPasswordExpires(User user) {
		if (cachedDaysUntilPwdExpires == null) {
			Date passwordChangeDate = user.getCurrentPasswordChangeDate();
			if (passwordChangeDate == null) {
				// The user is already authenticated, so the only way this can be true is if PE doesn't store passwords.
				// In such a configuration passwords never expire.
				cachedDaysUntilPwdExpires = new Integer(Integer.MAX_VALUE);
			}
			else {
				int expirationDays = ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper().getExpirationDays();
				int daysSinceChange = DateUtil.daysSince(passwordChangeDate);

				cachedDaysUntilPwdExpires = new Integer(expirationDays - daysSinceChange);
			}
		}
		return cachedDaysUntilPwdExpires.intValue();
	}

	/** Only meaningful when login failed. 
	 * @return failure reason
	 */
	public String getFailureReason() {
		return failureReason;
	}

	/** 
	 * @return user
	 * @throws IllegalStateException if login failed. 
	 */
	public User getUser() {
		if (failed()) {
			throw new IllegalStateException("Attempt to access user after failed login attempt.");
		}
		return SecurityCacheManager.getInstance().getUser(userId);
	}

	private boolean isLockedOut(User user) {
		return Constants.LOCKOUT_STATUS.equals(user.getStatus()) || getDaysUntilPasswordExpires(user) < 0
				|| user.getFailedLoginCounter() >= ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper().getMaxAttempts();
	}

	/** 
	 * @return true if password change required; false, otherwise
	 * @throws IllegalStateException if login failed. 
	 */
	public boolean isPasswordChangeRequired() {
		return getUser().getPasswordChangeRequired();
	}

	/** 
	 * @return true if password Expiry Notification required; false, otherwise
	 * @throws IllegalStateException if login failed. 
	 */
	public boolean isPasswordExpiryNotificationRequired() {
		if (failed()) {
			throw new IllegalStateException("Attempt to access user after failed login attempt.");
		}
		return ServiceProviderFactory.getUserManagementProvider().arePasswordsPersistable() && withinPasswordExpirationNotificationPeriod(getDaysUntilPasswordExpires());
	}

	private boolean withinPasswordExpirationNotificationPeriod(int daysUntilPasswordExpires) {
		int notificationDays = ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper().getNotificationDays();
		return daysUntilPasswordExpires <= notificationDays;
	}
}
