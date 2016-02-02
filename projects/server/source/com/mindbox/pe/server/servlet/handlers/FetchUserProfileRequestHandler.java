package com.mindbox.pe.server.servlet.handlers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.common.config.UserManagementConfig;
import com.mindbox.pe.communication.ErrorResponse;
import com.mindbox.pe.communication.FetchUserProfileRequest;
import com.mindbox.pe.communication.FetchUserProfileResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.model.UserProfile;
import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.cache.SessionManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.db.LDAPUserManagementProvider;
import com.mindbox.pe.server.model.PowerEditorSession;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.spi.ServiceProviderFactory;

public final class FetchUserProfileRequestHandler extends AbstractActionRequestHandler<FetchUserProfileRequest> {

	public ResponseComm handleRequest(FetchUserProfileRequest request, HttpServletRequest httpservletrequest) throws ServerException {
		UserProfile userProfile = null;
		PowerEditorSession session = SessionManager.getInstance().getSession(request.getSessionID());
		if (session != null) {
			String userID = session.getUserID();
			User user = SecurityCacheManager.getInstance().getUser(userID);
			if (user == null) return new ErrorResponse("ServerError", "No user " + userID + " found");

			UserManagementConfig userMgmtConfig = ConfigurationManager.getInstance().getLDAPConnectionConfig().asUserManagementConfig();
			//! n.b. the order of these two override method calls is important for correctly setting the password is changeable property.
			overrideUserManagemenConfig(userMgmtConfig);
			overridePasswordMgmtConfiguration(userMgmtConfig);

			userProfile = new UserProfile(
					userID,
					getPrivileges(userID),
					ConfigurationManager.getInstance().getFeatureConfiguration(),
					ConfigurationManager.getInstance().getUIConfiguration(),
					ConfigurationManager.getInstance().getCondMsgDelims(),
					userMgmtConfig,
					ConfigurationManager.getInstance().getUserPasswordPoliciesConfig().getHistoryConfig().getLookback(),
					ConfigurationManager.getInstance().getUserPasswordPoliciesConfig().getLockoutConfig().getMaxAttempts(),
					ConfigurationManager.getInstance().getUserPasswordPoliciesConfig().getExpirationConfig().getExpirationDays());
		}
		return new FetchUserProfileResponse(request.getSessionID(), userProfile);
	}

	private Set<Privilege> getPrivileges(String userID) throws ServerException {
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

	/**
	 * Two conditions must be true in order for UI to manage passwords:
	 * 1. PE is configured to manage passwords, and
	 * 2. No custom authentication plugin is responsible for managing passwords.
	 * 
	 * Here we override the configured value iff the UserAuthenticationProvider stores passwords externally.
	 */
	private void overridePasswordMgmtConfiguration(UserManagementConfig userMgmtConfig) {
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
	private void overrideUserManagemenConfig(UserManagementConfig userMgmtConfig) {
		if (!ServiceProviderFactory.getUserManagementProvider().cacheUserObjects()) {
			userMgmtConfig.setReadOnly(true);
		}
		else if (!LDAPUserManagementProvider.class.getName().equals(
				ConfigurationManager.getInstance().getServerConfiguration().getDatabaseConfig().getUserManagementProviderClassName())) {
			userMgmtConfig.setReadOnly(false);
			userMgmtConfig.setAllowDelete(true);
			userMgmtConfig.setPasswordChangeable(true);
			userMgmtConfig.setNameChangeable(true);
			userMgmtConfig.setRoleChangeable(true);
			userMgmtConfig.setStatusChangeable(true);
		}
	}
}