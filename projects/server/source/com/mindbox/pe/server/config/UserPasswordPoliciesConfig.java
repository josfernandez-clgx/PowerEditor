/*
 * Created on Mar 29, 2006
 *
 */
package com.mindbox.pe.server.config;

import java.io.File;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import com.mindbox.pe.server.spi.pwd.DefaultPasswordValidator;


/**
 * User password policies configuration.
 * This is used by the digester to contain configuration info 
 * &lt;PowerEditorConfiguration&gt;&lt;Server&gt;&lt;UserPasswordPolicies&gt; element 
 * in the PowerEditorConfiguration.xml file.
 * <p>
 * <b>Note:</b><br>
 * Changes to this class may require changes to {@link com.mindbox.pe.server.config.ConfigXMLDigester#digestUserPasswordPoliciesConfig(File, UserPasswordPoliciesConfig)}.
 * @author davies
 * @since PowerEditor 5.1.0
 */
public class UserPasswordPoliciesConfig {
	public UserPasswordPoliciesConfig(InputStreamReader configReader) {
		try {
			ConfigXMLDigester.getInstance().digestUserPasswordPoliciesConfig(configReader, this);
		} catch (Exception e) {
			throw new RuntimeException("Failed to digest User Password Policies.", e);
		}
	}
	
	private ValidatorConfig validatorConfig = new ValidatorConfig();
	private ExpirationConfig expirationConfig = new ExpirationConfig();
    private HistoryConfig historyConfig = new HistoryConfig();    
	private LockoutConfig lockoutConfig = new LockoutConfig();
	
	public ValidatorConfig getValidatorConfig() {
		return validatorConfig;
	}
	
	public void setValidatorConfig(ValidatorConfig validatorConfig) {
		this.validatorConfig = validatorConfig;
	}

	public ExpirationConfig getExpirationConfig() {
		return expirationConfig;
	}

	public void setExpirationConfig(ExpirationConfig expirationConfig) {
		this.expirationConfig = expirationConfig;
	}
	
	public LockoutConfig getLockoutConfig() {
		return lockoutConfig;
	}

	public void setLockoutConfig(LockoutConfig lockoutConfig) {
		this.lockoutConfig = lockoutConfig;
	}

	public static class ValidatorConfig {
		public static final Class<DefaultPasswordValidator> DEFAULT_VALIDATOR_CLASS = DefaultPasswordValidator.class;
		
		private Class<?> clazz = DEFAULT_VALIDATOR_CLASS;
        private Set<ConfigParameter> configParameters;

        public void addConfigParameter(ConfigParameter param) {
            if (configParameters == null) {
                configParameters = new HashSet<ConfigParameter>();
            }
            configParameters.add(param);
        }
        
        public Set<ConfigParameter> getConfigParameters() {
            return configParameters;
         }

		public Class<?> getProviderClass() {
			return clazz;
		}
		
		public void setProviderClassName(String className) {
			try {
				this.clazz = Class.forName(className);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static class ExpirationConfig {
		public static final int DEFAULT_EXPIRATION_DAYS = Integer.MAX_VALUE;
		public static final int DEFAULT_NOTIFICATION_DAYS = 0;
		
		private int expirationDays = DEFAULT_EXPIRATION_DAYS;
		private int notificationDays = DEFAULT_NOTIFICATION_DAYS;
		
		public int getExpirationDays() {
			return expirationDays;
		}
		
		public void setExpirationDays(int expirationDays) {
			if (expirationDays > -1) {
				this.expirationDays = expirationDays;
			}
		}
		
		public int getNotificationDays() {
			return notificationDays;
		}
		
		public void setNotificationDays(int notificationDays) {
			if (notificationDays > -1) {
				this.notificationDays = notificationDays;
			}
		}
	}

    public static class HistoryConfig {
        public static final int DEFAULT_LOOKBACK = 0;
        
        private int lookback = DEFAULT_LOOKBACK;

        public int getLookback() {
            return lookback;
        }

        public void setLookback(int lookback) {
            this.lookback = lookback;
        }

    }
    
	public static class LockoutConfig {
		public static final int DEFAULT_MAX_ATTEMPTS = Integer.MAX_VALUE;
		
		private int maxAttempts = DEFAULT_MAX_ATTEMPTS;

		public int getMaxAttempts() {
			return maxAttempts;
		}

		public void setMaxAttempts(int maxAttempts) {
			if (maxAttempts > -1) {
				this.maxAttempts = maxAttempts;
			}
		}
	}
    
    public HistoryConfig getHistoryConfig() {
        return historyConfig;
    }

    public void setHistoryConfig(HistoryConfig historyConfig) {
        this.historyConfig = historyConfig;
    }
}
