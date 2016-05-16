/*
 * Created on Mar 29, 2006
 *
 */
package com.mindbox.pe.server.config;

import java.util.ArrayList;
import java.util.List;

import com.mindbox.pe.server.spi.PasswordValidatorProvider;
import com.mindbox.pe.server.spi.pwd.DefaultPasswordValidator;
import com.mindbox.pe.xsd.config.ParamType;
import com.mindbox.pe.xsd.config.UserPasswordPolicies;


/**
 * User password policies configuration.
 * This is used by the digester to contain configuration info 
 * &lt;PowerEditorConfiguration&gt;&lt;Server&gt;&lt;UserPasswordPolicies&gt; element 
 * in the PowerEditorConfiguration.xml file.
 * <p>
 * <b>Note:</b><br>
 * Changes to this class may require changes to <code>com.mindbox.pe.server.config.ConfigXMLDigester#digestUserPasswordPoliciesConfig(File, UserPasswordPoliciesConfigHelper)</code>.
 * @author davies
 * @since PowerEditor 5.1.0
 */
public class UserPasswordPoliciesConfigHelper {

	public static final int DEFAULT_EXPIRATION_DAYS = Integer.MAX_VALUE;
	public static final int DEFAULT_NOTIFICATION_DAYS = 0;
	public static final int DEFAULT_LOOKBACK = 0;
	public static final int DEFAULT_MAX_ATTEMPTS = Integer.MAX_VALUE;

	private int maxAttempts = DEFAULT_MAX_ATTEMPTS;
	private int lookback = DEFAULT_LOOKBACK;
	private int expirationDays = DEFAULT_EXPIRATION_DAYS;
	private int notificationDays = DEFAULT_NOTIFICATION_DAYS;
	private int lockoutCounterResetIntervalMins = 0;
	private int cannotChangeIntervalMins = 0;
	private final PasswordValidatorProvider validatorClass;
	private final UserPasswordPolicies userPasswordPolicies;

	public UserPasswordPoliciesConfigHelper(final UserPasswordPolicies userPasswordPolicies) {
		try {
			if (userPasswordPolicies.getValidator() == null || userPasswordPolicies.getValidator().getProviderClassName() == null) {
				this.validatorClass = new DefaultPasswordValidator();
			}
			else {
				this.validatorClass = PasswordValidatorProvider.class.cast(Class.forName(userPasswordPolicies.getValidator().getProviderClassName()).newInstance());
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		if (userPasswordPolicies.getChange() != null) {
			if (userPasswordPolicies.getChange().getCannotChangeIntervalMins() != null && userPasswordPolicies.getChange().getCannotChangeIntervalMins().intValue() > 0) {
				cannotChangeIntervalMins = userPasswordPolicies.getChange().getCannotChangeIntervalMins().intValue();
			}
		}
		if (userPasswordPolicies.getExpiration() != null) {
			if (userPasswordPolicies.getExpiration().getExpirationDays() != null && userPasswordPolicies.getExpiration().getExpirationDays().intValue() > 0) {
				expirationDays = userPasswordPolicies.getExpiration().getExpirationDays().intValue();
			}
			if (userPasswordPolicies.getExpiration().getNotificationDays() != null && userPasswordPolicies.getExpiration().getNotificationDays().intValue() > 0) {
				notificationDays = userPasswordPolicies.getExpiration().getNotificationDays().intValue();
			}
		}
		if (userPasswordPolicies.getHistory() != null) {
			if (userPasswordPolicies.getHistory().getLookback() != null && userPasswordPolicies.getHistory().getLookback().intValue() > 0) {
				lookback = userPasswordPolicies.getHistory().getLookback().intValue();
			}
		}
		if (userPasswordPolicies.getLockout() != null) {
			if (userPasswordPolicies.getLockout().getMaxAttempts() != null && userPasswordPolicies.getLockout().getMaxAttempts().intValue() > 0) {
				maxAttempts = userPasswordPolicies.getLockout().getMaxAttempts().intValue();
			}
			if (userPasswordPolicies.getLockout().getResetIntervalMins() != null && userPasswordPolicies.getLockout().getResetIntervalMins().intValue() > 0) {
				lockoutCounterResetIntervalMins = userPasswordPolicies.getLockout().getResetIntervalMins().intValue();
			}
		}

		this.userPasswordPolicies = userPasswordPolicies;
	}

	public int getCannotChangeIntervalMins() {
		return cannotChangeIntervalMins;
	}

	public int getExpirationDays() {
		return expirationDays;
	}

	public int getLockoutCounterResetIntervalMins() {
		return lockoutCounterResetIntervalMins;
	}

	public int getLookback() {
		return lookback;
	}

	public int getMaxAttempts() {
		return maxAttempts;
	}

	public int getNotificationDays() {
		return notificationDays;
	}

	public PasswordValidatorProvider getValidatorInstance() {
		return validatorClass;
	}

	public List<ParamType> getValidatorParams() {
		return userPasswordPolicies.getValidator() == null ? new ArrayList<ParamType>() : userPasswordPolicies.getValidator().getParam();
	}
}