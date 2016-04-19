package com.mindbox.pe.server.ldap;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class UserObjectUserAuthenticationProviderTest extends AbstractTestBase {

	@Test
	public void testArePasswordsStoredExternallyReturnsTrueAlways() throws Exception {
		assertTrue(new UserObjectUserAuthenticationProvider().arePasswordsStoredExternally());
	}
}
