package com.mindbox.pe.server.db;

import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.PasswordOneWayHashUtil;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.server.bizlogic.AbstractServerTestBase;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.model.User;

public class PeDbUserAuthenticationProviderTest extends AbstractServerTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("PeDbUserAuthenticationProviderTest Tests");
		suite.addTestSuite(PeDbUserAuthenticationProviderTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private PeDbUserAuthenticationProvider peDbUserAuthenticationProvider;
	private User user = null;

	public PeDbUserAuthenticationProviderTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		peDbUserAuthenticationProvider = new PeDbUserAuthenticationProvider();
	}

	protected void tearDown() throws Exception {
		if (user != null) {
			SecurityCacheManager.getInstance().removeFromCache(user.getUserID());
		}
		super.tearDown();
	}

	@SuppressWarnings("unchecked")
	public void testAuthenticateUsesOneWayHashedPassword() throws Exception {
		user = ObjectMother.createUser();
		String clearTextPwd = user.getCurrentPassword();
		user.setPassword(PasswordOneWayHashUtil.convertToOneWayHash(user.getCurrentPassword(), PasswordOneWayHashUtil.HASH_ALGORITHM_MD5));
		ReflectionUtil.getPrivate(SecurityCacheManager.getInstance(), "mUserHash", Map.class).put(user.getUserID(), user);
		
		useMockBizActionCoordinator();
		mockBizActionCoordinator.updateFailedLoginCounter(user.getUserID(), 0);
		replayAllMockControls();

		assertTrue(peDbUserAuthenticationProvider.authenticate(user.getUserID(), clearTextPwd));
		verifyAllMockControls();
	}

	public void testArePasswordsStoredExternallyReturnsFalse() throws Exception {
		assertFalse(peDbUserAuthenticationProvider.arePasswordsStoredExternally());
	}
}
