package com.mindbox.pe.server.config;


import junit.framework.TestSuite;

// Most of these tests assume test/config/PowerEditorConfiguration.xml has LDAP configured
public class LDAPConnectionConfigTest extends ConfigXmlTest {
	public static TestSuite suite() {
		TestSuite suite = new TestSuite(LDAPConnectionConfigTest.class.getName());
		suite.addTestSuite(LDAPConnectionConfigTest.class);
		return suite;
	}
	
	public LDAPConnectionConfigTest(String name) {
		super(name);
	}
	
	public void testLdapConfigured() throws Exception {
		LDAPConnectionConfig ldapConfig = new LDAPConnectionConfig(getPeConfigXml());
		assertTrue(ldapConfig.isLdapConfigured());
	}
	
	public void testLdapNotConfigured() throws Exception {
		removeAll("Server/LDAP");
		LDAPConnectionConfig ldapConfig = new LDAPConnectionConfig(getPeConfigXml());
		assertFalse(ldapConfig.isLdapConfigured());
	}
	
	public void testConnectionUrl() throws Exception {
		assertValidElementValue("ldap://localhost:389", LDAPConnectionConfig.class, "connectionURL");
	}
	
	public void testConnectionUrlRequired() throws Exception {
		assertElementRequired("Server/LDAP/Connection", LDAPConnectionConfig.class);
	}
	
	public void testAnonymousAuthenticationScheme() throws Exception {
		replaceText("Server/LDAP/AuthenticationScheme", "anonymous");
		assertValidElementValue("none", LDAPConnectionConfig.class, "authenticationScheme");
	}
	
	public void testSimpleAuthenticationScheme() throws Exception {
		replaceText("Server/LDAP/AuthenticationScheme", "simple");
		assertValidElementValue("simple", LDAPConnectionConfig.class, "authenticationScheme");
	}
	
	public void testCramMd5AuthenticationScheme() throws Exception {
		replaceText("Server/LDAP/AuthenticationScheme", "cram-md5");
		assertValidElementValue("CRAM-MD5", LDAPConnectionConfig.class, "authenticationScheme");
	}
	
	public void testDigestMd5AuthenticationScheme() throws Exception {
		replaceText("Server/LDAP/AuthenticationScheme", "digest-md5");
		assertValidElementValue("DIGEST-MD5", LDAPConnectionConfig.class, "authenticationScheme");
	}
	
	public void testMd5AuthenticationScheme() throws Exception {
		replaceText("Server/LDAP/AuthenticationScheme", "md5");
		assertValidElementValue("DIGEST-MD5", LDAPConnectionConfig.class, "authenticationScheme");
	}
	
	public void testExternalAuthenticationScheme() throws Exception {
		replaceText("Server/LDAP/AuthenticationScheme", "EXTERNAL");
		assertValidElementValue("EXTERNAL", LDAPConnectionConfig.class, "authenticationScheme");
	}
	
	public void testGssApiAuthenticationScheme() throws Exception {
		replaceText("Server/LDAP/AuthenticationScheme", "GSSAPI");
		assertValidElementValue("GSSAPI", LDAPConnectionConfig.class, "authenticationScheme");
	}
	
	public void testInvalidAuthenitcationScheme() throws Exception {
		assertInvalidElementValue("Server/LDAP/AuthenticationScheme", LDAPConnectionConfig.class, "foobar");
	}
	
	public void testAuthenticationSchemeRequired() throws Exception {
		assertElementRequired("Server/LDAP/AuthenticationScheme", LDAPConnectionConfig.class);
	}
	
	public void testPrincipal() throws Exception {
		assertValidElementValue("cn=Manager,dc=mindbox,dc=com", LDAPConnectionConfig.class, "principal");
	}
	
	public void testPrincipalRequiredWhenAuthenticationSimple() throws Exception {
		replaceText("Server/LDAP/AuthenticationScheme", "simple");
		assertElementRequired("Server/LDAP/Principal", LDAPConnectionConfig.class);
	}
	
	public void testPrincipalRequiredWhenAuthenticationCramMd5() throws Exception {
		replaceText("Server/LDAP/AuthenticationScheme", "cram-md5");
		assertElementRequired("Server/LDAP/Principal", LDAPConnectionConfig.class);
	}
	
	public void testPrincipalRequiredWhenAuthenticationDigestMd5() throws Exception {
		replaceText("Server/LDAP/AuthenticationScheme", "digest-md5");
		assertElementRequired("Server/LDAP/Principal", LDAPConnectionConfig.class);
	}
	
	public void testPrincipalRequiredWhenAuthenticationMd5() throws Exception {
		replaceText("Server/LDAP/AuthenticationScheme", "md5");
		assertElementRequired("Server/LDAP/Principal", LDAPConnectionConfig.class);
	}
	
	public void testPrincipalRequiredWhenAuthenticationExternal() throws Exception {
		replaceText("Server/LDAP/AuthenticationScheme", "EXTERNAL");
		assertElementRequired("Server/LDAP/Principal", LDAPConnectionConfig.class);
	}

