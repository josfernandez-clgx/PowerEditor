package com.mindbox.pe.server.servlet.handlers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.common.XmlUtil;
import com.mindbox.pe.common.config.UserManagementConfig;
import com.mindbox.pe.communication.ErrorResponse;
import com.mindbox.pe.communication.FetchPeConfigurationRequest;
import com.mindbox.pe.communication.FetchPeConfigurationResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.model.UserProfile;
import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.cache.SessionManager;
import com.mindbox.pe.server.cache.TypeEnumValueManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.db.LDAPUserManagementProvider;
import com.mindbox.pe.server.model.PowerEditorSession;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.spi.ServiceProviderFactory;
import com.mindbox.pe.xsd.config.PowerEditorConfiguration;
import com.mindbox.pe.xsd.config.ServerConfig;

public final class FetchPeConfigurationRequestHandler extends AbstractActionRequestHandler<FetchPeConfigurationRequest> {

	private static Set<Privilege> getPrivileges(String userID) throws ServerException {
		List<Role> roleList = SecurityCacheManager.getInstance().getRoles(userID);
		Set<Privilege> privSet = new HashSet<Privilege>();
		for (Iterator<Role> iter = roleList.iterator(); iter.hasNext();) {
			Role role = iter.next();
			for (Iterator<Privilege> prevIterator = role.getPrivileges().iterator(); prevIterator.hasNext();) {
				Privilege privilege = prevIterator.next();
				if (!privSet.contains((privilege.getName()))) {
					privSet.add(privilege);
				}
			}
		}
		return Collections.unmodifiableSet(privSet);
	}

	private static void overridePasswordMgmtConfiguration(UserManagementConfig userMgmtConfig) {
		if (ServiceProviderFactory.getUserAuthenticationProvider().arePasswordsStoredExternally()) {
			userMgmtConfig.setPasswordChangeable(false);
		}
	}

	/**
	 * Even if LDAP element exists, if UserManagementProviderClass is not set to LDAPUserManagementProvider, 
	 * set read only to false and everything else to true. When user authentication is happening through PE database
	 * and not LDAP, there exists only one privilege, 'ManageUsers' and it is not divided into finer grain
	 * privileges like change password, change status etc. Hence all of these fine grained privileges need to be set 
	 * to true.
	 * @param userMgmtConfig
	 */
	private static void overrideUserManagemenConfig(UserManagementConfig userMgmtConfig) {
		if (!ServiceProviderFactory.getUserManagementProvider().cacheUserObjects()) {
			userMgmtConfig.setReadOnly(true);
		}
		else if (!LDAPUserManagementProvider.class.getName().equals(ConfigurationManager.getInstance().getServerConfigHelper().getDatabaseConfig().getUserManagementProviderClassName())) {
			userMgmtConfig.setReadOnly(false);
			userMgmtConfig.setAllowDelete(true);
			userMgmtConfig.setPasswordChangeable(true);
			userMgmtConfig.setNameChangeable(true);
			userMgmtConfig.setRoleChangeable(true);
			userMgmtConfig.setStatusChangeable(true);
		}
	}

	public ResponseComm handleRequest(final FetchPeConfigurationRequest request, final HttpServletRequest httpservletrequest) {
		PowerEditorSession session = SessionManager.getInstance().getSession(request.getSessionID());
		if (session == null) {
			return new FetchPeConfigurationResponse(request.getSessionID(), null, null, null, null);
		}
		else {
			try {
				String userID = session.getUserID();
				User user = SecurityCacheManager.getInstance().getUser(userID);
				if (user == null) {
					return new ErrorResponse("ServerError", "No user " + userID + " found");
				}

				// Create a copy without RuleGeneration and Server configuration
				final PowerEditorConfiguration powerEditorConfiguration = new PowerEditorConfiguration();
				powerEditorConfiguration.setEntityConfig(ConfigurationManager.getInstance().getPowerEditorConfiguration().getEntityConfig());
				powerEditorConfiguration.setFeatureConfig(ConfigurationManager.getInstance().getPowerEditorConfiguration().getFeatureConfig());
				powerEditorConfiguration.setKnowledgeBaseFilter(ConfigurationManager.getInstance().getPowerEditorConfiguration().getKnowledgeBaseFilter());
				powerEditorConfiguration.setUserInterface(ConfigurationManager.getInstance().getPowerEditorConfiguration().getUserInterface());
				powerEditorConfiguration.setServer(new ServerConfig());
				powerEditorConfiguration.getServer().setSession(ConfigurationManager.getInstance().getPowerEditorConfiguration().getServer().getSession());

				final String configXml = XmlUtil.marshal(powerEditorConfiguration, false);

				final UserManagementConfig userMgmtConfig = ConfigurationManager.getInstance().getUserManagementConfig();
				//! n.b. the order of these two override method calls is important for correctly setting the password is changeable property.
				overrideUserManagemenConfig(userMgmtConfig);
				overridePasswordMgmtConfiguration(userMgmtConfig);

				final String[] defaultTime = (powerEditorConfiguration.getUserInterface().getDateSynonym() == null
						|| powerEditorConfiguration.getUserInterface().getDateSynonym().getDefaultTime() == null
						? null
						: powerEditorConfiguration.getUserInterface().getDateSynonym().getDefaultTime().trim().split(":"));
				final int defaultExpirationDays = (powerEditorConfiguration.getUserInterface().getDeployExpirationDate() == null
						|| powerEditorConfiguration.getUserInterface().getDeployExpirationDate().getDefaultDays() == null
						? 0
						: powerEditorConfiguration.getUserInterface().getDeployExpirationDate().getDefaultDays().intValue());
				final boolean showDeployAsOfDate = (powerEditorConfiguration.getUserInterface().getDeployExpirationDate() == null
						|| powerEditorConfiguration.getUserInterface().getDeployExpirationDate().isShowAsOfDate() == null
						? false
						: powerEditorConfiguration.getUserInterface().getDeployExpirationDate().isShowAsOfDate().booleanValue());
				final UserProfile userProfile = new UserProfile(
						userID,
						getPrivileges(userID),
						ConfigurationManager.getInstance().getCondMsgDelims(),
						ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper().getLookback(),
						ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper().getMaxAttempts(),
						ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper().getExpirationDays(),
						defaultTime,
						defaultExpirationDays,
						showDeployAsOfDate);


				return new FetchPeConfigurationResponse(request.getSessionID(), configXml, userProfile, TypeEnumValueManager.getInstance().getTypeEnumValueMap(), userMgmtConfig);
			}
			catch (Exception e) {
				logger.error("Error in FetchPeConfigurationRequestHandler", e);
				return new ErrorResponse("ServerError", e.getMessage());
			}
		}
	}
}