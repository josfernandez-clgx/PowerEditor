package com.mindbox.pe.server.audit.command;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Collection of all server audit test cases.
 * 
 */
public final class AllServerAuditCommandTestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Server Audit Command Tests");
		suite.addTest(ActivationContextUpdateAuditCommandTest.suite());
		suite.addTest(AuditDataBuilderTest.suite());
		suite.addTest(SimpleEventAuditCommandTest.suite());
		return suite;
	}
}
