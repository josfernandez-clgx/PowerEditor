package com.mindbox.pe.server.spi;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.mindbox.pe.server.spi.audit.AllServerSpiAuditTestSuite;
import com.mindbox.pe.server.spi.db.AllServerSPIDBTestSuite;
import com.mindbox.pe.server.spi.pwd.AllServerSpiPwdTestSuite;

public final class AllServerSpiTestSuite {

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Server SPI Tests");
		suite.addTest(AllServerSpiAuditTestSuite.suite());
		suite.addTest(AllServerSPIDBTestSuite.suite());
		suite.addTest(AllServerSpiPwdTestSuite.suite());
		return suite;
	}
}
