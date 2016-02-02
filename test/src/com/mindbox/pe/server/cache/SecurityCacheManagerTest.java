package com.mindbox.pe.server.cache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.server.bizlogic.AbstractServerTestBase;
import com.mindbox.pe.server.model.User;

public class SecurityCacheManagerTest extends AbstractServerTestBase {

	public static TestSuite suite() {
		TestSuite suite = new TestSuite(SecurityCacheManagerTest.class.getName());
		suite.addTestSuite(SecurityCacheManagerTest.class);
		return suite;
	}

	public SecurityCacheManagerTest(String name) {
		super(name);
	}

	private Privilege priv;
	private SecurityCacheManager securityCacheManager;

	protected void setUp() throws Exception {
		super.setUp();
		priv = getTestPriv();
		securityCacheManager = SecurityCacheManager.getInstance();
	}

	protected void tearDown() throws Exception {
		SecurityCacheManager.getInstance().startLoading();
		super.tearDown();
	}

	public void testAuthorizeWithNoUserCacheDoesNotCheckUserExistence() throws Exception {
		useMockUserManagementProvider(1);
		userManagementProviderMockControl.expectAndReturn(mockUserManagementProvider.cacheUserObjects(), false, 1);

		replayAllMockControls();

		assertTrue(securityCacheManager.authorize("no-one", ""));
	}

	public void testGetRolesWithUserCachesWithInvalidUserIDThrowsNullPointerException() throws Exception {
		useMockUserManagementProvider(2);
		userManagementProviderMockControl.expectAndReturn(mockUserManagementProvider.cacheUserObjects(), true, 2);

		replayAllMockControls();

		assertThrowsNullPointerException(securityCacheManager, "getRoles", new Class[] { String.class }, new Object[] { "no-one" });
	}

	public void testGetRolesWithNoUserCacheCallsGetRolesOnUserManagementProvider() throws Exception {
		useMockUserManagementProvider(2);
		List<Role> emptyList = new ArrayList<Role>();
		String userID = "some-user-id";
		userManagementProviderMockControl.expectAndReturn(mockUserManagementProvider.cacheUserObjects(), false);
		userManagementProviderMockControl.expectAndReturn(mockUserManagementProvider.getRoles(userID), emptyList);

		replayAllMockControls();

		assertSame(emptyList, securityCacheManager.getRoles(userID));
		verifyAllMockControls();
	}

	public void testGetUserWithUserCacheAndInvalidIDReturnsNull() throws Exception {
		useMockUserManagementProvider();
		userManagementProviderMockControl.expectAndReturn(mockUserManagementProvider.cacheUserObjects(), true);
		replayAllMockControls();

		assertNull(securityCacheManager.getUser("no-one"));
	}

	public void testGetUserWithUserCacheAndNullIDReturnsNull() throws Exception {
		useMockUserManagementProvider();
		userManagementProviderMockControl.expectAndReturn(mockUserManagementProvider.cacheUserObjects(), true);
		replayAllMockControls();

		assertNull(securityCacheManager.getUser(null));
	}

	public void testGetUserWithNoUserCacheReturnsNewUserObject() throws Exception {
		useMockUserManagementProvider(2);
		userManagementProviderMockControl.expectAndReturn(mockUserManagementProvider.cacheUserObjects(), false, 2);
		replayAllMockControls();

		User user = securityCacheManager.getUser(null);
		assertNotNull(user);

		User user2 = securityCacheManager.getUser(null);
		assertNotSame(user, user2);
	}

	public void testFindPrivilegeByName() throws Exception {
		assertEquals(priv, SecurityCacheManager.getInstance().findPrivilegeByName(priv.getName()));
		assertNull(SecurityCacheManager.getInstance().findPrivilegeByName(priv.getName() + "foobar"));
	}

	private Privilege getTestPriv() {
		Iterator<Privilege> privIter = SecurityCacheManager.getInstance().getPrivileges();
		if (privIter.hasNext()) {
			return privIter.next();
		}

		Privilege newPriv = ObjectMother.createPrivilege();
		SecurityCacheManager.getInstance().addPrivilege(
				newPriv.getId(),
				newPriv.getName(),
				newPriv.getDisplayString(),
				newPriv.getPrivilegeType());
		return newPriv;
	}
}
