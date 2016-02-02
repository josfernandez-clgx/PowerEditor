package com.mindbox.pe.server.spi.pwd;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public final class AllServerSpiPwdTestSuite {

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Server SPI Password Tests");
		suite.addTest(RegexpPasswordValidatorTest.suite());
		suite.addTest(DefaultPasswordValidatorTest.suite());
		return suite;
	}
}
