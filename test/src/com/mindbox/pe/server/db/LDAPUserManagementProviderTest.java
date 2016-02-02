package com.mindbox.pe.server.db;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.server.spi.UserManagementProvider;

public class LDAPUserManagementProviderTest extends AbstractTestWithTestConfig {
	
	private UserManagementProvider userMgr;

	public static TestSuite suite() {
		TestSuite suite = new TestSuite(LDAPUserManagementProviderTest.class.getName());
		suite.addTestSuite(LDAPUserManagementProviderTest.class);
		return suite;
	}

	public LDAPUserManagementProviderTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();

		userMgr = new LDAPUserManagementProvider();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		config.resetConfiguration();
	}

	public void testArePasswordsPersistableExternalPasswordStorage() throws Exception {
		getMockUserAuthenticationProvider().arePasswordsStoredExternally();
		getMockUserAuthenticationProviderControl().setReturnValue(true);

		replay();
		assertFalse(userMgr.arePasswordsPersistable());
		verify();
	}
	}
