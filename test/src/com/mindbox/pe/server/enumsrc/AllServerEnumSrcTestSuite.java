package com.mindbox.pe.server.enumsrc;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.server.enumsrc.xml.AllServerEnumSrcXmlTestSuite;

public final class AllServerEnumSrcTestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Server EnumSource Tests");
		suite.addTest(AbstractEnumerationSourceTest.suite());
		suite.addTest(AllServerEnumSrcXmlTestSuite.suite());
		suite.addTest(XMLEnumerationSourceTest.suite());
		return suite;
	}
}
