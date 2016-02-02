package com.mindbox.pe.server.audit;

import com.mindbox.pe.server.audit.command.AllServerAuditCommandTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Collection of all server audit test cases.
 * 
 */
public final class AllServerAuditTestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Server Audit Tests");
		suite.addTest(DefaultAuditStorageTest.suite());
		suite.addTest(AllServerAuditCommandTestSuite.suite());
		return suite;
	}
}
