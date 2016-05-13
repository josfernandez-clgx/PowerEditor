package com.mindbox.pe.server.ldap;

import java.util.Hashtable;
import java.util.List;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.directory.InitialDirContext;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.spi.UserAuthenticationProvider;

/**
 * A concrete implemenation of {@link UserAuthenticationProvider} that authenticates using LDAP connection authentication mechanism.
 * <p>
 * To use this class as the system {@link UserAuthenticationProvider}, configure:
 * <pre>
 <PowerEditorConfiguration>
   <Server>
     <Session>
       <UserAuthenticationProviderClass>com.mindbox.pe.server.ldap.DefaultUserAuthenticationProvider</UserAuthenticationProviderClass>
</pre>
 */
public class DefaultUserAuthenticationProvider implements UserAuthenticationProvider {

	private final Logger logger = Logger.getLogger(getClass());

	@Override
	public boolean authenticate(String userId, String password) throws Exception {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ConfigurationManager.getInstance().getLdapConfig().getConnection());
		env.put(Context.SECURITY_AUTHENTICATION, ConfigurationManager.getInstance().getLdapConfig().getAuthenticationScheme());

		List<String> userDirDNs = ConfigurationManager.getInstance().getLdapConfig().getUserDirectoryDN();
		if (userDirDNs != null) {
			for (String userDNString : userDirDNs) {
				String userDN = asUserDN(userId, userDNString);
				logger.debug("Authenticating " + userDN + "...");
				env.put(Context.SECURITY_PRINCIPAL, userDN);
				env.put(Context.SECURITY_CREDENTIALS, password);
				try {
					new InitialDirContext(env);
					logger.debug(userId + " authenticated!");
					return true;
				}
				catch (AuthenticationException ex) {
					// continue and try with the next user dir dn
					logger.warn("Failed to authenticate " + userId, ex);
				}
			}
		}
		return false;
	}

	private String asUserDN(String userId, String userDirDN) {
		return ConfigurationManager.getInstance().getLdapConfig().getUserIDAttribute() + "=" + userId + (UtilBase.isEmpty(userDirDN) ? "" : "," + userDirDN);
	}

	/**
	 * This implementation does nothing.
	 */
	@Override
	public void notifySsoAuthentication(String userId) throws Exception {
		// no-op
	}
}
