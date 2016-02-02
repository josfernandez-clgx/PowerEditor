package com.mindbox.pe.server.spi.db;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class AllServerSPIDBTestSuite {

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Server SPI DB Tests");
		suite.addTest(AbstractCachedUserDataProviderTest.suite());
		suite.addTest(AbstractNotCachedUserDataProviderTest.suite());
		suite.addTest(AbstractUnmodifiableUserDataUpdaterTest.suite());
		return suite;
	}
}
