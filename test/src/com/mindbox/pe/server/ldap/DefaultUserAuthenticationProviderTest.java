package com.mindbox.pe.server.ldap;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.easymock.MockControl;
import org.easymock.classextension.MockClassControl;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.LDAPConnectionConfig;

public class DefaultUserAuthenticationProviderTest extends AbstractTestWithTestConfig {

	public static Test suite() {
		TestSuite suite = new TestSuite("DefaultUserAuthenticationProviderTest Tests");
		suite.addTestSuite(DefaultUserAuthenticationProviderTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private MockControl ldapConfigurationMockControl;
	private LDAPConnectionConfig mockLDAPConnectionConfig;

	public DefaultUserAuthenticationProviderTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		ldapConfigurationMockControl = MockClassControl.createControl(LDAPConnectionConfig.class);
		mockLDAPConnectionConfig = (LDAPConnectionConfig) ldapConfigurationMockControl.getMock();
		ReflectionUtil.setPrivate(ConfigurationManager.getInstance(), "ldapConfig", mockLDAPConnectionConfig);
	}

	private void replayAll() {
		ldapConfigurationMockControl.replay();
	}

	private void verifyAll() {
		ldapConfigurationMockControl.verify();
	}

	protected void tearDown() throws Exception {
		ReflectionUtil.setPrivate(ConfigurationManager.getInstance(), "ldapConfig", null);
		super.tearDown();
	}

	public void testAsUserDNHappyCase() throws Exception {
		String userID = "user-id-1";
		String userDN = "ou=users,o=department,dc=company,dc=com";
		String userIDAttribute = "UIDAttr2";
		ldapConfigurationMockControl.expectAndReturn(mockLDAPConnectionConfig.getUserIDAttribute(), userIDAttribute);
		replayAll();
		
		testAsUserDN(userID, userDN, userIDAttribute + "=" + userID + "," + userDN);
		verifyAll();
	}
	
	public void testAsUserDNWithNullDNHappyCase() throws Exception {
		String userID = "user-id-1";
		String userIDAttribute = "UIDAttr";
		ldapConfigurationMockControl.expectAndReturn(mockLDAPConnectionConfig.getUserIDAttribute(), userIDAttribute);
		replayAll();
		
		testAsUserDN(userID, null, userIDAttribute + "=" + userID);
		verifyAll();
	}
	
	private void testAsUserDN(String userID, String userDN, String expectedResult) throws Exception {
		String result = (String) ReflectionUtil.executePrivate(
				new DefaultUserAuthenticationProvider(),
				"asUserDN",
				new Class[] { String.class, String.class },
				new Object[] { userID, userDN });
		assertEquals(expectedResult, result);
	}
}
