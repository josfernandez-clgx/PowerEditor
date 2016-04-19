package com.mindbox.pe.server.ldap;

import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;

import com.mindbox.pe.model.Password;
import com.mindbox.pe.server.JNDIUtil;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.xsd.config.LDAPConfig;

public class LdapUtil {

	private static class ImmutableAttributes implements Attributes {

		/**
		 * 
		 */
		private static final long serialVersionUID = 200704150000L;

		private Attributes delegate;

		private ImmutableAttributes(Attributes attrs) {
			this.delegate = attrs;
		}

		public Object clone() {
			return new ImmutableAttributes(delegate);
		}

		public Attribute get(String id) {
			return delegate.get(id);
		}

		public NamingEnumeration<? extends Attribute> getAll() {
			return delegate.getAll();
		}

		public NamingEnumeration<String> getIDs() {
			return delegate.getIDs();
		}

		public boolean isCaseIgnored() {
			return delegate.isCaseIgnored();
		}

		public Attribute put(Attribute attr) {
			throw new UnsupportedOperationException("ImmutableAttributes doesn't support the put operation.");
		}

		public Attribute put(String id, Object value) {
			throw new UnsupportedOperationException("ImmutableAttributes doesn't support the put operation.");
		}

		public Attribute remove(String id) {
			throw new UnsupportedOperationException("ImmutableAttributes doesn't support the remove operation.");
		}

		public int size() {
			return delegate.size();
		}
	}

	private static LDAPConfig ldapConfig = null;

	private static SearchResult findSearchResultForUser(NamingEnumeration<?> results, String userId) throws NamingException {
		while (results.hasMore()) {
			SearchResult sr = (SearchResult) results.next();
			if (userId.equals(JNDIUtil.getRequiredAttributeAsString(sr.getAttributes(), getLdapConfig().getUserIDAttribute()))) {
				results.close();
				return sr;
			}
		}
		return null;
	}

	public static SearchResult findUserObject(String userId) throws NamingException {
		SearchResult result = null;
		DirContext context = getInitialDirContext();
		try {
			List<String> userDirDNs = getLdapConfig().getUserDirectoryDN();
			if (userDirDNs == null || userDirDNs.isEmpty()) {
				result = findSearchResultForUser(context.search("", getSearchAttributesForUser(userId)), userId);
			}
			else {
				for (final String userDirDn : userDirDNs) {
					result = findSearchResultForUser(context.search(userDirDn, getSearchAttributesForUser(userId)), userId);
					if (result != null) {
						break;
					}
				}
			}
		}
		finally {
			context.close();
		}
		return result;
	}

	/**
	 * Called or this method should call InitialContext.close() method, when done.
	 * @return
	 * @throws NamingException
	 */
	static DirContext getInitialDirContext() throws NamingException {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapConfig.getConnection());
		env.put(Context.SECURITY_AUTHENTICATION, getLdapConfig().getAuthenticationScheme());
		if (!getLdapConfig().getAuthenticationScheme().equals(ConfigurationManager.AUTH_NONE)) {
			env.put(Context.SECURITY_PRINCIPAL, getLdapConfig().getPrincipal());
			env.put(Context.SECURITY_CREDENTIALS, Password.fromEncryptedString(getLdapConfig().getCredentials()).getClearText());
		}
		InitialDirContext ctx = new InitialDirContext(env);
		return ctx;
	}

	private static LDAPConfig getLdapConfig() {
		if (ldapConfig == null) {
			ldapConfig = ConfigurationManager.getInstance().getLdapConfig();

			Attributes mutableSearchAttrs = new BasicAttributes(true);
			mutableSearchAttrs.put(new BasicAttribute("objectclass", ldapConfig.getUserObjectClass()));
		}
		return ldapConfig;
	}

	private static Attributes getSearchAttributesForUser(String userId) {
		Attributes mutableSearchAttrs = new BasicAttributes(true);
		mutableSearchAttrs.put(new BasicAttribute("objectclass", ldapConfig.getUserObjectClass()));
		mutableSearchAttrs.put(new BasicAttribute(getLdapConfig().getUserIDAttribute(), userId));
		return new ImmutableAttributes(mutableSearchAttrs);
	}

	public static Attributes immutableAttributes(Attributes attrs) {
		return new ImmutableAttributes(attrs);
	}

	public static String userIdAsLdapName(String userID) {
		return getLdapConfig().getUserIDAttribute() + "=" + userID;
	}

	private LdapUtil() {
	}
}
