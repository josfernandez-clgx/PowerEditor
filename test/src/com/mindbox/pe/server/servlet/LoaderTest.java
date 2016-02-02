package com.mindbox.pe.server.servlet;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.server.bizlogic.AbstractServerTestBase;
import com.mindbox.pe.server.cache.SecurityCacheManager;

public class LoaderTest extends AbstractServerTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("LoaderTest Tests");
		suite.addTestSuite(LoaderTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public LoaderTest(String name) {
		super(name);
	}
	
	public void testLoadUserDataWithLDAPUserManagementProviderDoesNotLoadUsers() throws Exception {
		useMockUserManagementProvider();
		
		userManagementProviderMockControl.expectAndReturn(mockUserManagementProvider.cacheUserObjects(), false);
		mockUserManagementProvider.loadAllPrivileges(SecurityCacheManager.getInstance());
		mockUserManagementProvider.loadAllRoles(SecurityCacheManager.getInstance());
		mockUserManagementProvider.loadAllPrivilegesToRoles(SecurityCacheManager.getInstance());
		replayAllMockControls();
		
		Loader.loadUserData();
		verifyAllMockControls();
	}

}
