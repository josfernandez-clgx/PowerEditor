package com.mindbox.pe.server.servlet.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.TestSuite;

import org.easymock.MockControl;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.common.config.UserManagementConfig;
import com.mindbox.pe.communication.FetchUserProfileRequest;
import com.mindbox.pe.communication.FetchUserProfileResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.server.db.LDAPUserManagementProvider;
import com.mindbox.pe.server.model.PowerEditorSession;
import com.mindbox.pe.server.servlet.ServletTest;
import com.mindbox.pe.server.spi.PEDBCProvider;
import com.mindbox.pe.server.spi.ServiceProviderFactory;

public class FetchUserProfileRequestHandlerTest extends ServletTest {
	public static TestSuite suite() {
		TestSuite suite = new TestSuite(FetchUserProfileRequestHandlerTest.class.getName());
		suite.addTestSuite(FetchUserProfileRequestHandlerTest.class);
		return suite;
	}

	public FetchUserProfileRequestHandlerTest(String name) {
		super(name);
	}

	public void testNullSession() throws Exception {
		// record
		getMockSessionManager().getSession("sessionId");
		getMockSessionManagerControl().setReturnValue(null);

		replay();
		ResponseComm responseComm = doFetchUserProfile("sessionId");

		verify();
		FetchUserProfileResponse resp = (FetchUserProfileResponse) responseComm;
		assertEquals("sessionId", resp.getSessionID());
		assertNull(resp.getUserProfile());
		assertFalse(resp.getWarningFlag());
	}

	public void testNullUser() throws Exception {
		// record
		getMockSessionManager().getSession("sessionId");
		getMockSessionManagerControl().setReturnValue(new PowerEditorSession(getMockHttpSession(), "userId"));

		getMockSecurityCacheManager().getUser("userId");
		getMockSecurityCacheManagerControl().setReturnValue(null);

		replay();
		ResponseComm response = doFetchUserProfile("sessionId");

		verify();
		assertFailureResponse(response, "ServerError", "No user userId found", null);
	}

	public void testOverrideUserManagementIsReadOnlySetsReadOnlyIfUsersAreNotCached() throws Exception {
		MockControl mockControl = MockControl.createControl(PEDBCProvider.class);
		PEDBCProvider mockProvider = (PEDBCProvider) mockControl.getMock();
		LDAPUserManagementProvider userManagementProvider = new LDAPUserManagementProvider();
		mockControl.expectAndReturn(mockProvider.getUserManagementProvider(), userManagementProvider);
		mockControl.replay();

		ReflectionUtil.setPrivate(ServiceProviderFactory.class, "peDBCProvider", mockProvider);

		FetchUserProfileRequestHandler fetchUserProfileRequestHandler = new FetchUserProfileRequestHandler();
		UserManagementConfig userManagementConfig = new UserManagementConfig();
		invokeOverrideUserManagementIsReadOnly(fetchUserProfileRequestHandler, userManagementConfig);
		mockControl.verify();

		assertTrue(userManagementConfig.isReadOnly());
	}

	public void testGetPrivilegesReturnsPrivilegeObjects() throws Exception {
		String userID = "user-" + ObjectMother.createString();
		List<Role> roleList = new ArrayList<Role>();
		Role role = ObjectMother.attachPrivileges(ObjectMother.createRole(), 1);
		roleList.add(role);

		useMockSecurityCacheManager();
		securityCacheManagerMockControl.expectAndReturn(mockSecurityCacheManager.getRoles(userID), roleList);
		replayAllMockControls();

		FetchUserProfileRequestHandler fetchUserProfileRequestHandler = new FetchUserProfileRequestHandler();
		Set<Privilege> privList = invokeGetPrivileges(fetchUserProfileRequestHandler, userID);
		assertEquals(1, privList.size());
		assertEquals(Privilege.class, privList.iterator().next().getClass());
	}

	private void invokeOverrideUserManagementIsReadOnly(FetchUserProfileRequestHandler fetchUserProfileRequestHandler,
			UserManagementConfig userManagementConfig) throws Exception {
		ReflectionUtil.executePrivate(
				fetchUserProfileRequestHandler,
				"overrideUserManagemenConfig",
				new Class[] { UserManagementConfig.class },
				new Object[] { userManagementConfig });
	}

	@SuppressWarnings("unchecked")
	private Set<Privilege> invokeGetPrivileges(FetchUserProfileRequestHandler fetchUserProfileRequestHandler, String userID) throws Exception {
		return (Set<Privilege>) ReflectionUtil.executePrivate(
				fetchUserProfileRequestHandler,
				"getPrivileges",
				new Class[] { String.class },
				new Object[] { userID });
	}

	private ResponseComm doFetchUserProfile(String sessionId) {
		return new FetchUserProfileRequestHandler().serviceRequest(new FetchUserProfileRequest(sessionId), getMockHttpServletRequest());
	}

	protected void tearDown() throws Exception {
		ReflectionUtil.setPrivate(ServiceProviderFactory.class, "peDBCProvider", null);
		super.tearDown();
	}
}
