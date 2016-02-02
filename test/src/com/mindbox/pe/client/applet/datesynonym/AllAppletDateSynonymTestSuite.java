package com.mindbox.pe.client.applet.datesynonym;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllAppletDateSynonymTestSuite  {
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AllAppletDateSynonymTestSuite Tests");
		suite.addTest(DateSynonymTableTest.suite());
		suite.addTest(ManageDateSynonymTabTest.suite());
		return suite;
	}

}
