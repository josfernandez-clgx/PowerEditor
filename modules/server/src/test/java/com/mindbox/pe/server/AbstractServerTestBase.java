package com.mindbox.pe.server;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;

import org.junit.After;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.spi.PEDBCProvider;
import com.mindbox.pe.server.spi.ServiceProviderFactory;
import com.mindbox.pe.server.spi.UserManagementProvider;

public abstract class AbstractServerTestBase extends AbstractTestWithTestConfig {

	protected PEDBCProvider mockPEDBCProvider;
	protected UserManagementProvider mockUserManagementProvider;
	protected BizActionCoordinator mockBizActionCoordinator;

	@Override
	protected final void replayAllMocks() {
		super.replayAllMocks();
		replayMocks(mockBizActionCoordinator, mockPEDBCProvider, mockUserManagementProvider);
	}

	@After
	public void tearDown() throws Exception {
		if (mockPEDBCProvider != null) {
			ReflectionUtil.setPrivate(ServiceProviderFactory.class, "peDBCProvider", null);
		}
		if (mockBizActionCoordinator != null) {
			ReflectionUtil.setPrivate(BizActionCoordinator.class, "instance", null);
		}
		super.tearDown();
	}

	/**
	 * Instantiates {@link #mockbizActionCoordinator} and {@link #mockBizActionCoordinator}.
	 * 
	 * @throws Exception
	 */
	protected final void useMockBizActionCoordinator() throws Exception {
		mockBizActionCoordinator = createMock(BizActionCoordinator.class);
		ReflectionUtil.setPrivate(BizActionCoordinator.class, "instance", mockBizActionCoordinator);
	}

	/**
	 * Instantiates {@link #mockpeDBCProvider} and {@link #mockPEDBCProvider}.
	 * 
	 * @throws Exception
	 */
	protected final void useMockPEDBCProvider() throws Exception {
		mockPEDBCProvider = createMock(PEDBCProvider.class);
		ReflectionUtil.setPrivate(ServiceProviderFactory.class, "peDBCProvider", mockPEDBCProvider);
	}

	/**
	 * Instantiates {@link #mockuserManagementProvider} and {@link #mockUserManagementProvider}. This calls
	 * {@link #useMockPEDBCProvider()}. This calls calls
	 * <code>mockpeDBCProvider.expectAndReturn(mockPEDBCProvider.getUserManagementProvider(), mockUserManagementProvider)</code>
	 * 
	 * @throws Exception
	 */
	protected final void useMockUserManagementProvider() throws Exception {
		useMockPEDBCProvider();

		mockUserManagementProvider = createMock(UserManagementProvider.class);

		expect(mockPEDBCProvider.getUserManagementProvider()).andReturn(mockUserManagementProvider).anyTimes();
	}

	protected final void useMockUserManagementProvider(int expectedCallsToGetUserManagementProvider) throws Exception {
		useMockPEDBCProvider();

		mockUserManagementProvider = createMock(UserManagementProvider.class);

		expect(mockPEDBCProvider.getUserManagementProvider()).andReturn(mockUserManagementProvider).times(expectedCallsToGetUserManagementProvider);
	}

	@Override
	protected final void verifyAllMocks() {
		super.verifyAllMocks();
		verifyMocks(mockBizActionCoordinator, mockPEDBCProvider, mockUserManagementProvider);
	}
}
