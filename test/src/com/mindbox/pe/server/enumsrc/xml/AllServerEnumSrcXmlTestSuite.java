package com.mindbox.pe.server.enumsrc.xml;

import junit.framework.Test;
import junit.framework.TestSuite;

public final class AllServerEnumSrcXmlTestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Server EnumSource XML Tests");
		suite.addTest(EnumValueDigestTest.suite());
		return suite;
	}
}
