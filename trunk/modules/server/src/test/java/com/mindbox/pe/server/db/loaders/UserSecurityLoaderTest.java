package com.mindbox.pe.server.db.loaders;

import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class UserSecurityLoaderTest extends AbstractTestBase {

	@Test
	public void testCacheUserObjectsReturnsTrue() throws Exception {
		assertTrue(new UserSecurityLoader().cacheUserObjects());
	}

	@Test
	public void testGetRolesThrowsRuntimeException() throws Exception {
		assertThrowsException(new UserSecurityLoader(), "getRoles", new Class[] { String.class }, new Object[] { "str" }, RuntimeException.class);
	}
}
