package com.mindbox.pe.server.db;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.spi.UserAuthenticationProviderPlugin;
import com.mindbox.pe.server.spi.UserManagementProvider;

public class LDAPUserManagementProviderTest extends AbstractTestWithTestConfig {

	private UserManagementProvider userMgr;

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();

		userMgr = new LDAPUserManagementProvider();
	}

	public void tearDown() throws Exception {
		super.tearDown();
		config.resetConfiguration();
	}

	@Test
	public void testArePasswordsPersistableExternalPasswordStorage() throws Exception {
		expect(UserAuthenticationProviderPlugin.class.cast(getMockUserAuthenticationProvider()).arePasswordsStoredExternally()).andReturn(true);
		replay(getMockUserAuthenticationProvider());
		assertFalse(userMgr.arePasswordsPersistable());
		verify(getMockUserAuthenticationProvider());
	}
}
