package com.mindbox.pe.server.model;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class AbstractTypeIDIdentityTest extends AbstractTestBase {

	private static class TypeIDIdentityImpl extends AbstractTypeIDIdentity {
		TypeIDIdentityImpl(int type, int id) {
			super(type, id);
		}
	}

	@Test
	public void testInitWithNegativeTypeThrowsIllegalArgumentException() throws Exception {
		try {
			new TypeIDIdentityImpl(-1, 1);
			fail("Expected IllegalArgumentException not thrown");
		}
		catch (IllegalArgumentException ex) {
			// success
		}
	}

	@Test
	public void testInitWithNonPositiveIDThrowsIllegalArgumentException() throws Exception {
		try {
			new TypeIDIdentityImpl(7, -1);
			fail("Expected IllegalArgumentException not thrown");
		}
		catch (IllegalArgumentException ex) {
			// success
		}
	}

	@Test
	public void testInitWithZeroTypeSucceeds() throws Exception {
		new TypeIDIdentityImpl(0, 1);
	}
}
