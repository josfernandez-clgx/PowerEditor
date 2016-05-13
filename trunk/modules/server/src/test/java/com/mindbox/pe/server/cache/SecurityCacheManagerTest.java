package com.mindbox.pe.server.cache;

import static com.mindbox.pe.server.ServerTestObjectMother.createPrivilege;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.server.AbstractServerTestBase;
import com.mindbox.pe.server.model.User;

public class SecurityCacheManagerTest extends AbstractServerTestBase {

	private Privilege priv;
	private SecurityCacheManager securityCacheManager;

	private Privilege getTestPriv() {
		Iterator<Privilege> privIter = SecurityCacheManager.getInstance().getPrivileges();
		if (privIter.hasNext()) {
			return privIter.next();
		}

		Privilege newPriv = createPrivilege();
		SecurityCacheManager.getInstance().addPrivilege(newPriv.getId(), newPriv.getName(), newPriv.getDisplayString(), newPriv.getPrivilegeType());
		return newPriv;
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		priv = getTestPriv();
		securityCacheManager = SecurityCacheManager.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		SecurityCacheManager.getInstance().startLoading();
		super.tearDown();
	}

	@Test
	public void testAuthorizeWithNoUserCacheDoesNotCheckUserExistence() throws Exception {
		useMockUserManagementProvider(1);
		expect(mockUserManagementProvider.cacheUserObjects()).andReturn(false);

		replayAllMocks();

		assertTrue(securityCacheManager.authorize("no-one", ""));
	}

	@Test
	public void testFindPrivilegeByName() throws Exception {
		assertEquals(priv, SecurityCacheManager.getInstance().findPrivilegeByName(priv.getName()));
		assertNull(SecurityCacheManager.getInstance().findPrivilegeByName(priv.getName() + "foobar"));
	}

	@Test
	public void testGetRolesWithNoUserCacheCallsGetRolesOnUserManagementProvider() throws Exception {
		useMockUserManagementProvider(2);
		List<Role> emptyList = new ArrayList<Role>();
		String userID = "some-user-id";
		expect(mockUserManagementProvider.cacheUserObjects()).andReturn(false);
		expect(mockUserManagementProvider.getRoles(userID)).andReturn(emptyList);

		replayAllMocks();

		assertSame(emptyList, securityCacheManager.getRoles(userID));
		verifyAllMocks();
	}

	@Test
	public void testGetRolesWithUserCachesWithInvalidUserIDThrowsNullPointerException() throws Exception {
		useMockUserManagementProvider(2);
		expect(mockUserManagementProvider.cacheUserObjects()).andReturn(true).times(2);

		replayAllMocks();

		assertThrowsNullPointerException(securityCacheManager, "getRoles", new Class[] { String.class }, new Object[] { "no-one" });
	}

	@Test
	public void testGetUserWithNoUserCacheReturnsNewUserObject() throws Exception {
		useMockUserManagementProvider(2);
		expect(mockUserManagementProvider.cacheUserObjects()).andReturn(false).times(2);
		replayAllMocks();

		User user = securityCacheManager.getUser(null);
		assertNotNull(user);

		User user2 = securityCacheManager.getUser(null);
		assertNotSame(user, user2);
	}

	@Test
	public void testGetUserWithUserCacheAndInvalidIDReturnsNull() throws Exception {
		useMockUserManagementProvider();
		expect(mockUserManagementProvider.cacheUserObjects()).andReturn(true);
		replayAllMocks();

		assertNull(securityCacheManager.getUser("no-one"));
	}

	@Test
	public void testGetUserWithUserCacheAndNullIDReturnsNull() throws Exception {
		useMockUserManagementProvider();
		expect(mockUserManagementProvider.cacheUserObjects()).andReturn(true);
		replayAllMocks();

		assertNull(securityCacheManager.getUser(null));
	}
}
