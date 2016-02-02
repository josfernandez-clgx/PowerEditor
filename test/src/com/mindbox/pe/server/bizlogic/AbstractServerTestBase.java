package com.mindbox.pe.server.bizlogic;

import org.easymock.MockControl;
import org.easymock.classextension.MockClassControl;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.spi.PEDBCProvider;
import com.mindbox.pe.server.spi.ServiceProviderFactory;
import com.mindbox.pe.server.spi.UserManagementProvider;

public abstract class AbstractServerTestBase extends AbstractTestWithTestConfig {

	protected MockControl peDBCProviderMockControl;
	protected PEDBCProvider mockPEDBCProvider;
	protected MockControl userManagementProviderMockControl;
	protected UserManagementProvider mockUserManagementProvider;
	protected MockControl bizActionCoordinatorMockControl;
	protected BizActionCoordinator mockBizActionCoordinator;
	protected MockControl securityCacheManagerMockControl;
	protected SecurityCacheManager mockSecurityCacheManager;

	protected AbstractServerTestBase(String name) {
		super(name);
	}

	/**
	 * Instantiates {@link #securityCacheManagerMockControl} and {@link #mockUserSecurityDataHolder}.
	 * @throws Exception
	 */
	protected final void useMockSecurityCacheManager() throws Exception {
		securityCacheManagerMockControl = MockClassControl.createControl(SecurityCacheManager.class);
		mockSecurityCacheManager = (SecurityCacheManager) securityCacheManagerMockControl.getMock();
		ReflectionUtil.setPrivate(SecurityCacheManager.class, "instance", mockSecurityCacheManager);
	}
	
	/**
	 * Instantiates {@link #bizActionCoordinatorMockControl} and {@link #mockBizActionCoordinator}.
	 * @throws Exception
	 */
	protected final void useMockBizActionCoordinator() throws Exception {
		bizActionCoordinatorMockControl = MockClassControl.createControl(BizActionCoordinator.class);
		mockBizActionCoordinator = (BizActionCoordinator) bizActionCoordinatorMockControl.getMock();
		ReflectionUtil.setPrivate(BizActionCoordinator.class, "instance", mockBizActionCoordinator);
	}
	
	/**
	 * Instantiates {@link #peDBCProviderMockControl} and {@link #mockPEDBCProvider}.
	 * @throws Exception
	 */
	protected final void useMockPEDBCProvider() throws Exception {
		peDBCProviderMockControl = MockControl.createControl(PEDBCProvider.class);
		mockPEDBCProvider = (PEDBCProvider) peDBCProviderMockControl.getMock();
		ReflectionUtil.setPrivate(ServiceProviderFactory.class, "peDBCProvider", mockPEDBCProvider);
	}

	/**
	 * Instantiates {@link #userManagementProviderMockControl} and {@link #mockUserManagementProvider}.
	 * This calls {@link #useMockPEDBCProvider()}.
	 * This calls calls <code>peDBCProviderMockControl.expectAndReturn(mockPEDBCProvider.getUserManagementProvider(), mockUserManagementProvider)</code>
	 * @throws Exception
	 */
	protected final void useMockUserManagementProvider() throws Exception {
		useMockUserManagementProvider(1);
	}

	protected final void useMockUserManagementProvider(int expectedCallsToGetUserManagementProvider) throws Exception {
		useMockPEDBCProvider();

		userManagementProviderMockControl = MockControl.createControl(UserManagementProvider.class);
		mockUserManagementProvider = (UserManagementProvider) userManagementProviderMockControl.getMock();

		peDBCProviderMockControl.expectAndReturn(
				mockPEDBCProvider.getUserManagementProvider(),
				mockUserManagementProvider,
				expectedCallsToGetUserManagementProvider);
	}

	protected final void replayAllMockControls() {
		if (bizActionCoordinatorMockControl != null) bizActionCoordinatorMockControl.replay();
		if (peDBCProviderMockControl != null) peDBCProviderMockControl.replay();
		if (userManagementProviderMockControl != null) userManagementProviderMockControl.replay();
		if (securityCacheManagerMockControl != null) securityCacheManagerMockControl.replay();
	}
	
	protected final void verifyAllMockControls() {
		if (bizActionCoordinatorMockControl != null) bizActionCoordinatorMockControl.verify();
		if (peDBCProviderMockControl != null) peDBCProviderMockControl.verify();
		if (userManagementProviderMockControl != null) userManagementProviderMockControl.verify();
		if (securityCacheManagerMockControl != null) securityCacheManagerMockControl.verify();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		if (peDBCProviderMockControl != null) {
			ReflectionUtil.setPrivate(ServiceProviderFactory.class, "peDBCProvider", null);
		}
		if (bizActionCoordinatorMockControl != null) {
			ReflectionUtil.setPrivate(BizActionCoordinator.class, "instance", null);
		}
		if (securityCacheManagerMockControl != null) {
			ReflectionUtil.setPrivate(SecurityCacheManager.class, "instance", null);
		}
		super.tearDown();
	}
}
