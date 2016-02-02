package com.mindbox.pe.server.model;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class AbstractTypeIDIdentityTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractTypeIDIdentityTest Tests");
		suite.addTestSuite(AbstractTypeIDIdentityTest.class);
		return suite;
	}

	private static class TypeIDIdentityImpl extends AbstractTypeIDIdentity {
		TypeIDIdentityImpl(int type, int id) {
			super(type, id);
		}
	}

	public AbstractTypeIDIdentityTest(String name) {
		super(name);
	}

	public void testInitWithNegativeTypeThrowsIllegalArgumentException() throws Exception {
		try {
			new TypeIDIdentityImpl(-1, 1);
			fail("Expected IllegalArgumentException not thrown");
		}
		catch (IllegalArgumentException ex) {
			// success
		}
	}

	public void testInitWithZeroTypeSucceeds() throws Exception {
		new TypeIDIdentityImpl(0, 1);
	}

	public void testInitWithNonPositiveIDThrowsIllegalArgumentException() throws Exception {
		try {
			new TypeIDIdentityImpl(7, -1);
			fail("Expected IllegalArgumentException not thrown");
		}
		catch (IllegalArgumentException ex) {
			// success
		}
	}
}