	public void testPrincipalRequiredWhenAuthenticationGssApi() throws Exception {
		replaceText("Server/LDAP/AuthenticationScheme", "GSSAPI");
		assertElementRequired("Server/LDAP/Principal", LDAPConnectionConfig.class);
	}
	
	public void testPrincipalNotRequiredWhenAnonymousAuthentication() throws Exception {
		replaceText("Server/LDAP/AuthenticationScheme", "anonymous");
		assertElementNotRequired("Server/LDAP/Principal", LDAPConnectionConfig.class, "principal");
	}
	
	public void testCredentials() throws Exception {
		assertValidElementValue("secret", LDAPConnectionConfig.class, "clearTextCredentials");
	}
	
	public void testCredentialsRequiredWhenAuthenticationSimple() throws Exception {
		replaceText("Server/LDAP/AuthenticationScheme", "simple");
		assertElementRequired("Server/LDAP/Credentials", LDAPConnectionConfig.class);
	}
	
	public void testCredentialsRequiredWhenAuthenticationCramMd5() throws Exception {
		replaceText("Server/LDAP/AuthenticationScheme", "cram-md5");
		assertElementRequired("Server/LDAP/Credentials", LDAPConnectionConfig.class);
	}
	
	public void testCredentialsRequiredWhenAuthenticationDigestMd5() throws Exception {
		replaceText("Server/LDAP/AuthenticationScheme", "digest-md5");
		assertElementRequired("Server/LDAP/Credentials", LDAPConnectionConfig.class);
	}
	
	public void testCredentialsRequiredWhenAuthenticationMd5() throws Exception {
		replaceText("Server/LDAP/AuthenticationScheme", "md5");
		assertElementRequired("Server/LDAP/Credentials", LDAPConnectionConfig.class);
	}
	
	public void testCredentialsRequiredWhenAuthenticationExternal() throws Exception {
		replaceText("Server/LDAP/AuthenticationScheme", "EXTERNAL");
		assertElementRequired("Server/LDAP/Credentials", LDAPConnectionConfig.class);
	}

	public void testCredentialsRequiredWhenAuthenticationGssApi() throws Exception {
		replaceText("Server/LDAP/AuthenticationScheme", "GSSAPI");
		assertElementRequired("Server/LDAP/Credentials", LDAPConnectionConfig.class);
	}
	
	public void testCredentialsNotRequiredWhenAnonymousAuthentication() throws Exception {
		replaceText("Server/LDAP/AuthenticationScheme", "anonymous");
		assertElementNotRequired("Server/LDAP/Credentials", LDAPConnectionConfig.class, "clearTextCredentials");
	}
	
	public void testUserDirectoryDN() throws Exception {
		assertValidElementValue("ou=Users,o=PowerEditor,dc=mindbox,dc=com", LDAPConnectionConfig.class, "userDirectoryDNs");
	}
	
	public void testUserDirectoryDNNotRequired() throws Exception {
		assertElementNotRequired("Server/LDAP/UserDirectoryDN", LDAPConnectionConfig.class, "userDirectoryDNs");
	}
	
	public void testUserObjectClass() throws Exception {
		assertValidElementValue("powerEditorOrgPerson", LDAPConnectionConfig.class, "userObjectClass");
	}
	
	public void testUserObjectClassNotRequired() throws Exception {
		assertElementDefaultValue("Server/LDAP/UserObjectClass", LDAPConnectionConfig.class, "userObjectClass", "person");
	}
	
	public void testUserObjectClassHierarchy() throws Exception {
		assertValidElementValue("top,person,organizationalPerson,powerEditorOrgPerson", LDAPConnectionConfig.class, "userObjectClassHierarchy");
	}
	
	public void testUserIDAttribute() throws Exception {
		assertValidElementValue("cn", LDAPConnectionConfig.class, "userIDAttribute");
	}
	
	public void testUserIDAttributeRequired() throws Exception {
		assertElementRequired("Server/LDAP/UserIDAttribute", LDAPConnectionConfig.class);
	}
	
	public void testUserPasswordAttribute() throws Exception {
		assertValidElementValue("userPassword", LDAPConnectionConfig.class, "userPasswordAttribute");
	}
	
	public void testUserPasswordAttributeNotRequired() throws Exception {
		assertElementDefaultValue("Server/LDAP/UserPasswordAttribute", LDAPConnectionConfig.class, "userPasswordAttribute", "userPassword");
	}

	public void testUserNameAttribute() throws Exception {
		assertValidElementValue("peUserName", LDAPConnectionConfig.class, "userNameAttribute");
	}
	
	public void testUserStatusAttribute() throws Exception {
		assertValidElementValue("peUserStatus", LDAPConnectionConfig.class, "userStatusAttribute");
	}
	
	public void testUserRolesAttribute() throws Exception {
		assertValidElementValue("peRoleName", LDAPConnectionConfig.class, "userRolesAttribute");
	}
	
	public void testUserRolesAttributeNotRequired() throws Exception {
		assertElementNotRequired("Server/LDAP/UserRolesAttribute", LDAPConnectionConfig.class, "userRolesAttribute");
	}
}
