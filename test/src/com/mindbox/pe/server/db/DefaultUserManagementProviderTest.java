package com.mindbox.pe.server.db;

import junit.framework.TestSuite;

import org.easymock.MockControl;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.server.Util;
import com.mindbox.pe.server.db.loaders.UserSecurityLoader;
import com.mindbox.pe.server.db.updaters.UserSecurityUpdater;
import com.mindbox.pe.server.spi.ServiceProviderFactory;
import com.mindbox.pe.server.spi.UserAuthenticationProviderPlugin;
import com.mindbox.pe.server.spi.UserManagementProvider;
import com.mindbox.pe.server.spi.db.UserDataProvider;
import com.mindbox.pe.server.spi.db.UserDataUpdater;
import com.mindbox.pe.server.spi.db.UserSecurityDataHolder;

public class DefaultUserManagementProviderTest extends AbstractTestBase {
	private static final int[] TEST_IDS = new int[] { 1, 2, 3 };

	private MockControl userLoaderController;
	private UserDataProvider userLoader;
	private MockControl userUpdaterController;
	private UserDataUpdater userUpdater;
	private MockControl userCacheController;
	private UserSecurityDataHolder userCache;

	private UserManagementProvider userMgr;

	public static TestSuite suite() {
		TestSuite suite = new TestSuite(DefaultUserManagementProviderTest.class.getName());
		suite.addTestSuite(DefaultUserManagementProviderTest.class);
		return suite;
	}

	public DefaultUserManagementProviderTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		userLoaderController = MockControl.createControl(UserDataProvider.class);
		userLoader = (UserDataProvider) userLoaderController.getMock();

		userUpdaterController = MockControl.createControl(UserDataUpdater.class);
		userUpdater = (UserDataUpdater) userUpdaterController.getMock();

		userCacheController = MockControl.createControl(UserSecurityDataHolder.class);
		userCache = (UserSecurityDataHolder) userCacheController.getMock();

		userMgr = new DefaultUserManagementProvider(userLoader, userUpdater);
	}

	public void testDefaultConstructor() throws Exception {
		UserManagementProvider userMgr = new DefaultUserManagementProvider();
		assertEquals(UserSecurityLoader.class, ReflectionUtil.getPrivate(userMgr, "userLoader").getClass());
		assertEquals(UserSecurityUpdater.class, ReflectionUtil.getPrivate(userMgr, "userUpdater").getClass());
	}

	public void testLoadAllPrivilegesDelegatesToLoader() throws Exception {
		userLoader.loadAllPrivileges(userCache);

		playback();
		userMgr.loadAllPrivileges(userCache);
		verify();
	}

	public void testLoadAllRolesDelegatesToLoader() throws Exception {
		userLoader.loadAllRoles(userCache);

		playback();
		userMgr.loadAllRoles(userCache);
		verify();
	}

	public void testLoadAllPrivilegesToRolesDelegatesToLoader() throws Exception {
		userLoader.loadAllPrivilegesToRoles(userCache);

		playback();
		userMgr.loadAllPrivilegesToRoles(userCache);
		verify();
	}

	public void testLoadAllUsersToRolesDelegatesToLoader() throws Exception {
		userLoader.loadAllUsersToRoles(userCache);

		playback();
		userMgr.loadAllUsersToRoles(userCache);
		verify();
	}

	public void testDeleteRoleDelegatesToUpdater() throws Exception {
		userUpdater.deleteRole(1);

		playback();
		userMgr.deleteRole(1);
		verify();
	}

	public void testDeleteUserDelegatesToUpdater() throws Exception {
		userUpdater.deleteUser("id1", Util.APPLICATION_USERNAME);

		playback();
		userMgr.deleteUser("id1", Util.APPLICATION_USERNAME);
		verify();
	}

	public void testInsertRoleDelegatesToUpdater() throws Exception {
		userUpdater.insertRole(1, "roleName", TEST_IDS);

		playback();
		userMgr.insertRole(1, "roleName", TEST_IDS);
		verify();
	}

	public void testInsertUserDelegatesToUpdater() throws Exception {

		userUpdater.insertUser("id1", "userName", "status", false, 0, TEST_IDS, null, Util.APPLICATION_USERNAME);

		playback();
		userMgr.insertUser("id1", "userName", "status", false, 0, TEST_IDS, null, Util.APPLICATION_USERNAME);
		verify();
	}

	public void testUpdateRoleDelegatesToUpdater() throws Exception {
		userUpdater.updateRole(1, "roleName", TEST_IDS);

		playback();
		userMgr.updateRole(1, "roleName", TEST_IDS);
		verify();
	}

	public void testUpdateUserDelegatesToUpdater() throws Exception {
		userUpdater.updateUser("id1", "userName", "status", false, 0, TEST_IDS, null, Util.APPLICATION_USERNAME);

		playback();
		userMgr.updateUser("id1", "userName", "status", false, 0, TEST_IDS, null, Util.APPLICATION_USERNAME);
		verify();
	}

	public void testUpdateFailedLoginCounterDelegatesToUpdater() throws Exception {
		int counter = ObjectMother.createInt();
		userUpdater.updateFailedLoginCounter("id1", counter);

		playback();
		userMgr.updateFailedLoginCounter("id1", counter);
		verify();
	}

	public void testArePasswordsPersistableFalse() throws Exception {
		// setup
		MockControl userAuthProviderController = MockControl.createControl(UserAuthenticationProviderPlugin.class);
		UserAuthenticationProviderPlugin mockUserAuthProvider = (UserAuthenticationProviderPlugin) userAuthProviderController.getMock();

		ReflectionUtil.setPrivate(ServiceProviderFactory.class, "userAuthenticationProvider", mockUserAuthProvider);

		// record
		mockUserAuthProvider.arePasswordsStoredExternally();
		userAuthProviderController.setReturnValue(true);

		userAuthProviderController.replay();
		assertFalse(userMgr.arePasswordsPersistable());
		userAuthProviderController.verify();

		ReflectionUtil.setPrivate(ServiceProviderFactory.class, "userAuthenticationProvider", null);
	}

	public void testArePasswordsPersistableTrue() throws Exception {
		// setup
		MockControl userAuthProviderController = MockControl.createControl(UserAuthenticationProviderPlugin.class);
		UserAuthenticationProviderPlugin mockUserAuthProvider = (UserAuthenticationProviderPlugin) userAuthProviderController.getMock();

		ReflectionUtil.setPrivate(ServiceProviderFactory.class, "userAuthenticationProvider", mockUserAuthProvider);

		// record
		mockUserAuthProvider.arePasswordsStoredExternally();
		userAuthProviderController.setReturnValue(false);

		userAuthProviderController.replay();
		assertTrue(userMgr.arePasswordsPersistable());
		userAuthProviderController.verify();

		ReflectionUtil.setPrivate(ServiceProviderFactory.class, "userAuthenticationProvider", null);
	}

	private void playback() {
		userLoaderController.replay();
		userUpdaterController.replay();
		userCacheController.replay();
	}

	private void verify() {
		userLoaderController.verify();
		userUpdaterController.verify();
		userCacheController.verify();
	}
}
