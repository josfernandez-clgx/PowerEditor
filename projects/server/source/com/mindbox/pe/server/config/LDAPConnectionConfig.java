/*
 * Created on Mar 29, 2006
 *
 */
package com.mindbox.pe.server.config;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.config.UserManagementConfig;
import com.mindbox.pe.server.Util;


/**
 * LDAP connection configuration.
 * This is used by the digester to contain configuration info 
 * &lt;PowerEditorConfiguraiton&gt;&lt;Server&gt;&lt;LDAP&gt; element 
 * in the PowerEditorConfiguration.xml file.
 * <p>
 * <b>Note:</b><br>
 * Changes to this class may require changes to {@link com.mindbox.pe.server.config.ConfigXMLDigester#digestLDAPConnectionConfig(InputStreamReader, LDAPConnectionConfig)}.
 * @author Geneho Kim
 * @since PowerEditor 4.5.0
 */
public class LDAPConnectionConfig {
	public LDAPConnectionConfig(InputStreamReader configReader) {
		try {
			ConfigXMLDigester.getInstance().digestLDAPConnectionConfig(configReader, this);
		} catch (Exception e) {
			throw new RuntimeException("Failed to digest LDAP confiuration.", e);
		}
		validateAndSetDefaults();
	}

	/**
	 * Container of &lt;UserObjectRequiredAttribute&gt; elements.
	 * @author Geneho Kim
	 * @since PowerEditor 4.5.0
	 */
	public static class RequiredAttribute {

		private String name, value;

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String toString() {
			return "ReqAttr[name=" + name + ",value=" + value + ']';
		}
	}

	private static String asValidAuthenticationScheme(String str) {
		if (str == null || str.trim().length() == 0) return null;
		if (str.equalsIgnoreCase("anonymous")) return AUTH_NONE;
		if (str.equalsIgnoreCase("simple")) return AUTH_SIMPLE;
		if (str.equalsIgnoreCase("cram-md5")) return "CRAM-MD5";
		if (str.equalsIgnoreCase("digest-md5")) return "DIGEST-MD5";
		if (str.equalsIgnoreCase("md5")) return "DIGEST-MD5";
		if (str.equalsIgnoreCase("EXTERNAL")) return "EXTERNAL";
		if (str.equalsIgnoreCase("GSSAPI")) return "GSSAPI";
		return null;
	}

	public static final String AUTH_NONE = "none";
	private static final String AUTH_SIMPLE = "simple";
	
	private static final Logger logger = Logger.getLogger(LDAPConnectionConfig.class);

	private boolean ldapConfigured;
	private String connectionURL;
	private String principal; //loginID
	private Password credentials; // password
	private String userIDAttribute;//name of userID called in LDAP database
	private String userNameAttribute;
	private String userPasswordAttribute;
	private String userRolesAttribute;
	private String userStatusAttribute;
	private String userObjectClass;
	private String userObjectClassHierarchy;
	private String authenticationScheme;
	private final UserManagementConfig umConfig = new UserManagementConfig();
	private final List<String> userDirectoryDNList = new ArrayList<String>();
	private final List<RequiredAttribute> userObjectRequiredAttrList = new ArrayList<RequiredAttribute>();

	public UserManagementConfig asUserManagementConfig() {
		return umConfig;
	}

	public void addUserDirectoryDN(String dn) {
		synchronized (userDirectoryDNList) {
			if (!userDirectoryDNList.contains(dn)) {
				userDirectoryDNList.add(dn);
			}
		}
	}

	public String[] getUserDirectoryDNs() {
		synchronized (userDirectoryDNList) {
			return userDirectoryDNList.toArray(new String[0]);
		}
	}

