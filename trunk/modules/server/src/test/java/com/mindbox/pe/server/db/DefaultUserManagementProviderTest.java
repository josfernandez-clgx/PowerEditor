package com.mindbox.pe.server.db;

import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.server.db.loaders.UserSecurityLoader;
import com.mindbox.pe.server.db.updaters.UserSecurityUpdater;
import com.mindbox.pe.server.spi.ServiceProviderFactory;
import com.mindbox.pe.server.spi.UserAuthenticationProviderPlugin;
import com.mindbox.pe.server.spi.UserManagementProvider;
import com.mindbox.pe.server.spi.db.UserDataProvider;
import com.mindbox.pe.server.spi.db.UserDataUpdater;
import com.mindbox.pe.server.spi.db.UserSecurityDataHolder;
import com.mindbox.pe.unittest.AbstractTestBase;

public class DefaultUserManagementProviderTest extends AbstractTestBase {
	private static final int[] TEST_IDS = new int[] { 1, 2, 3 };

	private UserDataProvider userLoader;
	private UserDataUpdater userUpdater;
	private UserSecurityDataHolder userCache;

	private UserManagementProvider userMgr;

	@Before
	public void setUp() throws Exception {
		userLoader = createMock(UserDataProvider.class);

		userUpdater = createMock(UserDataUpdater.class);

		userCache = createMock(UserSecurityDataHolder.class);

		userMgr = new DefaultUserManagementProvider(userLoader, userUpdater);
	}

	@Test
	public void testDefaultConstructor() throws Exception {
		UserManagementProvider userMgr = new DefaultUserManagementProvider();
		assertEquals(UserSecurityLoader.class, ReflectionUtil.getPrivate(userMgr, "userLoader").getClass());
		assertEquals(UserSecurityUpdater.class, ReflectionUtil.getPrivate(userMgr, "userUpdater").getClass());
	}

	@Test
	public void testLoadAllPrivilegesDelegatesToLoader() throws Exception {
		userLoader.loadAllPrivileges(userCache);

		replayAllMocks();
		userMgr.loadAllPrivileges(userCache);
		verify();
	}

	@Test
	public void testLoadAllRolesDelegatesToLoader() throws Exception {
		userLoader.loadAllRoles(userCache);

		replayAllMocks();
		userMgr.loadAllRoles(userCache);
		verify();
	}

	@Test
	public void testLoadAllPrivilegesToRolesDelegatesToLoader() throws Exception {
		userLoader.loadAllPrivilegesToRoles(userCache);

		replayAllMocks();
		userMgr.loadAllPrivilegesToRoles(userCache);
		verify();
	}

	@Test
	public void testLoadAllUsersToRolesDelegatesToLoader() throws Exception {
		userLoader.loadAllUsersToRoles(userCache);

		replayAllMocks();
		userMgr.loadAllUsersToRoles(userCache);
		verify();
	}

	@Test
	public void testDeleteRoleDelegatesToUpdater() throws Exception {
		userUpdater.deleteRole(1);

		replayAllMocks();
		userMgr.deleteRole(1);
		verify();
	}

	@Test
	public void testDeleteUserDelegatesToUpdater() throws Exception {
		userUpdater.deleteUser("id1", "test");

		replayAllMocks();
		userMgr.deleteUser("id1", "test");
		verify();
	}

	@Test
	public void testInsertRoleDelegatesToUpdater() throws Exception {
		userUpdater.insertRole(1, "roleName", TEST_IDS);

		replayAllMocks();
		userMgr.insertRole(1, "roleName", TEST_IDS);
		verify();
	}

	@Test
	public void testInsertUserDelegatesToUpdater() throws Exception {

		userUpdater.insertUser("id1", "userName", "status", false, 0, TEST_IDS, null, "test");

		replayAllMocks();
		userMgr.insertUser("id1", "userName", "status", false, 0, TEST_IDS, null, "test");
		verify();
	}

	@Test
	public void testUpdateRoleDelegatesToUpdater() throws Exception {
		userUpdater.updateRole(1, "roleName", TEST_IDS);

		replayAllMocks();
		userMgr.updateRole(1, "roleName", TEST_IDS);
		verify();
	}

	@Test
	public void testUpdateUserDelegatesToUpdater() throws Exception {
		userUpdater.updateUser("id1", "userName", "status", false, 0, TEST_IDS, null, "test");

		replayAllMocks();
		userMgr.updateUser("id1", "userName", "status", false, 0, TEST_IDS, null, "test");
		verify();
	}

	@Test
	public void testUpdateFailedLoginCounterDelegatesToUpdater() throws Exception {
		int counter = createInt();
		userUpdater.updateFailedLoginCounter("id1", counter);

		replayAllMocks();
		userMgr.updateFailedLoginCounter("id1", counter);
		verify();
	}

	@Test
	public void testArePasswordsPersistableFalse() throws Exception {
		// setup
		UserAuthenticationProviderPlugin mockUserAuthProvider = createMock(UserAuthenticationProviderPlugin.class);

		ReflectionUtil.setPrivate(ServiceProviderFactory.class, "userAuthenticationProvider", mockUserAuthProvider);

		// record
		expect(mockUserAuthProvider.arePasswordsStoredExternally()).andReturn(true);

		replay(mockUserAuthProvider);
		assertFalse(userMgr.arePasswordsPersistable());
		verify(mockUserAuthProvider);

		ReflectionUtil.setPrivate(ServiceProviderFactory.class, "userAuthenticationProvider", null);
	}

	@Test
	public void testArePasswordsPersistableTrue() throws Exception {
		// setup
		UserAuthenticationProviderPlugin mockUserAuthProvider = createMock(UserAuthenticationProviderPlugin.class);

		ReflectionUtil.setPrivate(ServiceProviderFactory.class, "userAuthenticationProvider", mockUserAuthProvider);

		// record
		expect(mockUserAuthProvider.arePasswordsStoredExternally()).andReturn(false);

		replay(mockUserAuthProvider);
		assertTrue(userMgr.arePasswordsPersistable());
		verify(mockUserAuthProvider);

		ReflectionUtil.setPrivate(ServiceProviderFactory.class, "userAuthenticationProvider", null);
	}

	private void replayAllMocks() {
		replay(userCache, userLoader, userUpdater);
	}
}
