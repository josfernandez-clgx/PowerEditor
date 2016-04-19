package com.mindbox.pe.server.ldap;

import javax.naming.directory.SearchResult;

import com.mindbox.pe.server.JNDIUtil;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.spi.UserAuthenticationProvider;
import com.mindbox.pe.server.spi.UserAuthenticationProviderPlugin;

/**
 * To use this class as the system {@link UserAuthenticationProvider}, configure:
 * <pre>
 <PowerEditorConfiguration>
   <Server>
     <Session>
       <UserAuthenticationProviderClass>com.mindbox.pe.server.ldap.UserObjectUserAuthenticationProvider</...>
</pre>
 */
public class UserObjectUserAuthenticationProvider implements UserAuthenticationProviderPlugin {

	@Override
	public boolean arePasswordsStoredExternally() {
		return true;
	}

	@Override
	public boolean authenticate(String userId, String password) throws Exception {
		if (userId == null || password == null) {
			return false;
		}

		SearchResult sr = LdapUtil.findUserObject(userId);
		return sr != null && password.equals(JNDIUtil.asPassword(sr.getAttributes().get(ConfigurationManager.getInstance().getLdapConfig().getUserPasswordAttribute())));
	}

	/**
	 * This implementation does nothing.
	 */
	@Override
	public void notifySsoAuthentication(String userId) throws Exception {
		// no-op
	}
}
