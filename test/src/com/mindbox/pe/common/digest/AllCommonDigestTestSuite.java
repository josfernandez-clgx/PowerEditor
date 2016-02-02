package com.mindbox.pe.common.digest;

import junit.framework.Test;
import junit.framework.TestSuite;

public final class AllCommonDigestTestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite(AllCommonDigestTestSuite.class.getName());
		suite.addTest(DomainXMLDigesterTest.suite());
		suite.addTest(DomainAttributeFactoryTest.suite());
		return suite;
	}
}
