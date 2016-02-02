package com.mindbox.pe.server.config;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public final class AllServerConfigTestSuite {

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Server Config Tests");
		suite.addTest(ConfigurationManagerTest.suite());
		suite.addTest(ConfigXMLDigesterTest.suite());
		suite.addTest(DateFilterConfigTest.suite());
		suite.addTest(LineagePatternConfigSetTest.suite());
		suite.addTest(RuleGenerationConfigurationTest.suite());
		suite.addTest(PasswordTest.suite());
		suite.addTest(LDAPConnectionConfigTest.suite());
		suite.addTest(SessionConfigurationTest.suite());
		suite.addTest(UserPasswordPoliciesConfigTest.suite());
		return suite;
	}
}
