package com.mindbox.pe.server.spi;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.db.DefaultAuditServiceProvider;
import com.mindbox.pe.server.spi.db.PEDataProvider;
import com.mindbox.pe.server.spi.db.PEDataUpdater;
import com.mindbox.pe.xsd.config.ParamType;

public final class ServiceProviderFactory {

	private static PasswordValidatorProvider passwordValidatorProvider = null;
	private static PEDBCProvider peDBCProvider = null;
	private static UserAuthenticationProviderPlugin userAuthenticationProvider = null;
	private static AuditServiceProvider auditServiceProvider = null;

	/*Singleton*/
	private ServiceProviderFactory() {
	}

	private static PEDBCProvider getPEDBCProvider() {
		if (peDBCProvider == null) {
			try {
				peDBCProvider = (PEDBCProvider) ConfigurationManager.getInstance().getServerConfigHelper().getDatabaseConfig().getProviderClass().newInstance();
			}
			catch (Exception ex) {
				Logger.getLogger(ServiceProviderFactory.class).fatal("Failed to get PEData Provider", ex);
				throw new IllegalStateException("Failed to get PEDataProvider - " + ex.getMessage());
			}
		}
		return peDBCProvider;
	}

	public static AuditServiceProvider getAuditServiceProvider() {
		if (auditServiceProvider == null) {
			try {
				auditServiceProvider = new DefaultAuditServiceProvider();
			}
			catch (Exception ex) {
				Logger.getLogger(ServiceProviderFactory.class).fatal("Failed to get AuditServiceProvider", ex);
				throw new IllegalStateException("Failed to get AuditServiceProvider - " + ex.getMessage());
			}
		}
		return auditServiceProvider;
	}

	public static PEDataProvider getPEDataProvider() {
		return getPEDBCProvider().getDataProvider();
	}

	public static PEDataUpdater getPEDataUpdater() {
		return getPEDBCProvider().getDataUpdater();
	}

	public static GuidelineRuleProvider getGuidelineRuleProvider() {
		return getPEDBCProvider().getGuidelineRuleProvider();
	}

	public static PasswordValidatorProvider getPasswordValidatorProvider() {
		if (passwordValidatorProvider == null) {
			try {
				passwordValidatorProvider = ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper().getValidatorInstance();
				for (final ParamType paramType : ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper().getValidatorParams()) {
					BeanUtils.setProperty(passwordValidatorProvider, paramType.getName(), paramType.getValue());
				}
			}
			catch (Exception ex) {
				Logger.getLogger(ServiceProviderFactory.class).fatal("Failed to get PasswordValidatorProvider Provider", ex);
				throw new IllegalStateException("Failed to get PasswordValidatorProvider - " + ex.getMessage());
			}
		}
		return passwordValidatorProvider;
	}

	public static UserManagementProvider getUserManagementProvider() {
		return getPEDBCProvider().getUserManagementProvider();
	}

	public static UserAuthenticationProviderPlugin getUserAuthenticationProvider() {
		if (userAuthenticationProvider == null) {
			try {
				// instantiate the configured class
				UserAuthenticationProvider configuredProvider = ConfigurationManager.getInstance().getUserAuthenticationProvider();

				// if the configured class is an externally provided plugin, decorate it.
				userAuthenticationProvider = (UserAuthenticationProviderPlugin.class.isAssignableFrom(configuredProvider.getClass())
						? (UserAuthenticationProviderPlugin) configuredProvider
						: new UserAuthenticationProviderPluginWrapper(configuredProvider));

			}
			catch (Exception ex) {
				throw new RuntimeException("Failed to instantiate UserAuthenticationProvider class: " + ConfigurationManager.getInstance().getUserAuthenticationProvider().toString(), ex);
			}
		}
		return userAuthenticationProvider;
	}
}
