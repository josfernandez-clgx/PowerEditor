package com.mindbox.pe.server.ldap;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.xsd.config.LDAPConfig;

public class DefaultUserAuthenticationProviderTest extends AbstractTestWithTestConfig {

	private LDAPConfig ldapConfig;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		ldapConfig = new LDAPConfig();
		ReflectionUtil.setPrivate(ConfigurationManager.getInstance().getPowerEditorConfiguration().getServer(), "ldap", ldapConfig);
	}

	@Override
	@After
	public void tearDown() throws Exception {
		ReflectionUtil.setPrivate(ConfigurationManager.getInstance().getPowerEditorConfiguration().getServer(), "ldap", null);
		super.tearDown();
	}

	private void testAsUserDN(String userID, String userDN, String expectedResult) throws Exception {
		String result = (String) ReflectionUtil.executePrivate(new DefaultUserAuthenticationProvider(), "asUserDN", new Class[] { String.class, String.class }, new Object[] { userID, userDN });
		assertEquals(expectedResult, result);
	}

	@Test
	public void testAsUserDNHappyCase() throws Exception {
		String userID = "user-id-1";
		String userDN = "ou=users,o=department,dc=company,dc=com";
		String userIDAttribute = "UIDAttr2";
		ldapConfig.setUserIDAttribute(userIDAttribute);

		testAsUserDN(userID, userDN, userIDAttribute + "=" + userID + "," + userDN);
	}

	@Test
	public void testAsUserDNWithNullDNHappyCase() throws Exception {
		String userID = "user-id-1";
		String userIDAttribute = "UIDAttr";
		ldapConfig.setUserIDAttribute(userIDAttribute);

		testAsUserDN(userID, null, userIDAttribute + "=" + userID);
	}
}
