package com.mindbox.pe.server.imexport.digest;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;

public class UserTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("UserTest Tests");
		suite.addTestSuite(UserTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public UserTest(String name) {
		super(name);
	}

	public void testSetStausSetsStatus() throws Exception {
		User user = new User();
		String status = "status-" + ObjectMother.createString();
		user.setStaus(status);
		assertEquals(status, user.getStatus());
	}
}
