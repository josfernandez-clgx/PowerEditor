package com.mindbox.pe.server.db;

import static com.mindbox.pe.server.ServerTestObjectMother.createUser;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.mindbox.pe.common.PasswordOneWayHashUtil;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.server.AbstractServerTestBase;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.model.User;

public class PeDbUserAuthenticationProviderTest extends AbstractServerTestBase {
	private PeDbUserAuthenticationProvider peDbUserAuthenticationProvider;
	private User user = null;

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		peDbUserAuthenticationProvider = new PeDbUserAuthenticationProvider();
	}

	public void tearDown() throws Exception {
		if (user != null) {
			SecurityCacheManager.getInstance().removeFromCache(user.getUserID());
		}
		super.tearDown();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAuthenticateUsesOneWayHashedPassword() throws Exception {
		user = createUser();
		String clearTextPwd = user.getCurrentPassword();
		user.setPassword(PasswordOneWayHashUtil.convertToOneWayHash(user.getCurrentPassword(), PasswordOneWayHashUtil.HASH_ALGORITHM_MD5));
		ReflectionUtil.getPrivate(SecurityCacheManager.getInstance(), "userMap", Map.class).put(user.getUserID(), user);

		useMockBizActionCoordinator();
		mockBizActionCoordinator.updateFailedLoginCounter(user.getUserID(), 0);
		replayAllMocks();

		assertTrue(peDbUserAuthenticationProvider.authenticate(user.getUserID(), clearTextPwd));
		verifyAllMocks();
	}

	@Test
	public void testArePasswordsStoredExternallyReturnsFalse() throws Exception {
		assertFalse(peDbUserAuthenticationProvider.arePasswordsStoredExternally());
	}
}