	/**
	 * Validates and sets default value.
	 * @param logger
	 */
	private void validateAndSetDefaults() {
		if (!isLdapConfigured()) {
			return;
		}
		if (Util.isEmpty(connectionURL))
				throw new IllegalArgumentException("<Server><LDAP><Connection> is not specified in PowerEditorConfiguration.xml");
		this.authenticationScheme = asValidAuthenticationScheme(this.authenticationScheme);
		if (authenticationScheme == null) { throw new IllegalArgumentException(
				"<Server><LDAP><AuthenticationScheme> is not specified or contains an invalid value in PowerEditorConfiguration.xml"); }
		boolean anonymous = authenticationScheme.equals(AUTH_NONE);
		if (!anonymous && Util.isEmpty(principal))
				throw new IllegalArgumentException(
						"<Server><LDAP><Principal> is required in PowerEditorConfiguration.xml for non-anonyous connection");
		if (!anonymous && (credentials == null || Util.isEmpty(credentials.getClearText())))
				throw new IllegalArgumentException(
						"<Server><LDAP><Credentials> is required in PowerEditorConfiguration.xml for non-anonyous connection");
		if (Util.isEmpty(userIDAttribute))
				throw new IllegalArgumentException("<Server><LDAP><UserIDAttribute> is not specified in PowerEditorConfiguration.xml");

		umConfig.setReadOnly(true);
		umConfig.setRoleChangeable(true);
		umConfig.setPasswordChangeable(false);
		umConfig.setAllowDelete(false);

		if (Util.isEmpty(userNameAttribute)) {
			umConfig.setNameChangeable(false);
			userNameAttribute = "cn";
			logger.warn("<Server><LDAP><UserNameAttribute> is not specified in PowerEditorConfiguration.xml>. Set to " + userNameAttribute);
		}
		else {
			umConfig.setNameChangeable(true);
		}

		if (Util.isEmpty(userPasswordAttribute)) {
			userPasswordAttribute = "userPassword";
			logger.warn("<Server><LDAP><UserPasswordAttribute> is not specified in PowerEditorConfiguration.xml. Set to " + userPasswordAttribute);
		}

		if (Util.isEmpty(userRolesAttribute)) {
			// if no role attribute is specified, MB_USER_ROLE table is used
			logger.warn("<Server><LDAP><UserRolesAttribute> is not specified in PowerEditorConfiguration.xml");
			umConfig.setRoleChangeable(!umConfig.isReadOnly());
		}
		else {
			umConfig.setRoleChangeable(true);
		}

		if (Util.isEmpty(userStatusAttribute)) {
			umConfig.setStatusChangeable(false);
			logger.warn("<Server><LDAP><UserPasswordAttribute> is not specified in PowerEditorConfiguration.xml: all users will be active status!");
		}
		else {
			umConfig.setStatusChangeable(!umConfig.isReadOnly());
		}

		if (userDirectoryDNList.isEmpty()) {
			logger.warn("No <Server><LDAP><UseDirectoryDN> is found. Will use initial directory.");
		}

		if (Util.isEmpty(userObjectClass)) {
			userObjectClass = "person";
			logger.warn("<Server><LDAP><UserObjectClass> is not specified in PowerEditorConfiguration.xml. Set to " + userObjectClass);
		}

		// validate user object required attributes
		for (Iterator<RequiredAttribute> iter = userObjectRequiredAttrList.iterator(); iter.hasNext();) {
			RequiredAttribute element = iter.next();
			if (element.getName() == null) throw new IllegalArgumentException("name attribute of <UserObjectRequiredAttribute> is required.");
			if (element.getValue() == null) throw new IllegalArgumentException("value attribute of <UserObjectRequiredAttribute> is required.");
			if (!element.getValue().equals("id") && !element.getValue().equals("name")) { throw new IllegalArgumentException(
					"value attribute of <UserObjectRequiredAttribute> must be \"id\" or \"name\"."); }
		}

		logger.info("Number of user directory DNs = " + userDirectoryDNList.size());
		logger.info("Number of required user object attributes = " + userObjectRequiredAttrList.size());
	}

	public String toString() {
		return "LDAP[conn=" + connectionURL + ",authScheme?=" + authenticationScheme + ",principal=" + principal + ",cred=" + credentials
				+ ",idAttr=" + userIDAttribute
				+ ",nameAttr=" + userNameAttribute + ",rolesAttr=" + userRolesAttribute + ",passwordAttr=" + userPasswordAttribute + ",statusAttr="
				+ userStatusAttribute + ",userObjectClass=" + userObjectClass + ",uocHierarchy=" + userObjectClassHierarchy + ",userDirDNs="
				+ Util.toString(getUserDirectoryDNs()) + ']';
	}

	public void addUserObjectRequiredAttribute(RequiredAttribute requiredAttribute) {
		userObjectRequiredAttrList.add(requiredAttribute);
	}

	public RequiredAttribute[] getUserObjectRequiredAttributes() {
		return userObjectRequiredAttrList.toArray(new RequiredAttribute[0]);
	}

	public String getUserObjectClassHierarchy() {
		return userObjectClassHierarchy;
	}

	public String[] getUserObjectClassesInOrder() {
		return (userObjectClassHierarchy == null ? null : userObjectClassHierarchy.split(","));
	}

	public void setUserObjectClassHierarchy(String userObjectClassHierarchy) {
		this.userObjectClassHierarchy = userObjectClassHierarchy;
	}

	public String getAuthenticationScheme() {
		return authenticationScheme;
	}

	public void setAuthenticationScheme(String authenticationScheme) {
		this.authenticationScheme = authenticationScheme;
	}

	public String getUserIDAttribute() {
		return userIDAttribute;
	}

	public void setUserIDAttribute(String userIDAttribute) {
		this.userIDAttribute = userIDAttribute;
	}

	public String getUserObjectClass() {
		return userObjectClass;
	}

	public void setUserObjectClass(String userObjectClass) {
		this.userObjectClass = userObjectClass;
	}

	public String getUserNameAttribute() {
		return userNameAttribute;
	}

	public String getUserRolesAttribute() {
		return userRolesAttribute;
	}

	public void setUserNameAttribute(String userNameAttribute) {
		this.userNameAttribute = userNameAttribute;
	}

	public void setUserRolesAttribute(String userRolesAttribute) {
		this.userRolesAttribute = userRolesAttribute;
	}

	public String getUserPasswordAttribute() {
		return userPasswordAttribute;
	}

	public String getUserStatusAttribute() {
		return userStatusAttribute;
	}

	public void setUserPasswordAttribute(String userPasswordAttribute) {
		this.userPasswordAttribute = userPasswordAttribute;
	}

	public void setUserStatusAttribute(String userStatusAttribute) {
		this.userStatusAttribute = userStatusAttribute;
	}

	public String getConnectionURL() {
		return connectionURL;
	}

	public String getClearTextCredentials() {
		return credentials == null ? null : credentials.getClearText();
	}

	public String getPrincipal() {
		return principal;
	}

	public void setConnectionURL(String connectionURL) {
		this.connectionURL = connectionURL;
	}

	public void setEncryptedCredentials(String credentials) {
		if (credentials != null) {
			setCredentials(Password.fromEncryptedString(credentials));
		}
	}

	private void setCredentials(Password credentials) {
		this.credentials = credentials;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public boolean isLdapConfigured() {
		return ldapConfigured;
	}

	public void setLdapConfigured() {
		this.ldapConfigured = true; // only called if LDAP element exists
	}

}
