package com.mindbox.pe.server.servlet;

import static org.easymock.EasyMock.expect;

import org.junit.Test;

import com.mindbox.pe.server.AbstractServerTestBase;
import com.mindbox.pe.server.cache.SecurityCacheManager;

public class LoaderTest extends AbstractServerTestBase {

	@Test
	public void testLoadUserDataWithLDAPUserManagementProviderDoesNotLoadUsers() throws Exception {
		useMockUserManagementProvider();

		expect(mockUserManagementProvider.cacheUserObjects()).andReturn(false);
		mockUserManagementProvider.loadAllPrivileges(SecurityCacheManager.getInstance());
		mockUserManagementProvider.loadAllRoles(SecurityCacheManager.getInstance());
		mockUserManagementProvider.loadAllPrivilegesToRoles(SecurityCacheManager.getInstance());
		replayAllMocks();

		Loader.loadUserData();
		verifyAllMocks();
	}

}
