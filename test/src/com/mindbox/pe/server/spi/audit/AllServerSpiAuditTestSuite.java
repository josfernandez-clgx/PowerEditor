package com.mindbox.pe.server.spi.audit;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public final class AllServerSpiAuditTestSuite {

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Server SPI Audit Tests");
		suite.addTest(MutableAuditEventTest.suite());
		return suite;
	}
}
