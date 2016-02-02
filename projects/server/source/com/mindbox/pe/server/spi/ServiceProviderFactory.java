package com.mindbox.pe.server.spi;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import com.mindbox.pe.server.config.ConfigParameter;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.db.DefaultAuditServiceProvider;
import com.mindbox.pe.server.spi.db.PEDataProvider;
import com.mindbox.pe.server.spi.db.PEDataUpdater;

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
				peDBCProvider = (PEDBCProvider) ConfigurationManager.getInstance().getServerConfiguration().getDatabaseConfig().getProviderClass().newInstance();
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
				passwordValidatorProvider = (PasswordValidatorProvider) ConfigurationManager.getInstance().getUserPasswordPoliciesConfig().getValidatorConfig().getProviderClass().newInstance();
				Set<ConfigParameter> parameters = ConfigurationManager.getInstance().getUserPasswordPoliciesConfig().getValidatorConfig().getConfigParameters();
				if (parameters != null) {
					for (Iterator<ConfigParameter> i = parameters.iterator(); i.hasNext();) {
						ConfigParameter param = i.next();
						BeanUtils.setProperty(passwordValidatorProvider, param.getName(), param.getValue());
					}
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
				UserAuthenticationProvider configuredProvider = (UserAuthenticationProvider) ConfigurationManager.getInstance().getSessionConfiguration().getUserAuthenticationProviderClass().newInstance();

				// if the configured class is an externally provided plugin, decorate it.
				userAuthenticationProvider = (UserAuthenticationProviderPlugin.class.isAssignableFrom(configuredProvider.getClass())
						? (UserAuthenticationProviderPlugin) configuredProvider
						: new UserAuthenticationProviderPluginWrapper(configuredProvider));

			}
			catch (Exception ex) {
				throw new RuntimeException("Failed to instantiate UserAuthenticationProvider class: "
						+ ConfigurationManager.getInstance().getSessionConfiguration().getUserAuthenticationProviderClass().getName(), ex);
			}
		}
		return userAuthenticationProvider;
	}
}
