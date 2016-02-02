package com.mindbox.pe.server.ldap;

import java.util.Hashtable;

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

import com.mindbox.pe.server.JNDIUtil;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.LDAPConnectionConfig;

public class LdapUtil {
	private static final LDAPConnectionConfig ldapConfig;

	static {
		ldapConfig = ConfigurationManager.getInstance().getLDAPConnectionConfig();

		Attributes mutableSearchAttrs = new BasicAttributes(true);
		mutableSearchAttrs.put(new BasicAttribute("objectclass", ldapConfig.getUserObjectClass()));
	}

	private LdapUtil() {/*Singleton*/
	}

	private static Attributes getSearchAttributesForUser(String userId) {
		Attributes mutableSearchAttrs = new BasicAttributes(true);
		mutableSearchAttrs.put(new BasicAttribute("objectclass", ldapConfig.getUserObjectClass()));
		mutableSearchAttrs.put(new BasicAttribute(ldapConfig.getUserIDAttribute(), userId));
		return new ImmutableAttributes(mutableSearchAttrs);
	}

	public static SearchResult findUserObject(String userId) throws NamingException {
		SearchResult result = null;
		DirContext context = getInitialDirContext();
		try {
			String[] userDirDNs = ldapConfig.getUserDirectoryDNs();
			if (userDirDNs == null || userDirDNs.length == 0) {
				result = findSearchResultForUser(context.search("", getSearchAttributesForUser(userId)), userId);
			}
			else {
				for (int i = 0; i < userDirDNs.length; i++) {
					result = findSearchResultForUser(context.search(userDirDNs[i], getSearchAttributesForUser(userId)), userId);
					if (result != null) break;
				}
			}
		}
		finally {
			context.close();
		}
		return result;
	}

	private static SearchResult findSearchResultForUser(NamingEnumeration<?> results, String userId) throws NamingException {
		while (results.hasMore()) {
			SearchResult sr = (SearchResult) results.next();
			if (userId.equals(JNDIUtil.getRequiredAttributeAsString(sr.getAttributes(), ldapConfig.getUserIDAttribute()))) {
				results.close();
				return sr;
			}
		}
		return null;
	}
 
	/**
	 * Called or this method should call InitialContext.close() method, when done.
	 * @return
	 * @throws NamingException
	 */
	static DirContext getInitialDirContext() throws NamingException {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapConfig.getConnectionURL());
		env.put(Context.SECURITY_AUTHENTICATION, ldapConfig.getAuthenticationScheme());
		if (!ldapConfig.getAuthenticationScheme().equals(LDAPConnectionConfig.AUTH_NONE)) {
			env.put(Context.SECURITY_PRINCIPAL, ldapConfig.getPrincipal());
			env.put(Context.SECURITY_CREDENTIALS, ldapConfig.getClearTextCredentials());
		}
		InitialDirContext ctx = new InitialDirContext(env);
		return ctx;
	}
	
	public static String userIdAsLdapName(String userID) {
		return ldapConfig.getUserIDAttribute() + "=" + userID;
	}
	
	public static Attributes immutableAttributes(Attributes attrs) {
		return new ImmutableAttributes(attrs);
	}
	
	private static class ImmutableAttributes implements Attributes {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 200704150000L;
		
		private Attributes delegate;
		private ImmutableAttributes(Attributes attrs) {
			this.delegate = attrs;
		}
		public int size() {
			return delegate.size();
		}
		public boolean isCaseIgnored() {
			return delegate.isCaseIgnored();
		}
		public Object clone() {
			return new ImmutableAttributes(delegate);
		}
		public NamingEnumeration<? extends Attribute> getAll() {
			return delegate.getAll();
		}
		public NamingEnumeration<String> getIDs() {
			return delegate.getIDs();
		}
		public Attribute get(String id) {
			return delegate.get(id);
		}
		public Attribute remove(String id) {
			throw new UnsupportedOperationException("ImmutableAttributes doesn't support the remove operation.");
		}
		public Attribute put(Attribute attr) {
			throw new UnsupportedOperationException("ImmutableAttributes doesn't support the put operation.");
		}
		public Attribute put(String id, Object value) {
			throw new UnsupportedOperationException("ImmutableAttributes doesn't support the put operation.");
		}
	}
}
