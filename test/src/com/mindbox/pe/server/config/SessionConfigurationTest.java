package com.mindbox.pe.server.config;

import static com.mindbox.pe.ObjectMother.createString;

import java.io.File;
import java.io.FileReader;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.server.db.PeDbUserAuthenticationProvider;
import com.mindbox.pe.server.ldap.DefaultUserAuthenticationProvider;
import com.mindbox.pe.server.spi.UserAuthenticationProvider;

public class SessionConfigurationTest extends AbstractTestBase {

	private static class UserAuthProviderTestImpl implements UserAuthenticationProvider {

		@Override
		public boolean authenticate(String userId, String password) throws Exception {
			return false;
		}
		
		@Override
		public void notifySsoAuthentication(String userId) throws Exception {
		}

	}

	public static Test suite() {
		TestSuite suite = new TestSuite("SessionConfigurationTest");
		suite.addTestSuite(SessionConfigurationTest.class);
		return suite;
	}

	public SessionConfigurationTest(String name) {
		super(name);
	}

	public void testPostParseProcess_SetsLdapAuthManagerCase() throws Exception {
		sessionConfiguration.postParseProcess(createString() + "LDAPUserManagementProvider");

		assertEquals(DefaultUserAuthenticationProvider.class, sessionConfiguration.getUserAuthenticationProviderClass());
	}

	public void testPostParseProcess_NoUserAuthManagerSetUsesDefaultCase() throws Exception {
		sessionConfiguration.postParseProcess(null);

		assertEquals(PeDbUserAuthenticationProvider.class, sessionConfiguration.getUserAuthenticationProviderClass());
	}

	public void testPostParseProcess_WithUserAuthManagerHappyCase() throws Exception {
		sessionConfiguration.setUserAuthenticationProviderClassName(UserAuthProviderTestImpl.class.getName());
		sessionConfiguration.postParseProcess(createString() + "LDAPUserManagementProvider");

		assertEquals(UserAuthProviderTestImpl.class, sessionConfiguration.getUserAuthenticationProviderClass());
	}

	public void setUp() throws Exception {
		sessionConfiguration = new SessionConfiguration(new FileReader(new File("test/data/PEServerSessionConfigNoAuthProvider.xml")));
	}

	private SessionConfiguration sessionConfiguration;
}
